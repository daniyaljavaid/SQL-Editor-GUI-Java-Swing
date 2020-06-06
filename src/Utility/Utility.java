package Utility;

import Model.Column;
import Model.Table;
import java.util.List;

public class Utility {
    // as JTable requires columns in the form of 1D array, this function converts List to 1d-array
    public static String[] getColumnsArray(List<Column> columnsList) {
        String[] colArray = new String[columnsList.size()];

        for (int i = 0; i < colArray.length; i++) {
            colArray[i] = columnsList.get(i).getName();
        }

        return colArray;

    }
// as JTable requires rows in the form of 2D array, this function converts List to 2d-array
    public static Object[][] getRows(List<Column> columns, List<List<String>> list) {
        Object[][] rows = new Object[list.size()][columns.size()];

        for (int i = 0; i < rows.length; i++) {
            for (int j = 0; j < rows[i].length; j++) {
                rows[i][j] = list.get(i).get(j);
            }
        }

        return rows;
    }
    //for all those columns which are non numeric, there query must have value to be quoted
    // so this function returns quoted string
    public static String getQuotedString(String s) {
        return "\'" + s + "\'";
    }
    //get table object from a list, givens its name
    public static Table getTableObjectFromName(String tableName, List<Table> tables) {
        Table table = tables.stream()
                .filter(t -> tableName.equalsIgnoreCase(t.getName()))
                .findAny()
                .orElse(null);

        return table;
    }

}
