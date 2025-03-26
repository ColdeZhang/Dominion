package cn.lunadeer.dominion.uis.tuis.template;

import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.doos.TemplateDOO;
import cn.lunadeer.dominion.uis.cuis.CreateTemplate;
import cn.lunadeer.dominion.uis.tuis.MainMenu;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import cn.lunadeer.dominion.utils.stui.ListView;
import cn.lunadeer.dominion.utils.stui.components.Line;
import cn.lunadeer.dominion.utils.stui.components.buttons.Button;
import cn.lunadeer.dominion.utils.stui.components.buttons.ListViewButton;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static cn.lunadeer.dominion.Dominion.defaultPermission;
import static cn.lunadeer.dominion.commands.TemplateCommand.deleteTemplate;
import static cn.lunadeer.dominion.misc.Converts.toIntegrity;
import static cn.lunadeer.dominion.misc.Converts.toPlayer;

public class TemplateList {

    public static class TemplateListTuiText extends ConfigurationPart {
        public String title = "Template List";
        public String button = "TEMPLATES";
        public String description = "Templates can be used to quickly setup privileges of member.";
        public String deleteButton = "DELETE";
    }

    public static ListViewButton button(CommandSender sender) {
        return (ListViewButton) new ListViewButton(Language.templateListTuiText.button) {
            @Override
            public void function(String pageStr) {
                show(sender, pageStr);
            }
        }.needPermission(defaultPermission);
    }

    public static void show(CommandSender sender, String pageStr) {
        try {
            Player player = toPlayer(sender);
            int page = toIntegrity(pageStr, 1);
            List<TemplateDOO> templates = TemplateDOO.selectAll(player.getUniqueId());

            ListView view = ListView.create(10, button(sender));
            view.title(Language.templateListTuiText.title);
            view.navigator(Line.create()
                    .append(MainMenu.button(sender).build())
                    .append(Language.templateListTuiText.button));

            view.add(Line.create().append(CreateTemplate.button(sender).build()));

            for (TemplateDOO template : templates) {
                Button setting = TemplateSetting.button(sender, template.getName()).green();
                Button delete = new ListViewButton(Language.templateListTuiText.deleteButton) {
                    @Override
                    public void function(String pageStr) {
                        deleteTemplate(sender, template.getName(), pageStr);
                    }
                }.needPermission(defaultPermission).red();
                Line line = Line.create()
                        .append(delete.build())
                        .append(setting.build())
                        .append(template.getName());
                view.add(line);
            }

            view.showOn(player, page);
        } catch (Exception e) {
            Notification.error(sender, e.getMessage());
        }
    }

}
