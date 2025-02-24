package cn.lunadeer.dominion.utils.databse;

import cn.lunadeer.dominion.utils.databse.exceptions.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Common {

    /**
     * Asserts that a specified field exists in a given table.
     *
     * @param tableName the name of the table to check
     * @param fieldName the name of the field to check
     * @throws DatabaseException if the field does not exist or if there is an error during the check
     */
    public static void assertFieldExist(String tableName, String fieldName) throws DatabaseException {
        if (DatabaseManager.instance.getType().equals(DatabaseType.PGSQL)) {
            String sql = "SELECT column_name FROM information_schema.columns WHERE table_name = '" + tableName + "' AND column_name = '" + fieldName + "';";
            try (ResultSet rs = DatabaseManager.instance.query(sql)) {
                if (rs != null && rs.next()) {
                    return;
                }
                throw new FieldNotFound(tableName, fieldName);
            } catch (SQLException e) {
                throw new DatabaseException("Failed to check table existence ? ?", tableName, e.getMessage());
            }
        } else if (DatabaseManager.instance.getType().equals(DatabaseType.SQLITE)) {
            try (ResultSet rs = DatabaseManager.instance.query("PRAGMA table_info(" + tableName + ");")) {
                if (rs != null) {
                    while (rs.next()) {
                        if (fieldName.equals(rs.getString("name"))) {
                            return;
                        }
                    }
                }
                throw new FieldNotFound(tableName, fieldName);
            } catch (SQLException e) {
                throw new DatabaseException("Failed to check table existence ? ?", tableName, e.getMessage());
            }
        } else if (DatabaseManager.instance.getType().equals(DatabaseType.MYSQL)) {
            try (ResultSet rs = DatabaseManager.instance.query("SHOW COLUMNS FROM " + tableName + " WHERE Field = '" + fieldName + "';")) {
                if (rs != null && rs.next()) {
                    return;
                }
                throw new FieldNotFound(tableName, fieldName);
            } catch (SQLException e) {
                throw new DatabaseException("Failed to check table existence ? ?", tableName, e.getMessage());
            }
        } else {
            throw new DatabaseTypeNotSupport(DatabaseManager.instance.getType().toString());
        }
    }

    /**
     * Asserts that a specified table exists in the database.
     *
     * @param tableName the name of the table to check
     * @throws DatabaseException if the table does not exist or if there is an error during the check
     */
    public static void assertTableExist(String tableName) throws DatabaseException {
        if (DatabaseManager.instance.getType().equals(DatabaseType.PGSQL)) {
            String sql = "SELECT tablename FROM pg_tables WHERE tablename = ?;";
            try (ResultSet rs = DatabaseManager.instance.query(sql, tableName)) {
                if (rs != null && rs.next()) {
                    return;
                }
                throw new TableNotFound(tableName);
            } catch (SQLException e) {
                throw new DatabaseException("Failed to check table existence ? ?", tableName, e.getMessage());
            }
        } else if (DatabaseManager.instance.getType().equals(DatabaseType.SQLITE)) {
            try (ResultSet rs = DatabaseManager.instance.query("SELECT name FROM sqlite_master WHERE type='table' AND name='" + tableName + "';")) {
                if (rs != null && rs.next()) {
                    return;
                }
                throw new TableNotFound(tableName);
            } catch (SQLException e) {
                throw new DatabaseException("Failed to check table existence ? ?", tableName, e.getMessage());
            }
        } else if (DatabaseManager.instance.getType().equals(DatabaseType.MYSQL)) {
            try (ResultSet rs = DatabaseManager.instance.query("SHOW TABLES LIKE '" + tableName + "';")) {
                if (rs != null && rs.next()) {
                    return;
                }
                throw new TableNotFound(tableName);
            } catch (SQLException e) {
                throw new DatabaseException("Failed to check table existence ? ?", tableName, e.getMessage());
            }
        } else {
            throw new DatabaseTypeNotSupport(DatabaseManager.instance.getType().toString());
        }
    }

    /**
     * Retrieves the columns and their types for a specified table.
     *
     * @param tableName the name of the table to retrieve columns from
     * @return a map where the key is the column name and the value is the column type
     * @throws QueryException if there is an error during the query execution
     */
    public static Map<String, FieldType> getTableColumns(String tableName) throws QueryException {
        Map<String, FieldType> columns = new HashMap<>(); // Column name, Column type
        if (DatabaseManager.instance.getType().equals(DatabaseType.PGSQL)) {
            String sql = "SELECT column_name, data_type FROM information_schema.columns WHERE table_name = '" + tableName + "';";
            try (ResultSet rs = DatabaseManager.instance.query(sql)) {
                if (rs != null) {
                    while (rs.next()) {
                        columns.put(rs.getString("column_name"), FieldType.getFieldTypeByName(rs.getString("data_type")));
                    }
                }
            } catch (Exception e) {
                throw new QueryException(e.getMessage() + " SQL: " + sql);
            }
        } else if (DatabaseManager.instance.getType().equals(DatabaseType.SQLITE)) {
            String sql = "PRAGMA table_info(" + tableName + ");";
            try (ResultSet rs = DatabaseManager.instance.query(sql)) {
                if (rs != null) {
                    while (rs.next()) {
                        columns.put(rs.getString("name"), FieldType.getFieldTypeByName(rs.getString("type")));
                    }
                }
            } catch (SQLException e) {
                throw new QueryException(e.getMessage() + " SQL: " + sql);
            }
        } else if (DatabaseManager.instance.getType().equals(DatabaseType.MYSQL)) {
            String sql = "SHOW COLUMNS FROM " + tableName + ";";
            try (ResultSet rs = DatabaseManager.instance.query(sql)) {
                if (rs != null) {
                    while (rs.next()) {
                        columns.put(rs.getString("Field"), FieldType.getFieldTypeByName(rs.getString("Type")));
                    }
                }
            } catch (SQLException e) {
                throw new QueryException(e.getMessage() + " SQL: " + sql);
            }
        } else {
            throw new DatabaseTypeNotSupport(DatabaseManager.instance.getType().toString());
        }
        return columns;
    }
}
