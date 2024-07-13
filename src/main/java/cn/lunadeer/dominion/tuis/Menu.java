package cn.lunadeer.dominion.tuis;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.minecraftpluginutils.stui.ListView;
import cn.lunadeer.minecraftpluginutils.stui.ViewStyles;
import cn.lunadeer.minecraftpluginutils.stui.components.Button;
import cn.lunadeer.minecraftpluginutils.stui.components.Line;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.commands.Apis.playerOnly;

public class Menu {
    public static void show(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;

        int page = 1;
        if (args.length == 2) {
            try {
                page = Integer.parseInt(args[1]);
            } catch (Exception ignored) {
            }
        }

        Line create = Line.create()
                .append(Button.create("创建领地").setExecuteCommand("/dominion cui_create").build())
                .append("以你为中心自动创建一个新的领地");
        Line list = Line.create()
                .append(Button.create("我的领地").setExecuteCommand("/dominion list").build())
                .append("查看我的领地");
        Line template = Line.create()
                .append(Button.create("模板列表").setExecuteCommand("/dominion template list").build())
                .append("成员权限模板列表");
        Line help = Line.create()
                .append(Button.create("指令帮助").setExecuteCommand("/dominion help").build())
                .append("查看指令帮助");
        Line link = Line.create()
                .append(Button.create("使用文档").setOpenURL("https://ssl.lunadeer.cn:14448/doc/23/").build())
                .append("在浏览器中打开使用文档");
        Line migrate = Line.create()
                .append(Button.create("迁移数据").setExecuteCommand("/dominion migrate_list").build())
                .append("把你的领地从Residence迁移到Dominion");
        Line all = Line.create()
                .append(Button.create("所有领地").setExecuteCommand("/dominion all_dominion").build())
                .append("查看所有领地");
        Line config = Line.create()
                .append(Button.create("系统配置").setExecuteCommand("/dominion sys_config").build())
                .append("查看/修改系统配置");
        Line reload_cache = Line.create()
                .append(Button.create("重载缓存").setExecuteCommand("/dominion reload_cache").build())
                .append("手动刷新缓存可解决一些玩家操作无效问题，不建议频繁操作");
        Line reload_config = Line.create()
                .append(Button.create("重载配置").setExecuteCommand("/dominion reload_config").build())
                .append("重载配置文件");
        ListView view = ListView.create(10, "/dominion menu");
        view.title("Dominion 领地系统")
                .navigator(Line.create().append("主菜单"))
                .add(create)
                .add(list)
                .add(template)
                .add(help)
                .add(link);
        if (Dominion.config.getResidenceMigration()) {
            view.add(migrate);
        }
        if (player.isOp()) {
            view.add(Line.create().append(""));
            view.add(Line.create().append(Component.text("--- 以下选项仅OP可见 ---", ViewStyles.main_color)));
            view.add(all);
            view.add(config);
            view.add(reload_cache);
            view.add(reload_config);
        }
        view.showOn(player, page);
    }
}
