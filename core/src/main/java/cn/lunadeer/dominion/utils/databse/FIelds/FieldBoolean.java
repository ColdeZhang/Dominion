package cn.lunadeer.dominion.utils.databse.FIelds;

import cn.lunadeer.dominion.utils.databse.DatabaseManager;

import java.util.List;

public class FieldBoolean extends Field<Boolean> {

    private Boolean value;

    public FieldBoolean(String name) {
        super(name);
    }

    public FieldBoolean(String name, Boolean value) {
        super(name);
        this.value = value;
    }

    @Override
    public String getSqlTypeStr() {
        return getTypeStrings().get(0);
    }

    @Override
    public String getUnifyTypeStr() {
        return "BOOLEAN";
    }

    @Override
    public Boolean getValue() {
        return value;
    }

    @Override
    public Field<Boolean> setValue(Boolean value) {
        this.value = value;
        return this;
    }

    public static List<String> getTypeStrings() {
        return switch (DatabaseManager.instance.getType()) {
            case MYSQL -> List.of("TINYINT(1)", "TINYINT");
            case SQLITE -> List.of("BOOLEAN");
            case PGSQL -> List.of("BOOLEAN");
            default ->
                    throw new UnsupportedOperationException("Database type: " + DatabaseManager.instance.getType() + " not supported FieldString");
        };
    }
}
