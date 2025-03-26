package cn.lunadeer.dominion.utils.databse.FIelds;

import cn.lunadeer.dominion.utils.databse.DatabaseManager;

import java.util.List;

public class FieldInteger extends Field<Integer> {

    private Integer value;

    public FieldInteger(String name) {
        super(name);
    }

    public FieldInteger(String name, Integer value) {
        super(name);
        this.value = value;
    }

    @Override
    public String getSqlTypeStr() {
        return getTypeStrings().get(0);
    }

    @Override
    public String getUnifyTypeStr() {
        return "INTEGER";
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public Field<Integer> setValue(Integer value) {
        this.value = value;
        return this;
    }

    public static List<String> getTypeStrings() {
        return switch (DatabaseManager.instance.getType()) {
            case MYSQL -> List.of("INT", "INTEGER");
            case SQLITE -> List.of("INTEGER", "INT");
            case PGSQL -> List.of("INTEGER", "INT");
            default ->
                    throw new UnsupportedOperationException("Database type: " + DatabaseManager.instance.getType() + " not supported FieldInteger");
        };
    }
}
