import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

// Kelas SoundPlayer = pembungkus (wrapper) untuk memutar satu file suara.
// Satu objek SoundPlayer mewakili satu file suara (misalnya suara lompat).
// Catatan: Java Sound bawaan hanya mendukung format WAV (bukan MP3).
//
// Cara kerja: data suara dibaca SEKALI ke memori (array byte), lalu setiap play()
// membuat Clip baru dari data itu. Karena tiap pemutaran punya Clip sendiri,
// suara yang belum selesai TIDAK terpotong ketika SPASI ditekan lagi (suara bertumpuk).
public class SoundPlayer {

    private AudioFormat format;   // format audio (sample rate, channel, dll)
    private byte[] data;          // isi suara dalam bentuk byte, dibaca sekali di konstruktor

    // Daftar Clip yang sedang berbunyi, dipakai oleh stop() untuk menghentikan semuanya.
    // CopyOnWriteArrayList aman dipakai walau diakses dari thread yang berbeda.
    private List<Clip> activeClips = new CopyOnWriteArrayList<Clip>();

    // path = lokasi file suara relatif terhadap kelas ini, contoh: "assets/jump.wav"
    public SoundPlayer(String path) {
        try {
            // Cari file lewat getResource, sama seperti cara memuat gambar burung dan pipa
            URL url = getClass().getResource(path);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(url);
            format = audioStream.getFormat();

            // Baca seluruh isi file suara ke array byte
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = audioStream.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            data = out.toByteArray();
            audioStream.close();
        } catch (Exception e) {
            // Jika file tidak ditemukan / format tidak didukung, game tetap jalan tanpa suara
            System.out.println("Gagal memuat suara " + path + ": " + e.getMessage());
            data = null;
        }
    }

    // Membuat Clip baru dari data suara yang sudah ada di memori
    private Clip createClip() {
        if (data == null) return null;   // tidak ada suara yang berhasil dimuat
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(format, data, 0, data.length);
            activeClips.add(clip);

            // Saat suara selesai (atau di-stop), tutup Clip agar tidak menumpuk di memori
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                    activeClips.remove(clip);
                }
            });
            return clip;
        } catch (Exception e) {
            // Misalnya terlalu banyak suara berbunyi bersamaan: suara ini dilewati saja
            System.out.println("Gagal memutar suara: " + e.getMessage());
            return null;
        }
    }

    // Memutar suara satu kali. Jika suara sebelumnya belum selesai, ia tetap berbunyi
    // dan suara baru ikut berbunyi bersamaan (tidak ada yang dihentikan).
    public void play() {
        Clip clip = createClip();
        if (clip != null) {
            clip.start();
        }
    }

    // Memutar suara berulang terus-menerus (dipakai jika ingin BGM)
    public void loop() {
        Clip clip = createClip();
        if (clip != null) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    // Menghentikan SEMUA suara dari objek ini (berguna untuk menghentikan BGM)
    public void stop() {
        for (Clip clip : activeClips) {
            clip.stop(); // listener di atas yang akan menutup Clip-nya
        }
    }
}