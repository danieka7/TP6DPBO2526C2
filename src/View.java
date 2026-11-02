import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class View extends JPanel {

    int width = 360;
    int height = 640;

    private Logic logic;

    // [BARU] JLabel untuk menampilkan skor di bagian atas layar
    private JLabel scoreLabel;

    public View(Logic logic) {
        this.logic = logic;
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.cyan);

        // BorderLayout dipakai agar label skor bisa ditempel di bagian atas (NORTH)
        setLayout(new BorderLayout());

        scoreLabel = new JLabel("Skor: 0", SwingConstants.CENTER); // teks rata tengah
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 28));
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0)); // jarak dari tepi atas
        add(scoreLabel, BorderLayout.NORTH);

        setFocusable(true);
        addKeyListener(logic);
    }

    // [BARU] dipanggil oleh Logic setiap kali skor berubah
    public void updateScore(int score) {
        scoreLabel.setText("Skor: " + score);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g){
        Image background = logic.getBackgroundImage();
        if(background != null){
            int bgX = logic.getBackgroundPosX();
            int bgW = logic.getBackgroundWidth();

            // lebar gambar setelah diskalakan ke tinggi panel, rasio asli tetap terjaga (± 851)
//            int bgX = background.getWidth(null) * height / background.getHeight(null);
//            int bgW = background.getWidth(null) * height / background.getHeight(null);

            g.drawImage(background, bgX, 0, bgW, height, null);          // salinan pertama
            g.drawImage(background, bgX + bgW, 0, bgW, height, null);
        }

        // pipa digambar lebih dulu agar burung tampil di depan pipa
        ArrayList<Pipe> pipes = logic.getPipes();
        if(pipes != null){
            for (int i = 0 ; i < pipes.size(); i++){
                Pipe pipe = pipes.get(i);
                g.drawImage(pipe.getImage(), pipe.getPosX(), pipe.getPosY(), pipe.getWidth(), pipe.getHeight(), null);
            }
        }

        Player player = logic.getPlayer();
        if(player != null){
            g.drawImage(player.getImage(), player.getPosX(), player.getPosY(), player.getWidth(), player.getHeight(), null);
        }

        // [BARU] tampilkan tulisan Game Over dan petunjuk restart
        if(logic.isGameOver()){
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            drawCenteredString(g, "GAME OVER", height / 2 - 20);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 18));
            drawCenteredString(g, "Tekan R untuk restart", height / 2 + 20);
            drawCenteredString(g, "Tekan Esc untuk kembali ke menu", height / 2 + 50);
        }
    }

    // [BARU] fungsi bantu: menggambar teks tepat di tengah secara horizontal
    private void drawCenteredString(Graphics g, String text, int y){
        FontMetrics fm = g.getFontMetrics();            // untuk mengukur lebar teks
        int x = (width - fm.stringWidth(text)) / 2;     // posisi X agar teks di tengah
        g.drawString(text, x, y);
    }
}