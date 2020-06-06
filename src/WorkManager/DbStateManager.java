package WorkManager;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import Model.Column;
import Model.FK_Relation;
import Model.Table;
import Utility.SQLTypesMapper;
import java.util.HashMap;

public class DbStateManager {
    //this class maintains state of database, when the database details are fetched first time.
    // eg table name, its cloumn names, foreignKeys, primaryKeys
    public static ArrayList<Table> tables;
    public static HashMap<String, Table> tablesMap;
    
    public void fetchDbCurrentState() throws SQLException {
        tables = new ArrayList<>();
        tablesMap = new HashMap<>();
        
        ResultSet rsTables = null, rsColumns = null;
        Connection conn = null;
        
        try {
            conn = ConnectionManager.getConnection();
            DatabaseMetaData dbm = conn.getMetaData();
            rsTables = dbm.getTables(null, null, "%", new String[]{"TABLE"});
            while (rsTables.next()) {
                String tableName = rsTables.getString("TABLE_NAME");
                Table table = new Table();
                table.setName(tableName);
                getColumns(dbm, table);
                getPrimaryKeys(dbm, table);
                getForeignKeys(dbm, table);
                tables.add(table);
                tablesMap.put(table.getName(), table);
//                System.out.println(table);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            conn.close();
            rsTables.close();
            
        }
        
    }
    
    private void getColumns(DatabaseMetaData dbm, Table table) throws SQLException {
        ResultSet rsColumns = null;
        
        rsColumns = dbm.getColumns(null, null, table.getName(), "%");
        ArrayList<Column> columnsList = new ArrayList<Column>();
        while (rsColumns.next()) {
            int dataType = rsColumns.getInt("DATA_TYPE");
            Class type = SQLTypesMapper.toClass(dataType);
            String columnName = rsColumns.getString("COLUMN_NAME");
            Column column = new Column();
            column.setName(columnName);
            columnsList.add(column);
            column.setType(type.getSimpleName());
            
        }
        table.setColumns(columnsList);
        rsColumns.close();
        
    }
    
    private void getPrimaryKeys(DatabaseMetaData dbm, Table table) throws SQLException {
        ResultSet rsPrimaryKeys = dbm.getPrimaryKeys(null, null, table.getName());
        while (rsPrimaryKeys.next() && rsPrimaryKeys.getString("PK_NAME") != null) {
            String colName = rsPrimaryKeys.getString("COLUMN_NAME");
            
            Column column = table.getColumns().stream()
                    .filter(c -> colName.equalsIgnoreCase(c.getName()))
                    .findAny()
                    .orElse(null);
            if (column != null) {
                column.setIsPrimary(true);
            }
        }
        rsPrimaryKeys.close();
    }
    
    private void getForeignKeys(DatabaseMetaData dbm, Table table) throws SQLException {
        ResultSet foreignKeys = dbm.getImportedKeys(null, null, table.getName());
        ArrayList<FK_Relation> relations = new ArrayList<>();
        while (foreignKeys.next()) {
            String fkTableName = foreignKeys.getString("FKTABLE_NAME");
            String fkColumnName = foreignKeys.getString("FKCOLUMN_NAME");
            String pkTableName = foreignKeys.getString("PKTABLE_NAME");
            String pkColumnName = foreignKeys.getString("PKCOLUMN_NAME");
            
            FK_Relation relation = new FK_Relation();
            relation.setFk(fkColumnName);
            relation.setPk_table(pkTableName);
            relation.setPk(pkColumnName);
            relations.add(relation);
//            System.out.println(fkTableName + "." + fkColumnName + " -> " + pkTableName + "." + pkColumnName);
        }
        table.setFkRelations(relations);
    }
    
}
