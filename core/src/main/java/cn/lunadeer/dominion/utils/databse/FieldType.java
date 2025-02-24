package cn.lunadeer.dominion.utils.databse;

import java.sql.Timestamp;

public enum FieldType {
    STRING(String.class, "VARCHAR(255)", "TEXT", "TEXT"),
    INT(Integer.class, "INT", "INTEGER", "INTEGER"),
    LONG(Long.class, "BIGINT", "INTEGER", "BIGINT"),
    DOUBLE(Double.class, "DOUBLE", "REAL", "DOUBLE PRECISION"),
    FLOAT(Float.class, "FLOAT", "REAL", "REAL"),
    BOOLEAN(Boolean.class, "TINYINT(1)", "INTEGER", "BOOLEAN"),
    DATETIME(Timestamp.class, "DATETIME", "TIMESTAMP", "TIMESTAMP"),
    UUID(java.util.UUID.class, "VARCHAR(36)", "TEXT", "UUID"),
    ;

    private final Class<?> type;
    private final String type_mysql;
    private final String type_sqlite;
    private final String type_postgresql;

    FieldType(Class<?> type, String type_mysql, String type_sqlite, String type_postgresql) {
        this.type = type;
        this.type_mysql = type_mysql;
        this.type_sqlite = type_sqlite;
        this.type_postgresql = type_postgresql;
    }

    public String getType(DatabaseType type) {
        return switch (type) {
            case MYSQL -> type_mysql;
            case SQLITE -> type_sqlite;
            case PGSQL -> type_postgresql;
            default -> throw new IllegalArgumentException("Unsupported database type: " + type);
        };
    }

    public static FieldType getFieldType(Object value) {
        for (FieldType fieldType : values()) {
            if (fieldType.type.equals(value.getClass())) {
                return fieldType;
            }
        }
        throw new IllegalArgumentException("Unsupported field type: " + value.getClass().getSimpleName());
    }

    public static FieldType getFieldTypeByName(String type_name) {
        return switch (DatabaseManager.instance.getType()) {
            case MYSQL:
                yield switch (type_name.toUpperCase()) {
                    case "VARCHAR", "TEXT", "VARCHAR(255)" -> STRING;
                    case "INT", "INTEGER" -> INT;
                    case "BIGINT" -> LONG;
                    case "DOUBLE" -> DOUBLE;
                    case "FLOAT" -> FLOAT;
                    case "TINYINT" -> BOOLEAN;
                    case "DATETIME" -> DATETIME;
                    default -> throw new IllegalArgumentException("Unsupported field type: " + type_name);
                };
            case SQLITE:
                yield switch (type_name.toUpperCase()) {
                    case "VARCHAR", "TEXT" -> STRING;
                    case "INT", "INTEGER" -> INT;
                    case "BIGINT" -> LONG;
                    case "REAL" -> DOUBLE;
                    case "BOOLEAN" -> BOOLEAN;
                    case "TIMESTAMP" -> DATETIME;
                    default -> throw new IllegalArgumentException("Unsupported field type: " + type_name);
                };
            case PGSQL:
                yield switch (type_name.toUpperCase()) {
                    case "VARCHAR", "TEXT", "CHARACTER VARYING" -> STRING;
                    case "INT", "INTEGER" -> INT;
                    case "BIGINT" -> LONG;
                    case "DOUBLE PRECISION" -> DOUBLE;
                    case "REAL" -> FLOAT;
                    case "BOOLEAN" -> BOOLEAN;
                    case "TIMESTAMP", "TIMESTAMP WITHOUT TIME ZONE" -> DATETIME;
                    case "UUID" -> UUID;
                    default -> throw new IllegalArgumentException("Unsupported field type: " + type_name);
                };
            default:
                throw new IllegalArgumentException("Unsupported database type: " + DatabaseManager.instance.getType());
        };
    }

    public static boolean isSupported(Object value) {
        for (FieldType fieldType : values()) {
            if (fieldType.type.equals(value.getClass())) {
                return true;
            }
        }
        return false;
    }
}
