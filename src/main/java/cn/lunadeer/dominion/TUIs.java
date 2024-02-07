package cn.lunadeer.dominion;

import cn.lunadeer.dominion.commands.Helper;
import cn.lunadeer.dominion.controllers.PlayerController;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.PlayerDTO;
import cn.lunadeer.dominion.dtos.PlayerPrivilegeDTO;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.STUI.Button;
import cn.lunadeer.dominion.utils.STUI.Line;
import cn.lunadeer.dominion.utils.STUI.ListView;
import cn.lunadeer.dominion.utils.STUI.View;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static cn.lunadeer.dominion.commands.Apis.playerOnly;
import static cn.lunadeer.dominion.commands.Helper.playerDominions;
import static cn.lunadeer.dominion.controllers.Apis.getPlayerCurrentDominion;

public class TUIs {
    private static int getPage(String[] args) {
        int page = 1;
        if (args.length == 3) {
            try {
                page = Integer.parseInt(args[2]);
            } catch (Exception e) {
                return 1;
            }
        }
        return page;
    }

    private static DominionDTO getDominion(Player player, String[] args) {
        if (args.length == 3) {
            return DominionDTO.select(args[2]);
        } else {
            return getPlayerCurrentDominion(player);
        }
    }

    private static boolean noAuthToManage(Player player, DominionDTO dominion) {
        if (player.isOp()) return false;
        if (!dominion.getOwner().equals(player.getUniqueId())) {
            List<PlayerPrivilegeDTO> privileges = PlayerPrivilegeDTO.select(player.getUniqueId(), dominion.getId());
            for (PlayerPrivilegeDTO privilege : privileges) {
                if (privilege.getAdmin()) return false;
            }
            Notification.error(player, "你不是领地 " + dominion.getName() + " 的拥有者或管理员，无权访问此页面");
            return true;
        } else {
            return false;
        }
    }

    public static void printHelp(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        int page = getPage(args);
        ListView view = ListView.create(5, "/dominion help");
        view.title("领地插件命令帮助")
                .add(Line.create().append("打开交互菜单").append(Button.create("/dominion menu", "/dominion menu")))
                .add(Line.create().append("列出所有领地").append(Button.create("/dominion list", "/dominion list")))
                .add(Line.create().append("查看帮助").append(Button.create("/dominion help [页码]", "/dominion help 1")))
                .add(Line.create().append("查看领地信息").append(Button.create("/dominion info [领地名称]", "/dominion info")))
                .add(Line.create().append("查看领地权限信息").append(Button.create("/dominion flag_info <领地名称> [页码]", "/dominion flag_info")))
                .add(Line.create().append("管理领地").append("/dominion manage <领地名称>"))
                .add(Line.create().append("创建领地").append("/dominion create <领地名称>"))
                .add(Line.create().append("自动创建领地").append("/dominion auto_create <领地名称>"))
                .add(Line.create().append("创建子领地").append("/dominion create_sub <子领地名称> [父领地名称]"))
                .add(Line.create().append("自动创建子领地").append("/dominion auto_create_sub <子领地名称> [父领地名称]"))
                .add(Line.create().append("扩张领地").append("/dominion expand [大小] [领地名称]"))
                .add(Line.create().append("缩小领地").append("/dominion contract [大小] [领地名称]"))
                .add(Line.create().append("删除领地").append("/dominion delete <领地名称> [force]"))
                .add(Line.create().append("设置领地权限").append("/dominion set <权限名称> <true/false> [领地名称]"))
                .add(Line.create().append("设置玩家权限").append("/dominion set_privilege <玩家名称> <权限名称> <true/false> [领地名称]"))
                .add(Line.create().append("重置玩家权限").append("/dominion clear_privilege <玩家名称> [领地名称]"))
                .add(Line.create().append("查看领地玩家权限").append("/dominion privilege_list <领地名称> [页码]"))
                .add(Line.create().append("查看权限组").append("/dominion group [页码]"))
                .add(Line.create().append("创建权限组").append("/dominion create_group <权限组名称>"))
                .add(Line.create().append("删除权限组").append("/dominion delete_group <权限组名称>"))
                .add(Line.create().append("设置权限组权限").append("/dominion set_group <权限组名称> <权限名称> <true/false>"))
                .add(Line.create().append("设置玩家在某个领地归属的权限组").append("/dominion add_player <玩家名称> <权限组名称> [领地名称]"))
                .add(Line.create().append("删除玩家在某个领地归属的权限组").append("/dominion remove_player <玩家名称> <权限组名称> [领地名称]"))
                .add(Line.create().append("查看领地的权限组").append("/dominion group_list <领地名称> [页码]"))
                .showOn(player, page);
    }

