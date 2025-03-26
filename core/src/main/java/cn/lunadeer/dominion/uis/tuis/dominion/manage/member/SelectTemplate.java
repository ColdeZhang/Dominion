package cn.lunadeer.dominion.uis.tuis.dominion.manage.member;

import cn.lunadeer.dominion.commands.TemplateCommand;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.doos.TemplateDOO;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import cn.lunadeer.dominion.utils.stui.ListView;
import cn.lunadeer.dominion.utils.stui.components.Line;
import cn.lunadeer.dominion.utils.stui.components.buttons.FunctionalButton;
import cn.lunadeer.dominion.utils.stui.components.buttons.ListViewButton;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static cn.lunadeer.dominion.Dominion.defaultPermission;
import static cn.lunadeer.dominion.misc.Converts.toIntegrity;
import static cn.lunadeer.dominion.misc.Converts.toPlayer;

public class SelectTemplate {

    public static class SelectTemplateTuiText extends ConfigurationPart {
        public String title = "Select Template";
        public String description = "Select a template to apply to this member.";
        public String button = "SELECT TEMPLATE";
        public String back = "BACK";
        public String apply = "APPLY";
    }

    public static ListViewButton button(CommandSender sender, String dominionName, String playerName) {
        return (ListViewButton) new ListViewButton(Language.selectTemplateTuiText.button) {
            @Override
            public void function(String pageStr) {
                show(sender, dominionName, playerName, pageStr);
            }
        }.needPermission(defaultPermission).setHoverText(Language.selectTemplateTuiText.description);
    }

    public static void show(CommandSender sender, String dominionName, String playerName, String pageStr) {
        try {
            Player player = toPlayer(sender);
            int page = toIntegrity(pageStr);
            List<TemplateDOO> templates = TemplateDOO.selectAll(player.getUniqueId());

            ListView view = ListView.create(10, button(sender, dominionName, playerName));
            view.title(Language.selectTemplateTuiText.title);
            Line sub = Line.create()
                    .append(MemberSetting.button(sender, dominionName, playerName).setText(Language.selectTemplateTuiText.back).build());
            view.subtitle(sub);

            for (TemplateDOO template : templates) {
                view.add(Line.create()
                        .append(new FunctionalButton(Language.selectTemplateTuiText.apply) {
                            @Override
                            public void function() {
                                TemplateCommand.memberApplyTemplate(sender, dominionName, playerName, template.getName());
                            }
                        }.build())
                        .append(Component.text(template.getName())));
            }
            view.showOn(player, page);
        } catch (Exception e) {
            Notification.error(sender, e.getMessage());
        }
    }

}
