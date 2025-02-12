package cn.lunadeer.dominion.utils.databse;

import cn.lunadeer.dominion.utils.databse.exceptions.*;
import cn.lunadeer.dominion.utils.databse.syntax.InsertRow;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.file.Files;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static cn.lunadeer.dominion.utils.databse.Common.getTableColumns;

public class DatabaseManager {

    public static DatabaseManager instance;
    private Connection conn;

    private final JavaPlugin plugin;
    private DatabaseType type;
    private String host;
    private String port;
    private String name;
    private String user;
    private String pass;

    /**
     * Constructs a new DatabaseManager instance with the provided parameters.
     *
     * @param plugin the JavaPlugin instance
     * @param type   the type of the database (e.g., PGSQL, MYSQL, SQLITE)
     * @param host   the database host
     * @param port   the database port
     * @param name   the database name
     * @param user   the database user
     * @param pass   the database password
     * @throws DatabaseTypeNotSupport if the provided database type is not supported
     */
    public DatabaseManager(JavaPlugin plugin, String type, String host, String port, String name, String user, String pass) throws DatabaseTypeNotSupport {
        instance = this;
        this.plugin = plugin;
        set(type, host, port, name, user, pass);
    }

    /**
     * Sets the database configuration parameters.
     *
     * @param type the type of the database (e.g., PGSQL, MYSQL, SQLITE)
     * @param host the database host
     * @param port the database port
     * @param name the database name
     * @param user the database user
     * @param pass the database password
     * @throws DatabaseTypeNotSupport if the provided database type is not supported
     */
    public void set(String type, String host, String port, String name, String user, String pass) throws DatabaseTypeNotSupport {
        try {
            this.type = DatabaseType.valueOf(type.toUpperCase());
            this.host = host;
            this.port = port;
            this.name = name;
            this.user = user;
            this.pass = pass;
        } catch (IllegalArgumentException e) {
            throw new DatabaseTypeNotSupport(type);
        }
    }

    /**
     * Reconnects to the database using the current configuration.
     *
     * @throws Exception if there is an error during the reconnection process
     */
    public void reconnect() throws Exception {
        // Close the existing connection if it is not null and not closed
        if (conn != null && !conn.isClosed()) {
            this.close();
        }
        String connectionUrl;
        // Check the database type and set up the connection URL accordingly
        if (type.equals(DatabaseType.PGSQL)) {
            Class.forName("org.postgresql.Driver");
            connectionUrl = "jdbc:postgresql://" + host + ":" + port;
            connectionUrl += "/" + name;
            conn = DriverManager.getConnection(connectionUrl, user, pass);
        } else if (type.equals(DatabaseType.SQLITE)) {
            Class.forName("org.sqlite.JDBC");
            connectionUrl = "jdbc:sqlite:" + plugin.getDataFolder() + "/" + name + ".db";
            conn = DriverManager.getConnection(connectionUrl);
            // Enable foreign key constraints
            query("PRAGMA foreign_keys = ON;");
        } else if (type.equals(DatabaseType.MYSQL)) {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connectionUrl = "jdbc:mysql://" + host + ":" + port;
            connectionUrl += "/" + name;
            conn = DriverManager.getConnection(connectionUrl, user, pass);
        } else {
            conn = null;
            throw new DatabaseTypeNotSupport(getType().name());
        }
        // Throw an exception if the connection is null or closed
        if (conn == null || conn.isClosed()) {
            throw new DatabaseConnectException("Connection failed");
        }
    }

    /**
     * Executes a SQL query with the provided arguments and returns the result set.
     *
     * @param sql  the SQL query to be executed
     * @param args the arguments to be set in the prepared statement
     * @return the result set of the query, or null if no result set is needed
     * @throws QueryException if there is an error during the query execution or reconnection
     */
    public ResultSet query(String sql, Object... args) throws QueryException {
        // Check if the connection is null, and attempt to reconnect if necessary
        if (conn == null) {
            try {
                reconnect();
            } catch (Exception e) {
                throw new QueryException(e.getMessage());
            }
        }
        try {
            // Prepare the SQL statement with the provided query
            PreparedStatement stmt = conn.prepareStatement(sql);
            // Set the arguments in the prepared statement
            for (int i = 0; i < args.length; i++) {
                stmt.setObject(i + 1, args[i]);
            }
            // Execute the statement and return the result set if available
            if (stmt.execute()) {
                return stmt.getResultSet();
            } else {
                return null; // means no result set needed
            }
        } catch (SQLException e) {
            // Throw a QueryException if there is an SQL error
            throw new QueryException(e.getMessage() + " SQL: " + sql, args);
        }
    }

