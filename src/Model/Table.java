package Model;

import java.util.List;

public class Table {

    private String name;
    private List<Column> columns;
    private List<FK_Relation> fkRelations;

    public Table() {
    }

    public Table(String name, List<Column> columns) {
        this.name = name;
        this.columns = columns;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    public List<FK_Relation> getFkRelations() {
        return fkRelations;
    }

    public void setFkRelations(List<FK_Relation> fkRelations) {
        this.fkRelations = fkRelations;
    }

    @Override
    public String toString() {
        return "Table{" + "name=" + name + ", columns=" + columns + '}';
    }

}
