package cn.lunadeer.dominion.utils.databse.FIelds;

import cn.lunadeer.dominion.utils.databse.DatabaseManager;

import java.util.List;

public class FieldString extends Field<String> {

    private String value;

    public FieldString(String name) {
        super(name);
    }

    public FieldString(String name, String value) {
        super(name);
        this.value = value;
    }

    @Override
    public String getSqlTypeStr() {
        return getTypeStrings().get(0);
    }

    @Override
    public String getUnifyTypeStr() {
        return "STRING";
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public Field<String> setValue(String value) {
        this.value = value;
        return this;
    }

    public static List<String> getTypeStrings() {
        return switch (DatabaseManager.instance.getType()) {
            case MYSQL -> List.of("VARCHAR(255)", "VARCHAR", "TEXT");
            case SQLITE -> List.of("TEXT", "VARCHAR");
            case PGSQL -> List.of("TEXT", "VARCHAR", "CHARACTER VARYING");
            default ->
                    throw new UnsupportedOperationException("Database type: " + DatabaseManager.instance.getType() + " not supported FieldString");
        };
    }
}
