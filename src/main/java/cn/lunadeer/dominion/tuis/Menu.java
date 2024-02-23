package cn.lunadeer.dominion.tuis;

import cn.lunadeer.dominion.utils.STUI.Button;
import cn.lunadeer.dominion.utils.STUI.Line;
import cn.lunadeer.dominion.utils.STUI.View;
import cn.lunadeer.dominion.utils.STUI.ViewStyles;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.commands.Apis.playerOnly;

public class Menu {
    public static void show(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        Line list = Line.create()
                .append(Button.create("我的领地", "/dominion list"))
                .append("查看我的领地");
        Line help = Line.create()
                .append(Button.create("指令帮助", "/dominion help"))
                .append("查看指令帮助");
        Line link = Line.create()
                .append(Component.text("[使用文档]", ViewStyles.action_color)
                        .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, "https://ssl.lunadeer.cn:14448/doc/23/")))
                .append("在浏览器中打开使用文档");
        View view = View.create();
        view.title("Dominion 领地系统")
                .navigator(Line.create().append("主菜单"))
                .addLine(list)
                .addLine(help)
                .addLine(link)
                .showOn(player);
    }
}
