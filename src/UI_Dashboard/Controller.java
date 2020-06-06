package UI_Dashboard;

import UI_Input.InputController;
import UI_Input.InputJFrame;
import Model.Column;
import Model.FK_Relation;
import Model.Table;
import Utility.DialogUtility;
import Utility.Utility;
import WorkManager.DbStateManager;
import WorkManager.QueryManager;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class Controller {

    private View view;
    // to maintain which table is selected so that add/update/delete/search buttons can perform operation based on the selection
    private Table selectedTable;
    // maintain all data so that selected row index can provide us data from here
    private Object[][] selectedTableData;

    public Controller(View view) {
        this.view = view;
        setupJTree();
        setupCrudButtons();
    }

    public void showFrame() {
        JFrame frame = view.getFrame();
        frame.pack();
        frame.setVisible(true);
    }

    // setup tree nodes, having table name and column names
    private void setupJTree() {
        List<Table> tables = DbStateManager.tables;
        JTree tree = view.getJTree();
        setJTreeMouseListener(tree);
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        model.setRoot(new DefaultMutableTreeNode("Database"));

        DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel()
                .getRoot();

        for (Table table : tables) {
            DefaultMutableTreeNode tableName = new DefaultMutableTreeNode(table.getName());
            for (Column column : table.getColumns()) {
                DefaultMutableTreeNode columnName = new DefaultMutableTreeNode(column.getName());
                tableName.add(columnName);
            }
            model.insertNodeInto(tableName, root, root.getChildCount());

        }

    }

    //handles action to be performed when a Jtree node is double clicked
    private void setJTreeMouseListener(JTree tree) {
        tree.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                    if (node == null || node.getChildCount() == 0 || node.getParent() == null) {
                        return;
                    }
                    Object nodeInfo = node.getUserObject();
                    try {
                        getTableRows(DbStateManager.tablesMap.get(nodeInfo));
                        setupFkTabs(DbStateManager.tablesMap.get(nodeInfo), false);
                    } catch (SQLException ex) {
                        DialogUtility.showMessage(view.getFrame(), ex.getMessage());

                        Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            }

        });
    }

    // get all rows/data of selected table
    private void getTableRows(Table table) throws SQLException {
        List<List<String>> rows = new QueryManager().getAll(table);
        selectedTable = table;
        setupTable(table, rows);
    }

    // setup jTable and show all rows and columns
    private void setupTable(Table table, List<List<String>> rows) {
        JTable jTable = view.getTable();
        selectedTableData = Utility.getRows(table.getColumns(), rows);
        DefaultTableModel model = new DefaultTableModel(selectedTableData, Utility.getColumnsArray(table.getColumns())) {

            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        };

        jTable.setModel(model);
        jTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                try {
                    setupFkTabs(selectedTable, true);
                } catch (SQLException ex) {
                    DialogUtility.showMessage(view.getFrame(), ex.getMessage());

                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    private void getTableRows(Table table, List<Column> filteredColumns, String query) throws SQLException {
        List<List<String>> rows = new QueryManager().executeQuery(table, filteredColumns, query);
        selectedTable = table;
        setupTable(table, filteredColumns, rows);
    }

    private void setupTable(Table table, List<Column> filteredColumns, List<List<String>> rows) {
        JTable jTable = view.getTable();
        Object[][] data = Utility.getRows(filteredColumns, rows);
        DefaultTableModel model = new DefaultTableModel(data, Utility.getColumnsArray(filteredColumns)) {

            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        };

        jTable.setModel(model);
        jTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                try {
                    setupFkTabs(selectedTable, true);
                } catch (SQLException ex) {
                    DialogUtility.showMessage(view.getFrame(), ex.getMessage());

                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // this is the 2nd table i.e the relation table
    private void setupFkTabs(Table table, boolean setRows) throws SQLException {
        JTabbedPane tabbedPane = view.getJTabbedPane();
        tabbedPane.removeAll();
        for (FK_Relation relation : table.getFkRelations()) {
            JPanel jPanel = new JPanel();
            JTable jTable = new JTable();
            int index = getSelectedRowIndex();

            if (setRows && index > -1) {
                Table relationalTable = Utility.getTableObjectFromName(relation.getPk_table(), DbStateManager.tables);
                List<List<String>> rows = new QueryManager().getAll(table, relationalTable, relation, selectedTableData, getSelectedRowIndex());
                Object[][] tableData = Utility.getRows(relationalTable.getColumns(), rows);
                DefaultTableModel model = new DefaultTableModel(tableData, Utility.getColumnsArray(relationalTable.getColumns())) {

                    @Override
                    public boolean isCellEditable(int row, int column) {
                        //all cells false
                        return false;
                    }
                };
                jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                jTable.setModel(model);
            }
            jPanel.setLayout(new BorderLayout());
            jPanel.add(new JScrollPane(jTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);
            tabbedPane.add(jPanel, relation.getPk_table());
        }
    }

    // Add/update/delete/search buttons click handler
    private void setupCrudButtons() {
        JButton buttonAdd = view.getAddButton();
        JButton buttonUpdate = view.getUpdateButton();
        JButton buttonDelete = view.getDeleteButton();
        JButton buttonSearch = view.getSearchButton();
        JButton buttonRefresh = view.getRefreshButton();

        buttonAdd.addActionListener((ActionEvent e) -> {
            try {
                add_search_operation(InputController.AUD);
            } catch (Exception ex) {
                DialogUtility.showMessage(view.getFrame(), ex.getMessage());
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        buttonUpdate.addActionListener((ActionEvent e) -> {
            updateOperation();
        });
        buttonDelete.addActionListener((ActionEvent e) -> {
            deleteOperation();
        });
        buttonSearch.addActionListener((ActionEvent e) -> {
            try {
                add_search_operation(InputController.SEARCH);
            } catch (Exception ex) {
                DialogUtility.showMessage(view.getFrame(), ex.getMessage());

                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        buttonRefresh.addActionListener((ActionEvent e) -> {
            if (selectedTable == null) {
                DialogUtility.showMessage(view.getFrame(), "Please select a table first");
                return;
            }
            try {
                getTableRows(selectedTable);
            } catch (SQLException ex) {
                DialogUtility.showMessage(view.getFrame(), ex.getMessage());

                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

    }

    // add the row by executing query and refresh table to get latest data
    private void add_search_operation(int operation) {
        InputJFrame frame = new InputJFrame();
        InputController controller = new InputController(frame, operation);
        if (selectedTable == null) {
            DialogUtility.showMessage(view.getFrame(), "Please select a table first");
            return;
        }
        controller.renderAddView(selectedTable, new TaskListener() {
            @Override
            public void onTaskComplete() {
                try {
                    getTableRows(selectedTable);
                    frame.dispose();
                } catch (SQLException ex) {
                    DialogUtility.showMessage(view.getFrame(), ex.getMessage());
                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            @Override
            public void onSearchComplete(String searchQuery, List<Column> filteredColumns) {
                try {
                    getTableRows(selectedTable, filteredColumns, searchQuery);

                } catch (SQLException ex) {
                    DialogUtility.showMessage(view.getFrame(), ex.getMessage());

                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // update the row by executing query and refresh table to get latest data
    private void updateOperation() {
        if (selectedTable == null) {
            DialogUtility.showMessage(view.getFrame(), "Please select a table first");
            return;
        } else if (getSelectedRowIndex() < 0) {
            DialogUtility.showMessage(view.getFrame(), "Please select a row first");
            return;
        }

        int selectedRow = getSelectedRowIndex();
        if (selectedRow >= 0) {
//            System.out.println(selectedTableData[selectedRow][0]);

            InputJFrame frame = new InputJFrame();
            InputController controller = new InputController(frame, InputController.AUD);
            controller.renderUpdateView(selectedTable, selectedTableData[selectedRow], new TaskListener() {
                @Override
                public void onTaskComplete() {
                    try {
                        getTableRows(selectedTable);
                        frame.dispose();
                    } catch (SQLException ex) {
                        Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                        DialogUtility.showMessage(view.getFrame(), ex.getMessage());

                    }
                }

                @Override
                public void onSearchComplete(String searchQuery, List<Column> filteredColumns) {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
            });
            frame.setVisible(true);

        }

    }

    //delete a row by getting selection in table
    private void deleteOperation() {
        if (selectedTable == null) {
            DialogUtility.showMessage(view.getFrame(), "Please select a table first");
            return;
        } else if (getSelectedRowIndex() < 0) {
            DialogUtility.showMessage(view.getFrame(), "Please select a row first");
            return;
        }

        int selectedRow = getSelectedRowIndex();
        if (selectedRow >= 0) {
//            System.out.println(selectedTableData[selectedRow][0]);
            QueryManager qManager = new QueryManager();
            String query = qManager.createDeleteQuery(selectedTable, selectedTableData[selectedRow]);
            try {
                qManager.executeQuery(query);
                DialogUtility.showMessage(view.getFrame(), "Row deleted successfully");
                getTableRows(selectedTable);

            } catch (SQLException ex) {
                DialogUtility.showMessage(view.getFrame(), ex.getMessage());

                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    //returns index of row that is selected in table
    private int getSelectedRowIndex() {
        JTable jTable = view.getTable();
        int selectedRow = jTable.getSelectedRow();
        return selectedRow;
    }

    public interface TaskListener {

        public void onTaskComplete();

        public void onSearchComplete(String searchQuery, List<Column> filteredColumns);

    }
}
