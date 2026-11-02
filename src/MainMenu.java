import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

// Kelas MainMenu = tampilan menu utama yang muncul di awal program.
// Berisi judul game + 2 tombol: "Mulai Main" dan "Keluar".
public class MainMenu extends JPanel {

    private JButton startButton;
    private JButton exitButton;

    private Image backgroundImage;

    // Konstruktor menerima ActionListener yang akan dijalankan saat tombol "Mulai Main" ditekan.
    // Dengan cara ini MainMenu tidak perlu tahu cara kerja game, cukup "memberi tahu" App.
    public MainMenu(ActionListener onStart) {
        setPreferredSize(new Dimension(360, 640));
        setBackground(Color.cyan);

        backgroundImage = new ImageIcon(getClass().getResource("assets/background.png")).getImage();
        setLayout(new GridBagLayout()); // GridBagLayout membuat komponen berada di tengah panel

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;                  // semua komponen berada di kolom yang sama
        gbc.insets = new Insets(10, 0, 10, 0); // jarak atas-bawah antar komponen



        // Judul game
        JLabel title = new JLabel("FLAPPY BIRD");
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setForeground(Color.WHITE);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 40, 0); // jarak ekstra di bawah judul
        add(title, gbc);

        // Tombol Mulai
        startButton = new JButton("Mulai Main");
        startButton.setFont(new Font("Arial", Font.BOLD, 18));
        startButton.setPreferredSize(new Dimension(180, 45));
        startButton.addActionListener(onStart); // aksi ditentukan oleh App
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 10, 0);
        add(startButton, gbc);

        // Tombol Keluar
        exitButton = new JButton("Keluar");
        exitButton.setFont(new Font("Arial", Font.BOLD, 18));
        exitButton.setPreferredSize(new Dimension(180, 45));
        exitButton.addActionListener(e -> System.exit(0)); // menutup seluruh program
        gbc.gridy = 2;
        add(exitButton, gbc);
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g); // bersihkan panel dulu (mengisi warna cadangan)

        if (backgroundImage != null) {
            // Skala mengikuti tinggi panel dengan rasio asli terjaga, supaya gambar tidak melar
            int bgHeight = getHeight();
            int bgWidth = backgroundImage.getWidth(null) * bgHeight / backgroundImage.getHeight(null);

            // Letakkan di tengah: kelebihan lebar terpotong sama banyak di kiri dan kanan
            int bgX = (getWidth() - bgWidth) / 2;

            g.drawImage(backgroundImage, bgX, 0, bgWidth, bgHeight, null);
        }
    }
}