package cn.lunadeer.dominion.handler;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.api.AbstractOperator;
import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.controllers.BukkitPlayerOperator;
import cn.lunadeer.dominion.events.dominion.DominionCreateEvent;
import cn.lunadeer.dominion.events.dominion.DominionDeleteEvent;
import cn.lunadeer.dominion.events.dominion.modify.*;
import cn.lunadeer.dominion.managers.Translation;
import cn.lunadeer.dominion.utils.Particle;
import cn.lunadeer.dominion.utils.VaultConnect.VaultConnect;
import cn.lunadeer.minecraftpluginutils.XLogger;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static cn.lunadeer.dominion.DominionNode.isInDominion;

public class DominionEventHandler implements Listener {

    public DominionEventHandler(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    // ========== DominionCreateEvent START
    @EventHandler(priority = EventPriority.HIGH)
    public void onDominionCreateEvent(DominionCreateEvent event) {
        int x1 = Math.min(event.getLoc1().getBlockX(), event.getLoc2().getBlockX());
        int y1 = Math.min(event.getLoc1().getBlockY(), event.getLoc2().getBlockY());
        int z1 = Math.min(event.getLoc1().getBlockZ(), event.getLoc2().getBlockZ());
        int x2 = Math.max(event.getLoc1().getBlockX(), event.getLoc2().getBlockX());
        int y2 = Math.max(event.getLoc1().getBlockY(), event.getLoc2().getBlockY());
        int z2 = Math.max(event.getLoc1().getBlockZ(), event.getLoc2().getBlockZ());
        cn.lunadeer.dominion.dtos.DominionDTO toBeCreated = cn.lunadeer.dominion.dtos.DominionDTO.create(event.getOwner(), event.getName(), event.getLoc1().getWorld(),
                x1, y1, z1, x2, y2, z2,
                event.getParent());
        event.getOperator().addResultHeader(AbstractOperator.ResultType.SUCCESS, Translation.Messages_CreateDominionSuccess, toBeCreated.getName());
        event.getOperator().addResultHeader(AbstractOperator.ResultType.FAILURE, Translation.Messages_CreateDominionFailed);
        // name check
        if (nameNotValid(event.getOperator(), toBeCreated.getName())) {
            event.setCancelledAdnComplete(true);
        }
        // world check
        if (worldNotValid(event.getOperator(), Objects.requireNonNull(toBeCreated.getWorld()).getName())) {
            event.setCancelled(true, AbstractOperator.ResultType.FAILURE, Translation.Messages_CreateDominionDisabledWorld, toBeCreated.getWorld().getName());
        }
        // amount check
        if (amountNotValid(event.getOperator())) {
            event.setCancelled(true, AbstractOperator.ResultType.FAILURE, Translation.Messages_DominionAmountLimit, Dominion.config.getLimitAmount(event.getOperator().getPlayer()));
        }
        // size check
        if (sizeNotValid(event.getOperator(),
                toBeCreated.getX1(), toBeCreated.getY1(), toBeCreated.getZ1(),
                toBeCreated.getX2(), toBeCreated.getY2(), toBeCreated.getZ2())) {
            event.setCancelled(true, AbstractOperator.ResultType.FAILURE, Translation.Messages_SizeInvalid);
        }
        // parent check
        if (parentNotValid(event.getOperator(), toBeCreated)) {
            event.setCancelledAdnComplete(true);
        }
        // intersect check
        if (intersectWithOther(event.getOperator(), toBeCreated)) {
            event.setCancelledAdnComplete(true);
        }
        // handle economy
        if (!event.isCancelled() && !event.isSkipEconomy()) {
            if (handleEconomyFailed(event.getOperator(),
                    Dominion.config.getEconomyOnlyXZ(event.getOperator().getPlayer()) ? toBeCreated.getSquare() : toBeCreated.getVolume(),
                    true)) {
                event.setCancelledAdnComplete(true);
            }
        }
        // do db insert
        if (!event.isCancelled()) {
            DominionDTO inserted = cn.lunadeer.dominion.dtos.DominionDTO.insert(toBeCreated);
            if (inserted != null) {
                event.setDominion(inserted);
                if (event.getOperator().getPlayer() != null) {
                    Particle.showBorder(event.getOperator().getPlayer(), inserted);
                }
            } else {
                event.setCancelled(true, AbstractOperator.ResultType.FAILURE, Translation.Messages_DatabaseError);
            }
        }
        // complete with result
        event.getOperator().completeResult();
    }
    // ========== DominionCreateEvent END

    // ========== DominionSizeChangeEvent START
    @EventHandler(priority = EventPriority.HIGH)
    public void onDominionSizeChangeEvent(DominionSizeChangeEvent event) {
        DominionDTO dominion = event.getDominionBefore();
        if (event.getType() == DominionSizeChangeEvent.SizeChangeType.EXPAND) {
            event.getOperator().addResultHeader(AbstractOperator.ResultType.SUCCESS, Translation.Messages_ExpandDominionSuccess, dominion.getName(), event.getSize());
            event.getOperator().addResultHeader(AbstractOperator.ResultType.FAILURE, Translation.Messages_ExpandDominionFailed);
        } else {
            event.getOperator().addResultHeader(AbstractOperator.ResultType.SUCCESS, Translation.Messages_ContractDominionSuccess, dominion.getName(), event.getSize());
            event.getOperator().addResultHeader(AbstractOperator.ResultType.FAILURE, Translation.Messages_ContractDominionFailed);
        }
        if (notOwner(event.getOperator(), dominion)) {
            event.setCancelled(true, AbstractOperator.ResultType.FAILURE, Translation.Messages_NotDominionOwner, dominion.getName());
            return;
        }
        DominionDTO tempDominion = constractSizeChangeDominionDTO(event.getOperator(), dominion, event.getType(), event.getSize(), event.getDirection());
        if (tempDominion == null) {
            event.setCancelledAdnComplete(true);
            return;
        }
        if (event.getType() == DominionSizeChangeEvent.SizeChangeType.EXPAND) {
            // parent check
            if (parentNotValid(event.getOperator(), tempDominion)) {
                event.setCancelledAdnComplete(true);
            }
            // intersect check
            if (intersectWithOther(event.getOperator(), tempDominion)) {
                event.setCancelledAdnComplete(true);
            }
        } else {
            // child check
            List<cn.lunadeer.dominion.dtos.DominionDTO> sub_dominions = cn.lunadeer.dominion.dtos.DominionDTO.selectByParentId(dominion.getWorldUid(), dominion.getId());
            for (cn.lunadeer.dominion.dtos.DominionDTO sub_dominion : sub_dominions) {
                if (!isContained(sub_dominion.getX1(), sub_dominion.getY1(), sub_dominion.getZ1(), sub_dominion.getX2(), sub_dominion.getY2(), sub_dominion.getZ2(),
                        tempDominion.getX1(), tempDominion.getY1(), tempDominion.getZ1(), tempDominion.getX2(), tempDominion.getY2(), tempDominion.getZ2())) {
                    event.setCancelled(true, AbstractOperator.ResultType.FAILURE, Translation.Messages_ContractDominionConflict, sub_dominion.getName());
                }
            }
        }
        // handle economy
        if (!event.isCancelled() && !event.isSkipEconomy()) {
            if (handleEconomyFailed(event.getOperator(),
                    Dominion.config.getEconomyOnlyXZ(event.getOperator().getPlayer()) ? tempDominion.getSquare() : tempDominion.getVolume(),
                    event.getType() == DominionSizeChangeEvent.SizeChangeType.EXPAND)) {
                event.setCancelledAdnComplete(true);
            }
        }
        // do db update
        if (!event.isCancelled()) {
            DominionDTO after = event.getDominionBefore().setXYZ(
                    tempDominion.getX1(), tempDominion.getY1(), tempDominion.getZ1(),
                    tempDominion.getX2(), tempDominion.getY2(), tempDominion.getZ2()
            );
            event.setDominionAfter(after);
            if (after != null) {
                if (event.getOperator().getPlayer() != null) {
                    Particle.showBorder(event.getOperator().getPlayer(), after);
                }
            } else {
                event.setCancelled(true, AbstractOperator.ResultType.FAILURE, Translation.Messages_DatabaseError);
            }
        }
        // complete with result
        event.getOperator().completeResult();
    }
    // ========== DominionSizeChangeEvent END

    // ========== DominionDeleteEvent START
    @EventHandler(priority = EventPriority.HIGH)
    public void onDominionDeleteEvent(DominionDeleteEvent event) {
        DominionDTO dominion = event.getDominion();
        event.getOperator().addResultHeader(AbstractOperator.ResultType.FAILURE, Translation.Messages_DeleteDominionFailed);
        // check owner
        if (notOwner(event.getOperator(), dominion)) {
            event.setCancelled(true, AbstractOperator.ResultType.FAILURE, Translation.Messages_NotDominionOwner, dominion.getName());
            return;
        }
        // check subs
        List<DominionDTO> sub_dominions = getSubDominionsRecursive(dominion);
        if (!event.isForce()) {
            event.setCancelled(true);
            event.getOperator().addResult(AbstractOperator.ResultType.WARNING, Translation.Messages_DeleteDominionConfirm, dominion.getName());
            if (!sub_dominions.isEmpty())
                event.getOperator().addResult(AbstractOperator.ResultType.WARNING, Translation.Messages_SubDominionList, sub_dominions.stream().map(DominionDTO::getName).toArray());
            event.getOperator().addResult(AbstractOperator.ResultType.WARNING, Translation.Messages_DeleteDominionForceConfirm, dominion.getName());
            event.getOperator().completeResult();
            return;
        }
        event.getOperator().addResultHeader(AbstractOperator.ResultType.SUCCESS, Translation.Messages_DeleteDominionSuccess, dominion.getName());
        // do db delete
        if (!event.isCancelled()) {
            cn.lunadeer.dominion.dtos.DominionDTO.deleteById(dominion.getId()); // do db delete
            int count;
            if (Dominion.config.getEconomyOnlyXZ(event.getOperator().getPlayer())) {
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
            if (!event.isSkipEconomy() && handleEconomyFailed(event.getOperator(), count, false)) {
                event.setCancelledAdnComplete(true);
            }
        }
        // complete with result
        event.getOperator().completeResult();
    }
    // ========== DominionDeleteEvent END

    // ========== DominionRenameEvent START
    @EventHandler(priority = EventPriority.HIGH)
    public void onDominionRenameEvent(DominionRenameEvent event) {
        DominionDTO dominion = event.getDominionBefore();
        event.getOperator().addResultHeader(AbstractOperator.ResultType.SUCCESS, Translation.Messages_RenameDominionSuccess, dominion.getName(), event.getNewName());
        event.getOperator().addResultHeader(AbstractOperator.ResultType.FAILURE, Translation.Messages_RenameDominionFailed);
        // check owner
        if (notOwner(event.getOperator(), dominion)) {
            event.setCancelled(true, AbstractOperator.ResultType.FAILURE, Translation.Messages_NotDominionOwner, dominion.getName());
            return;
        }
        // name check
        if (nameNotValid(event.getOperator(), event.getNewName())) {
            event.setCancelledAdnComplete(true);
        }
        // same name check
        if (Objects.equals(dominion.getName(), event.getNewName())) {
            event.setCancelled(true, AbstractOperator.ResultType.FAILURE, Translation.Messages_RenameDominionSameName);
        }
        // name exist check
        if (cn.lunadeer.dominion.dtos.DominionDTO.select(event.getNewName()) != null) {
            event.setCancelled(true, AbstractOperator.ResultType.FAILURE, Translation.Messages_DominionNameExist, event.getNewName());
        }
        // do db update
        if (!event.isCancelled()) {
            DominionDTO renamed = dominion.setName(event.getNewName());
            event.setDominionAfter(renamed);
            if (renamed == null) {
                event.setCancelled(true, AbstractOperator.ResultType.FAILURE, Translation.Messages_DatabaseError);
            }
        }
        event.getOperator().completeResult();
    }
    // ========== DominionRenameEvent END

    // ========== DominionTransferEvent START
    @EventHandler(priority = EventPriority.HIGH)
    public void onDominionTransferEvent(DominionTransferEvent event) {
        DominionDTO dominion = event.getDominionBefore();
        String newOwnerName = event.getNewOwner().getLastKnownName();
        event.getOperator().addResultHeader(AbstractOperator.ResultType.FAILURE, Translation.Messages_GiveDominionFailed);
        // check owner
        if (notOwner(event.getOperator(), dominion)) {
            event.setCancelled(true, AbstractOperator.ResultType.FAILURE, Translation.Messages_NotDominionOwner, dominion.getName());
            return;
        }
        // check same owner
        if (event.getNewOwner().getUuid().equals(event.getOldOwner().getUuid())) {
            event.setCancelled(true, AbstractOperator.ResultType.FAILURE, Translation.Messages_DominionAlreadyBelong, dominion.getName(), newOwnerName);
        }
        // check subs
        if (dominion.getParentDomId() != -1) {
            event.setCancelled(true, AbstractOperator.ResultType.FAILURE, Translation.Messages_SubDominionCannotGive, newOwnerName, dominion.getName());
        }
        // check amount
        Player newOwner = Dominion.instance.getServer().getPlayer(event.getNewOwner().getUuid());
        if (newOwner == null) {
            event.setCancelled(true, AbstractOperator.ResultType.FAILURE, Translation.Messages_PlayerNotOnline, newOwnerName);
            return;
        }
        if (amountNotValid(newOwner)) {
            event.setCancelled(true, AbstractOperator.ResultType.FAILURE, Translation.Messages_DominionAmountLimit, Dominion.config.getLimitAmount(event.getOperator().getPlayer()));
        }
        // check subs
        List<DominionDTO> sub_dominions = getSubDominionsRecursive(dominion);
        if (!event.isForce()) {
            event.setCancelled(true);
            event.getOperator().addResult(AbstractOperator.ResultType.WARNING, Translation.Messages_GiveDominionConfirm, dominion.getName(), newOwnerName);
            if (!sub_dominions.isEmpty())
                event.getOperator().addResult(AbstractOperator.ResultType.WARNING, Translation.Messages_SubDominionList, sub_dominions.stream().map(DominionDTO::getName).toArray());
            event.getOperator().addResult(AbstractOperator.ResultType.WARNING, Translation.Messages_GiveDominionForceConfirm, dominion.getName(), newOwnerName);
            event.getOperator().completeResult();
            return;
        }
        event.getOperator().addResultHeader(AbstractOperator.ResultType.SUCCESS, Translation.Messages_GiveDominionSuccess, dominion.getName(), newOwnerName);
        // do db update
        if (!event.isCancelled()) {
            DominionDTO transfer = dominion.setOwner(event.getNewOwner().getUuid());
            if (transfer != null) {
                event.setDominionAfter(transfer);
                for (DominionDTO sub_dominion : sub_dominions) {
                    sub_dominion.setOwner(event.getNewOwner().getUuid());
                }
            } else {
                event.setCancelled(true, AbstractOperator.ResultType.FAILURE, Translation.Messages_DatabaseError);
            }
        }
        // complete with result
        event.getOperator().completeResult();
    }
    // ========== DominionTransferEvent END

    // ========== DominionSetTpLocationEvent START
    @EventHandler(priority = EventPriority.HIGH)
    public void onDominionSetTpLocationEvent(DominionSetTpLocationEvent event) {
        DominionDTO dominion = event.getDominionBefore();
        event.getOperator().addResultHeader(AbstractOperator.ResultType.SUCCESS, Translation.Messages_SetTpLocationSuccess, dominion.getName(),
                event.getNewTpLocation().getBlockX(), event.getNewTpLocation().getBlockY(), event.getNewTpLocation().getBlockZ()
        );
        event.getOperator().addResultHeader(AbstractOperator.ResultType.FAILURE, Translation.Messages_SetTpLocationFailed);
        // check owner
        if (notOwner(event.getOperator(), dominion)) {
            event.setCancelled(true, AbstractOperator.ResultType.FAILURE, Translation.Messages_NotDominionOwner, dominion.getName());
            return;
        }
        // check tp location in dominion
        if (!isInDominion(event.getDominionBefore(), event.getNewTpLocation())) {
            event.setCancelled(true, AbstractOperator.ResultType.FAILURE, Translation.Messages_TpLocationNotInDominion, dominion.getName());
        }
        // do db update
        if (!event.isCancelled()) {
            Location loc = event.getNewTpLocation();
            loc.setY(loc.getY() + 1.5);
            DominionDTO modified = dominion.setTpLocation(loc);
            event.setDominionAfter(modified);
            if (modified == null) {
                event.setCancelled(true, AbstractOperator.ResultType.FAILURE, Translation.Messages_DatabaseError);
            }
        }
        // complete with result
        event.getOperator().completeResult();
    }
    // ========== DominionSetTpLocationEvent END

    // ========== DominionSetMessageEvent START
    @EventHandler(priority = EventPriority.HIGH)
    public void onDominionSetMessageEvent(DominionSetMessageEvent event) {
        DominionDTO dominion = event.getDominionBefore();
        if (event.getType() == DominionSetMessageEvent.MessageChangeType.ENTER) {
            event.getOperator().addResultHeader(AbstractOperator.ResultType.SUCCESS, Translation.Messages_SetEnterMessageSuccess, dominion.getName());
            event.getOperator().addResultHeader(AbstractOperator.ResultType.FAILURE, Translation.Messages_SetEnterMessageFailed);
        } else {
            event.getOperator().addResultHeader(AbstractOperator.ResultType.SUCCESS, Translation.Messages_SetLeaveMessageSuccess, dominion.getName());
            event.getOperator().addResultHeader(AbstractOperator.ResultType.FAILURE, Translation.Messages_SetLeaveMessageFailed);
        }
        // check owner
        if (notOwner(event.getOperator(), dominion)) {
            event.setCancelled(true, AbstractOperator.ResultType.FAILURE, Translation.Messages_NotDominionOwner, dominion.getName());
            return;
        }
        // do db update
        if (!event.isCancelled()) {
            DominionDTO modified;
            if (event.getType() == DominionSetMessageEvent.MessageChangeType.ENTER) {
                modified = dominion.setJoinMessage(event.getNewMessage());
            } else {
                modified = dominion.setLeaveMessage(event.getNewMessage());
            }
            event.setDominionAfter(modified);
            if (modified == null) {
                event.setCancelled(true, AbstractOperator.ResultType.FAILURE, Translation.Messages_DatabaseError);
            }
        }
        // complete with result
        event.getOperator().completeResult();
    }
    // ========== DominionSetMessageEvent END


    // ========== DominionSetMapColorEvent START
    @EventHandler(priority = EventPriority.HIGH)
    public void onDominionSetMapColorEvent(DominionSetMapColorEvent event) {
        DominionDTO dominion = event.getDominionBefore();
        event.getOperator().addResultHeader(AbstractOperator.ResultType.SUCCESS, Translation.Messages_SetMapColorSuccess, dominion.getName(), event.getNewColor());
        event.getOperator().addResultHeader(AbstractOperator.ResultType.FAILURE, Translation.Messages_SetMapColorFailed);
        // check owner
        if (notOwner(event.getOperator(), dominion)) {
            event.setCancelled(true, AbstractOperator.ResultType.FAILURE, Translation.Messages_NotDominionOwner, dominion.getName());
        }
        // do db update
        if (!event.isCancelled()) {
            DominionDTO modified = dominion.setColor(event.getNewColor());
            event.setDominionAfter(modified);
            if (modified == null) {
                event.setCancelled(true, AbstractOperator.ResultType.FAILURE, Translation.Messages_DatabaseError);
            }
        }
        // complete with result
        event.getOperator().completeResult();
    }
    // ========== DominionSetMapColorEvent END

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

    private static boolean amountNotValid(Player operator) {
        return amountNotValid(BukkitPlayerOperator.create(operator));
    }

    private static boolean amountNotValid(AbstractOperator operator) {
        if (operator.isOp() && Dominion.config.getLimitOpBypass()) {
            return false;
        }
        if (Dominion.config.getLimitAmount(operator.getPlayer()) == -1) {
            return false;
        }
        XLogger.debug("Dominion.config.getLimitAmount(operator.getPlayer(): %d", Dominion.config.getLimitAmount(operator.getPlayer()));
        XLogger.debug("Cache.instance.getPlayerDominionCount(operator.getUniqueId()): %d", Cache.instance.getPlayerDominionCount(operator.getUniqueId()));
        return Cache.instance.getPlayerDominionCount(operator.getUniqueId()) >= Dominion.config.getLimitAmount(operator.getPlayer());
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

    /**
     * 检查是否与同级的领地相交 检查是否和出生点保护冲突
     *
     * @param operator 操作者
     * @param dominion 领地
     * @return 是否相交
     */
    private static boolean intersectWithOther(AbstractOperator operator, DominionDTO dominion) {
        // sub dominion intersect check
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
            operator.addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_DominionWorldLost);
            return true;
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

    /**
     * 构造 size change 事件的领地 DTO 同时校验是否合法
     *
     * @param operator 操作者
     * @param dominion 领地
     * @param type     类型
     * @param size     大小
     * @param face     方向
     * @return 领地 DTO
     */
    private static DominionDTO constractSizeChangeDominionDTO(AbstractOperator operator, @NotNull DominionDTO dominion, DominionSizeChangeEvent.SizeChangeType type, int size, BlockFace face) {
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
            operator.addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_SizeInvalid);
            return null;
        }
        return cn.lunadeer.dominion.dtos.DominionDTO.create(
                dominion.getOwner(),
                dominion.getName(),
                Objects.requireNonNull(dominion.getWorld()),
                result[0], result[1], result[2],
                result[3], result[4], result[5],
                cn.lunadeer.dominion.dtos.DominionDTO.select(dominion.getParentDomId())
        ).setId(dominion.getId());
    }

    private static List<DominionDTO> getSubDominionsRecursive(DominionDTO dominion) {
        List<DominionDTO> res = new ArrayList<>();
        List<cn.lunadeer.dominion.dtos.DominionDTO> sub_dominions = cn.lunadeer.dominion.dtos.DominionDTO.selectByParentId(dominion.getWorldUid(), dominion.getId());
        for (DominionDTO sub_dominion : sub_dominions) {
            res.add(sub_dominion);
            res.addAll(getSubDominionsRecursive(sub_dominion));
        }
        return res;
    }

}
