package cn.lunadeer.dominion.utils.databse.FIelds;

import cn.lunadeer.dominion.utils.databse.DatabaseManager;

import java.util.List;

public class FieldLong extends Field<Long> {

    private Long value;

    public FieldLong(String name) {
        super(name);
    }

    public FieldLong(String name, Long value) {
        super(name);
        this.value = value;
    }

    @Override
    public String getSqlTypeStr() {
        return getTypeStrings().get(0);
    }

    @Override
    public String getUnifyTypeStr() {
        return "LONG";
    }

    @Override
    public Long getValue() {
        return value;
    }

    @Override
    public Field<Long> setValue(Long value) {
        this.value = value;
        return this;
    }

    public static List<String> getTypeStrings() {
        return switch (DatabaseManager.instance.getType()) {
            case MYSQL -> List.of("BIGINT");
            case SQLITE -> List.of("BIGINT");
            case PGSQL -> List.of("BIGINT");
            default ->
                    throw new UnsupportedOperationException("Database type: " + DatabaseManager.instance.getType() + " not supported FieldString");
        };
    }
}
