package cn.lunadeer.dominion.doos;

import cn.lunadeer.dominion.api.dtos.PlayerDTO;
import cn.lunadeer.dominion.cache.CacheManager;
import cn.lunadeer.dominion.utils.databse.FIelds.Field;
import cn.lunadeer.dominion.utils.databse.FIelds.FieldInteger;
import cn.lunadeer.dominion.utils.databse.FIelds.FieldString;
import cn.lunadeer.dominion.utils.databse.FIelds.FieldTimestamp;
import cn.lunadeer.dominion.utils.databse.syntax.Delete;
import cn.lunadeer.dominion.utils.databse.syntax.Insert;
import cn.lunadeer.dominion.utils.databse.syntax.Select;
import cn.lunadeer.dominion.utils.databse.syntax.Update;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayerDOO implements PlayerDTO {

    private final FieldInteger id = new FieldInteger("id");
    private final FieldString uuid = new FieldString("uuid");
    private final FieldString lastKnownName = new FieldString("last_known_name");
    private final FieldTimestamp lastJoinAt = new FieldTimestamp("last_join_at");
    private final FieldInteger using_group_title_id = new FieldInteger("using_group_title_id");

    private static Field<?>[] fields() {
        return new Field<?>[]{
                new FieldInteger("id"),
                new FieldString("uuid"),
                new FieldString("last_known_name"),
                new FieldTimestamp("last_join_at"),
                new FieldInteger("using_group_title_id")
        };
    }

    private static PlayerDOO parse(Map<String, Field<?>> map) {
        return new PlayerDOO(
                (Integer) map.get("id").getValue(),
                UUID.fromString((String) map.get("uuid").getValue()),
                (String) map.get("last_known_name").getValue(),
                ((Timestamp) (map.get("last_join_at").getValue())).toLocalDateTime(),
                (Integer) map.get("using_group_title_id").getValue()
        );
    }

    public static List<PlayerDTO> all() throws SQLException {
        List<Map<String, Field<?>>> res = Select.select(fields())
                .from("player_name")
                .where("id > 0")
                .execute();
        return res.stream().map(PlayerDOO::parse).collect(Collectors.toList());
    }

    public static PlayerDOO selectById(Integer id) throws SQLException {
        List<Map<String, Field<?>>> res = Select.select(fields())
                .from("player_name")
                .where("id = ?", id)
                .execute();
        if (res.isEmpty()) return null;
        return parse(res.get(0));
    }

    public static void delete(PlayerDOO player) throws SQLException {
        Delete.delete().from("player_name").where("id = ?", player.getId()).execute();
        CacheManager.instance.getPlayerCache().delete(player.getId());
    }

    public static PlayerDOO create(Player player) throws SQLException {
        return create(player.getUniqueId(), player.getName());
    }

    public static PlayerDOO create(UUID playerUid, String playerName) throws SQLException {
        FieldString uuid = new FieldString("uuid", playerUid.toString());
        FieldString lastKnownName = new FieldString("last_known_name", playerName);
        FieldTimestamp lastJoinAt = new FieldTimestamp("last_join_at", Timestamp.valueOf(LocalDateTime.now()));
        Map<String, Field<?>> res = Insert.insert().into("player_name")
                .values(uuid, lastKnownName, lastJoinAt)
                .returning(fields())
                .onConflict(uuid.getName()).doUpdate()
                .execute();
        if (res.isEmpty()) {
            throw new SQLException("Create player failed");
        }
        PlayerDOO player = parse(res);
        CacheManager.instance.getPlayerCache().load(player.getId());
        return player;
    }

    private PlayerDOO(Integer id, UUID uuid, String lastKnownName, LocalDateTime lastJoinAt, Integer using_group_title_id) {
        this.id.setValue(id);
        this.uuid.setValue(uuid.toString());
        this.lastKnownName.setValue(lastKnownName);
        this.lastJoinAt.setValue(Timestamp.valueOf(lastJoinAt));
        this.using_group_title_id.setValue(using_group_title_id);
    }

    @Override
    public Integer getId() {
        return id.getValue();
    }

    @Override
    public UUID getUuid() {
        return UUID.fromString(uuid.getValue());
    }

    @Override
    public String getLastKnownName() {
        return lastKnownName.getValue();
    }

    @Override
    public PlayerDTO updateLastKnownName(String name) throws SQLException {
        this.setLastKnownName(name);
        this.setLastJoinAt(LocalDateTime.now());
        Update.update("player_name")
                .set(this.lastKnownName, this.lastJoinAt)
                .where("uuid = ?", this.getUuid().toString())
                .execute();
        CacheManager.instance.getPlayerCache().load(this.getId());
        return this;
    }

    public Long getLastJoinAt() {
        return lastJoinAt.getValue().getTime();
    }

    public void setId(Integer id) {
        this.id.setValue(id);
    }

    public void setUuid(UUID uuid) {
        this.uuid.setValue(uuid.toString());
    }

    public void setLastKnownName(String lastKnownName) {
        this.lastKnownName.setValue(lastKnownName);
    }

    public void setLastJoinAt(LocalDateTime lastJoinAt) {
        this.lastJoinAt.setValue(Timestamp.valueOf(lastJoinAt));
    }

    @Override
    public Integer getUsingGroupTitleID() {
        return using_group_title_id.getValue();
    }

    public void setUsingGroupTitleID(Integer usingGroupTitleID) throws SQLException {
        this.using_group_title_id.setValue(usingGroupTitleID);
        Update.update("player_name")
                .set(this.using_group_title_id)
                .where("id = ?", this.getId())
                .execute();
    }
}
