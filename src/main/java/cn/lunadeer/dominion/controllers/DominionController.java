package cn.lunadeer.dominion.controllers;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.PlayerDTO;
import cn.lunadeer.dominion.utils.Time;
import cn.lunadeer.minecraftpluginutils.ParticleRender;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static cn.lunadeer.dominion.controllers.Apis.*;

public class DominionController {

    public static List<DominionDTO> all(Player owner) {
        return DominionDTO.selectAll(owner.getUniqueId());
    }

    public static List<DominionDTO> all() {
        return DominionDTO.selectAll();
    }

    /**
     * 创建领地
     *
     * @param owner 拥有者
     * @param name  领地名称
     * @param loc1  位置1
     * @param loc2  位置2
     * @return 创建的领地
     */
    public static DominionDTO create(Player owner, String name, Location loc1, Location loc2) {
        DominionDTO parent = getPlayerCurrentDominion(owner, false);
        if (parent == null) {
            return create(owner, name, loc1, loc2, "");
        } else {
            return create(owner, name, loc1, loc2, parent.getName());
        }
    }

    /**
     * 创建子领地
     *
     * @param owner                拥有者
     * @param name                 领地名称
     * @param loc1                 位置1
     * @param loc2                 位置2
     * @param parent_dominion_name 父领地名称
     * @return 创建的领地
     */
    public static DominionDTO create(Player owner, String name,
                                     Location loc1, Location loc2,
                                     String parent_dominion_name) {
        if (name.contains(" ")) {
            Dominion.notification.error(owner, "领地名称不能包含空格");
            return null;
        }
        if (DominionDTO.select(name) != null) {
            Dominion.notification.error(owner, "已经存在名称为 %s 的领地", name);
            return null;
        }
        if (!loc1.getWorld().equals(loc2.getWorld())) {
            Dominion.notification.error(owner, "禁止跨世界操作");
            return null;
        }
        if (!owner.getWorld().equals(loc1.getWorld())) {
            Dominion.notification.error(owner, "禁止跨世界操作");
            return null;
        }
        // 检查世界是否可以创建
        if (worldNotValid(owner)) {
            return null;
        }
        // 检查领地数量是否达到上限
        if (amountNotValid(owner)) {
            return null;
        }
        // 检查领地大小是否合法
        if (sizeNotValid(owner,
                loc1.getBlockX(), loc1.getBlockY(), loc1.getBlockZ(),
                loc2.getBlockX(), loc2.getBlockY(), loc2.getBlockZ())) {
            return null;
        }
        DominionDTO dominion = new DominionDTO(owner.getUniqueId(), name, owner.getWorld().getName(),
                (int) Math.min(loc1.getX(), loc2.getX()), (int) Math.min(loc1.getY(), loc2.getY()),
                (int) Math.min(loc1.getZ(), loc2.getZ()), (int) Math.max(loc1.getX(), loc2.getX()),
                (int) Math.max(loc1.getY(), loc2.getY()), (int) Math.max(loc1.getZ(), loc2.getZ()));
        DominionDTO parent_dominion;
        if (parent_dominion_name.isEmpty()) {
            parent_dominion = DominionDTO.select(-1);
        } else {
            parent_dominion = DominionDTO.select(parent_dominion_name);
        }
        if (parent_dominion == null) {
            Dominion.notification.error(owner, "父领地 %s 不存在", parent_dominion_name);
            if (parent_dominion_name.isEmpty()) {
                Dominion.logger.err("根领地丢失！");
            }
            return null;
        }
        // 是否是父领地的拥有者
        if (parent_dominion.getId() != -1) {
            if (notOwner(owner, parent_dominion)) {
                return null;
            }
        }
        // 如果parent_dominion不为-1 检查是否在同一世界
        if (parent_dominion.getId() != -1 && !parent_dominion.getWorld().equals(dominion.getWorld())) {
            Dominion.notification.error(owner, "禁止跨世界操作");
            return null;
        }
        // 检查深度是否达到上限
        if (depthNotValid(owner, parent_dominion)) {
            return null;
        }
        // 检查是否超出父领地范围
        if (!isContained(dominion, parent_dominion)) {
            Dominion.notification.error(owner, "超出父领地 %s 范围", parent_dominion.getName());
            return null;
        }
        // 获取此领地的所有同级领地
        List<DominionDTO> sub_dominions = DominionDTO.selectByParentId(dominion.getWorld(), parent_dominion.getId());
        // 检查是否与其他子领地冲突
        for (DominionDTO sub_dominion : sub_dominions) {
            if (isIntersect(sub_dominion, dominion)) {
                Dominion.notification.error(owner, "与领地 %s 冲突", sub_dominion.getName());
                return null;
            }
        }
        // 检查经济
        if (Dominion.config.getEconomyEnable()) {
            int count;
            if (Dominion.config.getEconomyOnlyXZ()) {
                count = (loc2.getBlockX() - loc1.getBlockX() + 1) * (loc2.getBlockZ() - loc1.getBlockZ() + 1);
            } else {
                count = (loc2.getBlockX() - loc1.getBlockX() + 1) * (loc2.getBlockY() - loc1.getBlockY() + 1) * (loc2.getBlockZ() - loc1.getBlockZ() + 1);
            }
            float price = count * Dominion.config.getEconomyPrice();
            if (Dominion.vault.getEconomy().getBalance(owner) < price) {
                Dominion.notification.error(owner, "你的余额不足，创建此领地需要 %.2f %s", price, Dominion.vault.getEconomy().currencyNamePlural());
                return null;
            }
            Dominion.notification.info(owner, "已扣除 %.2f %s", price, Dominion.vault.getEconomy().currencyNamePlural());
            Dominion.vault.getEconomy().withdrawPlayer(owner, price);
        }
        dominion = DominionDTO.insert(dominion);
        if (dominion == null) {
            Dominion.notification.error(owner, "创建失败，详细错误请联系管理员查询日志（当前时间：%s）", Time.nowStr());
            return null;
        }
        ParticleRender.showBoxFace(Dominion.instance, owner, loc1, loc2);
        return dominion.setParentDomId(parent_dominion.getId());
    }

