package cn.lunadeer.dominion.utils.databse.syntax;

import cn.lunadeer.dominion.utils.XLogger;
import cn.lunadeer.dominion.utils.databse.DatabaseManager;
import cn.lunadeer.dominion.utils.databse.FIelds.Field;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static cn.lunadeer.dominion.utils.databse.FIelds.Field.getFromResultSet;

public abstract class Select implements Syntax {

    protected final Field<?>[] columns;
    protected final String columnsString;
    protected String tableName;
    protected String where;
    protected Object[] whereArgs;

    /**
     * Creates a new Select instance based on the database type.
     * <p>
     * Example usage:
     * <pre>
     *     Select select = Select.select("column1", "column2")
     *            .from("table_name")
     *            .where("column1 = ?", "value");
     * </pre>
     *
     * @param columns the columns to select
     * @return a Select instance for the appropriate database type
     * @throws UnsupportedOperationException if the database type is not supported
     */
    public static Select select(Field<?>... columns) {
        return switch (DatabaseManager.instance.getType()) {
            case SQLITE -> new sqlite_impl(columns);
            case MYSQL -> new mysql_impl(columns);
            case PGSQL -> new pgsql_impl(columns);
            default -> throw new UnsupportedOperationException(
                    "Database type: " + DatabaseManager.instance.getType() + " not supported with SELECT"
            );
        };
    }

    private Select(Field<?>[] columns) {
        this.columns = columns;
        this.columnsString = Arrays.stream(columns)
                .map(Field::getName)
                .reduce((a, b) -> a + ", " + b)
                .orElse("*");
    }

    public Select from(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public Select where(String conditions, Object... args) {
        this.where = conditions;
        this.whereArgs = args;
        return this;
    }

    public Select where(String conditions) {
        this.where = conditions;
        return this;
    }

    public List<Map<String, Field<?>>> execute() throws SQLException {
        try (Connection conn = DatabaseManager.instance.getConnection()) {
            String sql = getSql();
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            if (whereArgs != null) {
                for (int i = 0; i < whereArgs.length; i++) {
                    preparedStatement.setObject(i + 1, whereArgs[i]);
                }
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            return getFromResultSet(columns, resultSet);
        } catch (SQLException e) {
            XLogger.error("SQL: " + getSql());
            XLogger.error("WHERE Param: " + Arrays.toString(whereArgs));
            XLogger.error(e);
            throw new SQLException("Error executing query: " + e.getMessage());
        }
    }

    // Implementation of jdbc methods

    private static class sqlite_impl extends Select {
        public sqlite_impl(Field<?>[] columns) {
            super(columns);
        }

        @Override
        public String getSql() {
            StringBuilder sql = new StringBuilder("SELECT ");
            sql.append(columnsString);
            sql.append(" FROM ").append(tableName);
            if (where != null) {
                sql.append(" WHERE ").append(where);
            }
            return sql.toString();
        }
    }

    private static class mysql_impl extends Select {
        public mysql_impl(Field<?>[] columns) {
            super(columns);
        }

        @Override
        public String getSql() {
            StringBuilder sql = new StringBuilder("SELECT ");
            sql.append(columnsString);
            sql.append(" FROM ").append(tableName);
            if (where != null) {
                sql.append(" WHERE ").append(where);
            }
            return sql.toString();
        }
    }

    private static class pgsql_impl extends Select {
        public pgsql_impl(Field<?>[] columns) {
            super(columns);
        }

        @Override
        public String getSql() {
            StringBuilder sql = new StringBuilder("SELECT ");
            sql.append(columnsString);
            sql.append(" FROM ").append(tableName);
            if (where != null) {
                sql.append(" WHERE ").append(where);
            }
            return sql.toString();
        }
    }


}
