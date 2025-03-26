package cn.lunadeer.dominion.utils.databse.syntax;

import cn.lunadeer.dominion.utils.XLogger;
import cn.lunadeer.dominion.utils.databse.DatabaseManager;
import cn.lunadeer.dominion.utils.databse.FIelds.Field;

import java.sql.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.lunadeer.dominion.utils.databse.FIelds.Field.getFromResultSet;

public abstract class Insert implements Syntax {

    protected String tableName;
    protected Field<?>[] fields;
    protected Field<?>[] returnFields;
    protected Field<?>[] onConflictFields;
    protected boolean onConflictDoUpdate = false;

    public static Insert insert() {
        return switch (DatabaseManager.instance.getType()) {
            case PGSQL, SQLITE -> new pgsql_sqlite_impl();
            case MYSQL -> new mysql_impl();
            default -> throw new UnsupportedOperationException(
                    "Database type: " + DatabaseManager.instance.getType() + " not supported with INSERT"
            );
        };
    }

    private Insert() {
    }

    public Insert into(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public Insert values(Field<?>... fields) {
        this.fields = fields;
        return this;
    }

    public Insert onConflict(Field<?>... fields) {
        this.onConflictFields = fields;
        return this;
    }

    public Insert doNothing() {
        this.onConflictDoUpdate = false;
        return this;
    }

    public Insert doUpdate() {
        this.onConflictDoUpdate = true;
        return this;
    }

    public Insert returning(Field<?>... fields) {
        this.returnFields = fields;
        return this;
    }

    public abstract Map<String, Field<?>> execute() throws SQLException;

    @Override
    public String getSql() {
        StringBuilder sql = new StringBuilder("INSERT INTO " + tableName + " (");
        for (int i = 0; i < fields.length; i++) {
            sql.append(fields[i].getName());
            if (i < fields.length - 1) {
                sql.append(", ");
            }
        }
        sql.append(") VALUES (");
        for (int i = 0; i < fields.length; i++) {
            sql.append("?");
            if (i < fields.length - 1) {
                sql.append(", ");
            }
        }
        sql.append(")");
        return sql.toString();
    }

    // Implementation of jdbc methods

    private static class mysql_impl extends Insert {
        @Override
        public Map<String, Field<?>> execute() throws SQLException {
            try (Connection connection = DatabaseManager.instance.getConnection()) {
                PreparedStatement preparedStatement = connection.prepareStatement(getSql());
                for (int i = 0; i < fields.length; i++) {
                    preparedStatement.setObject(i + 1, fields[i].getValue());
                }
                preparedStatement.executeUpdate();
                if (returnFields != null && returnFields.length > 0) {
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery("SELECT LAST_INSERT_ID()");
                    int id = 0;
                    if (resultSet.next()) {
                        id = resultSet.getInt(1);
                    }
                    if (id == 0) {
                        return Map.of();
                    }
                    List<Map<String, Field<?>>> result = Select.select(returnFields).from(tableName).where("id = ?", id).execute();
                    if (result.isEmpty()) {
                        return Map.of();
                    }
                    return result.get(0);
                } else {
                    return Map.of();
                }
            } catch (SQLException e) {
                XLogger.error("SQL: " + getSql());
                XLogger.error("Param: " + Arrays.stream(fields).map(Field::getValue));
                XLogger.error(e);
                throw new SQLException("Error executing delete statement: " + e.getMessage(), e);
            }
        }

        @Override
        public String getSql() {
            StringBuilder sql = new StringBuilder(super.getSql());
            if (onConflictFields != null && onConflictFields.length > 0) {
                if (onConflictDoUpdate) {
                    sql.append(" ON DUPLICATE KEY UPDATE ");
                    for (int i = 0; i < fields.length; i++) {
                        String fieldName = fields[i].getName();
                        if (Arrays.stream(onConflictFields).map(Field::getName).anyMatch(fieldName::equals)) {
                            continue;
                        }
                        sql.append(fieldName).append(" = VALUES(").append(fieldName).append(")");
                        if (i < fields.length - 1) {
                            sql.append(", ");
                        }
                    }
                } else {
                    sql.replace(0, 6, "INSERT IGNORE");
                }
            }
            return sql.toString();
        }
    }

    private static class pgsql_sqlite_impl extends Insert {
        @Override
        public Map<String, Field<?>> execute() throws SQLException {
            try (
                    Connection connection = DatabaseManager.instance.getConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement(getSql())
            ) {
                for (int i = 0; i < fields.length; i++) {
                    preparedStatement.setObject(i + 1, fields[i].getValue());
                }
                if (returnFields != null && returnFields.length > 0) {
                    ResultSet resultSet = preparedStatement.executeQuery();
                    Map<String, Field<?>> result = new HashMap<>();
                    while (resultSet.next()) {
                        for (Field<?> field : returnFields) {
                            result.put(field.getName(), getFromResultSet(field, resultSet));
                        }
                    }
                    return result;
                } else {
                    preparedStatement.executeUpdate();
                    return Map.of();
                }
            } catch (SQLException e) {
                XLogger.error("SQL: " + getSql());
                XLogger.error("Param: " + Arrays.stream(fields).map(Field::getValue));
                XLogger.error(e);
                throw new SQLException("Error executing delete statement: " + e.getMessage(), e);
            }
        }

        @Override
        public String getSql() {
            StringBuilder sql = new StringBuilder(super.getSql());
            if (onConflictFields != null && onConflictFields.length > 0) {
                sql.append(" ON CONFLICT (");
                for (int i = 0; i < onConflictFields.length; i++) {
                    sql.append(onConflictFields[i].getName());
                    if (i < onConflictFields.length - 1) {
                        sql.append(", ");
                    }
                }
                sql.append(") DO ");
                if (onConflictDoUpdate) {
                    sql.append("UPDATE SET ");
                    for (int i = 0; i < fields.length; i++) {
                        String fieldName = fields[i].getName();
                        if (Arrays.stream(onConflictFields).map(Field::getName).anyMatch(fieldName::equals)) {
                            continue;
                        }
                        sql.append(fieldName).append(" = EXCLUDED.").append(fieldName);
                        if (i < fields.length - 1) {
                            sql.append(", ");
                        }
                    }
                } else {
                    sql.append("NOTHING");
                }
            }
            if (returnFields != null && returnFields.length > 0) {
                sql.append(" RETURNING ");
                for (int i = 0; i < returnFields.length; i++) {
                    sql.append(returnFields[i].getName());
                    if (i < returnFields.length - 1) {
                        sql.append(", ");
                    }
                }
            }
            return sql.toString();
        }
    }
}