    public static void sizeInfo(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        DominionDTO dominion = getDominion(player, args);
        if (dominion == null) {
            Notification.error(sender, "你不在任何领地内，请指定领地名称 /dominion info <领地名称>");
            return;
        }
        PlayerDTO owner = PlayerController.getPlayerDTO(dominion.getOwner());
        Integer x1 = dominion.getX1();
        Integer y1 = dominion.getY1();
        Integer z1 = dominion.getZ1();
        Integer x2 = dominion.getX2();
        Integer y2 = dominion.getY2();
        Integer z2 = dominion.getZ2();
        View view = View.create();
        view.title("领地 " + dominion.getName() + " 的尺寸信息")
                .subtitle("领地归属：" + owner.getLastKnownName())
                .addLine(Line.create().append("领地大小：").append((x2 - x1) + " x" + (y2 - y1) + " x" + (z2 - z1)))
                .addLine(Line.create().append("中心坐标：").append((x1 + (x2 - x1) / 2) + " " + (y1 + (y2 - y1) / 2) + " " + (z1 + (z2 - z1) / 2)))
                .addLine(Line.create().append("垂直高度：").append(String.valueOf(y2 - y1)))
                .addLine(Line.create().append("水平面积：").append(String.valueOf((x2 - x1) * (z2 - z1))))
                .addLine(Line.create().append("领地体积：").append(String.valueOf((x2 - x1) * (y2 - y1) * (z2 - z1))))
                .actionBar(Line.create()
                        .append(Button.create("管理界面", "/dominion manage " + dominion.getName()))
                        .append(Button.create("权限列表", "/dominion flag_info " + dominion.getName())))
                .showOn(player);
    }

    public static void group(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        int page = getPage(args);
        // todo
    }

    public static void menu(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        Line list = Line.create()
                .append(Button.create("我的领地", "/dominion list"))
                .append("查看我的领地");
        Line group = Line.create()
                .append(Button.create("权限组", "/dominion group"))
                .append("管理权限组");
        Line help = Line.create()
                .append(Button.create("指令帮助", "/dominion help"))
                .append("查看指令帮助");
        View view = View.create();
        view.title("Dominion 领地系统")
                .subtitle("主菜单")
                .addLine(list)
                .addLine(group)
                .addLine(help)
                .showOn(player);
    }

    public static void list(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        int page = getPage(args);
        ListView view = ListView.create(5, "/dominion list");
        List<String> dominions = playerDominions(sender);
        if (dominions.isEmpty()) {
            Notification.warn(sender, "你没有任何领地");
            return;
        }
        for (String dominion : dominions) {
            TextComponent manage = Button.createGreen("管理", "/dominion manage " + dominion);
            TextComponent delete = Button.createRed("删除", "/dominion delete " + dominion);
            view.add(Line.create().append(dominion).append(manage).append(delete));
        }
        view.showOn(player, page);
    }

