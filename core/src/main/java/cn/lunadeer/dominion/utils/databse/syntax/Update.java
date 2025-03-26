package cn.lunadeer.dominion.utils.databse.syntax;

import cn.lunadeer.dominion.utils.XLogger;
import cn.lunadeer.dominion.utils.databse.DatabaseManager;
import cn.lunadeer.dominion.utils.databse.FIelds.Field;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public abstract class Update implements Syntax {

    protected final String tableName;
    protected Field<?>[] fields;
    protected String where;
    protected Object[] whereArgs;

    /**
     * Factory method to create a new Update instance based on the database type.
     * <p>
     * Example usage:
     * <pre>
     *     Update update = Update.update("table_name")
     *        .set(field1, field2)
     *        .where("column1 = ?", "value");
     * </pre>
     *
     * @param tableName the name of the table to update
     * @return a new instance of Update implementation specific to the database type
     * @throws UnsupportedOperationException if the database type is not supported
     */
    public static Update update(String tableName) {
        return switch (DatabaseManager.instance.getType()) {
            case SQLITE -> new sqlite_impl(tableName);
            case MYSQL -> new mysql_impl(tableName);
            case PGSQL -> new pgsql_impl(tableName);
            default -> throw new UnsupportedOperationException(
                    "Database type: " + DatabaseManager.instance.getType() + " not supported with UPDATE"
            );
        };
    }

    private Update(String tableName) {
        this.tableName = tableName;
    }

    public Update set(Field<?>... fields) {
        this.fields = fields;
        return this;
    }

    public Update where(String conditions, Object... args) {
        this.where = conditions;
        this.whereArgs = args;
        return this;
    }

    /**
     * Executes the update statement.
     *
     * @return the number of rows affected by the update
     * @throws SQLException if a database access error occurs
     */
    public int execute() throws SQLException {
        try (Connection conn = DatabaseManager.instance.getConnection()) {
            String sql = getSql();
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            int index = 1;
            for (Field<?> field : fields) {
                preparedStatement.setObject(index++, field.getValue());
            }
            for (Object arg : whereArgs) {
                preparedStatement.setObject(index++, arg);
            }
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            XLogger.error("SQL: " + getSql());
            XLogger.error("SET Param: " + Arrays.stream(fields).map(Field::getValue));
            XLogger.error("WHERE Param: " + List.of(whereArgs));
            XLogger.error(e);
            throw new SQLException("Error executing update: " + e.getMessage(), e);
        }
    }

    // Implementation of jdbc methods

    private static class sqlite_impl extends Update {
        private sqlite_impl(String tableName) {
            super(tableName);
        }

        @Override
        public String getSql() {
            StringBuilder sql = new StringBuilder("UPDATE " + tableName + " SET ");
            for (Field<?> field : fields) {
                sql.append(field.getName()).append(" = ?, ");
            }
            sql.delete(sql.length() - 2, sql.length());
            if (where != null) {
                sql.append(" WHERE ").append(where);
            }
            return sql.toString();
        }
    }

    private static class mysql_impl extends Update {
        private mysql_impl(String tableName) {
            super(tableName);
        }

        @Override
        public String getSql() {
            StringBuilder sql = new StringBuilder("UPDATE " + tableName + " SET ");
            for (Field<?> field : fields) {
                sql.append(field.getName()).append(" = ?, ");
            }
            sql.delete(sql.length() - 2, sql.length());
            if (where != null) {
                sql.append(" WHERE ").append(where);
            }
            return sql.toString();
        }
    }

    private static class pgsql_impl extends Update {
        private pgsql_impl(String tableName) {
            super(tableName);
        }

        @Override
        public String getSql() {
            StringBuilder sql = new StringBuilder("UPDATE " + tableName + " SET ");
            for (Field<?> field : fields) {
                sql.append(field.getName()).append(" = ?, ");
            }
            sql.delete(sql.length() - 2, sql.length());
            if (where != null) {
                sql.append(" WHERE ").append(where);
            }
            return sql.toString();
        }
    }
}
