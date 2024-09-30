package cn.lunadeer.dominion.controllers;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.PlayerDTO;
import cn.lunadeer.dominion.managers.Translation;
import cn.lunadeer.dominion.utils.Particle;
import cn.lunadeer.minecraftpluginutils.Notification;
import cn.lunadeer.minecraftpluginutils.VaultConnect.VaultConnect;
import cn.lunadeer.minecraftpluginutils.XLogger;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static cn.lunadeer.dominion.DominionNode.isInDominion;
import static cn.lunadeer.dominion.utils.ControllerUtils.getPlayerCurrentDominion;
import static cn.lunadeer.dominion.utils.ControllerUtils.notOwner;

public class DominionController {

    /**
     * 获取玩家拥有的领地
     *
     * @param owner 玩家
     * @return 领地列表
     */
    public static List<DominionDTO> all(Player owner) {
        return DominionDTO.selectByOwner(owner.getUniqueId());
    }

    public static List<DominionDTO> all() {
        return DominionDTO.selectAll();
    }

    /**
     * 创建领地
     *
     * @param operator 拥有者
     * @param name     领地名称
     * @param loc1     位置1
     * @param loc2     位置2
     */
    public static void create(AbstractOperator operator, String name, Location loc1, Location loc2) {
        DominionDTO parent = getPlayerCurrentDominion(operator);
        if (parent == null) {
            create(operator, name, loc1, loc2, "");
        } else {
            create(operator, name, loc1, loc2, parent.getName());
        }
    }

    /**
     * 创建子领地
     *
     * @param operator             拥有者
     * @param name                 领地名称
     * @param loc1                 位置1
     * @param loc2                 位置2
     * @param parent_dominion_name 父领地名称
     */
    public static void create(AbstractOperator operator, String name,
                              Location loc1, Location loc2,
                              String parent_dominion_name) {
        create(operator, name, loc1, loc2, parent_dominion_name, false);
    }

