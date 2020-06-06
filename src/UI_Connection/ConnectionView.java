package UI_Connection;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

public interface ConnectionView {

    public JButton getSubmitButton();

    public JTextField getURLField();

    public JTextField getUserNameField();

    public JTextField getPasswordField();

    public JFrame getFrame();

}
