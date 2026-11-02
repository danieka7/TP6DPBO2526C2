# Tugas Praktikum 6 - Desain Pemrograman Berorientasi Objek

## Janji
Saya **Dani Eka Saputra** dengan NIM **2501158** mengerjakan Tugas Praktikum 6 pada Mata Kuliah Desain Pemrograman Berorientasi Objek untuk keberkahan-Nya maka saya tidak melakukan kecurangan seperti yang telah dispesifikasikan. Aamiin

Repository ini merupakan implementasi **game Flappy Bird** menggunakan **Java Swing**. Program ini mendemonstrasikan penggunaan konsep OOP (Object-Oriented Programming) pada aplikasi GUI berbasis game: pemisahan tanggung jawab antara logika (`Logic`), tampilan (`View`, `MainMenu`), dan objek permainan (`Player`, `Pipe`), serta pemutaran efek suara melalui `SoundPlayer`.

Pemain mengendalikan seekor burung yang harus melewati celah di antara pipa-pipa tanpa menabrak pipa maupun jatuh ke dasar layar. Setiap pasang pipa yang berhasil dilewati menambah skor sebanyak 1.

## 🗂️ Struktur Projek
```
.
├── dokumentasi
│   ├── design.png
│   ├── menuUtama.png
│   ├── permainan.png
│   └── gameOver.png
├── src
│   ├── App.java
│   ├── Logic.java
│   ├── View.java
│   ├── MainMenu.java
│   ├── Player.java
│   ├── Pipe.java
│   ├── SoundPlayer.java
│   └── assets
│       ├── background.png
│       ├── flappy_bird_backdrop.jpg
│       ├── bird.png
│       ├── upperPipe.png
│       ├── lowerPipe.png
│       └── sound
│           ├── bgm.wav
│           ├── spongebob-boowomp.wav
│           ├── metal-pipe-clang.wav
│           ├── rawr-keratau-berisikk.wav
│           └── add-points.wav
└── README.md
```

## Fitur Utama
### Java Swing (GUI)
- **Menu utama** berisi judul game serta tombol **Mulai Main** dan **Keluar**
- **Burung melompat** saat tombol **SPASI** ditekan, dan jatuh karena gravitasi
- **Pipa atas dan bawah** muncul otomatis setiap 1,5 detik dengan posisi celah acak
- **Latar belakang bergerak** (efek *scrolling*) dengan kecepatan lebih lambat dari pipa agar terkesan jauh
- **Skor** ditampilkan pada `JLabel` di bagian atas layar dan bertambah 1 setiap melewati sepasang pipa
- **Game Over** saat burung menabrak pipa atau jatuh ke dasar layar
- **Restart** dengan menekan tombol **R** saat Game Over
- **Kembali ke menu** dengan menekan tombol **Esc** saat Game Over
- **Efek suara dan musik latar**: suara lompat, suara melewati pipa, suara Game Over, dan BGM yang diputar berulang

### Kontrol Permainan
| Tombol  | Fungsi                                          | Kondisi            |
|---------|-------------------------------------------------|--------------------|
| `SPASI` | Burung melompat                                 | Game berjalan      |
| `R`     | Mengulang permainan dari awal                   | Saat Game Over     |
| `Esc`   | Kembali ke menu utama                           | Saat Game Over     |

## Desain dan Penjelasan


### Alur Program
1. `App` membuat `JFrame`, objek `Logic`, dan objek `View`, lalu menghubungkan keduanya (`logika.setView(tampilan)`).
2. `App` memakai **CardLayout** untuk menumpuk dua panel: `MainMenu` (kartu `"menu"`) dan `View` (kartu `"game"`). Hanya satu yang tampil pada satu waktu.
3. Saat tombol **Mulai Main** ditekan, `App` menampilkan kartu `"game"`, memfokuskan keyboard ke `View`, lalu memanggil `logika.startGame()`.
4. `Logic` menjalankan dua `Timer`: `gameLoop` (60 FPS) untuk menggerakkan objek dan memeriksa kondisi, serta `pipesCooldown` (1,5 detik) untuk membuat pipa baru.
5. Pada setiap *frame*, `actionPerformed()` memanggil `move()`, `checkScore()`, `checkGameOver()`, kemudian `view.repaint()`.
6. Saat Game Over, kedua `Timer` dihentikan. Pemain dapat menekan **R** (restart) atau **Esc** (kembali ke menu melalui *callback* `onBackToMenu`).

### Struktur Data Kelas

- #### App
  Kelas `App` adalah titik masuk program (memiliki `main`). Kelas ini menyusun jendela, menghubungkan `Logic` dengan `View`, dan mengatur perpindahan antara menu dan game.

  | Elemen                | Keterangan                                                              |
    |-----------------------|-------------------------------------------------------------------------|
  | `frame` *(JFrame)*    | Jendela utama berukuran 360 x 640, tidak dapat diubah ukurannya         |
  | `cardLayout` *(CardLayout)* | Layout yang menumpuk panel menu dan panel game                    |
  | `container` *(JPanel)* | Wadah untuk kedua kartu ("menu" dan "game")                            |