    /**
     * 向一个方向扩展领地
     * 会尝试对操作者当前所在的领地进行操作，当操作者不在一个领地内或者在子领地内时
     * 需要手动指定要操作的领地名称
     *
     * @param operator 操作者
     * @param size     扩展的大小
     * @return 扩展后的领地
     */
    public static DominionDTO expand(Player operator, Integer size) {
        DominionDTO dominion = getPlayerCurrentDominion(operator);
        if (dominion == null) {
            return null;
        }
        return expand(operator, size, dominion.getName());
    }

    /**
     * 向一个方向扩展领地
     *
     * @param operator      操作者
     * @param size          扩展的大小
     * @param dominion_name 领地名称
     * @return 扩展后的领地
     */
    public static DominionDTO expand(Player operator, Integer size, String dominion_name) {
        Location location = operator.getLocation();
        BlockFace face = getFace(operator);
        DominionDTO dominion = getExistDomAndIsOwner(operator, dominion_name);
        if (dominion == null) {
            return null;
        }
        if (!location.getWorld().getName().equals(dominion.getWorld())) {
            Dominion.notification.error(operator, "禁止跨世界操作");
            return null;
        }
        Integer x1 = dominion.getX1();
        Integer y1 = dominion.getY1();
        Integer z1 = dominion.getZ1();
        Integer x2 = dominion.getX2();
        Integer y2 = dominion.getY2();
        Integer z2 = dominion.getZ2();
        switch (face) {
            case NORTH:
                z1 -= size;
                break;
            case SOUTH:
                z2 += size;
                break;
            case WEST:
                x1 -= size;
                break;
            case EAST:
                x2 += size;
                break;
            case UP:
                y2 += size;
                break;
            case DOWN:
                y1 -= size;
                break;
            default:
                Dominion.notification.error(operator, "无效的方向");
                return null;
        }
        if (sizeNotValid(operator, x1, y1, z1, x2, y2, z2)) {
            return null;
        }
        // 校验是否超出父领地范围
        DominionDTO parent_dominion = DominionDTO.select(dominion.getParentDomId());
        if (parent_dominion == null) {
            Dominion.notification.error(operator, "父领地丢失");
            return null;
        }
        if (!isContained(x1, y1, z1, x2, y2, z2, parent_dominion)) {
            Dominion.notification.error(operator, "超出父领地 %s 范围", parent_dominion.getName());
            return null;
        }
        // 获取同世界下的所有同级领地
        List<DominionDTO> exist_dominions = DominionDTO.selectByParentId(dominion.getWorld(), dominion.getParentDomId());
        for (DominionDTO exist_dominion : exist_dominions) {
            if (isIntersect(exist_dominion, x1, y1, z1, x2, y2, z2)) {
                // 如果是自己，跳过
                if (exist_dominion.getId().equals(dominion.getId())) continue;
                Dominion.notification.error(operator, "与 %s 冲突", exist_dominion.getName());
                return null;
            }
        }
        // 检查经济
        if (Dominion.config.getEconomyEnable()) {
            int count;
            if (Dominion.config.getEconomyOnlyXZ()) {
                count = (x2 - x1 + 1) * (z2 - z1 + 1) - dominion.getSquare();
            } else {
                count = (x2 - x1 + 1) * (y2 - y1 + 1) * (z2 - z1 + 1) - dominion.getVolume();
            }
            float price = count * Dominion.config.getEconomyPrice();
            if (Dominion.vault.getEconomy().getBalance(operator) < price) {
                Dominion.notification.error(operator, "你的余额不足，扩展此领地需要 %.2f %s", price, Dominion.vault.getEconomy().currencyNamePlural());
                return null;
            }
            Dominion.notification.info(operator, "已扣除 %.2f %s", price, Dominion.vault.getEconomy().currencyNamePlural());
            Dominion.vault.getEconomy().withdrawPlayer(operator, price);
        }
        ParticleRender.showBoxFace(Dominion.instance, operator,
                new Location(location.getWorld(), x1, y1, z1),
                new Location(location.getWorld(), x2, y2, z2));
        return dominion.setXYZ(x1, y1, z1, x2, y2, z2);
    }

