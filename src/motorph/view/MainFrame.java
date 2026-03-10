package motorph.view;

import javax.swing.*;
import java.awt.Dimension;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("MotorPH Payroll System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(840, 560);
        setMinimumSize(new Dimension(840, 560));
        setLocationRelativeTo(null);
        setResizable(false);
        add(new LoginPanel(this));
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}