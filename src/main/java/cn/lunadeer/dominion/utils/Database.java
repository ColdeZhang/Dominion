package cn.lunadeer.dominion.utils;

import cn.lunadeer.dominion.Dominion;

import java.sql.*;

public class Database {

    public static Connection createConnection() {
        try {
            String connectionUrl;
            if (Dominion.config.getDbType().equals("pgsql")) {
                XLogger.info("正在连接到 PostgreSQL 数据库");
                Class.forName("org.postgresql.Driver");
                connectionUrl = "jdbc:postgresql://" + Dominion.config.getDbHost() + ":" + Dominion.config.getDbPort();
                connectionUrl += "/" + Dominion.config.getDbName();
                return DriverManager.getConnection(connectionUrl, Dominion.config.getDbUser(), Dominion.config.getDbPass());
            } else if (Dominion.config.getDbType().equals("sqlite")) {
                XLogger.info("正在连接到 SQLite 数据库");
                Class.forName("org.sqlite.JDBC");
                connectionUrl = "jdbc:sqlite:" + Dominion.instance.getDataFolder() + "/" + Dominion.config.getDbName() + ".db";
                return DriverManager.getConnection(connectionUrl);
            } else {
                XLogger.err("=== 严重错误 ===");
                XLogger.err("数据库类型错误，只能为 pgsql 或 sqlite");
                XLogger.err("===============");
                return null;
            }
        } catch (ClassNotFoundException | SQLException e) {
            XLogger.err("=== 严重错误 ===");
            XLogger.err("Database connection failed: " + e.getMessage());
            XLogger.err("===============");
            return null;
        }
    }

    public static ResultSet query(String sql) {
        Connection conn = Dominion.dbConnection;
        if (conn == null) {
            return null;
        }
        try {
            Statement stmt = conn.createStatement();
            if (sql.contains("SERIAL PRIMARY KEY") && Dominion.config.getDbType().equals("sqlite")) {
                sql = sql.replace("SERIAL PRIMARY KEY", "INTEGER PRIMARY KEY AUTOINCREMENT");
            }
            // if query with no result return null
            if (stmt.execute(sql)) {
                return stmt.getResultSet();
            }
        } catch (SQLException e) {
            handleDatabaseError("Database query failed: ", e, sql);
        }
        return null;
    }

    private static void handleDatabaseError(String errorMessage, SQLException e, String sql) {
        XLogger.err("=== 严重错误 ===");
        XLogger.err(errorMessage + e.getMessage());
        XLogger.err("SQL: " + sql);
        XLogger.err("===============");
    }

