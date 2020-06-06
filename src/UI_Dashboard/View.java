package UI_Dashboard;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTree;

public interface View {

    JTree getJTree();

    JFrame getFrame();

    JTable getTable();

    JButton getAddButton();

    JButton getUpdateButton();

    JButton getRefreshButton();

    JButton getDeleteButton();

    JButton getSearchButton();

    JTabbedPane getJTabbedPane();

}