    public static void flagInfo(CommandSender sender, String[] args) {
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
        ListView view = ListView.create(6, "/dominion flag_info " + dominion.getName());
        view.title("领地 " + dominion.getName() + " 默认权限")
                .subtitle(Button.create("前往管理界面", "/dominion manage " + dominion.getName()));
        if (dominion.getAnchor()) {
            view.add(Line.create().append("重生锚").append(Button.createRed("禁用", "/dominion set anchor false " + dominion.getName())));
        } else {
            view.add(Line.create().append("重生锚").append(Button.createGreen("启用", "/dominion set anchor true " + dominion.getName())));
        }
        if (dominion.getAnimalKilling()) {
            view.add(Line.create().append("动物伤害").append(Button.createRed("禁用", "/dominion set animal_killing false " + dominion.getName())));
        } else {
            view.add(Line.create().append("动物伤害").append(Button.createGreen("启用", "/dominion set animal_killing true " + dominion.getName())));
        }
        if (dominion.getAnvil()) {
            view.add(Line.create().append("使用铁砧").append(Button.createRed("禁用", "/dominion set anvil false " + dominion.getName())));
        } else {
            view.add(Line.create().append("使用铁砧").append(Button.createGreen("启用", "/dominion set anvil true " + dominion.getName())));
        }
        if (dominion.getBeacon()) {
            view.add(Line.create().append("信标交互").append(Button.createRed("禁用", "/dominion set beacon false " + dominion.getName())));
        } else {
            view.add(Line.create().append("信标交互").append(Button.createGreen("启用", "/dominion set beacon true " + dominion.getName())));
        }
        if (dominion.getBed()) {
            view.add(Line.create().append("床交互").append(Button.createRed("禁用", "/dominion set bed false " + dominion.getName())));
        } else {
            view.add(Line.create().append("床交互").append(Button.createGreen("启用", "/dominion set bed true " + dominion.getName())));
        }
        if (dominion.getBrew()) {
            view.add(Line.create().append("使用酿造台").append(Button.createRed("禁用", "/dominion set brew false " + dominion.getName())));
        } else {
            view.add(Line.create().append("使用酿造台").append(Button.createGreen("启用", "/dominion set brew true " + dominion.getName())));
        }
        if (dominion.getButton()) {
            view.add(Line.create().append("使用按钮").append(Button.createRed("禁用", "/dominion set button false " + dominion.getName())));
        } else {
            view.add(Line.create().append("使用按钮").append(Button.createGreen("启用", "/dominion set button true " + dominion.getName())));
        }
        if (dominion.getCake()) {
            view.add(Line.create().append("使用蛋糕").append(Button.createRed("禁用", "/dominion set cake false " + dominion.getName())));
        } else {
            view.add(Line.create().append("使用蛋糕").append(Button.createGreen("启用", "/dominion set cake true " + dominion.getName())));
        }
        if (dominion.getContainer()) {
            view.add(Line.create().append("容器交互").append(Button.createRed("禁用", "/dominion set container false " + dominion.getName())));
        } else {
            view.add(Line.create().append("容器交互").append(Button.createGreen("启用", "/dominion set container true " + dominion.getName())));
        }
        if (dominion.getCraft()) {
            view.add(Line.create().append("合成").append(Button.createRed("禁用", "/dominion set craft false " + dominion.getName())));
        } else {
            view.add(Line.create().append("合成").append(Button.createGreen("启用", "/dominion set craft true " + dominion.getName())));
        }
        if (dominion.getCreeperExplode()) {
            view.add(Line.create().append("苦力怕爆炸").append(Button.createRed("禁用", "/dominion set creeper_explode false " + dominion.getName())));
        } else {
            view.add(Line.create().append("苦力怕爆炸").append(Button.createGreen("启用", "/dominion set creeper_explode true " + dominion.getName())));
        }
        if (dominion.getDiode()) {
            view.add(Line.create().append("中继器交互").append(Button.createRed("禁用", "/dominion set diode false " + dominion.getName())));
        } else {
            view.add(Line.create().append("中继器交互").append(Button.createGreen("启用", "/dominion set diode true " + dominion.getName())));
        }
        if (dominion.getDoor()) {
            view.add(Line.create().append("门交互").append(Button.createRed("禁用", "/dominion set door false " + dominion.getName())));
        } else {
            view.add(Line.create().append("门交互").append(Button.createGreen("启用", "/dominion set door true " + dominion.getName())));
        }
        if (dominion.getDye()) {
            view.add(Line.create().append("染色").append(Button.createRed("禁用", "/dominion set dye false " + dominion.getName())));
        } else {
            view.add(Line.create().append("染色").append(Button.createGreen("启用", "/dominion set dye true " + dominion.getName())));
        }
        if (dominion.getEgg()) {
            view.add(Line.create().append("投掷鸡蛋").append(Button.createRed("禁用", "/dominion set egg false " + dominion.getName())));
        } else {
            view.add(Line.create().append("投掷鸡蛋").append(Button.createGreen("启用", "/dominion set egg true " + dominion.getName())));
        }
        if (dominion.getEnchant()) {
            view.add(Line.create().append("附魔").append(Button.createRed("禁用", "/dominion set enchant false " + dominion.getName())));
        } else {
            view.add(Line.create().append("附魔").append(Button.createGreen("启用", "/dominion set enchant true " + dominion.getName())));
        }
        if (dominion.getEnderPearl()) {
            view.add(Line.create().append("末影珍珠").append(Button.createRed("禁用", "/dominion set ender_pearl false " + dominion.getName())));
        } else {
            view.add(Line.create().append("末影珍珠").append(Button.createGreen("启用", "/dominion set ender_pearl true " + dominion.getName())));
        }
        if (dominion.getFeed()) {
            view.add(Line.create().append("喂食").append(Button.createRed("禁用", "/dominion set feed false " + dominion.getName())));
        } else {
            view.add(Line.create().append("喂食").append(Button.createGreen("启用", "/dominion set feed true " + dominion.getName())));
        }
        if (dominion.getFireSpread()) {
            view.add(Line.create().append("火焰蔓延").append(Button.createRed("禁用", "/dominion set fire_spread false " + dominion.getName())));
        } else {
            view.add(Line.create().append("火焰蔓延").append(Button.createGreen("启用", "/dominion set fire_spread true " + dominion.getName())));
        }
        if (dominion.getFlowInProtection()) {
            view.add(Line.create().append("流体保护").append(Button.createRed("禁用", "/dominion set flow_in_protection false " + dominion.getName())));
        } else {
            view.add(Line.create().append("流体保护").append(Button.createGreen("启用", "/dominion set flow_in_protection true " + dominion.getName())));
        }
        if (dominion.getGlow()) {
            view.add(Line.create().append("发光").append(Button.createRed("禁用", "/dominion set glow false " + dominion.getName())));
        } else {
            view.add(Line.create().append("发光").append(Button.createGreen("启用", "/dominion set glow true " + dominion.getName())));
        }
        if (dominion.getGrow()) {
            view.add(Line.create().append("植物生长").append(Button.createRed("禁用", "/dominion set grow false " + dominion.getName())));
        } else {
            view.add(Line.create().append("植物生长").append(Button.createGreen("启用", "/dominion set grow true " + dominion.getName())));
        }
        if (dominion.getHoney()) {
            view.add(Line.create().append("蜂巢交互").append(Button.createRed("禁用", "/dominion set honey false " + dominion.getName())));
        } else {
            view.add(Line.create().append("蜂巢交互").append(Button.createGreen("启用", "/dominion set honey true " + dominion.getName())));
        }
        if (dominion.getHook()) {
            view.add(Line.create().append("钩子交互").append(Button.createRed("禁用", "/dominion set hook false " + dominion.getName())));
        } else {
            view.add(Line.create().append("钩子交互").append(Button.createGreen("启用", "/dominion set hook true " + dominion.getName())));
        }
        if (dominion.getIgnite()) {
            view.add(Line.create().append("点燃").append(Button.createRed("禁用", "/dominion set ignite false " + dominion.getName())));
        } else {
            view.add(Line.create().append("点燃").append(Button.createGreen("启用", "/dominion set ignite true " + dominion.getName())));
        }
        if (dominion.getMobKilling()) {
            view.add(Line.create().append("生物伤害").append(Button.createRed("禁用", "/dominion set mob_killing false " + dominion.getName())));
        } else {
            view.add(Line.create().append("生物伤害").append(Button.createGreen("启用", "/dominion set mob_killing true " + dominion.getName())));
        }
        if (dominion.getMove()) {
            view.add(Line.create().append("移动").append(Button.createRed("禁用", "/dominion set move false " + dominion.getName())));
        } else {
            view.add(Line.create().append("移动").append(Button.createGreen("启用", "/dominion set move true " + dominion.getName())));
        }
        if (dominion.getPlace()) {
            view.add(Line.create().append("放置").append(Button.createRed("禁用", "/dominion set place false " + dominion.getName())));
        } else {
            view.add(Line.create().append("放置").append(Button.createGreen("启用", "/dominion set place true " + dominion.getName())));
        }
        if (dominion.getPressure()) {
            view.add(Line.create().append("压力板交互").append(Button.createRed("禁用", "/dominion set pressure false " + dominion.getName())));
        } else {
            view.add(Line.create().append("压力板交互").append(Button.createGreen("启用", "/dominion set pressure true " + dominion.getName())));
        }
        if (dominion.getRiding()) {
            view.add(Line.create().append("骑乘").append(Button.createRed("禁用", "/dominion set riding false " + dominion.getName())));
        } else {
            view.add(Line.create().append("骑乘").append(Button.createGreen("启用", "/dominion set riding true " + dominion.getName())));
        }
        if (dominion.getShear()) {
            view.add(Line.create().append("剪羊毛").append(Button.createRed("禁用", "/dominion set shear false " + dominion.getName())));
        } else {
            view.add(Line.create().append("剪羊毛").append(Button.createGreen("启用", "/dominion set shear true " + dominion.getName())));
        }
        if (dominion.getShoot()) {
            view.add(Line.create().append("发射").append(Button.createRed("禁用", "/dominion set shoot false " + dominion.getName())));
        } else {
            view.add(Line.create().append("发射").append(Button.createGreen("启用", "/dominion set shoot true " + dominion.getName())));
        }
        if (dominion.getTntExplode()) {
            view.add(Line.create().append("TNT爆炸").append(Button.createRed("禁用", "/dominion set tnt_explode false " + dominion.getName())));
        } else {
            view.add(Line.create().append("TNT爆炸").append(Button.createGreen("启用", "/dominion set tnt_explode true " + dominion.getName())));
        }
        if (dominion.getTrade()) {
            view.add(Line.create().append("交易").append(Button.createRed("禁用", "/dominion set trade false " + dominion.getName())));
        } else {
            view.add(Line.create().append("交易").append(Button.createGreen("启用", "/dominion set trade true " + dominion.getName())));
        }
        if (dominion.getVehicleDestroy()) {
            view.add(Line.create().append("破坏载具").append(Button.createRed("禁用", "/dominion set vehicle_destroy false " + dominion.getName())));
        } else {
            view.add(Line.create().append("破坏载具").append(Button.createGreen("启用", "/dominion set vehicle_destroy true " + dominion.getName())));
        }
        if (dominion.getWitherSpawn()) {
            view.add(Line.create().append("凋零生成").append(Button.createRed("禁用", "/dominion set wither_spawn false " + dominion.getName())));
        } else {
            view.add(Line.create().append("凋零生成").append(Button.createGreen("启用", "/dominion set wither_spawn true " + dominion.getName())));
        }
        if (dominion.getHarvest()) {
            view.add(Line.create().append("收获").append(Button.createRed("禁用", "/dominion set harvest false " + dominion.getName())));
        } else {
            view.add(Line.create().append("收获").append(Button.createGreen("启用", "/dominion set harvest true " + dominion.getName())));
        }
        view.showOn(player, page);
    }

