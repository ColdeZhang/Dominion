package cn.lunadeer.dominion.tuis;

import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.STUI.Button;
import cn.lunadeer.dominion.utils.STUI.Line;
import cn.lunadeer.dominion.utils.STUI.ListView;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.commands.Apis.playerOnly;

public class DominionFlagInfo {

    public static void show(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        if (args.length < 2) {
            Notification.error(sender, "用法: /dominion flag_info <领地名称> [页码]");
            return;
        }
        DominionDTO dominion = DominionDTO.select(args[1]);
        if (dominion == null) {
            Notification.error(sender, "领地 " + args[1] + " 不存在");
            return;
        }
        int page = 1;
        if (args.length == 3) {
            try {
                page = Integer.parseInt(args[2]);
            } catch (Exception ignored) {
            }
        }
        ListView view = ListView.create(10, "/dominion flag_info " + dominion.getName());
        view.title("领地 " + dominion.getName() + " 默认权限")
                .navigator(Line.create()
                        .append(Button.create("主菜单", "/dominion menu"))
                        .append(Button.create("我的领地", "/dominion list"))
                        .append(Button.create("管理界面", "/dominion manage " + dominion.getName()))
                        .append("权限列表"));
        if (dominion.getAnchor()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set anchor false " + dominion.getName() + " " + page))
                    .append("重生锚"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set anchor true " + dominion.getName() + " " + page))
                    .append("重生锚"));
        }
        if (dominion.getAnimalKilling()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set animal_killing false " + dominion.getName() + " " + page))
                    .append("动物伤害"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set animal_killing true " + dominion.getName() + " " + page))
                    .append("动物伤害"));
        }
        if (dominion.getAnvil()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set anvil false " + dominion.getName() + " " + page))
                    .append("使用铁砧"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set anvil true " + dominion.getName() + " " + page))
                    .append("使用铁砧"));
        }
        if (dominion.getBeacon()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set beacon false " + dominion.getName() + " " + page))
                    .append("信标交互"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set beacon true " + dominion.getName() + " " + page))
                    .append("信标交互"));
        }
        if (dominion.getBed()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set bed false " + dominion.getName() + " " + page))
                    .append("床交互"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set bed true " + dominion.getName() + " " + page))
                    .append("床交互"));
        }
        if (dominion.getBrew()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set brew false " + dominion.getName() + " " + page))
                    .append("使用酿造台"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set brew true " + dominion.getName() + " " + page))
                    .append("使用酿造台"));
        }
        if (dominion.getBreak()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set break false " + dominion.getName() + " " + page))
                    .append("破坏方块"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set break true " + dominion.getName() + " " + page))
                    .append("破坏方块"));
        }
        if (dominion.getButton()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set button false " + dominion.getName() + " " + page))
                    .append("使用按钮"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set button true " + dominion.getName() + " " + page))
                    .append("使用按钮"));
        }
        if (dominion.getCake()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set cake false " + dominion.getName() + " " + page))
                    .append("使用蛋糕"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set cake true " + dominion.getName() + " " + page))
                    .append("使用蛋糕"));
        }
        if (dominion.getContainer()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set container false " + dominion.getName() + " " + page))
                    .append("箱子/木桶/潜影盒/盔甲架/展示框"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set container true " + dominion.getName() + " " + page))
                    .append("箱子/木桶/潜影盒/盔甲架/展示框"));
        }
        if (dominion.getCraft()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set craft false " + dominion.getName() + " " + page))
                    .append("工作台"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set craft true " + dominion.getName() + " " + page))
                    .append("工作台"));
        }
        if (dominion.getCreeperExplode()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set creeper_explode false " + dominion.getName() + " " + page))
                    .append("苦力怕/凋零头颅/水晶爆炸"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set creeper_explode true " + dominion.getName() + " " + page))
                    .append("苦力怕/凋零头颅/水晶爆炸"));
        }
        if (dominion.getComparer()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set comparer false " + dominion.getName() + " " + page))
                    .append("比较器交互"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set comparer true " + dominion.getName() + " " + page))
                    .append("比较器交互"));
        }
        if (dominion.getDoor()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set door false " + dominion.getName() + " " + page))
                    .append("门交互"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set door true " + dominion.getName() + " " + page))
                    .append("门交互"));
        }
        if (dominion.getDye()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set dye false " + dominion.getName() + " " + page))
                    .append("染色"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set dye true " + dominion.getName() + " " + page))
                    .append("染色"));
        }
        if (dominion.getEgg()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set egg false " + dominion.getName() + " " + page))
                    .append("投掷鸡蛋"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set egg true " + dominion.getName() + " " + page))
                    .append("投掷鸡蛋"));
        }
        if (dominion.getEnchant()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set enchant false " + dominion.getName() + " " + page))
                    .append("附魔"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set enchant true " + dominion.getName() + " " + page))
                    .append("附魔"));
        }
        if (dominion.getEnderPearl()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set ender_pearl false " + dominion.getName() + " " + page))
                    .append("末影珍珠"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set ender_pearl true " + dominion.getName() + " " + page))
                    .append("末影珍珠"));
        }
        if (dominion.getFeed()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set feed false " + dominion.getName() + " " + page))
                    .append("喂食"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set feed true " + dominion.getName() + " " + page))
                    .append("喂食"));
        }
        if (dominion.getFireSpread()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set fire_spread false " + dominion.getName() + " " + page))
                    .append("火焰蔓延"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set fire_spread true " + dominion.getName() + " " + page))
                    .append("火焰蔓延"));
        }
        if (dominion.getFlowInProtection()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set flow_in_protection false " + dominion.getName() + " " + page))
                    .append("流体保护"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set flow_in_protection true " + dominion.getName() + " " + page))
                    .append("流体保护"));
        }
        if (dominion.getGlow()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set glow false " + dominion.getName() + " " + page))
                    .append("发光"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set glow true " + dominion.getName() + " " + page))
                    .append("发光"));
        }
        if (dominion.getHarvest()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set harvest false " + dominion.getName() + " " + page))
                    .append("收获"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set harvest true " + dominion.getName() + " " + page))
                    .append("收获"));
        }
        if (dominion.getHoney()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set honey false " + dominion.getName() + " " + page))
                    .append("蜂巢交互"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set honey true " + dominion.getName() + " " + page))
                    .append("蜂巢交互"));
        }
        if (dominion.getHook()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set hook false " + dominion.getName() + " " + page))
                    .append("使用钓钩"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set hook true " + dominion.getName() + " " + page))
                    .append("使用钓钩"));
        }
        if (dominion.getHopper()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set hopper false " + dominion.getName() + " " + page))
                    .append("漏斗/熔炉/发射器等特殊容器"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set hopper true " + dominion.getName() + " " + page))
                    .append("漏斗/熔炉/发射器等特殊容器"));
        }
        if (dominion.getIgnite()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set ignite false " + dominion.getName() + " " + page))
                    .append("点燃"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set ignite true " + dominion.getName() + " " + page))
                    .append("点燃"));
        }
        if (dominion.getLever()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set lever false " + dominion.getName() + " " + page))
                    .append("使用拉杆"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set lever true " + dominion.getName() + " " + page))
                    .append("使用拉杆"));
        }
        if (dominion.getMobDropItem()){
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set mob_drop_item false " + dominion.getName() + " " + page))
                    .append("生物战利品掉落"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set mob_drop_item true " + dominion.getName() + " " + page))
                    .append("生物战利品掉落"));
        }
        if (dominion.getMonsterKilling()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set monster_killing false " + dominion.getName() + " " + page))
                    .append("怪物伤害"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set monster_killing true " + dominion.getName() + " " + page))
                    .append("怪物伤害"));
        }
        if (dominion.getMove()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set move false " + dominion.getName() + " " + page))
                    .append("移动"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set move true " + dominion.getName() + " " + page))
                    .append("移动"));
        }
        if (dominion.getPlace()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set place false " + dominion.getName() + " " + page))
                    .append("放置方块"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set place true " + dominion.getName() + " " + page))
                    .append("放置方块"));
        }
        if (dominion.getPressure()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set pressure false " + dominion.getName() + " " + page))
                    .append("压力板交互"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set pressure true " + dominion.getName() + " " + page))
                    .append("压力板交互"));
        }
        if (dominion.getRiding()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set riding false " + dominion.getName() + " " + page))
                    .append("骑乘载具"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set riding true " + dominion.getName() + " " + page))
                    .append("骑乘载具"));
        }
        if (dominion.getRepeater()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set repeater false " + dominion.getName() + " " + page))
                    .append("中继器交互"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set repeater true " + dominion.getName() + " " + page))
                    .append("中继器交互"));
        }
        if (dominion.getShear()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set shear false " + dominion.getName() + " " + page))
                    .append("剪羊毛"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set shear true " + dominion.getName() + " " + page))
                    .append("剪羊毛"));
        }
        if (dominion.getShoot()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set shoot false " + dominion.getName() + " " + page))
                    .append("射箭/雪球/三叉戟"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set shoot true " + dominion.getName() + " " + page))
                    .append("射箭/雪球/三叉戟"));
        }
        if (dominion.getTntExplode()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set tnt_explode false " + dominion.getName() + " " + page))
                    .append("TNT爆炸"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set tnt_explode true " + dominion.getName() + " " + page))
                    .append("TNT爆炸"));
        }
        if (dominion.getTrade()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set trade false " + dominion.getName() + " " + page))
                    .append("交易"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set trade true " + dominion.getName() + " " + page))
                    .append("交易"));
        }
        if (dominion.getTrample()){
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set trample false " + dominion.getName() + " " + page))
                    .append("践踏耕地"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set trample true " + dominion.getName() + " " + page))
                    .append("践踏耕地"));
        }
        if (dominion.getVehicleDestroy()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set vehicle_destroy false " + dominion.getName() + " " + page))
                    .append("破坏载具"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set vehicle_destroy true " + dominion.getName() + " " + page))
                    .append("破坏载具"));
        }
        if (dominion.getVehicleSpawn()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set vehicle_spawn false " + dominion.getName() + " " + page))
                    .append("放置载具"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set vehicle_spawn true " + dominion.getName() + " " + page))
                    .append("放置载具"));
        }
        if (dominion.getWitherSpawn()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑", "/dominion set wither_spawn false " + dominion.getName() + " " + page))
                    .append("凋零生成"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐", "/dominion set wither_spawn true " + dominion.getName() + " " + page))
                    .append("凋零生成"));
        }
        view.showOn(player, page);
    }
}