    /**
     * Closes the database connection if it is open.
     *
     * @throws DatabaseConnectException if there is an error while closing the connection
     */
    public void close() throws DatabaseConnectException {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                conn = null;
            }
        } catch (SQLException e) {
            throw new DatabaseConnectException(e.getMessage());
        }
    }

    /**
     * Returns the type of the database.
     *
     * @return the type of the database
     */
    public DatabaseType getType() {
        return type;
    }

    /**
     * Export a table to a CSV string
     * The format is:
     * column1,column2,column3, ...
     * TYPE1,TYPE2,TYPE3, ...
     * value1,value2,value3, ...
     *
     * @param tableName The table name
     * @return The CSV string
     */
    public static String exportCSV(String tableName) {
        StringBuilder csv = new StringBuilder();
        Map<String, FieldType> columnTypes = getTableColumns(tableName);
        List<String> column_names = new ArrayList<>(columnTypes.keySet());
        csv.append(String.join(",", column_names)).append("\n");
        List<String> column_types = new ArrayList<>(columnTypes.values()).stream().map(FieldType::toString).toList();
        csv.append(String.join(",", column_types)).append("\n");
        String sql = "SELECT * FROM " + tableName;
        if (column_names.contains("id")) {
            sql += " ORDER BY id";
        } else {
            sql += ";";
        }
        try (ResultSet rs = DatabaseManager.instance.query(sql)) {
            if (rs != null) {
                while (rs.next()) {
                    List<String> values = new ArrayList<>();
                    for (String column : column_names) {
                        FieldType type = columnTypes.get(column);
                        switch (type) {
                            case STRING -> values.add(rs.getString(column));
                            case INT -> values.add(String.valueOf(rs.getInt(column)));
                            case LONG -> values.add(String.valueOf(rs.getLong(column)));
                            case DOUBLE -> values.add(String.valueOf(rs.getDouble(column)));
                            case FLOAT -> values.add(String.valueOf(rs.getFloat(column)));
                            case BOOLEAN -> values.add(String.valueOf(rs.getBoolean(column)));
                            case DATETIME -> values.add(rs.getTimestamp(column).toString());
                            case UUID -> values.add(UUID.fromString(rs.getString(column)).toString());
                        }
                    }
                    csv.append(String.join(",", values)).append("\n");
                }
            }
        } catch (SQLException e) {
            throw new QueryException(e.getMessage());
        }
        return csv.toString();
    }

    /**
     * Export a table to a CSV file
     *
     * @param tableName The table name
     * @param file      The file to export
     */
    public static void exportCSV(String tableName, File file) throws Exception {
        String csv = exportCSV(tableName);
        if (file.exists()) {
            File backup = new File(file.getAbsolutePath() + ".bak");
            Files.move(file.toPath(), backup.toPath());
        }
        boolean re = file.createNewFile();
        Files.writeString(file.toPath(), csv);
    }

    /**
     * Imports data from a CSV string into the specified table.
     *
     * @param tableName    the name of the table to import data into
     * @param keyFieldName the name of the key field in the table
     * @param csv          the CSV string containing the data to be imported
     * @throws DatabaseException if there is an error during the import process
     */
    public static void importCSV(String tableName, String keyFieldName, String csv) throws DatabaseException {
        String[] lines = csv.split("\n");
        String[] columns = lines[0].split(",");
        String[] types = lines[1].split(",");
        List<String> column_names = new ArrayList<>();
        List<FieldType> column_types = new ArrayList<>();
        for (int i = 0; i < columns.length; i++) {
            column_names.add(columns[i]);
            column_types.add(FieldType.valueOf(types[i]));
        }
        if (!column_names.contains(keyFieldName)) {
            throw new FieldNotFound(tableName, keyFieldName);
        }
        for (int i = 2; i < lines.length; i++) {
            String[] values = lines[i].split(",");
            InsertRow insertRow = new InsertRow().table(tableName).onConflictDoNothing(new Field(keyFieldName, values[column_names.indexOf(keyFieldName)]));
            for (int j = 0; j < values.length; j++) {
                FieldType type = column_types.get(j);
                switch (type) {
                    case STRING -> insertRow.field(new Field(column_names.get(j), values[j]));
                    case INT -> insertRow.field(new Field(column_names.get(j), Integer.parseInt(values[j])));
                    case LONG -> insertRow.field(new Field(column_names.get(j), Long.parseLong(values[j])));
                    case DOUBLE -> insertRow.field(new Field(column_names.get(j), Double.parseDouble(values[j])));
                    case FLOAT -> insertRow.field(new Field(column_names.get(j), Float.parseFloat(values[j])));
                    case BOOLEAN -> insertRow.field(new Field(column_names.get(j), Boolean.parseBoolean(values[j])));
                    case DATETIME -> insertRow.field(new Field(column_names.get(j), Timestamp.valueOf(values[j])));
                    case UUID -> {
                        // UUID is a special case only for PostgreSQL
                        if (DatabaseManager.instance.getType().equals(DatabaseType.PGSQL)) {
                            insertRow.field(new Field(column_names.get(j), UUID.fromString(values[j])));
                        } else {
                            insertRow.field(new Field(column_names.get(j), values[j]));
                        }
                    }
                }
            }
            insertRow.execute();
        }
    }

    /**
     * Imports data from a CSV file into the specified table.
     *
     * @param tableName    the name of the table to import data into
     * @param keyFieldName the name of the key field in the table
     * @param file         the CSV file containing the data to be imported
     * @throws Exception if there is an error during the import process
     */
    public static void importCSV(String tableName, String keyFieldName, File file) throws Exception {
        String csv = java.nio.file.Files.readString(file.toPath());
        importCSV(tableName, keyFieldName, csv);
    }

}
