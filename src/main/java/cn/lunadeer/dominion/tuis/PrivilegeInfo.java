package cn.lunadeer.dominion.tuis;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.PlayerDTO;
import cn.lunadeer.dominion.dtos.PlayerPrivilegeDTO;
import cn.lunadeer.dominion.utils.STUI.Button;
import cn.lunadeer.dominion.utils.STUI.Line;
import cn.lunadeer.dominion.utils.STUI.ListView;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.commands.Apis.playerOnly;
import static cn.lunadeer.dominion.tuis.Apis.getDominionNameArg_2;
import static cn.lunadeer.dominion.tuis.Apis.noAuthToManage;

public class PrivilegeInfo {
    // /dominion privilege_info <玩家名称> [领地名称] [页码]
    public static void show(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        DominionDTO dominion = getDominionNameArg_2(player, args);
        int page = 1;
        if (args.length == 4) {
            try {
                page = Integer.parseInt(args[3]);
            } catch (Exception ignored) {
            }
        }
        String playerName = args[1];
        if (dominion == null) {
            Dominion.notification.error(sender, "你不在任何领地内，请指定领地名称 /dominion privilege_info <玩家名称> [领地名称]");
            return;
        }
        ListView view = ListView.create(10, "/dominion privilege_info " + playerName + " " + dominion.getName());
        if (noAuthToManage(player, dominion)) return;
        PlayerDTO playerDTO = PlayerDTO.select(playerName);
        if (playerDTO == null) {
            Dominion.notification.error(sender, "玩家 %s 不存在", playerName);
            return;
        }
        PlayerPrivilegeDTO privilege = PlayerPrivilegeDTO.select(playerDTO.getUuid(), dominion.getId());
        if (privilege == null) {
            Dominion.notification.warn(sender, "玩家 %s 没有任何特权", playerName);
            return;
        }
        view.title("玩家 " + playerName + " 在领地 " + dominion.getName() + " 的特权信息");
        view.navigator(
                Line.create()
                        .append(Button.create("主菜单", "/dominion menu"))
                        .append(Button.create("我的领地", "/dominion list"))
                        .append(Button.create("管理界面", "/dominion manage " + dominion.getName()))
                        .append(Button.create("特权列表", "/dominion privilege_list " + dominion.getName()))
                        .append("特权信息")
        );
        if (privilege.getAdmin()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set_privilege " + playerName + " admin false " + dominion.getName() + " " + page))
                    .append("管理员"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set_privilege " + playerName + " admin true " + dominion.getName() + " " + page))
                    .append("管理员"));
        }
        if (privilege.getAnchor()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set_privilege " + playerName + " anchor false " + dominion.getName() + " " + page))
                    .append("重生锚"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set_privilege " + playerName + " anchor true " + dominion.getName() + " " + page))
                    .append("重生锚"));
        }
        if (privilege.getAnimalKilling()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set_privilege " + playerName + " animal_killing false " + dominion.getName() + " " + page))
                    .append("动物伤害"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set_privilege " + playerName + " animal_killing true " + dominion.getName() + " " + page))
                    .append("动物伤害"));
        }
        if (privilege.getAnvil()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set_privilege " + playerName + " anvil false " + dominion.getName() + " " + page))
                    .append("使用铁砧"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set_privilege " + playerName + " anvil true " + dominion.getName() + " " + page))
                    .append("使用铁砧"));
        }
        if (privilege.getBeacon()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set_privilege " + playerName + " beacon false " + dominion.getName() + " " + page))
                    .append("信标交互"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set_privilege " + playerName + " beacon true " + dominion.getName() + " " + page))
                    .append("信标交互"));
        }
        if (privilege.getBed()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set_privilege " + playerName + " bed false " + dominion.getName() + " " + page))
                    .append("床交互"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set_privilege " + playerName + " bed true " + dominion.getName() + " " + page))
                    .append("床交互"));
        }
        if (privilege.getBrew()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set_privilege " + playerName + " brew false " + dominion.getName() + " " + page))
                    .append("使用酿造台"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set_privilege " + playerName + " brew true " + dominion.getName() + " " + page))
                    .append("使用酿造台"));
        }
        if (privilege.getBreak()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set_privilege " + playerName + " break false " + dominion.getName() + " " + page))
                    .append("破坏方块"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set_privilege " + playerName + " break true " + dominion.getName() + " " + page))
                    .append("破坏方块"));
        }
        if (privilege.getButton()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set_privilege " + playerName + " button false " + dominion.getName() + " " + page))
                    .append("使用按钮"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set_privilege " + playerName + " button true " + dominion.getName() + " " + page))
                    .append("使用按钮"));
        }
        if (privilege.getCake()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set_privilege " + playerName + " cake false " + dominion.getName() + " " + page))
                    .append("使用蛋糕"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set_privilege " + playerName + " cake true " + dominion.getName() + " " + page))
                    .append("使用蛋糕"));
        }
        if (privilege.getContainer()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set_privilege " + playerName + " container false " + dominion.getName() + " " + page))
                    .append("箱子/木桶/潜影盒/盔甲架/展示框"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set_privilege " + playerName + " container true " + dominion.getName() + " " + page))
                    .append("箱子/木桶/潜影盒/盔甲架/展示框"));
        }
        if (privilege.getCraft()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set_privilege " + playerName + " craft false " + dominion.getName() + " " + page))
                    .append("使用工作台"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set_privilege " + playerName + " craft true " + dominion.getName() + " " + page))
                    .append("使用工作台"));
        }
        if (privilege.getComparer()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set_privilege " + playerName + " comparer false " + dominion.getName() + " " + page))
                    .append("比较器交互"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set_privilege " + playerName + " comparer true " + dominion.getName() + " " + page))
                    .append("比较器交互"));
        }
        if (privilege.getDoor()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set_privilege " + playerName + " door false " + dominion.getName() + " " + page))
                    .append("门交互"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set_privilege " + playerName + " door true " + dominion.getName() + " " + page))
                    .append("门交互"));
        }
        if (privilege.getDye()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set_privilege " + playerName + " dye false " + dominion.getName() + " " + page))
                    .append("染色"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set_privilege " + playerName + " dye true " + dominion.getName() + " " + page))
                    .append("染色"));
        }
        if (privilege.getEgg()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set_privilege " + playerName + " egg false " + dominion.getName() + " " + page))
                    .append("投掷鸡蛋"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set_privilege " + playerName + " egg true " + dominion.getName() + " " + page))
                    .append("投掷鸡蛋"));
        }
        if (privilege.getEnchant()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set_privilege " + playerName + " enchant false " + dominion.getName() + " " + page))
                    .append("使用附魔台"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set_privilege " + playerName + " enchant true " + dominion.getName() + " " + page))
                    .append("使用附魔台"));
        }
        if (privilege.getEnderPearl()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set_privilege " + playerName + " ender_pearl false " + dominion.getName() + " " + page))
                    .append("投掷末影珍珠"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set_privilege " + playerName + " ender_pearl true " + dominion.getName() + " " + page))
                    .append("投掷末影珍珠"));
        }
        if (privilege.getFeed()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set_privilege " + playerName + " feed false " + dominion.getName() + " " + page))
                    .append("喂食"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set_privilege " + playerName + " feed true " + dominion.getName() + " " + page))
                    .append("喂食"));
        }
        if (privilege.getGlow()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set_privilege " + playerName + " glow false " + dominion.getName() + " " + page))
                    .append("发光"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set_privilege " + playerName + " glow true " + dominion.getName() + " " + page))
                    .append("发光"));
        }
        if (privilege.getHarvest()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set_privilege " + playerName + " harvest false " + dominion.getName() + " " + page))
                    .append("收获"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set_privilege " + playerName + " harvest true " + dominion.getName() + " " + page))
                    .append("收获"));
        }
        if (privilege.getHoney()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set_privilege " + playerName + " honey false " + dominion.getName() + " " + page))
                    .append("蜂巢交互"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set_privilege " + playerName + " honey true " + dominion.getName() + " " + page))
                    .append("蜂巢交互"));
        }
        if (privilege.getHook()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set_privilege " + playerName + " hook false " + dominion.getName() + " " + page))
                    .append("使用钓钩"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set_privilege " + playerName + " hook true " + dominion.getName() + " " + page))
                    .append("使用钓钩"));
        }
        if (privilege.getHopper()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set_privilege " + playerName + " hopper false " + dominion.getName() + " " + page))
                    .append("漏斗/熔炉/发射器等特殊容器"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set_privilege " + playerName + " hopper true " + dominion.getName() + " " + page))
                    .append("漏斗/熔炉/发射器等特殊容器"));
        }
        if (privilege.getIgnite()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set_privilege " + playerName + " ignite false " + dominion.getName() + " " + page))
                    .append("点燃"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set_privilege " + playerName + " ignite true " + dominion.getName() + " " + page))
                    .append("点燃"));
        }
        if (privilege.getLever()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set_privilege " + playerName + " lever false " + dominion.getName() + " " + page))
                    .append("使用拉杆"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set_privilege " + playerName + " lever true " + dominion.getName() + " " + page))
                    .append("使用拉杆"));
        }
        if (privilege.getMonsterKilling()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set_privilege " + playerName + " monster_killing false " + dominion.getName() + " " + page))
                    .append("怪物伤害"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set_privilege " + playerName + " monster_killing true " + dominion.getName() + " " + page))
                    .append("怪物伤害"));
        }
        if (privilege.getMove()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set_privilege " + playerName + " move false " + dominion.getName() + " " + page))
                    .append("玩家移动"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set_privilege " + playerName + " move true " + dominion.getName() + " " + page))
                    .append("玩家移动"));
        }
        if (privilege.getPlace()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set_privilege " + playerName + " place false " + dominion.getName() + " " + page))
                    .append("放置方块"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set_privilege " + playerName + " place true " + dominion.getName() + " " + page))
                    .append("放置方块"));
        }
        if (privilege.getPressure()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set_privilege " + playerName + " pressure false " + dominion.getName() + " " + page))
                    .append("压力板交互"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set_privilege " + playerName + " pressure true " + dominion.getName() + " " + page))
                    .append("压力板交互"));
        }
        if (privilege.getRiding()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set_privilege " + playerName + " riding false " + dominion.getName() + " " + page))
                    .append("骑乘载具"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set_privilege " + playerName + " riding true " + dominion.getName() + " " + page))
                    .append("骑乘载具"));
        }
        if (privilege.getRepeater()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set_privilege " + playerName + " repeater false " + dominion.getName() + " " + page))
                    .append("中继器交互"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set_privilege " + playerName + " repeater true " + dominion.getName() + " " + page))
                    .append("中继器交互"));
        }
        if (privilege.getShear()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set_privilege " + playerName + " shear false " + dominion.getName() + " " + page))
                    .append("剪羊毛"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set_privilege " + playerName + " shear true " + dominion.getName() + " " + page))
                    .append("剪羊毛"));
        }
        if (privilege.getShoot()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set_privilege " + playerName + " shoot false " + dominion.getName() + " " + page))
                    .append("射箭/雪球/三叉戟"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set_privilege " + playerName + " shoot true " + dominion.getName() + " " + page))
                    .append("射箭/雪球/三叉戟"));
        }
        if (privilege.getTeleport()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set_privilege " + playerName + " teleport false " + dominion.getName() + " " + page))
                    .append("领地传送"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set_privilege " + playerName + " teleport true " + dominion.getName() + " " + page))
                    .append("领地传送"));
        }
        if (privilege.getTrade()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set_privilege " + playerName + " trade false " + dominion.getName() + " " + page))
                    .append("交易"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set_privilege " + playerName + " trade true " + dominion.getName() + " " + page))
                    .append("交易"));
        }
        if (privilege.getVehicleDestroy()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set_privilege " + playerName + " vehicle_destroy false " + dominion.getName() + " " + page))
                    .append("破坏载具"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set_privilege " + playerName + " vehicle_destroy true " + dominion.getName() + " " + page))
                    .append("破坏载具"));
        }
        if (privilege.getVehicleSpawn()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set_privilege " + playerName + " vehicle_spawn false " + dominion.getName() + " " + page))
                    .append("放置载具"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set_privilege " + playerName + " vehicle_spawn true " + dominion.getName() + " " + page))
                    .append("放置载具"));
        }
        view.showOn(player, page);
    }
}
