package cn.lunadeer.dominion.utils.databse.FIelds;


import cn.lunadeer.dominion.utils.XLogger;
import cn.lunadeer.dominion.utils.databse.DatabaseManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class Field<T> {
    private final String name;

    public Field(String name) {
        this.name = name;
    }

    public static Field<?> fieldOf(String name, String typeString) {
        if (FieldBoolean.getTypeStrings().stream().anyMatch(s -> s.equalsIgnoreCase(typeString))) {
            return new FieldBoolean(name);
        }
        if (FieldFloat.getTypeStrings().stream().anyMatch(s -> s.equalsIgnoreCase(typeString))) {
            return new FieldFloat(name);
        }
        if (FieldInteger.getTypeStrings().stream().anyMatch(s -> s.equalsIgnoreCase(typeString))) {
            return new FieldInteger(name);
        }
        if (FieldLong.getTypeStrings().stream().anyMatch(s -> s.equalsIgnoreCase(typeString))) {
            return new FieldLong(name);
        }
        if (FieldString.getTypeStrings().stream().anyMatch(s -> s.equalsIgnoreCase(typeString))) {
            return new FieldString(name);
        }
        if (FieldTimestamp.getTypeStrings().stream().anyMatch(s -> s.equalsIgnoreCase(typeString))) {
            return new FieldTimestamp(name);
        }
        throw new IllegalArgumentException("Unsupported type string: " + typeString + " for field: " + name + " with database type: " + DatabaseManager.instance.getType());
    }

    public static Field<?> getFromResultSet(@NotNull Field<?> column, ResultSet resultSet) {
        try {
            if (column instanceof FieldString) {
                return new FieldString(column.getName(), resultSet.getString(column.getName()));
            }
            if (column instanceof FieldInteger) {
                return new FieldInteger(column.getName(), resultSet.getInt(column.getName()));
            }
            if (column instanceof FieldLong) {
                return new FieldLong(column.getName(), resultSet.getLong(column.getName()));
            }
            if (column instanceof FieldFloat) {
                return new FieldFloat(column.getName(), resultSet.getFloat(column.getName()));
            }
            if (column instanceof FieldBoolean) {
                return new FieldBoolean(column.getName(), resultSet.getBoolean(column.getName()));
            }
            if (column instanceof FieldTimestamp) {
                return new FieldTimestamp(column.getName(), resultSet.getTimestamp(column.getName()));
            }
            throw new IllegalArgumentException("Unsupported field type: " + column.getClass().getSimpleName());
        } catch (Exception e) {
            XLogger.error(e);
            throw new RuntimeException(e);
        }
    }

    public String getName() {
        return name;
    }

    public abstract String getSqlTypeStr();

    public abstract String getUnifyTypeStr();

    public abstract T getValue();

    public abstract Field<T> setValue(T value);

    public static List<Map<String, Field<?>>> getFromResultSet(@Nullable Field<?>[] columns, ResultSet res) throws SQLException {
        if (columns != null && columns.length > 0) {
            List<Map<String, Field<?>>> result = new ArrayList<>();
            while (res.next()) {
                Map<String, Field<?>> row = new java.util.HashMap<>();
                for (Field<?> column : columns) {
                    if (column == null) {
                        continue;
                    }
                    Field<?> fieldContent = Field.getFromResultSet(column, res);
                    row.put(fieldContent.getName(), fieldContent);
                }
                result.add(row);
            }
            return result;
        } else {
            return new ArrayList<>();
        }
    }
}
