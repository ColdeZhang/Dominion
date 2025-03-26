package cn.lunadeer.dominion.utils.databse.FIelds;

import cn.lunadeer.dominion.utils.databse.DatabaseManager;

import java.sql.Timestamp;
import java.util.List;

public class FieldTimestamp extends Field<Timestamp> {

    private Timestamp value;

    public FieldTimestamp(String name) {
        super(name);
    }

    public FieldTimestamp(String name, Timestamp value) {
        super(name);
        this.value = value;
    }

    @Override
    public String getSqlTypeStr() {
        return getTypeStrings().get(0);
    }

    @Override
    public String getUnifyTypeStr() {
        return "TIMESTAMP";
    }

    @Override
    public Timestamp getValue() {
        return value;
    }

    @Override
    public Field<Timestamp> setValue(Timestamp value) {
        this.value = value;
        return this;
    }

    public static List<String> getTypeStrings() {
        return switch (DatabaseManager.instance.getType()) {
            case MYSQL -> List.of("DATETIME");
            case SQLITE -> List.of("TIMESTAMP");
            case PGSQL -> List.of("TIMESTAMP", "TIMESTAMP WITHOUT TIME ZONE");
            default ->
                    throw new UnsupportedOperationException("Database type: " + DatabaseManager.instance.getType() + " not supported FieldString");
        };
    }
}
