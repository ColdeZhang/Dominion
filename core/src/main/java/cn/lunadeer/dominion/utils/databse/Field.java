package cn.lunadeer.dominion.utils.databse;

public class Field {

    public Field(String name, Object value) {
        this.name = name;
        this.value = value;
        this.type = FieldType.getFieldType(value);
    }

    public Field(String name, FieldType type) {
        this.name = name;
        this.type = type;
        this.value = null;
    }

    public String name;
    public FieldType type;
    public Object value;

}
