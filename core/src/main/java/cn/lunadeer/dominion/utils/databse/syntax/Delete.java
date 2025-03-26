package cn.lunadeer.dominion.utils.databse.syntax;

import cn.lunadeer.dominion.utils.XLogger;
import cn.lunadeer.dominion.utils.databse.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;

public abstract class Delete implements Syntax {

    protected String tableName;
    protected String where;
    protected Object[] whereArgs;

    /**
     * Factory method to create a new Delete instance based on the database type.
     * <p>
     * Example usage:
     * <pre>
     *     Delete delete = Delete.delete()
     *        .from("table_name")
     *        .where("column1 = ?", "value");
     * </pre>
     *
     * @return a new instance of Delete implementation specific to the database type.
     * @throws UnsupportedOperationException if the database type is not supported.
     */
    public static Delete delete() {
        return switch (DatabaseManager.instance.getType()) {
            case PGSQL -> new pgsql_impl();
            case SQLITE -> new sqlite_impl();
            case MYSQL -> new mysql_impl();
            default -> throw new UnsupportedOperationException(
                    "Database type: " + DatabaseManager.instance.getType() + " not supported with DELETE"
            );
        };
    }

    private Delete() {
    }

    public Delete from(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public Delete where(String conditions, Object... args) {
        this.where = conditions;
        this.whereArgs = args;
        return this;
    }

    /**
     * Executes the delete statement.
     *
     * @return the number of rows affected by the delete statement.
     * @throws SQLException if a database access error occurs or the SQL statement is invalid.
     */
    public int execute() throws SQLException {
        try (Connection conn = DatabaseManager.instance.getConnection()) {
            String sql = getSql();
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            int index = 1;
            if (whereArgs != null) {
                for (Object arg : whereArgs) {
                    preparedStatement.setObject(index++, arg);
                }
            }
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            XLogger.error("SQL: " + getSql());
            XLogger.error("WHERE Param: " + Arrays.toString(whereArgs));
            XLogger.error(e);
            throw new SQLException("Error executing delete statement: " + e.getMessage(), e);
        }
    }

    @Override
    public String getSql() {
        StringBuilder sql = new StringBuilder("DELETE FROM " + tableName);
        if (where != null) {
            sql.append(" WHERE ").append(where);
        }
        return sql.toString();
    }

    // Implementation of jdbc methods

    private static class sqlite_impl extends Delete {
        @Override
        public String getSql() {
            return super.getSql();
        }
    }

    private static class mysql_impl extends Delete {
        @Override
        public String getSql() {
            return super.getSql();
        }
    }

    private static class pgsql_impl extends Delete {
        @Override
        public String getSql() {
            return super.getSql();
        }
    }
}
