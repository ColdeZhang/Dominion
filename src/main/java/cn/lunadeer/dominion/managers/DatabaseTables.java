package cn.lunadeer.dominion.managers;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.dtos.Flag;

public class DatabaseTables {
    public static void migrate() {
        String sql = "";

        // player name
        Dominion.database.createTableIfNotExists(
                "player_name",
                new String[]{"id", "uuid", "last_known_name", "last_join_at"},
                new String[]{"SERIAL PRIMARY KEY", "VARCHAR(36) NOT NULL UNIQUE", "TEXT NOT NULL", "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP"},
                null
        );

        // dominion table
        Dominion.database.createTableIfNotExists(
                "dominion",
                new String[]{"id",
                        "owner",
                        "name",
                        "world",
                        "x1", "y1", "z1", "x2", "y2", "z2",
                        "parent_dom_id",
                        "join_message",
                        "leave_message"},
                new String[]{
                        "SERIAL PRIMARY KEY",
                        "VARCHAR(36) NOT NULL",
                        "TEXT NOT NULL UNIQUE",
                        "TEXT NOT NULL", "INT NOT NULL", "INT NOT NULL", "INT NOT NULL", "INT NOT NULL", "INT NOT NULL", "INT NOT NULL",
                        "INT NOT NULL DEFAULT -1",
                        "TEXT NOT NULL DEFAULT '欢迎'",
                        "TEXT NOT NULL DEFAULT '再见'"},
                new String[]{
                        "FOREIGN KEY (owner) REFERENCES player_name(uuid) ON DELETE CASCADE",
                        "FOREIGN KEY (parent_dom_id) REFERENCES dominion(id) ON DELETE CASCADE"
                }
        );

        // player privilege
        Dominion.database.createTableIfNotExists(
                "player_privilege",
                new String[]{"id", "player_uuid", "dom_id", "admin"},
                new String[]{"SERIAL PRIMARY KEY", "VARCHAR(36) NOT NULL", "INT NOT NULL", "BOOLEAN NOT NULL DEFAULT FALSE"},
                new String[]{
                        "UNIQUE (player_uuid, dom_id)",
                        "FOREIGN KEY (player_uuid) REFERENCES player_name(uuid) ON DELETE CASCADE",
                        "FOREIGN KEY (dom_id) REFERENCES dominion(id) ON DELETE CASCADE"
                }
        );

        Dominion.database.insertRowIfNotExists("player_name",
                new String[]{"id", "uuid", "last_known_name"},
                new String[]{"-1", "00000000-0000-0000-0000-000000000000", "server"},
                0
        );

        Dominion.database.insertRowIfNotExists("dominion",
                new String[]{
                        "id",
                        "owner",
                        "name",
                        "world",
                        "x1", "y1", "z1", "x2", "y2", "z2",
                        "parent_dom_id",
                        "join_message",
                        "leave_message"},
                new String[]{
                        "-1",
                        "00000000-0000-0000-0000-000000000000",
                        "根领地",
                        "all",
                        "-2147483648", "-2147483648", "-2147483648",
                        "2147483647", "2147483647", "2147483647",
                        "-1",
                        "欢迎",
                        "再见"},
                0
        );

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
        Dominion.database.createTableIfNotExists(
                "privilege_template",
                new String[]{"id", "creator", "name", "admin"},
                new String[]{"SERIAL PRIMARY KEY", "VARCHAR(36) NOT NULL", "TEXT NOT NULL", "BOOLEAN NOT NULL DEFAULT FALSE"},
                new String[]{
                        "UNIQUE (creator, name)",
                        "FOREIGN KEY (creator) REFERENCES player_name(uuid) ON DELETE CASCADE"
                }
        );

        for (Flag flag : Flag.getAllPrivilegeFlags()) {
            Dominion.database.addColumnIfNotExists("privilege_template",
                    flag.getFlagName(),
                    "BOOLEAN NOT NULL DEFAULT " + flag.getDefaultValue());
        }

        // 1.31.6
        Dominion.database.addColumnIfNotExists("dominion", "color", "TEXT NOT NULL DEFAULT '#00BFFF'");
    }
}
