import javax.swing.*;
import java.awt.*;

public class App {
    public static void main (String[] args){
        JFrame frame =  new JFrame("FlappyBird");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(360,640);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        Logic logika = new Logic();
        View tampilan = new View(logika); // supaya view bisa komunikasi dengan logic
        logika.setView(tampilan);

        // CardLayout = layout yang menumpuk beberapa panel seperti kartu,
        // hanya satu yang tampil pada satu waktu (menu ATAU game).
        CardLayout cardLayout = new CardLayout();
        JPanel container = new JPanel(cardLayout);

        // Saat tombol "Mulai Main" ditekan: tampilkan panel game, fokuskan keyboard
        // ke panel game (agar tombol SPASI/R terbaca), lalu mulai game loop.
        MainMenu menu = new MainMenu(e -> {
            cardLayout.show(container, "game");
            SwingUtilities.invokeLater(() -> tampilan.requestFocusInWindow());
            logika.startGame();
        });

        container.add(menu, "menu");       // kartu pertama (tampil paling awal)
        container.add(tampilan, "game");   // kartu kedua

        frame.add(container);
        frame.pack();
        frame.setVisible(true);
    }
}