package UI_Connection;

import UI_Dashboard.Controller;
import UI_Dashboard.JFrameForm;
import Utility.DialogUtility;
import WorkManager.ConnectionManager;
import WorkManager.DbStateManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;
import swingapp.SwingApp;

public class ConnectionController {
    
    private ConnectionView view;
    
    public ConnectionController(ConnectionView view) {
        this.view = view;
        setupSubmitButton();
    }
    
    private void setupSubmitButton() {
        JFrame frame = view.getFrame();
        JButton button = view.getSubmitButton();
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String url, username, password;
                JTextField urlField = view.getURLField(),
                        usernameField = view.getUserNameField(),
                        passwordField = view.getPasswordField();
                
                url = urlField.getText();
                username = usernameField.getText();
                password = passwordField.getText();
                
                if (url.isEmpty() || username.isEmpty() || password.isEmpty()) {
                    DialogUtility.showMessage(frame, "Enter correct details");
                } else {
                    Connection connection = ConnectionManager.setConnectionDetails(url, username, password);
                    if (connection == null) {
                        DialogUtility.showMessage(frame, "Unable to connect to database");
                    } else {
                        getDatabaseDetails();
                    }
                }
            }
            
        });
    }
    
    private void getDatabaseDetails() {
        try {
            //fetch current state of DB -> Tables, Columns, Constraints
            new DbStateManager().fetchDbCurrentState();
        } catch (Exception ex) {
            Logger.getLogger(SwingApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        startDashboard();
    }
    
    private void startDashboard() {
        view.getFrame().setVisible(false);
        //Open dashboard (tables/ related tables/ CRUD operation buttons)
        JFrameForm frame = new JFrameForm();
        Controller controller = new Controller(frame);
        controller.showFrame();
    }
}
