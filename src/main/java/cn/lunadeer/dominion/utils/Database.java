package cn.lunadeer.dominion.utils;

import cn.lunadeer.dominion.Dominion;

import java.sql.*;

public class Database {

    public static Connection createConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(Dominion.config.getDBConnectionUrl(), Dominion.config.getDbUser(), Dominion.config.getDbPass());
        } catch (ClassNotFoundException | SQLException e) {
            XLogger.err("Database connection failed: " + e.getMessage());
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
            // if query with no result return null
            if (stmt.execute(sql)) {
                return stmt.getResultSet();
            } else {
                return null;
            }
        } catch (SQLException e) {
            XLogger.err("Database query failed: " + e.getMessage());
            XLogger.err("SQL: " + sql);
            return null;
        }
    }

    public static void migrate() {
        String sql = "";

        // player name
        sql += "CREATE TABLE IF NOT EXISTS player_name (" +
                " id                SERIAL PRIMARY KEY," +
                " uuid              VARCHAR(36) NOT NULL UNIQUE," +
                " last_known_name   TEXT NOT NULL," +
                " last_join_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP" +
                ");";

        // dominion table
        sql += "CREATE TABLE IF NOT EXISTS dominion (" +
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

                " FOREIGN KEY (owner) REFERENCES player_name(uuid)," +
                " FOREIGN KEY (parent_dom_id) REFERENCES dominion(id)" +
                ");";

        // player privilege
        sql += "CREATE TABLE IF NOT EXISTS player_privilege (" +
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
                " FOREIGN KEY (player_uuid) REFERENCES player_name(uuid)," +
                " FOREIGN KEY (dom_id) REFERENCES dominion(id)" +
                ");";

        sql += "INSERT INTO player_name (" +
                "id, uuid, last_known_name" +
                ") VALUES (" +
                "-1, '00000000-0000-0000-0000-000000000000', 'server'" +
                ") ON CONFLICT DO NOTHING;";

        sql += "INSERT INTO dominion (" +
                "id, owner, name, world, x1, y1, z1, x2, y2, z2, parent_dom_id, join_message, leave_message" +
                ") VALUES (" +
                "-1, '00000000-0000-0000-0000-000000000000', '根领地', 'all', " +
                "-2147483648, -2147483648, -2147483648, " +
                "2147483647, 2147483647, 2147483647, -1, " +
                "'欢迎', '再见'" +
                ") ON CONFLICT DO NOTHING;";

        query(sql);

        // 1.5.0
        sql = "ALTER TABLE dominion ADD hopper BOOLEAN NOT NULL DEFAULT FALSE;";
        query(sql);
        sql = "ALTER TABLE player_privilege ADD hopper BOOLEAN NOT NULL DEFAULT FALSE;";
        query(sql);
    }
}