    private static void addColumnIfNotExists(String tableName, String columnName, String columnDefinition) {
        if (Dominion.config.getDbType().equals("pgsql")) {
            String sql = "ALTER TABLE " + tableName + " ADD COLUMN IF NOT EXISTS " + columnName + " " + columnDefinition + ";";
            query(sql);
        } else if (Dominion.config.getDbType().equals("sqlite")) {
            try {
                ResultSet rs = query("PRAGMA table_info(" + tableName + ");");
                boolean columnExists = false;
                if (rs != null) {
                    while (rs.next()) {
                        if (columnName.equals(rs.getString("name"))) {
                            columnExists = true;
                            break;
                        }
                    }
                }
                if (!columnExists) {
                    query("ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnDefinition + ";");
                }
            } catch (SQLException e) {
                handleDatabaseError("Database operation failed: ", e, "");
            }
        }
    }

    public static void migrate() {
        String sql = "";

        // player name
        sql = "CREATE TABLE IF NOT EXISTS player_name (" +
                " id                SERIAL PRIMARY KEY," +
                " uuid              VARCHAR(36) NOT NULL UNIQUE," +
                " last_known_name   TEXT NOT NULL," +
                " last_join_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP" +
                ");";
        query(sql);

        // dominion table
        sql = "CREATE TABLE IF NOT EXISTS dominion (" +
                " id    SERIAL PRIMARY KEY," +
                " owner VARCHAR(36) NOT NULL," +
                " name  TEXT NOT NULL UNIQUE," +
                " world TEXT NOT NULL," +
                " x1    INT NOT NULL," +
                " y1    INT NOT NULL," +
                " z1    INT NOT NULL," +
                " x2    INT NOT NULL," +
                " y2    INT NOT NULL," +
                " z2    INT NOT NULL," +
                " parent_dom_id INT NOT NULL DEFAULT -1," +
                " join_message TEXT NOT NULL DEFAULT '欢迎', " +
                " leave_message TEXT NOT NULL DEFAULT '再见', " +

                " anchor BOOLEAN NOT NULL DEFAULT FALSE," +
                " animal_killing BOOLEAN NOT NULL DEFAULT FALSE," +
                " anvil BOOLEAN NOT NULL DEFAULT FALSE," +
                " beacon BOOLEAN NOT NULL DEFAULT FALSE," +
                " bed BOOLEAN NOT NULL DEFAULT FALSE," +
                " brew BOOLEAN NOT NULL DEFAULT FALSE," +
                " break BOOLEAN NOT NULL DEFAULT FALSE," +
                " button BOOLEAN NOT NULL DEFAULT FALSE," +
                " cake BOOLEAN NOT NULL DEFAULT FALSE," +
                " container BOOLEAN NOT NULL DEFAULT FALSE," +
                " craft BOOLEAN NOT NULL DEFAULT FALSE," +
                " creeper_explode BOOLEAN NOT NULL DEFAULT FALSE," +
                " comparer BOOLEAN NOT NULL DEFAULT FALSE," +
                " door BOOLEAN NOT NULL DEFAULT FALSE," +
                " dye BOOLEAN NOT NULL DEFAULT FALSE," +
                " egg BOOLEAN NOT NULL DEFAULT FALSE," +
                " enchant BOOLEAN NOT NULL DEFAULT FALSE," +
                " ender_pearl BOOLEAN NOT NULL DEFAULT FALSE," +
                " feed BOOLEAN NOT NULL DEFAULT FALSE," +
                " fire_spread BOOLEAN NOT NULL DEFAULT FALSE," +
                " flow_in_protection BOOLEAN NOT NULL DEFAULT FALSE," +
                " glow BOOLEAN NOT NULL DEFAULT TRUE," +
                " harvest BOOLEAN NOT NULL DEFAULT FALSE," +
                " honey BOOLEAN NOT NULL DEFAULT FALSE," +
                " hook BOOLEAN NOT NULL DEFAULT FALSE," +
                " ignite BOOLEAN NOT NULL DEFAULT FALSE," +
                " lever BOOLEAN NOT NULL DEFAULT FALSE," +
                " monster_killing BOOLEAN NOT NULL DEFAULT FALSE," +
                " move BOOLEAN NOT NULL DEFAULT TRUE," +
                " place BOOLEAN NOT NULL DEFAULT FALSE," +
                " pressure BOOLEAN NOT NULL DEFAULT FALSE," +
                " riding BOOLEAN NOT NULL DEFAULT FALSE," +
                " repeater BOOLEAN NOT NULL DEFAULT FALSE," +
                " shear BOOLEAN NOT NULL DEFAULT FALSE," +
                " shoot BOOLEAN NOT NULL DEFAULT FALSE," +
                " tnt_explode BOOLEAN NOT NULL DEFAULT FALSE," +
                " trade BOOLEAN NOT NULL DEFAULT FALSE," +
                " vehicle_destroy BOOLEAN NOT NULL DEFAULT FALSE," +
                " wither_spawn BOOLEAN NOT NULL DEFAULT FALSE," +

                " FOREIGN KEY (owner) REFERENCES player_name(uuid) ON DELETE CASCADE," +
                " FOREIGN KEY (parent_dom_id) REFERENCES dominion(id) ON DELETE CASCADE" +
                ");";
        query(sql);

        // player privilege
        sql = "CREATE TABLE IF NOT EXISTS player_privilege (" +
                " id          SERIAL PRIMARY KEY," +
                " player_uuid VARCHAR(36) NOT NULL," +
                " dom_id      INT NOT NULL," +

                " admin BOOLEAN NOT NULL DEFAULT FALSE," +
                " anchor BOOLEAN NOT NULL DEFAULT FALSE," +
                " animal_killing BOOLEAN NOT NULL DEFAULT FALSE," +
                " anvil BOOLEAN NOT NULL DEFAULT FALSE," +
                " beacon BOOLEAN NOT NULL DEFAULT FALSE," +
                " bed BOOLEAN NOT NULL DEFAULT FALSE," +
                " brew BOOLEAN NOT NULL DEFAULT FALSE," +
                " break BOOLEAN NOT NULL DEFAULT FALSE," +
                " button BOOLEAN NOT NULL DEFAULT FALSE," +
                " cake BOOLEAN NOT NULL DEFAULT FALSE," +
                " container BOOLEAN NOT NULL DEFAULT FALSE," +
                " craft BOOLEAN NOT NULL DEFAULT FALSE," +
                " comparer BOOLEAN NOT NULL DEFAULT FALSE," +
                " door BOOLEAN NOT NULL DEFAULT FALSE," +
                " dye BOOLEAN NOT NULL DEFAULT FALSE," +
                " egg BOOLEAN NOT NULL DEFAULT FALSE," +
                " enchant BOOLEAN NOT NULL DEFAULT FALSE," +
                " ender_pearl BOOLEAN NOT NULL DEFAULT FALSE," +
                " feed BOOLEAN NOT NULL DEFAULT FALSE," +
                " glow BOOLEAN NOT NULL DEFAULT TRUE," +
                " harvest BOOLEAN NOT NULL DEFAULT FALSE," +
                " honey BOOLEAN NOT NULL DEFAULT FALSE," +
                " hook BOOLEAN NOT NULL DEFAULT FALSE," +
                " ignite BOOLEAN NOT NULL DEFAULT FALSE," +
                " lever BOOLEAN NOT NULL DEFAULT FALSE," +
                " monster_killing BOOLEAN NOT NULL DEFAULT FALSE," +
                " move BOOLEAN NOT NULL DEFAULT TRUE," +
                " place BOOLEAN NOT NULL DEFAULT FALSE," +
                " pressure BOOLEAN NOT NULL DEFAULT FALSE," +
                " riding BOOLEAN NOT NULL DEFAULT FALSE," +
                " repeater BOOLEAN NOT NULL DEFAULT FALSE," +
                " shear BOOLEAN NOT NULL DEFAULT FALSE," +
                " shoot BOOLEAN NOT NULL DEFAULT FALSE," +
                " trade BOOLEAN NOT NULL DEFAULT FALSE," +
                " vehicle_destroy BOOLEAN NOT NULL DEFAULT FALSE," +

                " UNIQUE (player_uuid, dom_id)," +
                " FOREIGN KEY (player_uuid) REFERENCES player_name(uuid) ON DELETE CASCADE," +
                " FOREIGN KEY (dom_id) REFERENCES dominion(id) ON DELETE CASCADE" +
                ");";
        query(sql);

        sql = "INSERT INTO player_name (" +
                "id, uuid, last_known_name" +
                ") VALUES (" +
                "-1, '00000000-0000-0000-0000-000000000000', 'server'" +
                ") ON CONFLICT DO NOTHING;";
        query(sql);

        sql = "INSERT INTO dominion (" +
                "id, owner, name, world, x1, y1, z1, x2, y2, z2, parent_dom_id, join_message, leave_message" +
                ") VALUES (" +
                "-1, '00000000-0000-0000-0000-000000000000', '根领地', 'all', " +
                "-2147483648, -2147483648, -2147483648, " +
                "2147483647, 2147483647, 2147483647, -1, " +
                "'欢迎', '再见'" +
                ") ON CONFLICT DO NOTHING;";
        query(sql);

        // 1.5.0
        addColumnIfNotExists("dominion", "hopper", "BOOLEAN NOT NULL DEFAULT FALSE");
        addColumnIfNotExists("player_privilege", "hopper", "BOOLEAN NOT NULL DEFAULT FALSE");

        // 1.9.0
        addColumnIfNotExists("dominion", "vehicle_spawn", "BOOLEAN NOT NULL DEFAULT FALSE");
        addColumnIfNotExists("player_privilege", "vehicle_spawn", "BOOLEAN NOT NULL DEFAULT FALSE");

        // 1.10.0
        addColumnIfNotExists("dominion", "trample", "BOOLEAN NOT NULL DEFAULT FALSE");

        // 1.11.0
        addColumnIfNotExists("dominion", "mob_drop_item", "BOOLEAN NOT NULL DEFAULT TRUE");

        // 1.12.0
        addColumnIfNotExists("dominion", "ender_man", "BOOLEAN NOT NULL DEFAULT FAlSE");
    }
}
