package swingapp;

import UI_Connection.ConnectionController;
import UI_Connection.ConnectionFrame;
import UI_Dashboard.Controller;
import UI_Dashboard.JFrameForm;
import WorkManager.DbStateManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SwingApp {

    public static void main(String[] args) {

        ConnectionFrame frame = new ConnectionFrame();
        ConnectionController controller = new ConnectionController(frame);
        frame.setVisible(true);
    }

}