- #### Player
  Kelas model untuk burung yang dikendalikan pemain.

  | Atribut                | Tipe    | Keterangan                                           |
    |------------------------|---------|------------------------------------------------------|
  | `posX`, `posY`         | `int`   | Posisi burung pada layar                             |
  | `width`, `height`      | `int`   | Ukuran burung (34 x 24)                              |
  | `image`                | `Image` | Gambar burung                                        |
  | `velocityY`            | `int`   | Kecepatan vertikal (negatif = naik, positif = turun) |

  **Method**: konstruktor berparameter, getter dan setter semua atribut

- #### Pipe
  Kelas model untuk satu buah pipa (atas atau bawah).

  | Atribut                | Tipe      | Keterangan                                                  |
    |------------------------|-----------|-------------------------------------------------------------|
  | `posX`, `posY`         | `int`     | Posisi pipa pada layar                                      |
  | `width`, `height`      | `int`     | Ukuran pipa (64 x 512)                                      |
  | `image`                | `Image`   | Gambar pipa (atas atau bawah)                               |
  | `velocityX`            | `int`     | Atribut kecepatan horizontal pipa                           |
  | `passed`               | `boolean` | Penanda apakah pipa sudah dilewati burung (agar skor tidak dihitung berulang) |

  **Method**: konstruktor berparameter, getter dan setter semua atribut

- #### Logic
  Kelas pengatur seluruh logika permainan. Mengimplementasikan `ActionListener` (untuk `Timer` game loop) dan `KeyListener` (untuk membaca tombol keyboard).

  | Atribut                                   | Keterangan                                                                 |
    |-------------------------------------------|----------------------------------------------------------------------------|
  | `frameWidth`, `frameHeight`               | Ukuran area permainan (360 x 640)                                          |
  | `playerStartPosX`, `playerStartPosY`      | Posisi awal burung (tengah layar)                                          |
  | `pipeStartPosX`, `pipeWidth`, `pipeHeight` | Posisi awal dan ukuran pipa                                               |
  | `gravity` *(int)*                         | Percepatan jatuh burung per *frame* (nilai 1)                              |
  | `pipeVelocityX` *(int)*                   | Kecepatan geser pipa ke kiri (nilai -2)                                    |
  | `backgroundPosX`, `backgroundVelocityX`, `backgroundWidth` | Posisi, kecepatan geser (-1), dan lebar latar belakang    |
  | `gameLoop` *(Timer)*                      | Timer 60 FPS yang menjalankan permainan                                    |
  | `pipesCooldown` *(Timer)*                 | Timer pembuat pipa baru setiap 1,5 detik                                   |
  | `player` *(Player)*                       | Objek burung                                                               |
  | `pipes` *(ArrayList&lt;Pipe&gt;)*         | Penyimpan seluruh pipa yang sedang aktif                                   |
  | `gameOver` *(boolean)*, `score` *(int)*   | Status permainan dan skor                                                  |
  | `onBackToMenu` *(Runnable)*               | *Callback* untuk kembali ke menu, diisi oleh `App`                         |
  | `bgm`, `jumpSound`, `gameOverSound`, `gameOverSound1`, `passSound` *(SoundPlayer)* | Efek suara dan musik latar |

  **Method**:

  | Method                    | Keterangan                                                                              |
    |---------------------------|-----------------------------------------------------------------------------------------|
  | `setView()`               | Menghubungkan `Logic` dengan `View`                                                     |
  | `startGame()`             | Memulai game dari menu: menyalakan BGM dan memanggil `resetGame()`                      |
  | `resetGame()`             | Mengembalikan semua nilai ke kondisi awal lalu menjalankan kedua timer                  |
  | `placePipes()`            | Membuat sepasang pipa (atas dan bawah) dengan posisi celah acak setinggi `frameHeight / 4` |
  | `move()`                  | Menggerakkan latar, burung (gravitasi), dan pipa; menghapus pipa yang sudah keluar layar |
  | `checkScore()`            | Menambah skor saat pipa atas telah dilewati burung                                      |
  | `isCollide()`             | Mendeteksi tabrakan dua persegi (burung dan pipa)                                       |
  | `checkGameOver()`         | Memeriksa tabrakan atau jatuh ke dasar; jika ya, menghentikan timer dan memutar suara   |
  | `actionPerformed()`       | Dipanggil setiap *frame*: `move()`, `checkScore()`, `checkGameOver()`, lalu `repaint()` |
  | `keyPressed()`            | Menangani tombol SPASI (lompat), R (restart), dan Esc (kembali ke menu)                 |
  | getter dan setter         | `getPlayer()`, `getPipes()`, `getBackgroundImage()`, `getBackgroundPosX()`, `getBackgroundWidth()`, `isGameOver()`, `getScore()`, `setOnBackToMenu()`, `getOnBackToMenu()` |

