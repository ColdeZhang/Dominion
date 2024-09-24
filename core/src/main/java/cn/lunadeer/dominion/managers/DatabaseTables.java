package cn.lunadeer.dominion.managers;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.dtos.Flag;
import cn.lunadeer.minecraftpluginutils.databse.*;
import cn.lunadeer.minecraftpluginutils.databse.syntax.AddColumn;
import cn.lunadeer.minecraftpluginutils.databse.syntax.CreateTable;
import cn.lunadeer.minecraftpluginutils.databse.syntax.InsertRow;
import cn.lunadeer.minecraftpluginutils.databse.syntax.RemoveColumn;
import org.bukkit.World;

import java.sql.ResultSet;
import java.util.List;

public class DatabaseTables {
    public static void migrate() {
        // player name
        TableColumn player_name_id = new TableColumn("id", FieldType.INT, true, true, true, true, 0);
        TableColumn player_name_uuid = new TableColumn("uuid", FieldType.STRING, false, false, true, true, "''");
        TableColumn player_name_last_known_name = new TableColumn("last_known_name", FieldType.STRING, false, false, true, false, "'unknown'");
        TableColumn player_name_last_join_at = new TableColumn("last_join_at", FieldType.DATETIME, false, false, true, false, "CURRENT_TIMESTAMP");
        CreateTable player_name = new CreateTable().ifNotExists();
        player_name.table("player_name")
                .field(player_name_id)
                .field(player_name_uuid)
                .field(player_name_last_known_name)
                .field(player_name_last_join_at);
        player_name.execute();

        // dominion table
        TableColumn dominion_id = new TableColumn("id", FieldType.INT, true, true, true, true, 0);
        TableColumn dominion_owner = new TableColumn("owner", FieldType.STRING, false, false, true, false, "''");
        TableColumn dominion_name = new TableColumn("name", FieldType.STRING, false, false, true, false, "'未命名'");
        TableColumn dominion_world = new TableColumn("world", FieldType.STRING, false, false, true, false, "'world'");
        TableColumn dominion_x1 = new TableColumn("x1", FieldType.INT, false, false, true, false, 0);
        TableColumn dominion_y1 = new TableColumn("y1", FieldType.INT, false, false, true, false, 0);
        TableColumn dominion_z1 = new TableColumn("z1", FieldType.INT, false, false, true, false, 0);
        TableColumn dominion_x2 = new TableColumn("x2", FieldType.INT, false, false, true, false, 0);
        TableColumn dominion_y2 = new TableColumn("y2", FieldType.INT, false, false, true, false, 0);
        TableColumn dominion_z2 = new TableColumn("z2", FieldType.INT, false, false, true, false, 0);
        TableColumn dominion_parent_dom_id = new TableColumn("parent_dom_id", FieldType.INT, false, false, true, false, -1);
        TableColumn dominion_join_message = new TableColumn("join_message", FieldType.STRING, false, false, true, false, "'欢迎'");
        TableColumn dominion_leave_message = new TableColumn("leave_message", FieldType.STRING, false, false, true, false, "'再见'");
        CreateTable.ForeignKey dominion_owner_fk = new CreateTable.ForeignKey(dominion_owner, "player_name", player_name_uuid, true);
        CreateTable.ForeignKey dominion_parent_dom_id_fk = new CreateTable.ForeignKey(dominion_parent_dom_id, "dominion", dominion_id, true);
        CreateTable dominion = new CreateTable().ifNotExists();
        dominion.table("dominion")
                .field(dominion_id)
                .field(dominion_owner)
                .field(dominion_name)
                .field(dominion_world)
                .field(dominion_x1)
                .field(dominion_y1)
                .field(dominion_z1)
                .field(dominion_x2)
                .field(dominion_y2)
                .field(dominion_z2)
                .field(dominion_parent_dom_id)
                .field(dominion_join_message)
                .field(dominion_leave_message)
                .foreignKey(dominion_owner_fk)
                .foreignKey(dominion_parent_dom_id_fk);
        dominion.execute();

        for (Flag flag : Flag.getAllDominionFlags()) {
            TableColumn column = new TableColumn(flag.getFlagName(), FieldType.BOOLEAN, false, false, true, false, flag.getDefaultValue());
            new AddColumn(column).table("dominion").ifNotExists().execute();
        }

        // player privilege
        if (!Common.IsTableExist("dominion_member")) {
            TableColumn player_privilege_id = new TableColumn("id", FieldType.INT, true, true, true, true, 0);
            TableColumn player_privilege_player_uuid = new TableColumn("player_uuid", FieldType.STRING, false, false, true, false, "''");
            TableColumn player_privilege_dom_id = new TableColumn("dom_id", FieldType.INT, false, false, true, false, -1);
            TableColumn player_privilege_admin = new TableColumn("admin", FieldType.BOOLEAN, false, false, true, false, false);
            CreateTable.ForeignKey player_privilege_player_uuid_fk = new CreateTable.ForeignKey(player_privilege_player_uuid, "player_name", player_name_uuid, true);
            CreateTable.ForeignKey player_privilege_dom_id_fk = new CreateTable.ForeignKey(player_privilege_dom_id, "dominion", dominion_id, true);
            CreateTable player_privilege = new CreateTable().ifNotExists();
            player_privilege.table("player_privilege")
                    .field(player_privilege_id)
                    .field(player_privilege_player_uuid)
                    .field(player_privilege_dom_id)
                    .field(player_privilege_admin)
                    .foreignKey(player_privilege_player_uuid_fk)
                    .foreignKey(player_privilege_dom_id_fk)
                    .unique(player_privilege_player_uuid, player_privilege_dom_id);
            player_privilege.execute();

            for (Flag flag : Flag.getAllPrivilegeFlags()) {
                TableColumn column = new TableColumn(flag.getFlagName(), FieldType.BOOLEAN, false, false, true, false, flag.getDefaultValue());
                new AddColumn(column).table("player_privilege").ifNotExists().execute();
            }
        }

        // server root player name
        Field server_player_name_id_field = new Field("id", -1);
        Field server_player_name_uuid_field = new Field("uuid", "00000000-0000-0000-0000-000000000000");
        Field server_player_name_last_known_name_field = new Field("last_known_name", "server");
        InsertRow insert_server_player_name = new InsertRow().table("player_name").onConflictDoNothing(server_player_name_id_field)
                .field(server_player_name_id_field)
                .field(server_player_name_uuid_field)
                .field(server_player_name_last_known_name_field);
        insert_server_player_name.execute();

        // server root dominion
        Field server_dom_id_field = new Field("id", -1);
        Field server_dom_owner_field = new Field("owner", "00000000-0000-0000-0000-000000000000");
        Field server_dom_name_field = new Field("name", "根领地");
        Field server_dom_world_field = new Field("world", "all");
        Field server_dom_x1_field = new Field("x1", -2147483648);
        Field server_dom_y1_field = new Field("y1", -2147483648);
        Field server_dom_z1_field = new Field("z1", -2147483648);
        Field server_dom_x2_field = new Field("x2", 2147483647);
        Field server_dom_y2_field = new Field("y2", 2147483647);
        Field server_dom_z2_field = new Field("z2", 2147483647);
        Field server_dom_parent_dom_id_field = new Field("parent_dom_id", -1);
        Field server_dom_join_message_field = new Field("join_message", "");
        Field server_dom_leave_message_field = new Field("leave_message", "");
        InsertRow insert_server_dom = new InsertRow().table("dominion").onConflictDoNothing(server_dom_id_field)
                .field(server_dom_id_field)
                .field(server_dom_owner_field)
                .field(server_dom_name_field)
                .field(server_dom_world_field)
                .field(server_dom_x1_field)
                .field(server_dom_y1_field)
                .field(server_dom_z1_field)
                .field(server_dom_x2_field)
                .field(server_dom_y2_field)
                .field(server_dom_z2_field)
                .field(server_dom_parent_dom_id_field)
                .field(server_dom_join_message_field)
                .field(server_dom_leave_message_field);
        insert_server_dom.execute();

        // 1.18.0   dominion add tp_location
        TableColumn dominion_tp_location = new TableColumn("tp_location", FieldType.STRING, false, false, true, false, "'default'");
        new AddColumn(dominion_tp_location).table("dominion").ifNotExists().execute();

        // 1.31.0   add privilege_template
        TableColumn privilege_template_id = new TableColumn("id", FieldType.INT, true, true, true, true, 0);
        TableColumn privilege_template_creator = new TableColumn("creator", FieldType.STRING, false, false, true, false, "''");
        TableColumn privilege_template_name = new TableColumn("name", FieldType.STRING, false, false, true, false, "'未命名'");
        TableColumn privilege_template_admin = new TableColumn("admin", FieldType.BOOLEAN, false, false, true, false, false);
        CreateTable.ForeignKey privilege_template_creator_fk = new CreateTable.ForeignKey(privilege_template_creator, "player_name", player_name_uuid, true);
        CreateTable privilege_template = new CreateTable().ifNotExists();
        privilege_template.table("privilege_template")
                .field(privilege_template_id)
                .field(privilege_template_creator)
                .field(privilege_template_name)
                .field(privilege_template_admin)
                .foreignKey(privilege_template_creator_fk)
                .unique(privilege_template_creator, privilege_template_name);
        privilege_template.execute();


        for (Flag flag : Flag.getAllPrivilegeFlags()) {
            TableColumn column = new TableColumn(flag.getFlagName(), FieldType.BOOLEAN, false, false, true, false, flag.getDefaultValue());
            new AddColumn(column).table("privilege_template").ifNotExists().execute();
        }

        // 1.31.6   dominion add blue-map tile color
        TableColumn dominion_color = new TableColumn("color", FieldType.STRING, false, false, true, false, "'#00BFFF'");
        new AddColumn(dominion_color).table("dominion").ifNotExists().execute();

        // 1.34.0   add dominion_group
        if (!Common.IsTableExist("dominion_member")) {
            TableColumn player_privilege_group_id = new TableColumn("group_id", FieldType.INT, false, false, true, false, -1);
            new AddColumn(player_privilege_group_id).table("player_privilege").ifNotExists().execute();
        }

        TableColumn dominion_group_id = new TableColumn("id", FieldType.INT, true, true, true, true, 0);
        TableColumn dominion_group_dom_id = new TableColumn("dom_id", FieldType.INT, false, false, true, false, -1);
        TableColumn dominion_group_name = new TableColumn("name", FieldType.STRING, false, false, true, false, "'未命名'");
        TableColumn dominion_group_admin = new TableColumn("admin", FieldType.BOOLEAN, false, false, true, false, false);
        CreateTable.ForeignKey group_dom_id_fk = new CreateTable.ForeignKey(dominion_group_dom_id, "dominion", dominion_id, true);
        CreateTable group = new CreateTable().ifNotExists();
        group.table("dominion_group")
                .field(dominion_group_id)
                .field(dominion_group_dom_id)
                .field(dominion_group_name)
                .field(dominion_group_admin)
                .foreignKey(group_dom_id_fk)
                .unique(dominion_group_dom_id, dominion_group_name);
        group.execute();
        for (Flag flag : Flag.getAllPrivilegeFlags()) {
            TableColumn column = new TableColumn(flag.getFlagName(), FieldType.BOOLEAN, false, false, true, false, flag.getDefaultValue());
            new AddColumn(column).table("dominion_group").ifNotExists().execute();
        }

        // 1.35.0 migrate player_privilege -> dominion_member
        TableColumn dominion_member_id = new TableColumn("id", FieldType.INT, true, true, true, true, 0);
        TableColumn dominion_member_player_uuid = new TableColumn("player_uuid", FieldType.STRING, false, false, true, false, "''");
        TableColumn dominion_member_dom_id = new TableColumn("dom_id", FieldType.INT, false, false, true, false, -1);
        TableColumn dominion_member_admin = new TableColumn("admin", FieldType.BOOLEAN, false, false, true, false, false);
        TableColumn dominion_member_group_id = new TableColumn("group_id", FieldType.INT, false, false, true, false, -1);
        CreateTable.ForeignKey dominion_member_player_uuid_fk = new CreateTable.ForeignKey(dominion_member_player_uuid, "player_name", player_name_uuid, true);
        CreateTable.ForeignKey dominion_member_dom_id_fk = new CreateTable.ForeignKey(dominion_member_dom_id, "dominion", dominion_id, true);
        CreateTable dominion_member = new CreateTable().ifNotExists();
        dominion_member.table("dominion_member")
                .field(dominion_member_id)
                .field(dominion_member_player_uuid)
                .field(dominion_member_dom_id)
                .field(dominion_member_admin)
                .field(dominion_member_group_id)
                .foreignKey(dominion_member_player_uuid_fk)
                .foreignKey(dominion_member_dom_id_fk)
                .unique(dominion_member_player_uuid, dominion_member_dom_id);
        dominion_member.execute();
        for (Flag flag : Flag.getAllPrivilegeFlags()) {
            TableColumn column = new TableColumn(flag.getFlagName(), FieldType.BOOLEAN, false, false, true, false, flag.getDefaultValue());
            new AddColumn(column).table("dominion_member").ifNotExists().execute();
        }
        if (Common.IsTableExist("player_privilege")) {
            // migrate from player_privilege to dominion_member
            String sql = "SELECT * FROM player_privilege;";
            try (ResultSet rs = DatabaseManager.instance.query(sql)) {
                while (rs.next()) {
                    String player_uuid = rs.getString("player_uuid");
                    int dom_id = rs.getInt("dom_id");
                    boolean admin = rs.getBoolean("admin");
                    int group_id = rs.getInt("group_id");
                    InsertRow insert = new InsertRow().table("dominion_member")
                            .field(new Field("player_uuid", player_uuid))
                            .field(new Field("dom_id", dom_id))
                            .field(new Field("group_id", group_id))
                            .field(new Field("admin", admin));
                    for (Flag flag : Flag.getAllPrivilegeFlags()) {
                        insert.field(new Field(flag.getFlagName(), rs.getBoolean(flag.getFlagName())));
                    }
                    insert.execute();
                }
                sql = "DROP TABLE player_privilege;";
                DatabaseManager.instance.query(sql);
            } catch (Exception e) {
                DatabaseManager.handleDatabaseError("迁移 player_privilege 到 dominion_member 失败", e, sql);
            }
        }

        // 2.1.0-beta add group name colored
        if (!Common.IsFieldExist("dominion_group", "name_colored")) {
            TableColumn dominion_group_name_colored = new TableColumn("name_colored", FieldType.STRING, false, false, true, false, "'未命名'");
            new AddColumn(dominion_group_name_colored).table("dominion_group").ifNotExists().execute();
            String copy_sql = "UPDATE dominion_group SET name_colored = name;";
            DatabaseManager.instance.query(copy_sql);

            TableColumn player_name_using_group_title_id = new TableColumn("using_group_title_id", FieldType.INT, false, false, true, false, -1);
            new AddColumn(player_name_using_group_title_id).table("player_name").ifNotExists().execute();
        }

        // 2.3.0 change world name to world uid
        if (!Common.IsFieldExist("dominion", "world_uid")) {
            TableColumn dominion_world_uid = new TableColumn("world_uid", FieldType.STRING, false, false, true, false, "'00000000-0000-0000-0000-000000000000'");
            new AddColumn(dominion_world_uid).table("dominion").ifNotExists().execute();
            List<World> worlds = Dominion.instance.getServer().getWorlds();
            for (World world : worlds) {
                String sql = String.format("UPDATE dominion SET world_uid = '%s' WHERE world = '%s';", world.getUID().toString(), world.getName());
                DatabaseManager.instance.query(sql);
            }
            DatabaseManager.instance.query("UPDATE dominion SET world_uid = '00000000-0000-0000-0000-000000000000' WHERE world = 'all';");
            new RemoveColumn("world").table("dominion").IfExists().execute();
        }
    }
}
