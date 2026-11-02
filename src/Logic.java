import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class Logic implements ActionListener, KeyListener {
    int frameWidth = 360;
    int frameHeight = 640;

    int playerStartPosX = frameWidth / 2;
    int playerStartPosY = frameHeight / 2;
    int playerWidth = 34;
    int playerHeight = 24;

    // atribut posisi dan ukuran pipa
    int pipeStartPosX = frameWidth;
    int pipeStartPosY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    Timer gameLoop;
    Timer pipesCooldown;
    int gravity = 1;

    int pipeVelocityX = -2;

    View view;
    Image birdImage;
    Player player;

    // latar belakang
    Image backgroundImage;
    int backgroundPosX = 0;        // posisi X latar saat ini
    int backgroundVelocityX = -1;  // kecepatan geser latar (lebih lambat dari pipa agar terkesan jauh)
    int backgroundWidth;   // lebar latar setelah diskalakan

    // list pipa dan gambarnya
    Image lowerPipeImage;
    Image upperPipeImage;
    ArrayList<Pipe> pipes;

    // [BARU] status permainan dan skor
    boolean gameOver = false;
    int score = 0;

    Runnable onBackToMenu;


    SoundPlayer bgm;
    // [BARU] efek suara saat burung melompat
    SoundPlayer jumpSound;

    // [BARU] efek suara saat Game Over (menabrak pipa atau jatuh ke dasar)
    SoundPlayer gameOverSound;
    SoundPlayer gameOverSound1;

    SoundPlayer passSound;

    public Logic(){
        backgroundImage = new ImageIcon(getClass().getResource("assets/flappy_bird_backdrop.jpg")).getImage();
        birdImage = new ImageIcon(getClass().getResource("assets/bird.png")).getImage();
        player = new Player(playerStartPosX, playerStartPosY, playerWidth, playerHeight, birdImage);

        lowerPipeImage = new ImageIcon(getClass().getResource("assets/lowerPipe.png")).getImage();
        upperPipeImage = new ImageIcon(getClass().getResource("assets/upperPipe.png")).getImage();
        pipes = new ArrayList<Pipe>();

        backgroundWidth = backgroundImage.getWidth(null) * frameHeight / backgroundImage.getHeight(null);

        bgm = new SoundPlayer("assets/sound/bgm.wav");
        // [BARU] muat file suara lompat sekali saja di awal (bukan setiap SPASI ditekan)
        jumpSound = new SoundPlayer("assets/sound/spongebob-boowomp.wav");
        // [BARU] muat suara game over sekali di awal juga
        gameOverSound = new SoundPlayer("assets/sound/metal-pipe-clang.wav");
        gameOverSound1 = new SoundPlayer("assets/sound/rawr-keratau-berisikk.wav");
        passSound = new SoundPlayer("assets/sound/add-points.wav");


        // Timer pembuat pipa tiap 1,5 detik.
        // [UBAH] Timer hanya DIBUAT di sini, belum dijalankan.
        // Dijalankan lewat startGame() setelah pemain menekan "Mulai Main" di menu.
        pipesCooldown = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                placePipes();
            }
        });

        gameLoop = new Timer(1000/60, this); // 60 FPS
    }

    public void setView(View view){
        this.view = view;
    }

    public Player getPlayer() {
        return player;
    }

    public ArrayList<Pipe> getPipes(){
        return pipes;
    }

    public Image getBackgroundImage() {
        return backgroundImage;
    }

    public int getBackgroundPosX(){
        return backgroundPosX;
    }

    public int getBackgroundWidth() {
        return backgroundWidth;
    }

    // [BARU] getter agar View bisa tahu status game dan skor
    public boolean isGameOver(){
        return gameOver;
    }

    public void setOnBackToMenu(Runnable onBackToMenu) {
        this.onBackToMenu = onBackToMenu;
    }

    public Runnable getOnBackToMenu() {
        return onBackToMenu;
    }

    public int getScore(){
        return score;
    }

    // [BARU] dipanggil dari tombol "Mulai Main" di menu
    public void startGame(){
        bgm.stop();
        bgm.loop();
        resetGame();
    }

    // [BARU] mengembalikan semua nilai ke kondisi awal lalu menjalankan timer.
    // Dipakai untuk mulai pertama kali DAN untuk restart (tombol R).
    public void resetGame(){
        player.setPosX(playerStartPosX);
        player.setPosY(playerStartPosY);
        player.setVelocityY(0);   // burung diam dulu, gravitasi yang akan menariknya
        pipes.clear();            // hapus semua pipa lama
        score = 0;
        gameOver = false;
        backgroundPosX = 0;

        if(view != null){
            view.updateScore(score);
            view.repaint();
        }

        // restart() = start kalau belum jalan, atau mulai ulang hitungan dari awal
        pipesCooldown.restart();
        gameLoop.restart();
    }

    // [UBAH] sekarang membuat DUA pipa (atas dan bawah) dengan celah di antaranya
    public void placePipes(){
        // posisi Y pipa atas diacak (nilainya negatif, sebagian pipa berada di luar layar)
        int randomPosY = (int) (pipeStartPosY - (pipeHeight / 4) - Math.random() * (pipeHeight / 2));
        int openingSpace = frameHeight / 4; // tinggi celah tempat burung lewat

        Pipe upperPipe = new Pipe(pipeStartPosX, randomPosY, pipeWidth, pipeHeight, upperPipeImage);
        pipes.add(upperPipe);

        // pipa bawah dimulai tepat di bawah pipa atas + tinggi celah
        Pipe lowerPipe = new Pipe(pipeStartPosX, randomPosY + pipeHeight + openingSpace,
                pipeWidth, pipeHeight, lowerPipeImage);
        pipes.add(lowerPipe);
    }

    public void move(){
        backgroundPosX += backgroundVelocityX;
        if(backgroundPosX <= -backgroundWidth){
            backgroundPosX += backgroundWidth;   // salinan pertama sudah keluar layar, kembalikan ke awal
        }

        player.setVelocityY(player.getVelocityY() + gravity);
        player.setPosY(player.getPosY() + player.getVelocityY());
        player.setPosY(Math.max(player.getPosY(), 0)); // burung tidak bisa keluar dari batas atas

        for(int i = 0; i < pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            pipe.setPosX(pipe.getPosX() + pipeVelocityX);
        }

        // hapus pipa yang sudah keluar sepenuhnya dari sisi kiri layar supaya list tidak membengkak
        pipes.removeIf(p -> p.getPosX() + p.getWidth() < 0);
    }

    // [BARU] menambah skor saat burung melewati pipa
    public void checkScore(){
        for(int i = 0; i < pipes.size(); i++){
            Pipe pipe = pipes.get(i);

            // pipa dianggap "terlewati" jika sisi kanannya sudah di kiri burung
            if(!pipe.passed && pipe.getPosX() + pipe.getWidth() < player.getPosX()){
                pipe.passed = true; // tandai agar tidak dihitung berulang
                passSound.play();
                // Satu "pasangan" pipa hanya boleh bernilai +1, jadi yang dihitung
                // hanya pipa atas (dikenali dari gambarnya). Pipa bawah cuma ditandai.
                if(pipe.getImage() == upperPipeImage){
                    score += 1;
                    if(view != null){
                        view.updateScore(score);
                    }
                }

            }
        }
    }

    // [BARU] cek tabrakan antara dua persegi (burung dan pipa)
    private boolean isCollide(Player p, Pipe pipe){
        return p.getPosX() < pipe.getPosX() + pipe.getWidth()    // sisi kiri burung sebelum sisi kanan pipa
                && p.getPosX() + p.getWidth() > pipe.getPosX()       // sisi kanan burung melewati sisi kiri pipa
                && p.getPosY() < pipe.getPosY() + pipe.getHeight()   // sisi atas burung di atas sisi bawah pipa
                && p.getPosY() + p.getHeight() > pipe.getPosY();     // sisi bawah burung di bawah sisi atas pipa
    }

    // [BARU] cek kondisi Game Over: menabrak pipa atau jatuh ke dasar layar
    public void checkGameOver(){
        // jatuh ke batas bawah JFrame
        if(player.getPosY() + player.getHeight() >= frameHeight){
            gameOver = true;
        }

        // menabrak salah satu pipa
        for(int i = 0; i < pipes.size(); i++){
            if(isCollide(player, pipes.get(i))){
                gameOver = true;
            }
        }

        // jika game over: hentikan kedua timer sehingga permainan berhenti total
        if(gameOver){
            gameLoop.stop();
            bgm.stop();
            pipesCooldown.stop();
            gameOverSound.play(); // [BARU] bunyi sekali saat game over
            gameOverSound1.play(); // [BARU] bunyi sekali saat game over
        }
    }

    @Override
    public void actionPerformed(ActionEvent e){
        move();
        checkScore();
        checkGameOver();
        if(view != null){
            view.repaint(); // tetap digambar sekali lagi agar tulisan "GAME OVER" muncul
        }
    }

    @Override
    public void keyTyped(KeyEvent e){}

    @Override
    public void keyPressed(KeyEvent e){
        // SPASI = lompat, hanya berlaku saat game belum berakhir
        if(e.getKeyCode() == KeyEvent.VK_SPACE && !gameOver){
            player.setVelocityY(-10);
            jumpSound.play(); // [BARU] putar efek suara setiap burung melompat
        }

        // R = restart, hanya berlaku saat sudah Game Over
        if(e.getKeyCode() == KeyEvent.VK_R && gameOver){
            resetGame();
            bgm.loop();
            gameOverSound.stop();
            gameOverSound1.stop();
        }

        if (e.getKeyCode() == KeyEvent.VK_ESCAPE && gameOver){
            if (onBackToMenu != null){
                onBackToMenu.run();
                gameOverSound.stop();
                gameOverSound1.stop();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e){}
}