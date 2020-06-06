package Utility;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class DialogUtility {

    public static void showMessage(JFrame frame, String message) {
        JOptionPane.showMessageDialog(frame, message);
    }
}
