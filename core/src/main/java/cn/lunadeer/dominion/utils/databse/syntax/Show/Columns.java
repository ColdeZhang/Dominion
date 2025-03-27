package cn.lunadeer.dominion.utils.databse.syntax.Show;

import cn.lunadeer.dominion.utils.databse.DatabaseManager;
import cn.lunadeer.dominion.utils.databse.FIelds.Field;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import static cn.lunadeer.dominion.utils.databse.FIelds.Field.fieldOfSqlType;

public abstract class Columns extends Show {

    protected String tableName;

    public static Columns showColumns() {
        return switch (DatabaseManager.instance.getType()) {
            case SQLITE -> new sqlite_impl();
            case MYSQL -> new mysql_impl();
            case PGSQL -> new pgsql_impl();
            default -> throw new UnsupportedOperationException(
                    "Database type: " + DatabaseManager.instance.getType() + " not supported with SHOW COLUMNS"
            );
        };
    }

    private Columns() {
    }

    public Columns from(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public abstract Map<String, Field<?>> execute() throws SQLException;

    // Implementation of jdbc methods

    private static class sqlite_impl extends Columns {
        @Override
        public String getSql() {
            return "SELECT name, type FROM pragma_table_info('" + tableName + "')";
        }

        @Override
        public Map<String, Field<?>> execute() throws SQLException {
            try (
                    Connection conn = DatabaseManager.instance.getConnection();
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(getSql());
            ) {
                Map<String, Field<?>> columns = new HashMap<>();
                while (rs.next()) {
                    String columnName = rs.getString("name");
                    String columnType = rs.getString("type");
                    columns.put(columnName, fieldOfSqlType(columnName, columnType));
                }
                return columns;
            } catch (SQLException e) {
                throw new SQLException("Error executing SQL: " + getSql(), e);
            }
        }
    }

    private static class mysql_impl extends Columns {

        @Override
        public String getSql() {
            return "SHOW COLUMNS FROM " + tableName;
        }

        @Override
        public Map<String, Field<?>> execute() throws SQLException {
            try (
                    Connection conn = DatabaseManager.instance.getConnection();
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(getSql());
            ) {
                Map<String, Field<?>> columns = new HashMap<>();
                while (rs.next()) {
                    String columnName = rs.getString("Field");
                    String columnType = rs.getString("Type");
                    columns.put(columnName, fieldOfSqlType(columnName, columnType));
                }
                return columns;
            } catch (SQLException e) {
                throw new SQLException("Error executing SQL: " + getSql(), e);
            }
        }
    }

    private static class pgsql_impl extends Columns {

        @Override
        public String getSql() {
            return "SELECT column_name, data_type FROM information_schema.columns WHERE table_name = '" + tableName + "'";
        }

        @Override
        public Map<String, Field<?>> execute() throws SQLException {
            try (
                    Connection conn = DatabaseManager.instance.getConnection();
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(getSql());
            ) {
                Map<String, Field<?>> columns = new HashMap<>();
                while (rs.next()) {
                    String columnName = rs.getString("column_name");
                    String columnType = rs.getString("data_type");
                    columns.put(columnName, fieldOfSqlType(columnName, columnType));
                }
                return columns;
            } catch (SQLException e) {
                throw new SQLException("Error executing SQL: " + getSql(), e);
            }
        }
    }
}