    /**
     * 缩小领地
     * 会尝试对操作者当前所在的领地进行操作，当操作者不在一个领地内或者在子领地内时
     * 需要手动指定要操作的领地名称
     *
     * @param operator 操作者
     * @param size     缩小的大小
     * @return 缩小后的领地
     */
    public static DominionDTO contract(Player operator, Integer size) {
        DominionDTO dominion = getPlayerCurrentDominion(operator);
        if (dominion == null) {
            return null;
        }
        return contract(operator, size, dominion.getName());
    }

    /**
     * 缩小领地
     *
     * @param operator      操作者
     * @param size          缩小的大小
     * @param dominion_name 领地名称
     * @return 缩小后的领地
     */
    public static DominionDTO contract(Player operator, Integer size, String dominion_name) {
        Location location = operator.getLocation();
        BlockFace face = getFace(operator);
        DominionDTO dominion = getExistDomAndIsOwner(operator, dominion_name);
        if (dominion == null) {
            return null;
        }
        if (!location.getWorld().getName().equals(dominion.getWorld())) {
            Dominion.notification.error(operator, "禁止跨世界操作");
            return null;
        }
        Integer x1 = dominion.getX1();
        Integer y1 = dominion.getY1();
        Integer z1 = dominion.getZ1();
        Integer x2 = dominion.getX2();
        Integer y2 = dominion.getY2();
        Integer z2 = dominion.getZ2();
        switch (face) {
            case SOUTH:
                z2 -= size;
                break;
            case NORTH:
                z1 += size;
                break;
            case EAST:
                x2 -= size;
                break;
            case WEST:
                x1 += size;
                break;
            case UP:
                y2 -= size;
                break;
            case DOWN:
                y1 += size;
                break;
            default:
                Dominion.notification.error(operator, "无效的方向");
                return null;
        }
        // 校验第二组坐标是否小于第一组坐标
        if (x1 >= x2 || y1 >= y2 || z1 >= z2) {
            Dominion.notification.error(operator, "缩小后的领地无效");
            return null;
        }
        if (sizeNotValid(operator, x1, y1, z1, x2, y2, z2)) {
            return null;
        }
        // 获取所有的子领地
        List<DominionDTO> sub_dominions = DominionDTO.selectByParentId(dominion.getWorld(), dominion.getId());
        for (DominionDTO sub_dominion : sub_dominions) {
            if (!isContained(sub_dominion, x1, y1, z1, x2, y2, z2)) {
                Dominion.notification.error(operator, "缩小后的领地 %s 无法包含子领地 %s", dominion_name, sub_dominion.getName());
                return null;
            }
        }
        // 退还经济
        if (Dominion.config.getEconomyEnable()) {
            int count;
            if (Dominion.config.getEconomyOnlyXZ()) {
                count = dominion.getSquare() - (x2 - x1 + 1) * (z2 - z1 + 1);
            } else {
                count = dominion.getVolume() - (x2 - x1 + 1) * (y2 - y1 + 1) * (z2 - z1 + 1);
            }
            float refund = count * Dominion.config.getEconomyPrice() * Dominion.config.getEconomyRefund();
            Dominion.vault.getEconomy().depositPlayer(operator, refund);
            Dominion.notification.info(operator, "已经退还 %.2f %s", refund, Dominion.vault.getEconomy().currencyNamePlural());
        }
        ParticleRender.showBoxFace(Dominion.instance, operator,
                new Location(location.getWorld(), x1, y1, z1),
                new Location(location.getWorld(), x2, y2, z2));
        return dominion.setXYZ(x1, y1, z1, x2, y2, z2);
    }

