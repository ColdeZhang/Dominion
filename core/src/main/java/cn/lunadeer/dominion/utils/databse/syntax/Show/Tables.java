package cn.lunadeer.dominion.utils.databse.syntax.Show;

import cn.lunadeer.dominion.utils.databse.DatabaseManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public abstract class Tables extends Show {

    public static Tables showTables() {
        return switch (DatabaseManager.instance.getType()) {
            case SQLITE -> new sqlite_impl();
            case MYSQL -> new mysql_impl();
            case PGSQL -> new pgsql_impl();
            default -> throw new UnsupportedOperationException(
                    "Database type: " + DatabaseManager.instance.getType() + " not supported with SHOW TABLES"
            );
        };
    }

    private Tables() {
    }

    public List<String> execute() throws SQLException {
        try (Connection conn = DatabaseManager.instance.getConnection()) {
            String sql = getSql();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                List<String> tables = new java.util.ArrayList<>();
                while (rs.next()) {
                    tables.add(rs.getString(1));
                }
                return tables;
            }
        } catch (SQLException e) {
            throw new SQLException("Error executing SQL: " + getSql(), e);
        }
    }

    // Implementation of jdbc methods

    private static class sqlite_impl extends Tables {
        @Override
        public String getSql() {
            return "SELECT name FROM sqlite_master WHERE type='table'";
        }
    }

    private static class mysql_impl extends Tables {

        @Override
        public String getSql() {
            return "SHOW TABLES";
        }
    }

    private static class pgsql_impl extends Tables {

        @Override
        public String getSql() {
            return "SELECT tablename FROM pg_tables WHERE schemaname = 'public'";
        }
    }
}
