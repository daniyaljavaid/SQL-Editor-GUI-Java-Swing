package Model;

public class FK_Relation {

    private String fk;
    private String pk;//another table pk
    private String pk_table;//another table pk

    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    public String getFk() {
        return fk;
    }

    public void setFk(String fk) {
        this.fk = fk;
    }

    public String getPk_table() {
        return pk_table;
    }

    public void setPk_table(String pk_table) {
        this.pk_table = pk_table;
    }

    @Override
    public String toString() {
        return "FK_Relation{" + "fk=" + fk + ", pk=" + pk + ", pk_table=" + pk_table + '}';
    }
    
    

}
