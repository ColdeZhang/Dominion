package cn.lunadeer.dominion.controllers;

import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.utils.Notification;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.controllers.Apis.noAuthToChangeFlags;

public class FlagsController {

    /**
     * 设置领地权限
     * @param operator 操作者
     * @param flag 权限名称
     * @param value 权限值
     * @return 设置后的领地信息
     */
    public static DominionDTO setFlag(Player operator, String flag, boolean value){
        DominionDTO dominion = Apis.getPlayerCurrentDominion(operator);
        if (dominion == null) return null;
        return setFlag(operator, flag, value, dominion.getName());
    }

    /**
     * 设置领地权限
     * @param operator 操作者
     * @param flag 权限名称
     * @param value 权限值
     * @param dominionName 领地名称
     * @return 设置后的领地信息
     */
    public static DominionDTO setFlag(Player operator, String flag, boolean value, String dominionName) {
        DominionDTO dominion = DominionDTO.select(dominionName);
        if (dominion == null) {
            Notification.error(operator, "领地 " + dominionName + " 不存在");
            return null;
        }
        if (noAuthToChangeFlags(operator, dominion)) return null;
        switch (flag) {
            case "anchor": return dominion.setAnchor(value);
            case "animal_killing": return dominion.setAnimalKilling(value);
            case "anvil": return dominion.setAnvil(value);
            case "beacon": return dominion.setBeacon(value);
            case "bed": return dominion.setBed(value);
            case "brew": return dominion.setBrew(value);
            case "break": return dominion.setBreak(value);
            case "button": return dominion.setButton(value);
            case "cake": return dominion.setCake(value);
            case "container": return dominion.setContainer(value);
            case "craft": return dominion.setCraft(value);
            case "creeper_explode": return dominion.setCreeperExplode(value);
            case "comparer": return dominion.setComparer(value);
            case "door": return dominion.setDoor(value);
            case "dye": return dominion.setDye(value);
            case "egg": return dominion.setEgg(value);
            case "enchant": return dominion.setEnchant(value);
            case "ender_pearl": return dominion.setEnderPearl(value);
            case "feed": return dominion.setFeed(value);
            case "fire_spread": return dominion.setFireSpread(value);
            case "flow_in_protection": return dominion.setFlowInProtection(value);
            case "glow": return dominion.setGlow(value);
            case "harvest": return dominion.setHarvest(value);
            case "honey": return dominion.setHoney(value);
            case "hook": return dominion.setHook(value);
            case "hopper": return dominion.setHopper(value);
            case "ignite": return dominion.setIgnite(value);
            case "lever": return dominion.setLever(value);
            case "monster_killing": return dominion.setMonsterKilling(value);
            case "move": return dominion.setMove(value);
            case "place": return dominion.setPlace(value);
            case "pressure": return dominion.setPressure(value);
            case "riding": return dominion.setRiding(value);
            case "repeater": return dominion.setRepeater(value);
            case "shear": return dominion.setShear(value);
            case "shoot": return dominion.setShoot(value);
            case "tnt_explode": return dominion.setTntExplode(value);
            case "trade": return dominion.setTrade(value);
            case "vehicle_destroy": return dominion.setVehicleDestroy(value);
            case "wither_spawn": return dominion.setWitherSpawn(value);
            default:
                Notification.error(operator, "未知的领地权限 " + flag);
                return null;
        }
    }
}