- #### View
  Kelas tampilan permainan, turunan dari `JPanel`. Hanya bertugas menggambar; seluruh data diambil dari `Logic`.

  | Atribut                | Keterangan                                                      |
    |------------------------|-----------------------------------------------------------------|
  | `width`, `height`      | Ukuran panel (360 x 640)                                        |
  | `logic` *(Logic)*      | Referensi ke `Logic` untuk membaca data yang akan digambar      |
  | `scoreLabel` *(JLabel)* | Label skor yang ditempel di bagian atas (`BorderLayout.NORTH`) |

  **Method**:

  | Method                 | Keterangan                                                                     |
    |------------------------|--------------------------------------------------------------------------------|
  | `updateScore()`        | Memperbarui teks skor; dipanggil `Logic` saat skor berubah                     |
  | `paintComponent()`     | Menggambar ulang panel dengan memanggil `draw()`                               |
  | `draw()`               | Menggambar latar (dua salinan agar mulus), pipa, burung, dan tulisan Game Over |
  | `drawCenteredString()` | Fungsi bantu untuk menggambar teks tepat di tengah secara horizontal           |

- #### MainMenu
  Kelas tampilan menu utama, turunan dari `JPanel`.

  | Atribut                       | Keterangan                                                    |
    |-------------------------------|---------------------------------------------------------------|
  | `startButton` *(JButton)*     | Tombol **Mulai Main**, aksinya ditentukan oleh `App`          |
  | `exitButton` *(JButton)*      | Tombol **Keluar**, menutup seluruh program (`System.exit(0)`) |
  | `backgroundImage` *(Image)*   | Gambar latar menu                                             |

  **Method**: konstruktor (menerima `ActionListener` untuk tombol Mulai) dan `paintComponent()` (menggambar latar di tengah dengan rasio asli terjaga)

- #### SoundPlayer
  Kelas pembungkus (*wrapper*) untuk memutar satu file suara berformat **WAV**. Data suara dibaca satu kali ke memori, lalu setiap pemutaran membuat `Clip` baru sehingga suara yang belum selesai tidak terpotong saat diputar lagi.

  | Atribut                          | Keterangan                                                           |
    |----------------------------------|----------------------------------------------------------------------|
  | `format` *(AudioFormat)*         | Format audio (sample rate, channel, dll)                             |
  | `data` *(byte[])*                | Isi suara dalam bentuk byte, dibaca sekali di konstruktor            |
  | `activeClips` *(List&lt;Clip&gt;)* | Daftar `Clip` yang sedang berbunyi, dipakai oleh `stop()`          |

  **Method**:

  | Method         | Keterangan                                                          |
    |----------------|---------------------------------------------------------------------|
  | `createClip()` | Membuat `Clip` baru dari data di memori dan menutupnya otomatis saat selesai |
  | `play()`       | Memutar suara satu kali                                             |
  | `loop()`       | Memutar suara berulang terus-menerus (untuk BGM)                    |
  | `stop()`       | Menghentikan semua suara yang sedang berbunyi dari objek ini        |

### Penerapan Konsep OOP

- **Enkapsulasi**: atribut pada `Player` dan `Pipe` dibuat `private` dan diakses melalui getter dan setter.
- **Pewarisan (Inheritance)**: `View` dan `MainMenu` merupakan turunan `JPanel`.
- **Implementasi Interface**: `Logic` mengimplementasikan `ActionListener` dan `KeyListener`.
- **Komposisi / Asosiasi**: `Logic` memiliki objek `Player`, kumpulan `Pipe`, dan beberapa `SoundPlayer`; `View` menyimpan referensi ke `Logic`.
- **Pemisahan tanggung jawab**: `Logic` mengurus aturan permainan, `View` dan `MainMenu` mengurus tampilan, `Player` dan `Pipe` menyimpan data objek, `SoundPlayer` mengurus suara.
- **Callback**: `MainMenu` menerima `ActionListener` dan `Logic` menerima `Runnable` (`onBackToMenu`) dari `App`, sehingga kelas-kelas tersebut tidak saling bergantung langsung.

### Pengaturan Fisika Permainan

| Parameter                     | Nilai            |
|-------------------------------|------------------|
| Kecepatan *frame*             | 60 FPS           |
| Gravitasi                     | +1 per *frame*   |
| Kecepatan lompat              | -10              |
| Kecepatan geser pipa          | -2 per *frame*   |
| Kecepatan geser latar         | -1 per *frame*   |
| Jeda kemunculan pipa          | 1500 ms          |
| Tinggi celah antar pipa       | 160 px (`frameHeight / 4`) |


## Dokumentasi
- ### Java Swing
    - #### Tampilan Menu Utama
      ![menuUtama](<Dokumentasi/menuUtama.png>)
    - #### Tampilan Saat Bermain
      ![permainan](<Dokumentasi/permainan.png>)
    - #### Tampilan Game Over
      ![gameOver](<Dokumentasi/gameOver.png>)