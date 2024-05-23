import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame editor = new MainFrame();
            editor.setVisible(true);
        });
    }

}