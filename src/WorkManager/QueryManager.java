package WorkManager;

import Model.Column;
import Model.FK_Relation;
import Model.Table;
import Utility.SQLTypesMapper;
import Utility.Utility;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JTextField;

public class QueryManager {
// get all rows and columns of a given table
    public List<List<String>> getAll(Table table) throws SQLException {
        ResultSet rsRows = null;
        Connection conn = null;
        List<List<String>> listOfLists = new ArrayList<>();
        try {
            conn = ConnectionManager.getConnection();
            String sql = "select * from " + table.getName();
            PreparedStatement preparedStatement = conn.prepareStatement(sql);

            rsRows = preparedStatement.executeQuery();
            while (rsRows.next()) {
                ArrayList<String> columnValuesList = new ArrayList<>();
                for (Column column : table.getColumns()) {
                    columnValuesList.add(rsRows.getString(column.getName()));
                }
                listOfLists.add(columnValuesList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rsRows.close();
        }
        return listOfLists;
    }

    //for filtered columns and search query
    public List<List<String>> executeQuery(Table table, List<Column> filteredColumns, String query) throws SQLException {
        ResultSet rsRows = null;
        Connection conn = null;
        List<List<String>> listOfLists = new ArrayList<>();
        try {
            conn = ConnectionManager.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);

            rsRows = preparedStatement.executeQuery();
            while (rsRows.next()) {
                ArrayList<String> columnValuesList = new ArrayList<>();
                for (Column column : filteredColumns) {
                    columnValuesList.add(rsRows.getString(column.getName()));
                }
                listOfLists.add(columnValuesList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rsRows.close();
        }
        return listOfLists;
    }
//given a string, execute that query (used for add update delete operations)
    public void executeQuery(String query) {
        Connection conn = null;
        try {
            conn = ConnectionManager.getConnection();
            Statement st = conn.createStatement();
            st.executeUpdate(query);

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
//            conn.close();
        }

    }
    //creates add query of a table and returns it
    public String createAddQuery(Table selectedTable, ArrayList<JTextField> inputFields) {
        String queryStart = "INSERT INTO " + selectedTable.getName() + " VALUES (";
        String queryValues = "";
        String queryEnd = " )";
        List<Column> columns = selectedTable.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            Column column = columns.get(i);
            if (SQLTypesMapper.shouldBeQuoted(column.getType())) {
                queryValues += Utility.getQuotedString(inputFields.get(i).getText());

            } else {
                queryValues += inputFields.get(i).getText();

            }
            if (i != columns.size() - 1) {
                queryValues += ",";
            }
        }

        String query = queryStart + queryValues + queryEnd;
//        System.out.println(query);
        return query;

    }
    //creates update query of a table and returns it
    public String createUpdateQuery(Table selectedTable, ArrayList<JTextField> inputFields) {
        String queryStart = "UPDATE " + selectedTable.getName() + " SET";
        String queryValues = "";
        String whereClause = null;
        List<Column> columns = selectedTable.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            Column column = columns.get(i);
            if (column.isIsPrimary()) {
                if (whereClause == null) {
                    whereClause = " where";
                }
                if (SQLTypesMapper.shouldBeQuoted(column.getType())) {
                    whereClause += " " + column.getName() + " = " + Utility.getQuotedString(inputFields.get(i).getText()) + " AND";

                } else {
                    whereClause += " " + column.getName() + " = " + inputFields.get(i).getText() + " AND";

                }
            } else if (SQLTypesMapper.shouldBeQuoted(column.getType())) {
                queryValues += " " + column.getName() + " = ";
                queryValues += Utility.getQuotedString(inputFields.get(i).getText());
                if (i != columns.size() - 1) {
                    queryValues += ",";
                }
            } else {
                queryValues += " " + column.getName() + " = ";
                queryValues += inputFields.get(i).getText();
                if (i != columns.size() - 1) {
                    queryValues += ",";
                }
            }

        }
        if (whereClause != null) {
            whereClause = whereClause.substring(0, whereClause.length() - 3);
        }
        String query = queryStart + queryValues + whereClause;
//        System.out.println(query);
        return query;
    }
    
    //creates delete query of a table and returns it
    public String createDeleteQuery(Table selectedTable, Object[] values) {
        String query = "Delete from " + selectedTable.getName() + " where";
        List<Column> columns = selectedTable.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            Column col = columns.get(i);
            query += " " + col.getName() + " = ";
            if (SQLTypesMapper.shouldBeQuoted(col.getType())) {
                query += Utility.getQuotedString((String) values[i]);
            } else {
                query += " " + values[i];
            }

            if (i != columns.size() - 1) {
                query += " AND";
            }
        }
//        System.out.println(query);
        return query;
    }

    //get rows and columns of a related table (based on foreign key)
    public List<List<String>> getAll(Table table, Table relationalTable, FK_Relation relation, Object[][] selectedTableData, int selectedRow) throws SQLException {
        List<List<String>> listOfLists = new ArrayList<>();
        String pkValue = "";
        List<Column> columns = table.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).getName().equalsIgnoreCase(relation.getFk())) {
                if (SQLTypesMapper.shouldBeQuoted(columns.get(i).getType())) {
                    pkValue = Utility.getQuotedString((String) selectedTableData[selectedRow][i]);

                } else {
                    pkValue = (String) selectedTableData[selectedRow][i];
                }
                break;
            }
        }
        String query = "Select * from " + relation.getPk_table() + " where " + relation.getPk() + " = " + pkValue;
        ResultSet rsRows = null;
        Connection conn = null;
        try {
            conn = ConnectionManager.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);

            rsRows = preparedStatement.executeQuery();
            while (rsRows.next()) {
                ArrayList<String> columnValuesList = new ArrayList<>();
                for (Column column : relationalTable.getColumns()) {
                    columnValuesList.add(rsRows.getString(column.getName()));
                }
                listOfLists.add(columnValuesList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            conn.close();
            rsRows.close();
        }
        return listOfLists;
    }

    //create search query
    public String createSearchQuery(Table selectedTable, ArrayList<JTextField> inputFields) {
        String queryStart = "select * from " + selectedTable.getName();
        String subString = "";
        List<Column> columnsList = selectedTable.getColumns();
        for (int i = 0; i < columnsList.size(); i++) {
            Column column = columnsList.get(i);
            String input = inputFields.get(i).getText();
            if (!input.isEmpty()) {
                if (subString.isEmpty()) {
                    subString = " where ";
                }
                subString += column.getName() + " = ";
                if (SQLTypesMapper.shouldBeQuoted(column.getType())) {
                    subString += Utility.getQuotedString(input);
                } else {
                    subString += input;
                }
                if (i != columnsList.size() - 1) {
                    subString += "___";
                }

            }
        }
        String query = queryStart + subString;
        if (query.endsWith("___")) {
            query = query.substring(0, query.length() - 3);

        }
        query = query.replaceAll("___", " AND ");
//        System.out.println(query);
        return query;
    }

}