    /**
     * 删除领地 会同时删除其所有子领地
     *
     * @param operator      操作者
     * @param dominion_name 领地名称
     * @param force         是否强制删除
     */
    public static void delete(Player operator, String dominion_name, boolean force) {
        DominionDTO dominion = getExistDomAndIsOwner(operator, dominion_name);
        if (dominion == null) {
            return;
        }
        List<DominionDTO> sub_dominions = getSubDominionsRecursive(dominion);
        if (!force) {
            Dominion.notification.warn(operator, "删除领地 %s 会同时删除其所有子领地，是否继续？", dominion_name);
            String sub_names = "";
            for (DominionDTO sub_dominion : sub_dominions) {
                sub_names = sub_dominion.getName() + ", ";
            }
            if (sub_dominions.size() > 0) {
                sub_names = sub_names.substring(0, sub_names.length() - 2);
                Dominion.notification.warn(operator, "当前子领地：" + sub_names);
            }
            Dominion.notification.warn(operator, "输入 /dominion delete %s force 确认删除", dominion_name);
            return;
        }
        DominionDTO.delete(dominion);
        // 退还经济
        if (Dominion.config.getEconomyEnable()) {
            int count = 0;
            if (Dominion.config.getEconomyOnlyXZ()) {
                for (DominionDTO sub_dominion : sub_dominions) {
                    count += sub_dominion.getSquare();
                }
            } else {
                for (DominionDTO sub_dominion : sub_dominions) {
                    count += sub_dominion.getVolume();
                }
            }
            float refund = count * Dominion.config.getEconomyPrice() * Dominion.config.getEconomyRefund();
            Dominion.vault.getEconomy().depositPlayer(operator, refund);
            Dominion.notification.info(operator, "已经退还 %.2f %s", refund, Dominion.vault.getEconomy().currencyNamePlural());
        }
        Dominion.notification.info(operator, "领地 %s 及其所有子领地已删除", dominion_name);
    }

