package UI_Input;

import UI_Dashboard.Controller;
import Model.Column;
import Model.Table;
import Utility.DialogUtility;
import WorkManager.QueryManager;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class InputController {

    public static int AUD = 0, SEARCH = 1;
    // operation can be either AUD(Add/Update/Delete) or either Search/Filter
    private int operation = -1;
    private InputView view;

    public InputController(InputView view, int operation) {
        this.operation = operation;
        this.view = view;
    }
    // renders add view to insert a row to the table
    // same view is reused to search, for search we show checkboxes for filtering columns

    public void renderAddView(Table selectedTable, Controller.TaskListener listener) {
        ArrayList<JTextField> inputFields = new ArrayList<>();
        ArrayList<JCheckBox> checkBoxes = new ArrayList<>();

        JFrame frame = view.getFrame();
        Container pane = frame.getContentPane();

        GridBagConstraints gbc = new GridBagConstraints();
        pane.setLayout(new GridBagLayout());
        int rowCounter = 0;
        for (Column column : selectedTable.getColumns()) {
            JLabel label = new JLabel(column.getName());
            JTextField textField = new JTextField("", 20);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0;
            gbc.gridy = rowCounter;
            pane.add(label, gbc);

            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 1;
            gbc.gridy = rowCounter;
            pane.add(textField, gbc);
            inputFields.add(textField);

            if (operation == SEARCH) {
                JCheckBox checkBox1 = new JCheckBox("", true);
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.gridx = 2;
                gbc.gridy = rowCounter;
                pane.add(checkBox1, gbc);
                checkBoxes.add(checkBox1);
            }
            rowCounter++;

        }
        JButton button = new JButton("Submit");

        button.addActionListener((ActionEvent e) -> {
            if (this.operation == AUD && performAddOperation(selectedTable, inputFields)) {
                listener.onTaskComplete();
            } else if (this.operation == SEARCH) {
                String searchQuery = generateSearchQuery(selectedTable, inputFields);
                listener.onSearchComplete(searchQuery, getFilteredColumns(selectedTable, checkBoxes));
            }
        });

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = rowCounter;
        pane.add(button, gbc);
        frame.setVisible(true);

    }

    // this returns a list of columns that will be filtered in the main table
    private List<Column> getFilteredColumns(Table table, ArrayList<JCheckBox> checkBoxes) {
        List<Column> filteredColumns = new ArrayList<>();
        List<Column> columns = table.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            if (checkBoxes.get(i).isSelected()) {
                filteredColumns.add(columns.get(i));
            }
        }
        return filteredColumns;
    }

    // create add query and execute
    private boolean performAddOperation(Table selectedTable, ArrayList<JTextField> inputFields) {
        QueryManager qManager = new QueryManager();
        String query = qManager.createAddQuery(selectedTable, inputFields);
        try {
            qManager.executeQuery(query);

            return true;
        } catch (Exception ex) {
            DialogUtility.showMessage(view.getFrame(), ex.getMessage());
            Logger.getLogger(InputController.class.getName()).log(Level.SEVERE, null, ex);
            return false;

        }

    }

    // render update view
    public void renderUpdateView(Table selectedTable, Object[] object, Controller.TaskListener listener) {
        ArrayList<JTextField> inputFields = new ArrayList<>();
        JFrame frame = view.getFrame();
        Container pane = frame.getContentPane();
        GridBagConstraints gbc = new GridBagConstraints();
        pane.setLayout(new GridBagLayout());
        int rowCounter = 0;

        List<Column> columns = selectedTable.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            Column column = columns.get(i);
            JLabel label = new JLabel(column.getName());
            JTextField textField = new JTextField((String) object[i], 20);
            if (column.isIsPrimary()) {
                textField.setEditable(false);
            }
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0;
            gbc.gridy = rowCounter;
            pane.add(label, gbc);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            gbc.gridx = 1;
            gbc.gridy = rowCounter;
            pane.add(textField, gbc);
            inputFields.add(textField);
            rowCounter++;
        }
        JButton button = new JButton("Update");
        button.addActionListener((ActionEvent e) -> {
            if (performUpdateOperation(selectedTable, inputFields)) {
                listener.onTaskComplete();
            }
        });

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = rowCounter;
        pane.add(button, gbc);
        frame.setVisible(true);

    }

    // create update query and execute
    private boolean performUpdateOperation(Table selectedTable, ArrayList<JTextField> inputFields) {
        QueryManager qManager = new QueryManager();
        String query = qManager.createUpdateQuery(selectedTable, inputFields);
        try {
            qManager.executeQuery(query);
            DialogUtility.showMessage(view.getFrame(), "Row updated successfully");

            return true;
        } catch (Exception ex) {
            Logger.getLogger(InputController.class.getName()).log(Level.SEVERE, null, ex);
            DialogUtility.showMessage(view.getFrame(), ex.getMessage());
            return false;

        }
    }

    // create search query and return that query
    private String generateSearchQuery(Table selectedTable, ArrayList<JTextField> inputFields) {
        QueryManager qManager = new QueryManager();
        String query = qManager.createSearchQuery(selectedTable, inputFields);
        return query;
    }
}
