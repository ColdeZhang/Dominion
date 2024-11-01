package cn.lunadeer.dominion.handler;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.api.AbstractOperator;
import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.events.DominionCreateEvent;
import cn.lunadeer.dominion.events.DominionSizeChangeEvent;
import cn.lunadeer.dominion.managers.Translation;
import cn.lunadeer.dominion.utils.Particle;
import cn.lunadeer.dominion.utils.VaultConnect.VaultConnect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class DominionEventHandler implements Listener {

    public DominionEventHandler(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDominionCreateEventPreCheck(DominionCreateEvent event) {
        DominionDTO dominion = event.getDominion();
        // name check
        if (nameNotValid(event.getOperator(), dominion.getName())) {
            event.setCancelled(true);
        }
        // world check
        if (worldNotValid(event.getOperator(), Objects.requireNonNull(dominion.getWorld()).getName())) {
            event.getOperator().addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_CreateDominionDisabledWorld, dominion.getWorld().getName());
            event.setCancelled(true);
        }
        // amount check
        if (amountNotValid(event.getOperator())) {
            event.getOperator().addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_DominionAmountLimit, Dominion.config.getLimitAmount(event.getOperator().getPlayer()));
            event.setCancelled(true);
        }
        // size check
        if (sizeNotValid(event.getOperator(),
                dominion.getX1(), dominion.getY1(), dominion.getZ1(),
                dominion.getX2(), dominion.getY2(), dominion.getZ2())) {
            event.setCancelled(true);
        }
        // parent check
        if (parentNotValid(event.getOperator(), dominion)) {
            event.setCancelled(true);
        }
        // intersect check
        if (intersectWithOther(event.getOperator(), dominion)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onDominionCreateEventPostProcess(DominionCreateEvent event) {
        if (!event.isCancelled() && !event.isSkipEconomy()) {
            if (handleEconomyFailed(event.getOperator(),
                    Dominion.config.getEconomyOnlyXZ(event.getOperator().getPlayer()) ? event.getDominion().getSquare() : event.getDominion().getVolume(),
                    true)) {
                event.setCancelled(true);
            }
        }
        if (!event.isCancelled()) {
            DominionDTO inserted = cn.lunadeer.dominion.dtos.DominionDTO.insert((cn.lunadeer.dominion.dtos.DominionDTO) event.getDominion());
            if (inserted != null) {
                event.setDominion(inserted);
                event.getOperator().addResult(AbstractOperator.ResultType.SUCCESS, Translation.Messages_CreateDominionSuccess, event.getDominion().getName());
                if (event.getOperator().getPlayer() != null) {
                    Particle.showBorder(event.getOperator().getPlayer(), event.getDominion());
                }
            } else {
                event.getOperator().addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_CreateDominionFailed);
                event.setCancelled(true);
            }
        } else {
            event.getOperator().addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_CreateDominionFailed);
            event.setCancelled(true);
        }
        event.getOperator().completeResult();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDominionSizeChangeEventPreCheck(DominionSizeChangeEvent event) {
        DominionDTO dominion = event.getBefore();
        DominionDTO tempDominion = expandContractSizeChange(event.getOperator(), dominion, event.getType(), event.getSize(), event.getFace());
        if (tempDominion == null) {
            event.setCancelled(true);
            return;
        }
        if (event.getType() == DominionSizeChangeEvent.SizeChangeType.EXPAND) {
            // parent check
            if (parentNotValid(event.getOperator(), tempDominion)) {
                event.setCancelled(true);
            }
            // intersect check
            if (intersectWithOther(event.getOperator(), tempDominion)) {
                event.setCancelled(true);
            }
        } else {
            // child check
            List<cn.lunadeer.dominion.dtos.DominionDTO> sub_dominions = cn.lunadeer.dominion.dtos.DominionDTO.selectByParentId(dominion.getWorldUid(), dominion.getId());
            for (cn.lunadeer.dominion.dtos.DominionDTO sub_dominion : sub_dominions) {
                if (!isContained(sub_dominion.getX1(), sub_dominion.getY1(), sub_dominion.getZ1(), sub_dominion.getX2(), sub_dominion.getY2(), sub_dominion.getZ2(),
                        tempDominion.getX1(), tempDominion.getY1(), tempDominion.getZ1(), tempDominion.getX2(), tempDominion.getY2(), tempDominion.getZ2())) {
                    event.getOperator().addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_ContractDominionConflict, sub_dominion.getName());
                    event.setCancelled(true);
                }
            }
        }
        event.setAfter(tempDominion);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onDominionSizeChangeEventPostProcess(DominionSizeChangeEvent event) {
        if (!event.isCancelled() && !event.isSkipEconomy()) {
            if (handleEconomyFailed(event.getOperator(),
                    Dominion.config.getEconomyOnlyXZ(event.getOperator().getPlayer()) ? event.getAfter().getSquare() : event.getAfter().getVolume(),
                    event.getType() == DominionSizeChangeEvent.SizeChangeType.EXPAND)) {
                event.setCancelled(true);
            }
        }
        if (!event.isCancelled()) {
            DominionDTO after = event.getBefore().setXYZ(event.getAfter().getX1(), event.getAfter().getY1(), event.getAfter().getZ1(),
                    event.getAfter().getX2(), event.getAfter().getY2(), event.getAfter().getZ2());
            if (after != null) {
                event.getOperator().addResult(AbstractOperator.ResultType.SUCCESS, Translation.Messages_ExpandDominionSuccess, event.getBefore().getName(), event.getSize());
                if (event.getOperator().getPlayer() != null) {
                    Particle.showBorder(event.getOperator().getPlayer(), after);
                }
            } else {
                event.getOperator().addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_ExpandDominionFailed);
                event.setCancelled(true);
            }
        } else {
            event.getOperator().addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_ExpandDominionFailed);
            event.setCancelled(true);
        }
        event.getOperator().completeResult();
    }

    // ============================================================
    // ============================================================
    // ============================================================

    /**
     * 检查领地名称是否合法
     *
     * @param operator 操作者
     * @param name     领地名称
     * @return 是否合法
     */
    private static boolean nameNotValid(AbstractOperator operator, String name) {
        if (name.isEmpty()) {
            operator.addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_DominionNameShouldNotEmpty);
            return true;
        }
        if (name.contains(" ") || name.contains(".")) {
            operator.addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_DominionNameInvalid);
            return true;
        }
        if (cn.lunadeer.dominion.dtos.DominionDTO.select(name) != null) {
            operator.addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_DominionNameExist, name);
            return true;
        }
        return false;
    }

    /**
     * 检查世界是否合法
     *
     * @param operator  操作者
     * @param worldName 世界名称
     * @return 是否合法
     */
    private static boolean worldNotValid(AbstractOperator operator, String worldName) {
        if (operator.isOp() && Dominion.config.getLimitOpBypass()) {
            return false;
        }
        return Dominion.config.getWorldBlackList(operator.getPlayer()).contains(worldName);
    }

    private static boolean amountNotValid(AbstractOperator operator) {
        if (operator.isOp() && Dominion.config.getLimitOpBypass()) {
            return false;
        }
        return Cache.instance.getPlayerDominionCount(operator.getUniqueId()) >= Dominion.config.getLimitAmount(operator.getPlayer()) && Dominion.config.getLimitAmount(operator.getPlayer()) != -1;
    }

    /**
     * 检查领地大小是否合法
     *
     * @param operator 操作者
     * @param x1       x1
     * @param y1       y1
     * @param z1       z1
     * @param x2       x2
     * @param y2       y2
     * @param z2       z2
     * @return 是否合法
     */
    private static boolean sizeNotValid(AbstractOperator operator, int x1, int y1, int z1, int x2, int y2, int z2) {
        operator.addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_SizeInvalid);
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
            operator.addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_SizeXShouldBeGreaterThan, Dominion.config.getLimitSizeMinX(operator.getPlayer()));
            return true;
        }
        if (y_length < Dominion.config.getLimitSizeMinY(operator.getPlayer())) {
            operator.addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_SizeYShouldBeGreaterThan, Dominion.config.getLimitSizeMinY(operator.getPlayer()));
            return true;
        }
        if (z_length < Dominion.config.getLimitSizeMinZ(operator.getPlayer())) {
            operator.addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_SizeZShouldBeGreaterThan, Dominion.config.getLimitSizeMinZ(operator.getPlayer()));
            return true;
        }
        if (x_length > Dominion.config.getLimitSizeMaxX(operator.getPlayer()) && Dominion.config.getLimitSizeMaxX(operator.getPlayer()) > 0) {
            operator.addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_SizeXShouldBeLessThan, Dominion.config.getLimitSizeMaxX(operator.getPlayer()));
            return true;
        }
        if (y_length > Dominion.config.getLimitSizeMaxY(operator.getPlayer()) && Dominion.config.getLimitSizeMaxY(operator.getPlayer()) > 0) {
            operator.addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_SizeYShouldBeLessThan, Dominion.config.getLimitSizeMaxY(operator.getPlayer()));
            return true;
        }
        if (z_length > Dominion.config.getLimitSizeMaxZ(operator.getPlayer()) && Dominion.config.getLimitSizeMaxZ(operator.getPlayer()) > 0) {
            operator.addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_SizeZShouldBeLessThan, Dominion.config.getLimitSizeMaxZ(operator.getPlayer()));
            return true;
        }
        if (y2 > Dominion.config.getLimitMaxY(operator.getPlayer())) {
            operator.addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_MaxYShouldBeLessThan, Dominion.config.getLimitMaxY(operator.getPlayer()));
            return true;
        }
        if (y1 < Dominion.config.getLimitMinY(operator.getPlayer())) {
            operator.addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_MinYShouldBeLessThan, Dominion.config.getLimitMinY(operator.getPlayer()));
            return true;
        }
        return false;
    }

    /**
     * 检查是否不是领地所有者
     *
     * @param player   操作者
     * @param dominion 领地
     * @return 是否不是领地所有者
     */
    private static boolean notOwner(AbstractOperator player, DominionDTO dominion) {
        if (player.isOp() && Dominion.config.getLimitOpBypass()) return false;
        return !dominion.getOwner().equals(player.getUniqueId());
    }

    /**
     * 检查父领地是否合法(是否存在、是否在同一世界、深度是否合法)
     *
     * @param player   操作者
     * @param dominion 领地
     * @return 是否合法
     */
    private static boolean parentNotValid(AbstractOperator player, DominionDTO dominion) {
        if (dominion.getParentDomId() == -1) {
            return false;
        }
        DominionDTO parent = Cache.instance.getDominion(dominion.getParentDomId());
        if (parent == null) {
            player.addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_RootDominionLost);
            return true;
        }
        if (notOwner(player, parent)) {
            player.addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_NotParentDominionOwner, parent.getName());
            return true;
        }
        if (!parent.getWorldUid().equals(dominion.getWorldUid())) {
            player.addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_ParentDominionNotInSameWorld);
            return true;
        }
        // 检查深度是否合法
        if (depthNotValid(player, parent)) {
            return true;
        }
        // 检查是否在父领地范围内
        if (!isContained(dominion.getX1(), dominion.getY1(), dominion.getZ1(), dominion.getX2(), dominion.getY2(), dominion.getZ2(),
                parent.getX1(), parent.getY1(), parent.getZ1(), parent.getX2(), parent.getY2(), parent.getZ2())) {
            player.addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_OutOfParentDominionRange, parent.getName());
            return true;
        }
        return false;
    }

    /**
     * 检查深度是否合法
     *
     * @param operator   操作者
     * @param parent_dom 父领地
     * @return 是否合法
     */
    private static boolean depthNotValid(AbstractOperator operator, DominionDTO parent_dom) {
        if (operator.isOp() && Dominion.config.getLimitOpBypass()) {
            return false;
        }
        int limitDepth = Dominion.config.getLimitDepth(operator.getPlayer());
        if (limitDepth == -1) {
            return false;
        }
        if (parent_dom.getId() != -1 && limitDepth == 0) {
            operator.addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_CreateSubDominionDisabled);
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
        if (level >= limitDepth) {
            operator.addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_DepthShouldBeLessThan, limitDepth);
            return true;
        }
        return false;
    }

    private static boolean intersectWithOther(AbstractOperator operator, DominionDTO dominion) {
        List<cn.lunadeer.dominion.dtos.DominionDTO> sub_dominions = cn.lunadeer.dominion.dtos.DominionDTO.selectByParentId(dominion.getWorldUid(), dominion.getParentDomId());
        for (cn.lunadeer.dominion.dtos.DominionDTO sub_dominion : sub_dominions) {
            if (sub_dominion.getName().equals(dominion.getName())) {
                continue;
            }
            if (isIntersect(dominion.getX1(), dominion.getY1(), dominion.getZ1(), dominion.getX2(), dominion.getY2(), dominion.getZ2(),
                    sub_dominion.getX1(), sub_dominion.getY1(), sub_dominion.getZ1(), sub_dominion.getX2(), sub_dominion.getY2(), sub_dominion.getZ2())) {
                operator.addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_ConflictWithDominion, sub_dominion.getName());
                return true;
            }
        }
        // spawn protection intersect check
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
        if (isIntersect(dominion.getX1(), dominion.getY1(), dominion.getZ1(), dominion.getX2(), dominion.getY2(), dominion.getZ2(),
                spawn.getBlockX() - radius, spawn.getBlockY() - radius, spawn.getBlockZ() - radius,
                spawn.getBlockX() + radius, spawn.getBlockY() + radius, spawn.getBlockZ() + radius)) {
            operator.addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_ConflictWithSpawnProtect);
            return true;
        }
        return false;
    }

    /**
     * 检查 sub 是否在 parent 内
     *
     * @return 是否在 parent 内
     */
    private static boolean isContained(int sub_x1, int sub_y1, int sub_z1, int sub_x2, int sub_y2, int sub_z2,
                                       int parent_x1, int parent_y1, int parent_z1, int parent_x2, int parent_y2, int parent_z2) {
        return sub_x1 >= parent_x1 && sub_x2 <= parent_x2 &&
                sub_y1 >= parent_y1 && sub_y2 <= parent_y2 &&
                sub_z1 >= parent_z1 && sub_z2 <= parent_z2;
    }

    /**
     * 检查 a 和 b 是否相交
     *
     * @return 是否相交
     */
    private static boolean isIntersect(int a_x1, int a_y1, int a_z1, int a_x2, int a_y2, int a_z2,
                                       int b_x1, int b_y1, int b_z1, int b_x2, int b_y2, int b_z2) {
        return a_x1 < b_x2 && a_x2 > b_x1 &&
                a_y1 < b_y2 && a_y2 > b_y1 &&
                a_z1 < b_z2 && a_z2 > b_z1;
    }

    /**
     * 处理经济系统
     *
     * @param operator 操作者
     * @param count    数量
     * @param paid     操作类型 true 为扣费 false 为退费
     * @return 是否处理失败
     */
    private static boolean handleEconomyFailed(AbstractOperator operator, Integer count, boolean paid) {
        if (Dominion.config.getEconomyEnable()) {
            if (!VaultConnect.instance.economyAvailable()) {
                operator.addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_NoEconomyPlugin);
                return true;
            }
            if (operator.isOp() && Dominion.config.getLimitOpBypass()) {
                operator.addResult(AbstractOperator.ResultType.SUCCESS, Translation.Messages_OpBypassEconomyCheck);
                return false;
            }
            float priceOrRefund = count * Dominion.config.getEconomyPrice(operator.getPlayer());
            if (paid) {
                if (VaultConnect.instance.getBalance(operator.getPlayer()) < priceOrRefund) {
                    operator.addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_NotEnoughMoney, priceOrRefund, VaultConnect.instance.currencyNamePlural());
                    return true;
                }
                operator.addResult(AbstractOperator.ResultType.SUCCESS, Translation.Messages_ChargeMoney, priceOrRefund, VaultConnect.instance.currencyNamePlural());
                VaultConnect.instance.withdrawPlayer(operator.getPlayer(), priceOrRefund);
            } else {
                float refund = priceOrRefund * Dominion.config.getEconomyRefund(operator.getPlayer());
                VaultConnect.instance.depositPlayer(operator.getPlayer(), refund);
                operator.addResult(AbstractOperator.ResultType.SUCCESS, Translation.Messages_RefundMoney, refund, VaultConnect.instance.currencyNamePlural());
            }
        }
        return false;
    }

    private static DominionDTO expandContractSizeChange(AbstractOperator operator, @NotNull DominionDTO dominion, DominionSizeChangeEvent.SizeChangeType type, int size, BlockFace face) {
        int[] result = new int[6];
        result[0] = dominion.getX1();
        result[1] = dominion.getY1();
        result[2] = dominion.getZ1();
        result[3] = dominion.getX2();
        result[4] = dominion.getY2();
        result[5] = dominion.getZ2();
        if (type == DominionSizeChangeEvent.SizeChangeType.CONTRACT) {
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
        }
        if (type == DominionSizeChangeEvent.SizeChangeType.CONTRACT) {
            // 校验第二组坐标是否小于第一组坐标
            if (result[0] > result[3] || result[1] > result[4] || result[2] > result[5]) {
                operator.addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_ContractSizeInvalid);
                return null;
            }
        }
        if (sizeNotValid(operator, result[0], result[1], result[2], result[3], result[4], result[5])) {
            return null;
        }
        return cn.lunadeer.dominion.dtos.DominionDTO.create(
                dominion.getOwner(),
                dominion.getName(),
                Objects.requireNonNull(dominion.getWorld()),
                result[0], result[1], result[2],
                result[3], result[4], result[5],
                cn.lunadeer.dominion.dtos.DominionDTO.select(dominion.getParentDomId())
        );
    }

}