    /**
     * 设置领地的进入消息
     *
     * @param operator 操作者
     * @param message  消息
     */
    public static void setJoinMessage(Player operator, String message) {
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
    public static void setJoinMessage(Player operator, String message, String dominion_name) {
        DominionDTO dominion = getExistDomAndIsOwner(operator, dominion_name);
        if (dominion == null) {
            return;
        }
        dominion.setJoinMessage(message);
        Dominion.notification.info(operator, "成功设置领地 %s 的进入消息", dominion_name);
    }

    /**
     * 设置领地的离开消息
     *
     * @param operator 操作者
     * @param message  消息
     */
    public static void setLeaveMessage(Player operator, String message) {
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
    public static void setLeaveMessage(Player operator, String message, String dominion_name) {
        DominionDTO dominion = getExistDomAndIsOwner(operator, dominion_name);
        if (dominion == null) {
            return;
        }
        dominion.setLeaveMessage(message);
        Dominion.notification.info(operator, "成功设置领地 %s 的离开消息", dominion_name);
    }

    /**
     * 设置领地的传送点
     *
     * @param operator 操作者
     */
    public static void setTpLocation(Player operator) {
        DominionDTO dominion = getPlayerCurrentDominion(operator);
        if (dominion == null) {
            return;
        }
        setTpLocation(operator, dominion.getName());
    }

    /**
     * 设置领地的传送点
     *
     * @param operator      操作者
     * @param dominion_name 领地名称
     */
    public static void setTpLocation(Player operator, String dominion_name) {
        DominionDTO dominion = getExistDomAndIsOwner(operator, dominion_name);
        if (dominion == null) {
            return;
        }
        // 检查是否在领地内
        if (operator.getWorld().getName().equals(dominion.getWorld()) &&
                operator.getLocation().getBlockX() >= dominion.getX1() &&
                operator.getLocation().getBlockX() <= dominion.getX2() &&
                operator.getLocation().getBlockY() >= dominion.getY1() &&
                operator.getLocation().getBlockY() <= dominion.getY2() &&
                operator.getLocation().getBlockZ() >= dominion.getZ1() &&
                operator.getLocation().getBlockZ() <= dominion.getZ2()) {
            Location loc = operator.getLocation();
            loc.setY(loc.getY() + 1.5);
            dominion.setTpLocation(loc);
            Dominion.notification.info(operator, "成功设置领地 %s 的传送点，坐标：%d %d %d",
                    dominion_name, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        } else {
            Dominion.notification.error(operator, "你不在领地 %s 内，无法设置传送点", dominion_name);
        }
    }

    /**
     * 重命名领地
     *
     * @param operator 操作者
     * @param old_name 旧名称
     * @param new_name 新名称
     */
    public static void rename(Player operator, String old_name, String new_name) {
        if (new_name.contains(" ")) {
            Dominion.notification.error(operator, "领地名称不能包含空格");
            return;
        }
        if (Objects.equals(old_name, new_name)) {
            Dominion.notification.error(operator, "新名称与旧名称相同");
            return;
        }
        DominionDTO dominion = getExistDomAndIsOwner(operator, old_name);
        if (dominion == null) {
            return;
        }
        if (DominionDTO.select(new_name) != null) {
            Dominion.notification.error(operator, "已经存在名称为 %s 的领地", new_name);
            return;
        }
        dominion.setName(new_name);
        Dominion.notification.info(operator, "成功将领地 %s 重命名为 %s", old_name, new_name);
    }

    /**
     * 转让领地
     *
     * @param operator    操作者
     * @param dom_name    领地名称
     * @param player_name 玩家名称
     * @param force       是否强制转让
     */
    public static void give(Player operator, String dom_name, String player_name, boolean force) {
        if (Objects.equals(player_name, operator.getName())) {
            Dominion.notification.error(operator, "你不能将领地转让给自己");
            return;
        }
        DominionDTO dominion = getExistDomAndIsOwner(operator, dom_name);
        if (dominion == null) {
            return;
        }
        PlayerDTO player = PlayerController.getPlayerDTO(player_name);
        if (player == null) {
            Dominion.notification.error(operator, "玩家 %s 不存在", player_name);
            return;
        }
        if (dominion.getParentDomId() != -1) {
            Dominion.notification.error(operator, "子领地无法转让，你可以通过将玩家设置为管理员来让其管理子领地");
            return;
        }
        List<DominionDTO> sub_dominions = getSubDominionsRecursive(dominion);
        if (!force) {
            Dominion.notification.warn(operator, "转让领地 %s 给 %s 会同时转让其所有子领地，是否继续？", dom_name, player_name);
            String sub_names = "";
            for (DominionDTO sub_dominion : sub_dominions) {
                sub_names = sub_dominion.getName() + ", ";
            }
            if (sub_dominions.size() > 0) {
                sub_names = sub_names.substring(0, sub_names.length() - 2);
                Dominion.notification.warn(operator, "当前子领地：%s", sub_names);
            }
            Dominion.notification.warn(operator, "输入 /dominion give %s %s force 确认转让", dom_name, player_name);
            return;
        }
        dominion.setOwner(player.getUuid());
        for (DominionDTO sub_dominion : sub_dominions) {
            sub_dominion.setOwner(player.getUuid());
        }
        Dominion.notification.info(operator, "成功将领地 %s 及其所有子领地转让给 %s", dom_name, player_name);
    }

    /**
     * 判断两个领地是否相交
     */
    private static boolean isIntersect(DominionDTO a, DominionDTO b) {
        return a.getX1() < b.getX2() && a.getX2() > b.getX1() &&
                a.getY1() < b.getY2() && a.getY2() > b.getY1() &&
                a.getZ1() < b.getZ2() && a.getZ2() > b.getZ1();
    }

    private static boolean isIntersect(DominionDTO a, Integer x1, Integer y1, Integer z1, Integer x2, Integer y2, Integer z2) {
        return a.getX1() < x2 && a.getX2() > x1 &&
                a.getY1() < y2 && a.getY2() > y1 &&
                a.getZ1() < z2 && a.getZ2() > z1;
    }

    /**
     * 判断 sub 是否完全被 parent 包裹
     */
    private static boolean isContained(DominionDTO sub, DominionDTO parent) {
        if (parent.getId() == -1) {
            return true;
        }
        return sub.getX1() >= parent.getX1() && sub.getX2() <= parent.getX2() &&
                sub.getY1() >= parent.getY1() && sub.getY2() <= parent.getY2() &&
                sub.getZ1() >= parent.getZ1() && sub.getZ2() <= parent.getZ2();
    }

    private static boolean isContained(Integer x1, Integer y1, Integer z1, Integer x2, Integer y2, Integer z2, DominionDTO parent) {
        if (parent.getId() == -1) {
            return true;
        }
        return x1 >= parent.getX1() && x2 <= parent.getX2() &&
                y1 >= parent.getY1() && y2 <= parent.getY2() &&
                z1 >= parent.getZ1() && z2 <= parent.getZ2();
    }

    private static boolean isContained(DominionDTO sub, Integer x1, Integer y1, Integer z1, Integer x2, Integer y2, Integer z2) {
        return sub.getX1() >= x1 && sub.getX2() <= x2 &&
                sub.getY1() >= y1 && sub.getY2() <= y2 &&
                sub.getZ1() >= z1 && sub.getZ2() <= z2;
    }

    private static List<DominionDTO> getSubDominionsRecursive(DominionDTO dominion) {
        List<DominionDTO> sub_dominions = DominionDTO.selectByParentId(dominion.getWorld(), dominion.getId());
        List<DominionDTO> sub_sub_dominions = new ArrayList<>();
        for (DominionDTO sub_dominion : sub_dominions) {
            sub_sub_dominions.addAll(getSubDominionsRecursive(sub_dominion));
        }
        sub_dominions.addAll(sub_sub_dominions);
        return sub_dominions;
    }

    private static boolean sizeNotValid(Player operator, int x1, int y1, int z1, int x2, int y2, int z2) {
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
        if (x_length < 4 || y_length < 4 || z_length < 4) {
            Dominion.notification.error(operator, "领地的任意一边长度不得小于4");
            return true;
        }
        if (x_length > Dominion.config.getLimitSizeX() && Dominion.config.getLimitSizeX() > 0) {
            Dominion.notification.error(operator, "领地X方向长度不能超过 %s", Dominion.config.getLimitSizeX());
            return true;
        }
        if (y_length > Dominion.config.getLimitSizeY() && Dominion.config.getLimitSizeY() > 0) {
            Dominion.notification.error(operator, "领地Y方向高度不能超过 %s", Dominion.config.getLimitSizeY());
            return true;
        }
        if (z_length > Dominion.config.getLimitSizeZ() && Dominion.config.getLimitSizeZ() > 0) {
            Dominion.notification.error(operator, "领地Z方向长度不能超过 %s", Dominion.config.getLimitSizeZ());
            return true;
        }
        if (y2 > Dominion.config.getLimitMaxY()) {
            Dominion.notification.error(operator, "领地Y坐标不能超过 %s", Dominion.config.getLimitMaxY());
            return true;
        }
        if (y1 < Dominion.config.getLimitMinY()) {
            Dominion.notification.error(operator, "领地Y坐标不能低于 %s", Dominion.config.getLimitMinY());
            return true;
        }
        return false;
    }

    private static boolean depthNotValid(Player operator, DominionDTO parent_dom) {
        if (operator.isOp() && Dominion.config.getLimitOpBypass()) {
            return false;
        }
        if (Dominion.config.getLimitDepth() == -1) {
            return false;
        }
        if (parent_dom.getId() != -1 && Dominion.config.getLimitDepth() == 0) {
            Dominion.notification.error(operator, "不允许创建子领地");
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
        if (level >= Dominion.config.getLimitDepth()) {
            Dominion.notification.error(operator, "子领地嵌套深度不能超过 %s", Dominion.config.getLimitDepth());
            return true;
        }
        return false;
    }

    private static boolean amountNotValid(Player operator) {
        if (operator.isOp() && Dominion.config.getLimitOpBypass()) {
            return false;
        }
        if (Cache.instance.getPlayerDominionCount(operator) >= Dominion.config.getLimitAmount() && Dominion.config.getLimitAmount() != -1) {
            Dominion.notification.error(operator, "你的领地数量已达上限，当前上限为 %s", Dominion.config.getLimitAmount());
            return true;
        }
        return false;
    }

    private static boolean worldNotValid(Player operator) {
        if (operator.isOp() && Dominion.config.getLimitOpBypass()) {
            return false;
        }
        if (Dominion.config.getWorldBlackList().contains(operator.getWorld().getName())) {
            Dominion.notification.error(operator, "禁止在世界 %s 创建领地", operator.getWorld().getName());
            return true;
        }
        return false;
    }

    private static DominionDTO getExistDomAndIsOwner(Player operator, String dominion_name) {
        DominionDTO dominion = DominionDTO.select(dominion_name);
        if (dominion == null) {
            Dominion.notification.error(operator, "领地 %s 不存在", dominion_name);
            return null;
        }
        if (notOwner(operator, dominion)) {
            Dominion.notification.error(operator, "你不是领地 %s 的拥有者，无法执行此操作", dominion_name);
            return null;
        }
        return dominion;
    }

}
