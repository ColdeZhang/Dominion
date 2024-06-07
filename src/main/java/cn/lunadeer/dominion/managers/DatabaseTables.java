package cn.lunadeer.dominion.managers;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.dtos.Flag;

public class DatabaseTables {
    public static void migrate() {
        String sql = "";

        // player name
        sql = "CREATE TABLE IF NOT EXISTS player_name (" +
                " id                SERIAL PRIMARY KEY," +
                " uuid              VARCHAR(36) NOT NULL UNIQUE," +
                " last_known_name   TEXT NOT NULL," +
                " last_join_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP" +
                ");";
        Dominion.database.query(sql);

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

                " FOREIGN KEY (owner) REFERENCES player_name(uuid) ON DELETE CASCADE," +
                " FOREIGN KEY (parent_dom_id) REFERENCES dominion(id) ON DELETE CASCADE" +
                ");";
        Dominion.database.query(sql);

        // player privilege
        sql = "CREATE TABLE IF NOT EXISTS player_privilege (" +
                " id          SERIAL PRIMARY KEY," +
                " player_uuid VARCHAR(36) NOT NULL," +
                " dom_id      INT NOT NULL," +

                " admin BOOLEAN NOT NULL DEFAULT FALSE," +

                " UNIQUE (player_uuid, dom_id)," +
                " FOREIGN KEY (player_uuid) REFERENCES player_name(uuid) ON DELETE CASCADE," +
                " FOREIGN KEY (dom_id) REFERENCES dominion(id) ON DELETE CASCADE" +
                ");";
        Dominion.database.query(sql);

        sql = "INSERT INTO player_name (" +
                "id, uuid, last_known_name" +
                ") VALUES (" +
                "-1, '00000000-0000-0000-0000-000000000000', 'server'" +
                ") ON CONFLICT DO NOTHING;";
        Dominion.database.query(sql);

        sql = "INSERT INTO dominion (" +
                "id, owner, name, world, x1, y1, z1, x2, y2, z2, parent_dom_id, join_message, leave_message" +
                ") VALUES (" +
                "-1, '00000000-0000-0000-0000-000000000000', '根领地', 'all', " +
                "-2147483648, -2147483648, -2147483648, " +
                "2147483647, 2147483647, 2147483647, -1, " +
                "'欢迎', '再见'" +
                ") ON CONFLICT DO NOTHING;";
        Dominion.database.query(sql);

        for (Flag flag : Flag.getAllDominionFlags()) {
            Dominion.database.addColumnIfNotExists("dominion",
                    flag.getFlagName(),
                    "BOOLEAN NOT NULL DEFAULT " + flag.getDefaultValue());
        }

        for (Flag flag : Flag.getAllPrivilegeFlags()) {
            Dominion.database.addColumnIfNotExists("player_privilege",
                    flag.getFlagName(),
                    "BOOLEAN NOT NULL DEFAULT " + flag.getDefaultValue());
        }

        // 1.18.0
        Dominion.database.addColumnIfNotExists("dominion", "tp_location", "TEXT NOT NULL DEFAULT 'default'");

        // 1.31.0
        sql = "CREATE TABLE IF NOT EXISTS privilege_template (" +
                " id          SERIAL PRIMARY KEY," +
                " creator     VARCHAR(36) NOT NULL," +
                " name        TEXT NOT NULL," +
                " admin       BOOLEAN NOT NULL DEFAULT FALSE," +

                " UNIQUE (creator, name)," +
                " FOREIGN KEY (creator) REFERENCES player_name(uuid) ON DELETE CASCADE" +
                ");";
        Dominion.database.query(sql);

        for (Flag flag : Flag.getAllPrivilegeFlags()) {
            Dominion.database.addColumnIfNotExists("privilege_template",
                    flag.getFlagName(),
                    "BOOLEAN NOT NULL DEFAULT " + flag.getDefaultValue());
        }
    }
}