    /**
     * 创建子领地
     *
     * @param operator             拥有者
     * @param name                 领地名称
     * @param loc1                 位置1
     * @param loc2                 位置2
     * @param parent_dominion_name 父领地名称(留空表示为根领地)
     * @param skipEco              是否跳过经济检查
     */
    public static void create(AbstractOperator operator, String name,
                              Location loc1, Location loc2,
                              @NotNull String parent_dominion_name, boolean skipEco) {
        AbstractOperator.Result FAIL = new AbstractOperator.Result(AbstractOperator.Result.FAILURE, Translation.Messages_CreateDominionFailed);
        AbstractOperator.Result SUCCESS = new AbstractOperator.Result(AbstractOperator.Result.SUCCESS, Translation.Messages_CreateDominionSuccess, name);
        if (name.isEmpty()) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_DominionNameShouldNotEmpty));
            return;
        }
        if (name.contains(" ") || name.contains(".")) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_DominionNameInvalid));
            return;
        }
        if (DominionDTO.select(name) != null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_DominionNameExist, name));
            return;
        }
        if (!loc1.getWorld().getUID().equals(loc2.getWorld().getUID())) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_SelectPointsWorldNotSame));
            return;
        }
        if (operator.getLocation() == null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_CommandPlayerOnly));
            return;
        }
        if (!loc1.getWorld().getUID().equals(operator.getLocation().getWorld().getUID())) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_CrossWorldOperationDisallowed));
            return;
        }
        // 检查世界是否可以创建
        if (worldNotValid(operator, loc1.getWorld().getName())) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_CreateDominionDisabledWorld, loc1.getWorld().getName()));
            return;
        }
        // 检查领地数量是否达到上限
        if (amountNotValid(operator)) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_DominionAmountLimit, Dominion.config.getLimitAmount(operator.getPlayer())));
            return;
        }
        int minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        int maxX = Math.max(loc1.getBlockX(), loc2.getBlockX()) + 1;
        int maxY = Math.max(loc1.getBlockY(), loc2.getBlockY()) + 1;
        int maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ()) + 1;
        // 检查领地大小是否合法
        if (sizeNotValid(operator, minX, minY, minZ, maxX, maxY, maxZ)) {
            return;
        }
        DominionDTO parent_dominion;
        if (parent_dominion_name.isEmpty() || parent_dominion_name.equals("root")) {
            parent_dominion = DominionDTO.select(-1);
        } else {
            parent_dominion = DominionDTO.select(parent_dominion_name);
        }
        if (parent_dominion == null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_ParentDominionNotExist, parent_dominion_name));
            if (parent_dominion_name.isEmpty()) {
                XLogger.err(Translation.Messages_RootDominionLost);
            }
            return;
        }
        // 是否是父领地的拥有者
        if (parent_dominion.getId() != -1) {
            if (notOwner(operator, parent_dominion)) {
                operator.setResponse(FAIL.addMessage(Translation.Messages_NotParentDominionOwner, parent_dominion_name));
                return;
            }
        }
        // 创建 dominion (此步骤不会写入数据)
        DominionDTO dominion = DominionDTO.create(parent_dominion.getId() == -1 ? operator.getUniqueId() : parent_dominion.getOwner(), name, loc1.getWorld(),
                minX, minY, minZ, maxX, maxY, maxZ, parent_dominion);
        // 如果parent_dominion不为-1 检查是否在同一世界
        if (parent_dominion.getId() != -1 && !parent_dominion.getWorldUid().equals(dominion.getWorldUid())) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_ParentDominionNotInSameWorld));
            return;
        }
        // 检查深度是否达到上限
        if (depthNotValid(operator, parent_dominion)) {
            return;
        }
        // 检查是否超出父领地范围
        if (!isContained(dominion, parent_dominion)) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_OutOfParentDominionRange, parent_dominion.getName()));
            return;
        }
        // 获取此领地的所有同级领地
        List<DominionDTO> sub_dominions = DominionDTO.selectByParentId(dominion.getWorldUid(), parent_dominion.getId());
        // 检查是否与出生点保护冲突
        if (isIntersectSpawn(operator, dominion)) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_ConflictWithSpawnProtect));
            return;
        }
        // 检查是否与其他子领地冲突
        for (DominionDTO sub_dominion : sub_dominions) {
            if (isIntersect(sub_dominion, dominion)) {
                operator.setResponse(FAIL.addMessage(Translation.Messages_ConflictWithDominion, sub_dominion.getName()));
                return;
            }
        }
        // 检查经济
        if (!skipEco) {
            if (handleEconomyFailed(operator, Dominion.config.getEconomyOnlyXZ(operator.getPlayer()) ? dominion.getSquare() : dominion.getVolume(), true, FAIL, SUCCESS)) {
                return;
            }
        }
        dominion = DominionDTO.insert(dominion);
        if (dominion == null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_DatabaseError));
            return;
        }
        // 显示粒子效果
        handleParticle(operator, dominion);
        operator.setResponse(SUCCESS);
    }

    private static boolean isIntersectSpawn(AbstractOperator operator, DominionDTO dominion) {
        if (operator.isOp() && Dominion.config.getLimitOpBypass()) {
            return false;
        }
        int radius = Dominion.config.getSpawnProtection();
        if (radius == -1) {
            return false;
        }
        World world = dominion.getWorld();
        if (world == null) {
            return false;
        }
        Location spawn = world.getSpawnLocation();
        return isIntersect(dominion, spawn.getBlockX() - radius, spawn.getBlockY() - radius, spawn.getBlockZ() - radius
                , spawn.getBlockX() + radius, spawn.getBlockY() + radius, spawn.getBlockZ() + radius);
    }

    private static boolean isIntersectSpawn(AbstractOperator operator, @NotNull World world, int[] cords) {
        if (operator.isOp() && Dominion.config.getLimitOpBypass()) {
            return false;
        }
        int radius = Dominion.config.getSpawnProtection();
        if (radius == -1) {
            return false;
        }
        Location spawn = world.getSpawnLocation();
        return isIntersect(cords, spawn.getBlockX() - radius, spawn.getBlockY() - radius, spawn.getBlockZ() - radius
                , spawn.getBlockX() + radius, spawn.getBlockY() + radius, spawn.getBlockZ() + radius);
    }

    /**
     * 向一个方向扩展领地
     * 会尝试对操作者当前所在的领地进行操作，当操作者不在一个领地内或者在子领地内时
     * 需要手动指定要操作的领地名称
     *
     * @param operator 操作者
     * @param size     扩展的大小
     */
    public static void expand(AbstractOperator operator, Integer size) {
        DominionDTO dominion = getPlayerCurrentDominion(operator);
        if (dominion == null) {
            operator.setResponse(new AbstractOperator.Result(AbstractOperator.Result.FAILURE, Translation.Messages_CannotGetDominionAuto));
            return;
        }
        expand(operator, size, dominion.getName());
    }

    /**
     * 向一个方向扩展领地
     *
     * @param operator      操作者
     * @param size          扩展的大小
     * @param dominion_name 领地名称
     */
    public static void expand(AbstractOperator operator, Integer size, String dominion_name) {
        AbstractOperator.Result FAIL = new AbstractOperator.Result(AbstractOperator.Result.FAILURE, Translation.Messages_ExpandDominionFailed);
        DominionDTO dominion = expandContractPreCheck(operator, getExistDomAndIsOwner(operator, dominion_name), FAIL);
        if (dominion == null) {
            return;
        }
        int[] newCords = expandContractSizeChange(operator, dominion, true, size, FAIL);
        if (newCords == null) {
            return;
        }
        // 检查是否与出生点保护冲突
        World world = Dominion.instance.getServer().getWorld(dominion.getWorldUid());
        if (world == null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_DominionWorldLost));
            return;
        }
        if (isIntersectSpawn(operator, world, newCords)) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_ConflictWithSpawnProtect));
            return;
        }
        // 校验是否超出父领地范围
        DominionDTO parent_dominion = DominionDTO.select(dominion.getParentDomId());
        if (parent_dominion == null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_ParentDominionLost));
            return;
        }
        if (!isContained(newCords, parent_dominion)) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_OutOfParentDominionRange, parent_dominion.getName()));
            return;
        }
        // 获取同世界下的所有同级领地
        List<DominionDTO> exist_dominions = DominionDTO.selectByParentId(dominion.getWorldUid(), dominion.getParentDomId());
        for (DominionDTO exist_dominion : exist_dominions) {
            if (isIntersect(exist_dominion, newCords)) {
                // 如果是自己，跳过
                if (exist_dominion.getId().equals(dominion.getId())) continue;
                operator.setResponse(FAIL.addMessage(Translation.Messages_ConflictWithDominion, exist_dominion.getName()));
                return;
            }
        }
        AbstractOperator.Result SUCCESS = new AbstractOperator.Result(AbstractOperator.Result.SUCCESS, Translation.Messages_ExpandDominionSuccess, dominion_name, size);
        // 检查经济
        if (handleEconomyFailed(operator, Dominion.config.getEconomyOnlyXZ(operator.getPlayer()) ? sqr(newCords) - dominion.getSquare() : vol(newCords) - dominion.getVolume()
                , true, FAIL, SUCCESS)) return;
        // 显示粒子效果
        dominion = dominion.setXYZ(newCords);
        handleParticle(operator, dominion);
        operator.setResponse(SUCCESS);
    }

    /**
     * 缩小领地
     * 会尝试对操作者当前所在的领地进行操作，当操作者不在一个领地内或者在子领地内时
     * 需要手动指定要操作的领地名称
     *
     * @param operator 操作者
     * @param size     缩小的大小
     */
    public static void contract(AbstractOperator operator, Integer size) {
        DominionDTO dominion = getPlayerCurrentDominion(operator);
        if (dominion == null) {
            operator.setResponse(new AbstractOperator.Result(AbstractOperator.Result.FAILURE, Translation.Messages_CannotGetDominionAuto));
            return;
        }
        contract(operator, size, dominion.getName());
    }

    /**
     * 缩小领地
     *
     * @param operator      操作者
     * @param size          缩小的大小
     * @param dominion_name 领地名称
     */
    public static void contract(AbstractOperator operator, Integer size, String dominion_name) {
        AbstractOperator.Result FAIL = new AbstractOperator.Result(AbstractOperator.Result.FAILURE, Translation.Messages_ContractDominionFailed);
        DominionDTO dominion = expandContractPreCheck(operator, getExistDomAndIsOwner(operator, dominion_name), FAIL);
        if (dominion == null) {
            return;
        }
        int[] newCords = expandContractSizeChange(operator, dominion, false, size, FAIL);
        if (newCords == null) {
            return;
        }
        // 获取所有的子领地
        List<DominionDTO> sub_dominions = DominionDTO.selectByParentId(dominion.getWorldUid(), dominion.getId());
        for (DominionDTO sub_dominion : sub_dominions) {
            if (!isContained(sub_dominion, newCords)) {
                operator.setResponse(FAIL.addMessage(Translation.Messages_ContractDominionConflict, sub_dominion.getName()));
                return;
            }
        }
        AbstractOperator.Result SUCCESS = new AbstractOperator.Result(AbstractOperator.Result.SUCCESS, Translation.Messages_ContractDominionSuccess, dominion_name, size);
        // 退还经济
        if (handleEconomyFailed(operator, Dominion.config.getEconomyOnlyXZ(operator.getPlayer()) ? dominion.getSquare() - sqr(newCords) : dominion.getVolume() - vol(newCords)
                , false, FAIL, SUCCESS)) return;
        // 显示粒子效果
        dominion = dominion.setXYZ(newCords);
        handleParticle(operator, dominion);
        operator.setResponse(SUCCESS);
    }

    private static int vol(int x1, int y1, int z1, int x2, int y2, int z2) {
        return (x2 - x1) * (y2 - y1) * (z2 - z1);
    }

    private static int vol(int[] cords) {
        return vol(cords[0], cords[1], cords[2], cords[3], cords[4], cords[5]);
    }

    private static int sqr(int x1, int z1, int x2, int z2) {
        return (x2 - x1) * (z2 - z1);
    }

    private static int sqr(int[] cords) {
        return sqr(cords[0], cords[2], cords[3], cords[5]);
    }

    /**
     * 删除领地 会同时删除其所有子领地
     *
     * @param operator      操作者
     * @param dominion_name 领地名称
     * @param force         是否强制删除
     */
    public static void delete(AbstractOperator operator, String dominion_name, boolean force) {
        AbstractOperator.Result FAIL = new AbstractOperator.Result(AbstractOperator.Result.FAILURE, Translation.Messages_DeleteDominionFailed);
        AbstractOperator.Result SUCCESS = new AbstractOperator.Result(AbstractOperator.Result.SUCCESS, Translation.Messages_DeleteDominionSuccess, dominion_name);
        DominionDTO dominion = getExistDomAndIsOwner(operator, dominion_name);
        if (dominion == null) {
            return;
        }
        List<DominionDTO> sub_dominions = getSubDominionsRecursive(dominion);
        if (!force) {
            AbstractOperator.Result WARNING = new AbstractOperator.Result(AbstractOperator.Result.WARNING, Translation.Messages_DeleteDominionConfirm, dominion_name);
            showSubNamesWarning(sub_dominions, WARNING);
            if (operator instanceof BukkitPlayerOperator) {
                Notification.warn(operator.getPlayer(), Translation.Messages_DeleteDominionForceConfirm, dominion_name);
            }
            operator.setResponse(WARNING);
            return;
        }
        DominionDTO.delete(dominion);
        // 退还经济
        int count;
        if (Dominion.config.getEconomyOnlyXZ(operator.getPlayer())) {
            count = dominion.getSquare();
            for (DominionDTO sub_dominion : sub_dominions) {
                count += sub_dominion.getSquare();
            }
        } else {
            count = dominion.getVolume();
            for (DominionDTO sub_dominion : sub_dominions) {
                count += sub_dominion.getVolume();
            }
        }
        if (handleEconomyFailed(operator, count, false, FAIL, SUCCESS)) return;
        operator.setResponse(SUCCESS);
    }

    /**
     * 设置领地的进入消息
     *
     * @param operator 操作者
     * @param message  消息
     */
    public static void setJoinMessage(AbstractOperator operator, String message) {
        DominionDTO dominion = getPlayerCurrentDominion(operator);
        if (dominion == null) {
            return;
        }
        setJoinMessage(operator, dominion.getName(), message);
    }

    /**
     * 设置进入领地的消息
     *
     * @param operator      操作者
     * @param dominion_name 领地名称
     * @param message       消息
     */
    public static void setJoinMessage(AbstractOperator operator, String message, String dominion_name) {
        DominionDTO dominion = getExistDomAndIsOwner(operator, dominion_name);
        if (dominion == null) {
            return;
        }
        dominion.setJoinMessage(message);
        operator.setResponse(new AbstractOperator.Result(AbstractOperator.Result.SUCCESS, Translation.Messages_SetEnterMessageSuccess, dominion_name));
    }

    /**
     * 设置领地的离开消息
     *
     * @param operator 操作者
     * @param message  消息
     */
    public static void setLeaveMessage(AbstractOperator operator, String message) {
        DominionDTO dominion = getPlayerCurrentDominion(operator);
        if (dominion == null) {
            return;
        }
        setLeaveMessage(operator, dominion.getName(), message);
    }

    /**
     * 设置离开领地的消息
     *
     * @param operator      操作者
     * @param dominion_name 领地名称
     * @param message       消息
     */
    public static void setLeaveMessage(AbstractOperator operator, String message, String dominion_name) {
        DominionDTO dominion = getExistDomAndIsOwner(operator, dominion_name);
        if (dominion == null) {
            return;
        }
        dominion.setLeaveMessage(message);
        operator.setResponse(new AbstractOperator.Result(AbstractOperator.Result.SUCCESS, Translation.Messages_SetLeaveMessageSuccess, dominion_name));
    }

    /**
     * 设置领地的传送点
     *
     * @param operator 操作者
     */
    public static void setTpLocation(AbstractOperator operator, int x, int y, int z) {
        DominionDTO dominion = getPlayerCurrentDominion(operator);
        if (dominion == null) {
            return;
        }
        setTpLocation(operator, x, y, z, dominion.getName());
    }

    /**
     * 设置领地的传送点
     *
     * @param operator      操作者
     * @param dominion_name 领地名称
     */
    public static void setTpLocation(AbstractOperator operator, int x, int y, int z, String dominion_name) {
        AbstractOperator.Result FAIL = new AbstractOperator.Result(AbstractOperator.Result.FAILURE, Translation.Messages_SetTpLocationFailed);
        DominionDTO dominion = getExistDomAndIsOwner(operator, dominion_name);
        if (dominion == null) {
            operator.setResponse(new AbstractOperator.Result(AbstractOperator.Result.FAILURE, Translation.Messages_DominionNotExist, dominion_name));
            return;
        }
        World world = dominion.getWorld();
        if (world == null) {
            operator.setResponse(new AbstractOperator.Result(AbstractOperator.Result.FAILURE, Translation.Messages_DominionWorldNotExist));
            return;
        }
        Location loc = new Location(world, x, y, z);
        // 检查是否在领地内
        if (isInDominion(dominion, loc)) {
            loc.setY(loc.getY() + 1.5);
            dominion.setTpLocation(loc);
            operator.setResponse(new AbstractOperator.Result(AbstractOperator.Result.SUCCESS,
                    Translation.Messages_SetTpLocationSuccess, dominion_name
                    , loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
        } else {
            operator.setResponse(FAIL.addMessage(Translation.Messages_TpLocationNotInDominion, dominion_name));
        }
    }

    /**
     * 重命名领地
     *
     * @param operator 操作者
     * @param old_name 旧名称
     * @param new_name 新名称
     */
    public static void rename(AbstractOperator operator, String old_name, String new_name) {
        AbstractOperator.Result FAIL = new AbstractOperator.Result(AbstractOperator.Result.FAILURE, Translation.Messages_RenameDominionFailed);
        if (new_name.isEmpty()) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_DominionNameShouldNotEmpty));
            return;
        }
        if (new_name.contains(" ") || new_name.contains(".")) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_DominionNameInvalid));
            return;
        }
        if (Objects.equals(old_name, new_name)) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_RenameDominionSameName));
            return;
        }
        DominionDTO dominion = getExistDomAndIsOwner(operator, old_name);
        if (dominion == null) {
            return;
        }
        if (DominionDTO.select(new_name) != null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_DominionNameExist, new_name));
            return;
        }
        dominion.setName(new_name);
        operator.setResponse(new AbstractOperator.Result(AbstractOperator.Result.SUCCESS, Translation.Messages_RenameDominionSuccess, old_name, new_name));
    }

    /**
     * 转让领地
     *
     * @param operator    操作者
     * @param dom_name    领地名称
     * @param player_name 玩家名称
     * @param force       是否强制转让
     */
    public static void give(AbstractOperator operator, String dom_name, String player_name, boolean force) {
        AbstractOperator.Result FAIL = new AbstractOperator.Result(AbstractOperator.Result.FAILURE, Translation.Messages_GiveDominionFailed);
        DominionDTO dominion = getExistDomAndIsOwner(operator, dom_name);
        if (dominion == null) {
            return;
        }
        PlayerDTO player = PlayerController.getPlayerDTO(player_name);
        if (player == null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_PlayerNotExist, player_name));
            return;
        }
        if (Objects.equals(dominion.getOwner(), player.getUuid())) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_DominionAlreadyBelong, dom_name, player_name));
            return;
        }
        if (dominion.getParentDomId() != -1) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_SubDominionCannotGive, player_name, dom_name));
            return;
        }
        List<DominionDTO> sub_dominions = getSubDominionsRecursive(dominion);
        if (!force) {
            AbstractOperator.Result WARNING = new AbstractOperator.Result(AbstractOperator.Result.WARNING, Translation.Messages_GiveDominionConfirm, dom_name, player_name);
            showSubNamesWarning(sub_dominions, WARNING);
            if (operator instanceof BukkitPlayerOperator) {
                Notification.warn(operator.getPlayer(), Translation.Messages_GiveDominionForceConfirm, dom_name, player_name);
            }
            operator.setResponse(WARNING);
            return;
        }
        dominion.setOwner(player.getUuid());
        for (DominionDTO sub_dominion : sub_dominions) {
            sub_dominion.setOwner(player.getUuid());
        }
        operator.setResponse(new AbstractOperator.Result(AbstractOperator.Result.SUCCESS, Translation.Messages_GiveDominionSuccess, dom_name, player_name));
    }

    /**
     * 设置领地的卫星地图地块颜色
     *
     * @param operator 操作者
     * @param color    16进制颜色 例如 #ff0000
     * @param dom_name 领地名称
     */
    public static void setMapColor(AbstractOperator operator, String color, String dom_name) {
        AbstractOperator.Result FAIL = new AbstractOperator.Result(AbstractOperator.Result.FAILURE, Translation.Messages_SetMapColorFailed);
        DominionDTO dominion = getExistDomAndIsOwner(operator, dom_name);
        if (dominion == null) {
            return;
        }
        color = color.toUpperCase();    // 转换为大写
        if (!color.matches("^#[0-9a-fA-F]{6}$")) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_MapColorInvalid));
            return;
        }
        dominion.setColor(color);
        operator.setResponse(new AbstractOperator.Result(AbstractOperator.Result.SUCCESS, Translation.Messages_SetMapColorSuccess, dom_name, color));
    }

    /**
     * 设置领地的卫星地图地块颜色
     *
     * @param operator 操作者
     * @param color    16进制颜色 例如 #ff0000
     */
    public static void setMapColor(AbstractOperator operator, String color) {
        AbstractOperator.Result FAIL = new AbstractOperator.Result(AbstractOperator.Result.FAILURE, Translation.Messages_SetMapColorFailed);
        DominionDTO dominion = getPlayerCurrentDominion(operator);
        if (dominion == null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_CannotGetDominionAuto));
            return;
        }
        setMapColor(operator, color, dominion.getName());
    }

    /**
     * 判断两个领地是否相交
     */
    private static boolean isIntersect(DominionDTO a, DominionDTO b) {
        return isIntersect(a, b.getX1(), b.getY1(), b.getZ1(), b.getX2(), b.getY2(), b.getZ2());
    }

    private static boolean isIntersect(DominionDTO a, Integer x1, Integer y1, Integer z1, Integer x2, Integer y2, Integer z2) {
        return a.getX1() < x2 && a.getX2() > x1 &&
                a.getY1() < y2 && a.getY2() > y1 &&
                a.getZ1() < z2 && a.getZ2() > z1;
    }

    private static boolean isIntersect(DominionDTO a, int[] cord) {
        return isIntersect(a, cord[0], cord[1], cord[2], cord[3], cord[4], cord[5]);
    }

    private static boolean isIntersect(int[] cord, Integer x1, Integer y1, Integer z1, Integer x2, Integer y2, Integer z2) {
        return cord[0] < x2 && cord[3] > x1 &&
                cord[1] < y2 && cord[4] > y1 &&
                cord[2] < z2 && cord[5] > z1;
    }

    /**
     * 判断 sub 是否完全被 parent 包裹
     */
    private static boolean isContained(DominionDTO sub, DominionDTO parent) {
        if (parent.getId() == -1) {
            return true;
        }
        return isContained(sub.getX1(), sub.getY1(), sub.getZ1(), sub.getX2(), sub.getY2(), sub.getZ2(), parent.getX1(), parent.getY1(), parent.getZ1(), parent.getX2(), parent.getY2(), parent.getZ2());
    }

    private static boolean isContained(int[] cords, DominionDTO parent) {
        return isContained(cords[0], cords[1], cords[2], cords[3], cords[4], cords[5], parent);
    }

    private static boolean isContained(DominionDTO sub, int[] cords) {
        return isContained(sub, cords[0], cords[1], cords[2], cords[3], cords[4], cords[5]);
    }

    private static boolean isContained(Integer x1, Integer y1, Integer z1, Integer x2, Integer y2, Integer z2, DominionDTO parent) {
        if (parent.getId() == -1) {
            return true;
        }
        return isContained(x1, y1, z1, x2, y2, z2, parent.getX1(), parent.getY1(), parent.getZ1(), parent.getX2(), parent.getY2(), parent.getZ2());
    }

    private static boolean isContained(DominionDTO sub, Integer x1, Integer y1, Integer z1, Integer x2, Integer y2, Integer z2) {
        return isContained(sub.getX1(), sub.getY1(), sub.getZ1(), sub.getX2(), sub.getY2(), sub.getZ2(), x1, y1, z1, x2, y2, z2);
    }

    private static boolean isContained(int sub_x1, int sub_y1, int sub_z1, int sub_x2, int sub_y2, int sub_z2, int parent_x1, int parent_y1, int parent_z1, int parent_x2, int parent_y2, int parent_z2) {
        return sub_x1 >= parent_x1 && sub_x2 <= parent_x2 &&
                sub_y1 >= parent_y1 && sub_y2 <= parent_y2 &&
                sub_z1 >= parent_z1 && sub_z2 <= parent_z2;
    }

    private static List<DominionDTO> getSubDominionsRecursive(DominionDTO dominion) {
        List<DominionDTO> sub_dominions = DominionDTO.selectByParentId(dominion.getWorldUid(), dominion.getId());
        List<DominionDTO> sub_sub_dominions = new ArrayList<>();
        for (DominionDTO sub_dominion : sub_dominions) {
            sub_sub_dominions.addAll(getSubDominionsRecursive(sub_dominion));
        }
        sub_dominions.addAll(sub_sub_dominions);
        return sub_dominions;
    }

    private static boolean sizeNotValid(AbstractOperator operator, int[] cords) {
        return sizeNotValid(operator, cords[0], cords[1], cords[2], cords[3], cords[4], cords[5]);
    }

    private static boolean sizeNotValid(AbstractOperator operator, int x1, int y1, int z1, int x2, int y2, int z2) {
        AbstractOperator.Result FAIL = new AbstractOperator.Result(AbstractOperator.Result.FAILURE, Translation.Messages_SizeInvalid);
        if (operator.isOp() && Dominion.config.getLimitOpBypass()) {
            return false;
        }
        // 如果 1 > 2 则交换
        if (x1 > x2) {
            int temp = x1;
            x1 = x2;
            x2 = temp;
        }
        if (y1 > y2) {
            int temp = y1;
            y1 = y2;
            y2 = temp;
        }
        if (z1 > z2) {
            int temp = z1;
            z1 = z2;
            z2 = temp;
        }
        int x_length = x2 - x1;
        int y_length = y2 - y1;
        int z_length = z2 - z1;
        if (x_length < Dominion.config.getLimitSizeMinX(operator.getPlayer())) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_SizeXShouldBeGreaterThan, Dominion.config.getLimitSizeMinX(operator.getPlayer())));
            return true;
        }
        if (y_length < Dominion.config.getLimitSizeMinY(operator.getPlayer())) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_SizeYShouldBeGreaterThan, Dominion.config.getLimitSizeMinY(operator.getPlayer())));
            return true;
        }
        if (z_length < Dominion.config.getLimitSizeMinZ(operator.getPlayer())) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_SizeZShouldBeGreaterThan, Dominion.config.getLimitSizeMinZ(operator.getPlayer())));
            return true;
        }
        if (x_length > Dominion.config.getLimitSizeMaxX(operator.getPlayer()) && Dominion.config.getLimitSizeMaxX(operator.getPlayer()) > 0) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_SizeXShouldBeLessThan, Dominion.config.getLimitSizeMaxX(operator.getPlayer())));
            return true;
        }
        if (y_length > Dominion.config.getLimitSizeMaxY(operator.getPlayer()) && Dominion.config.getLimitSizeMaxY(operator.getPlayer()) > 0) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_SizeYShouldBeLessThan, Dominion.config.getLimitSizeMaxY(operator.getPlayer())));
            return true;
        }
        if (z_length > Dominion.config.getLimitSizeMaxZ(operator.getPlayer()) && Dominion.config.getLimitSizeMaxZ(operator.getPlayer()) > 0) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_SizeZShouldBeLessThan, Dominion.config.getLimitSizeMaxZ(operator.getPlayer())));
            return true;
        }
        if (y2 > Dominion.config.getLimitMaxY(operator.getPlayer())) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_MaxYShouldBeLessThan, Dominion.config.getLimitMaxY(operator.getPlayer())));
            return true;
        }
        if (y1 < Dominion.config.getLimitMinY(operator.getPlayer())) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_MinYShouldBeLessThan, Dominion.config.getLimitMinY(operator.getPlayer())));
            return true;
        }
        return false;
    }

    private static boolean depthNotValid(AbstractOperator operator, DominionDTO parent_dom) {
        AbstractOperator.Result FAIL = new AbstractOperator.Result(AbstractOperator.Result.FAILURE, Translation.Messages_DepthInvalid);
        if (operator.isOp() && Dominion.config.getLimitOpBypass()) {
            return false;
        }
        if (Dominion.config.getLimitDepth(operator.getPlayer()) == -1) {
            return false;
        }
        if (parent_dom.getId() != -1 && Dominion.config.getLimitDepth(operator.getPlayer()) == 0) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_CreateSubDominionDisabled));
            return true;
        }
        if (parent_dom.getId() == -1) {
            return false;
        }
        int level = 0;
        while (parent_dom.getParentDomId() != -1) {
            parent_dom = Cache.instance.getDominion(parent_dom.getParentDomId());
            level++;
        }
        if (level >= Dominion.config.getLimitDepth(operator.getPlayer())) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_DepthShouldBeLessThan, Dominion.config.getLimitDepth(operator.getPlayer())));
            return true;
        }
        return false;
    }

    private static boolean amountNotValid(AbstractOperator operator) {
        if (operator.isOp() && Dominion.config.getLimitOpBypass()) {
            return false;
        }
        return Cache.instance.getPlayerDominionCount(operator.getUniqueId()) >= Dominion.config.getLimitAmount(operator.getPlayer()) && Dominion.config.getLimitAmount(operator.getPlayer()) != -1;
    }

    private static boolean worldNotValid(AbstractOperator operator, String worldName) {
        if (operator.isOp() && Dominion.config.getLimitOpBypass()) {
            return false;
        }
        return Dominion.config.getWorldBlackList(operator.getPlayer()).contains(worldName);
    }

    private static DominionDTO getExistDomAndIsOwner(AbstractOperator operator, String dominion_name) {
        AbstractOperator.Result FAIL = new AbstractOperator.Result(AbstractOperator.Result.FAILURE, "");
        DominionDTO dominion = DominionDTO.select(dominion_name);
        if (dominion == null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_DominionNotExist, dominion_name));
            return null;
        }
        if (notOwner(operator, dominion)) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_NotDominionOwner, dominion_name));
            return null;
        }
        return dominion;
    }

    /**
     * 处理经济系统
     *
     * @param operator 操作者
     * @param count    数量
     * @param paid     操作类型 true 为扣费 false 为退费
     * @param FAIL     失败消息
     * @param SUCCESS  成功消息
     */
    private static boolean handleEconomyFailed(AbstractOperator operator, Integer count, boolean paid, AbstractOperator.Result FAIL, AbstractOperator.Result SUCCESS) {
        if (Dominion.config.getEconomyEnable()) {
            if (!VaultConnect.instance.economyAvailable()) {
                operator.setResponse(FAIL.addMessage(Translation.Messages_NoEconomyPlugin));
                return true;
            }
            if (operator.isOp() && Dominion.config.getLimitOpBypass()) {
                SUCCESS.addMessage(Translation.Messages_OpBypassEconomyCheck);
                return false;
            }
            float priceOrRefund = count * Dominion.config.getEconomyPrice(operator.getPlayer());
            if (paid) {
                if (VaultConnect.instance.getBalance(operator.getPlayer()) < priceOrRefund) {
                    operator.setResponse(FAIL.addMessage(Translation.Messages_NotEnoughMoney, priceOrRefund, VaultConnect.instance.currencyNamePlural()));
                    return true;
                }
                SUCCESS.addMessage(Translation.Messages_ChargeMoney, priceOrRefund, VaultConnect.instance.currencyNamePlural());
                VaultConnect.instance.withdrawPlayer(operator.getPlayer(), priceOrRefund);
            } else {
                float refund = priceOrRefund * Dominion.config.getEconomyRefund(operator.getPlayer());
                VaultConnect.instance.depositPlayer(operator.getPlayer(), refund);
                SUCCESS.addMessage(Translation.Messages_RefundMoney, refund, VaultConnect.instance.currencyNamePlural());
            }
        }
        return false;
    }

    /**
     * 显示粒子效果
     *
     * @param operator 操作者
     * @param dominion 领地
     */
    private static void handleParticle(AbstractOperator operator, DominionDTO dominion) {
        if (operator instanceof BukkitPlayerOperator) {
            Particle.showBorder(operator.getPlayer(), dominion);
        }
    }

    private static @Nullable DominionDTO expandContractPreCheck(AbstractOperator operator, @Nullable DominionDTO dominion, AbstractOperator.Result FAIL) {
        if (dominion == null) {
            return null;
        }
        if (operator.getLocation() == null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_CannotGetLocation));
            return null;
        }
        if (!operator.getLocation().getWorld().getUID().equals(dominion.getWorldUid())) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_CrossWorldOperationDisallowed));
            return null;
        }
        if (!isInDominion(dominion, operator.getLocation())) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_NotInDominion, dominion.getName()));
            return null;
        }
        return dominion;
    }

    private static int[] expandContractSizeChange(AbstractOperator operator, @NotNull DominionDTO dominion, boolean expand, int size, AbstractOperator.Result FAIL) {
        BlockFace face = operator.getDirection();
        if (face == null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_CannotGetDirection));
            return null;
        }
        int[] result = new int[6];
        result[0] = dominion.getX1();
        result[1] = dominion.getY1();
        result[2] = dominion.getZ1();
        result[3] = dominion.getX2();
        result[4] = dominion.getY2();
        result[5] = dominion.getZ2();
        if (!expand) {
            size = size * -1;
        }
        switch (face) {
            case NORTH:
                result[2] -= size;
                break;
            case SOUTH:
                result[5] += size;
                break;
            case WEST:
                result[0] -= size;
                break;
            case EAST:
                result[3] += size;
                break;
            case UP:
                result[4] += size;
                break;
            case DOWN:
                result[1] -= size;
                break;
            default:
                operator.setResponse(FAIL.addMessage(Translation.Messages_InvalidDirection, face));
                return null;
        }
        if (!expand) {
            // 校验第二组坐标是否小于第一组坐标
            if (result[0] > result[3] || result[1] > result[4] || result[2] > result[5]) {
                operator.setResponse(FAIL.addMessage(Translation.Messages_ContractSizeInvalid));
                return null;
            }
        }
        if (sizeNotValid(operator, result)) {
            return null;
        }
        return result;
    }


    /**
     * 以警告形式打印所有子领地名称
     *
     * @param sub_dominions 子领地列表
     * @param WARNING       警告消息
     */
    public static void showSubNamesWarning(List<DominionDTO> sub_dominions, AbstractOperator.Result WARNING) {
        String sub_names = "";
        for (DominionDTO sub_dominion : sub_dominions) {
            sub_names = sub_dominion.getName() + ", ";
        }
        if (!sub_dominions.isEmpty()) {
            sub_names = sub_names.substring(0, sub_names.length() - 2);
            WARNING.addMessage(Translation.Messages_SubDominionList, sub_names);
        }
    }

}