    public static void manage(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        DominionDTO dominion = getDominion(player, args);
        if (dominion == null) {
            Notification.error(sender, "你不在任何领地内，请指定领地名称 /dominion manage <领地名称>");
            return;
        }
        if (noAuthToManage(player, dominion)) return;
        Line size_info = Line.create()
                .append(Button.create("尺寸信息", "/dominion info " + dominion.getName()))
                .append("查看领地尺寸信息");
        Line flag_info = Line.create()
                .append(Button.create("权限设置", "/dominion flag_info " + dominion.getName()))
                .append("管理领地默认权限");
        Line group_info = Line.create()
                .append(Button.create("权限组", "/dominion group_list " + dominion.getName()))
                .append("管理领地权限组");
        Line privilege_list = Line.create()
                .append(Button.create("玩家权限", "/dominion privilege_list " + dominion.getName()))
                .append("管理玩家特权");
        View view = View.create();
        view.title("领地 " + dominion.getName() + " 管理界面")
                .subtitle(Button.createRed("领地列表", "/dominion list"))
                .addLine(size_info)
                .addLine(flag_info)
                .addLine(group_info)
                .addLine(privilege_list)
                .showOn(player);
    }

    public static void groupList(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        if (args.length < 2) {
            Notification.error(sender, "用法: /dominion group_list <领地名称> [页码]");
            return;
        }
        DominionDTO dominion = DominionDTO.select(args[1]);
        if (dominion == null) {
            Notification.error(sender, "领地 " + args[1] + " 不存在");
            return;
        }
        if (noAuthToManage(player, dominion)) return;
        int page = 1;
        if (args.length == 3) {
            try {
                page = Integer.parseInt(args[2]);
            } catch (Exception ignored) {
            }
        }
        ListView view = ListView.create(6, "/dominion group_list " + dominion.getName());
        view.title("领地 " + dominion.getName() + " 权限组信息")
                .subtitle(Button.create("返回管理界面", "/dominion manage " + dominion.getName()));
        // todo
        view.showOn(player, page);
    }

    public static void privilegeList(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        if (args.length < 2) {
            Notification.error(sender, "用法: /dominion privilege_list <领地名称> [页码]");
            return;
        }
        DominionDTO dominion = DominionDTO.select(args[1]);
        if (dominion == null) {
            Notification.error(sender, "领地 " + args[1] + " 不存在");
            return;
        }
        if (noAuthToManage(player, dominion)) return;
        int page = 1;
        if (args.length == 3) {
            try {
                page = Integer.parseInt(args[2]);
            } catch (Exception ignored) {
            }
        }
        ListView view = ListView.create(6, "/dominion privilege_list " + dominion.getName());
        view.title("领地 " + dominion.getName() + " 玩家权限信息")
                .subtitle(Button.create("返回管理界面", "/dominion manage " + dominion.getName()));
        // todo
        view.showOn(player, page);
    }
}
