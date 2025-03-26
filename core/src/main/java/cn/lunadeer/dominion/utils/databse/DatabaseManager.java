package cn.lunadeer.dominion.utils.databse;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.java.JavaPlugin;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {

    public static DatabaseManager instance;

    private final JavaPlugin plugin;
    private DatabaseType type;
    private final HikariConfig config = new HikariConfig();
    private DataSource ds;

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
     */
    public DatabaseManager(JavaPlugin plugin, String type, String host, String port, String name, String user, String pass) throws IllegalArgumentException {
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
     */
    public void set(String type, String host, String port, String name, String user, String pass) throws IllegalArgumentException {
        try {
            this.type = DatabaseType.valueOf(type.toUpperCase());
            if (this.type.equals(DatabaseType.PGSQL)) {
                config.setDriverClassName("org.postgresql.Driver");
                config.setJdbcUrl("jdbc:postgresql://" + host + ":" + port + "/" + name);
            } else if (this.type.equals(DatabaseType.SQLITE)) {
                config.setDriverClassName("org.sqlite.JDBC");
                config.setJdbcUrl("jdbc:sqlite:" + plugin.getDataFolder() + "/" + name + ".db");
            } else if (this.type.equals(DatabaseType.MYSQL)) {
                config.setDriverClassName("com.mysql.cj.jdbc.Driver");
                config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + name);
            }
            config.setUsername(user);
            config.setPassword(pass);
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setIdleTimeout(60000);
            config.setConnectionTimeout(30000);
            config.setMaxLifetime(1800000);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unsupported database type: " + type, e);
        }
    }

    public void reconnect() {
        if (ds != null) {
            close();
        }
        this.ds = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        if (ds == null) {
            reconnect();
        }
        return ds.getConnection();
    }

    /**
     * Closes the database connection if it is open.
     */
    public void close() {
        this.ds = null;
    }

    /**
     * Returns the type of the database.
     *
     * @return the type of the database
     */
    public DatabaseType getType() {
        return type;
    }

//    /**
//     * Export a table to a CSV string
//     * The format is:
//     * column1,column2,column3, ...
//     * TYPE1,TYPE2,TYPE3, ...
//     * value1,value2,value3, ...
//     *
//     * @param tableName The table name
//     * @return The CSV string
//     */
//    public static String exportCSV(String tableName) {
//        StringBuilder csv = new StringBuilder();
//        Map<String, FieldType> columnTypes = getTableColumns(tableName);
//        List<String> column_names = new ArrayList<>(columnTypes.keySet());
//        csv.append(String.join(",", column_names)).append("\n");
//        List<String> column_types = new ArrayList<>(columnTypes.values()).stream().map(FieldType::toString).toList();
//        csv.append(String.join(",", column_types)).append("\n");
//        String sql = "SELECT * FROM " + tableName;
//        if (column_names.contains("id")) {
//            sql += " ORDER BY id";
//        } else {
//            sql += ";";
//        }
//        try (ResultSet rs = DatabaseManager.instance.query(sql)) {
//            if (rs != null) {
//                while (rs.next()) {
//                    List<String> values = new ArrayList<>();
//                    for (String column : column_names) {
//                        FieldType type = columnTypes.get(column);
//                        switch (type) {
//                            case STRING -> values.add(rs.getString(column));
//                            case INT -> values.add(String.valueOf(rs.getInt(column)));
//                            case LONG -> values.add(String.valueOf(rs.getLong(column)));
//                            case DOUBLE -> values.add(String.valueOf(rs.getDouble(column)));
//                            case FLOAT -> values.add(String.valueOf(rs.getFloat(column)));
//                            case BOOLEAN -> values.add(String.valueOf(rs.getBoolean(column)));
//                            case DATETIME -> values.add(rs.getTimestamp(column).toString());
//                            case UUID -> values.add(UUID.fromString(rs.getString(column)).toString());
//                        }
//                    }
//                    csv.append(String.join(",", values)).append("\n");
//                }
//            }
//        } catch (SQLException e) {
//            throw new QueryException(e.getMessage());
//        }
//        return csv.toString();
//    }
//
//    /**
//     * Export a table to a CSV file
//     *
//     * @param tableName The table name
//     * @param file      The file to export
//     */
//    public static void exportCSV(String tableName, File file) throws Exception {
//        String csv = exportCSV(tableName);
//        if (file.exists()) {
//            File backup = new File(file.getAbsolutePath() + ".bak");
//            Files.move(file.toPath(), backup.toPath());
//        }
//        boolean re = file.createNewFile();
//        Files.writeString(file.toPath(), csv);
//    }
//
//    /**
//     * Imports data from a CSV string into the specified table.
//     *
//     * @param tableName    the name of the table to import data into
//     * @param keyFieldName the name of the key field in the table
//     * @param csv          the CSV string containing the data to be imported
//     * @throws DatabaseException if there is an error during the import process
//     */
//    public static void importCSV(String tableName, String keyFieldName, String csv) throws DatabaseException {
//        String[] lines = csv.split("\n");
//        String[] columns = lines[0].split(",");
//        String[] types = lines[1].split(",");
//        List<String> column_names = new ArrayList<>();
//        List<FieldType> column_types = new ArrayList<>();
//        for (int i = 0; i < columns.length; i++) {
//            column_names.add(columns[i]);
//            column_types.add(FieldType.valueOf(types[i]));
//        }
//        if (!column_names.contains(keyFieldName)) {
//            throw new FieldNotFound(tableName, keyFieldName);
//        }
//        for (int i = 2; i < lines.length; i++) {
//            String[] values = lines[i].split(",");
//            InsertRow insertRow = new InsertRow().table(tableName).onConflictDoNothing(new field(keyFieldName, values[column_names.indexOf(keyFieldName)]));
//            for (int j = 0; j < values.length; j++) {
//                FieldType type = column_types.get(j);
//                switch (type) {
//                    case STRING -> insertRow.field(new field(column_names.get(j), values[j]));
//                    case INT -> insertRow.field(new field(column_names.get(j), Integer.parseInt(values[j])));
//                    case LONG -> insertRow.field(new field(column_names.get(j), Long.parseLong(values[j])));
//                    case DOUBLE -> insertRow.field(new field(column_names.get(j), Double.parseDouble(values[j])));
//                    case FLOAT -> insertRow.field(new field(column_names.get(j), Float.parseFloat(values[j])));
//                    case BOOLEAN -> insertRow.field(new field(column_names.get(j), Boolean.parseBoolean(values[j])));
//                    case DATETIME -> insertRow.field(new field(column_names.get(j), Timestamp.valueOf(values[j])));
//                    case UUID -> {
//                        // UUID is a special case only for PostgreSQL
//                        if (DatabaseManager.instance.getType().equals(DatabaseType.PGSQL)) {
//                            insertRow.field(new field(column_names.get(j), UUID.fromString(values[j])));
//                        } else {
//                            insertRow.field(new field(column_names.get(j), values[j]));
//                        }
//                    }
//                }
//            }
//            insertRow.execute();
//        }
//    }
//
//    /**
//     * Imports data from a CSV file into the specified table.
//     *
//     * @param tableName    the name of the table to import data into
//     * @param keyFieldName the name of the key field in the table
//     * @param file         the CSV file containing the data to be imported
//     * @throws Exception if there is an error during the import process
//     */
//    public static void importCSV(String tableName, String keyFieldName, File file) throws Exception {
//        String csv = java.nio.file.Files.readString(file.toPath());
//        importCSV(tableName, keyFieldName, csv);
//    }

}
