package cn.lunadeer.dominion.uis.tuis;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.managers.Translation;
import cn.lunadeer.minecraftpluginutils.stui.ListView;
import cn.lunadeer.minecraftpluginutils.stui.ViewStyles;
import cn.lunadeer.minecraftpluginutils.stui.components.Button;
import cn.lunadeer.minecraftpluginutils.stui.components.Line;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.utils.CommandUtils.playerOnly;

/**
 * 显示聊天框主菜单GUI
 */
public class Menu {

    /**
     * 在聊天栏显示主菜单，包含各指令的快捷按钮和简单介绍
     *
     * @param sender 命令的来源
     * @param args   传递的命令参数
     */
    public static void show(CommandSender sender, String[] args) {
        //调用playerOnly方法验证sender对象是否为Player并存储在player变量中
        Player player = playerOnly(sender);
        //如果player为null直接return
        if (player == null) return;

        //初始化页码为1
        int page = 1;
        if (args.length == 2) {
            try {
                //确定要显示的页码
                page = Integer.parseInt(args[1]);
            } catch (Exception ignored) {
            }
        }

        //链式创建菜单中的标题以及各命令的按钮和指令简介组件
        Line create = Line.create()
                .append(Button.create(Translation.TUI_Menu_CreateDominionButton).setExecuteCommand("/dominion cui_create").build())
                .append(Translation.TUI_Menu_CreateDominionDescription);
        Line list = Line.create()
                .append(Button.create(Translation.TUI_Menu_MyDominionButton).setExecuteCommand("/dominion list").build())
                .append(Translation.TUI_Menu_MyDominionDescription);
        Line title = Line.create()
                .append(Button.create(Translation.TUI_Menu_TitleListButton).setExecuteCommand("/dominion title_list").build())
                .append(Translation.TUI_Menu_TitleListDescription);
        Line template = Line.create()
                .append(Button.create(Translation.TUI_Menu_TemplateListButton).setExecuteCommand("/dominion template list").build())
                .append(Translation.TUI_Menu_TemplateListDescription);
        Line help = Line.create()
                .append(Button.create(Translation.TUI_Menu_CommandHelpButton).setExecuteCommand("/dominion help").build())
                .append(Translation.TUI_Menu_CommandHelpDescription);
        Line link = Line.create()
                .append(Button.create(Translation.TUI_Menu_DocumentButton).setOpenURL(
                        String.format("https://dominion.lunadeer.cn/%s", Dominion.config.getLanguage())
                ).build())
                .append(Translation.TUI_Menu_DocumentDescription);
        Line migrate = Line.create()
                .append(Button.create(Translation.TUI_Menu_MigrateButton).setExecuteCommand("/dominion migrate_list").build())
                .append(Translation.TUI_Menu_MigrateDescription);
        Line all = Line.create()
                .append(Button.create(Translation.TUI_Menu_AllDominionButton).setExecuteCommand("/dominion all_dominion").build())
                .append(Translation.TUI_Menu_AllDominionDescription);
        Line reload_cache = Line.create()
                .append(Button.create(Translation.TUI_Menu_ReloadCacheButton).setExecuteCommand("/dominion reload_cache").build())
                .append(Translation.TUI_Menu_ReloadCacheDescription);
        Line reload_config = Line.create()
                .append(Button.create(Translation.TUI_Menu_ReloadConfigButton).setExecuteCommand("/dominion reload_config").build())
                .append(Translation.TUI_Menu_ReloadConfigDescription);

        //以下用于将上方的组件添加到菜单中

        //设置主菜单十行为一页以及触发指令
        ListView view = ListView.create(10, "/dominion menu");
        //设置主菜单标题
        view.title(Translation.TUI_Menu_Title);
        //设置当前所在菜单名称
        view.navigator(Line.create().append(Translation.TUI_Navigation_Menu));
        //添加第一行指令为创建领地指令
        view.add(create);
        //添加第二行指令为领地列表指令
        view.add(list);
        //从配置文件获取GroupTitle是否开启，如果开启添加第三行指令为称号列表，默认关闭
        if (Dominion.config.getGroupTitleEnable()) view.add(title);
        //添加第四行(如果GroupTitle开启)指令为成员权限模版列表
        view.add(template);
        //添加第五行(如果GroupTitle开启)指令为跳转帮助列表网页，其中%s会被替换为对应语言，如zh-cn
        view.add(help);
        //添加第六行(如果GroupTitle开启)指令为跳转使用帮助网页，其中%s会被替换为对应语言，如zh-cn
        view.add(link);
        //获取配置文件ResidenceMigration的boolean值
        if (Dominion.config.getResidenceMigration()) {
            //如果开启添加第七行指令为迁移数据，用于帮助从Residence迁移数据至Dominion
            view.add(migrate);
        }
        //判断player是否拥有op权限
        if (player.isOp()) {
            //添加一行空白作为间隔
            view.add(Line.create().append(""));
            //添加一行提示“--- 以下选项仅OP可见 ---”
            view.add(Line.create().append(Component.text(Translation.TUI_Menu_OpOnlySection.trans(), ViewStyles.main_color)));
            //添加指令“所有领地”用以查看所有的领地
            view.add(all);
            //添加“重载缓存”，用以重新加载领地缓存数据
            view.add(reload_cache);
            //添加“重载配置”，用以重新加载配置文件
            view.add(reload_config);
        }
        //将请求显示菜单GUI的player对象和需要显示的页码传给showOn以显示GUI
        //由showOn检查page是否存在，若不存在则会在菜单第一行添加“页码错误”
        view.showOn(player, page);
    }
}
