package cn.lunadeer.dominion.uis.tuis.template;

import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.api.dtos.flag.PriFlag;
import cn.lunadeer.dominion.commands.TemplateCommand;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.doos.TemplateDOO;
import cn.lunadeer.dominion.uis.tuis.MainMenu;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import cn.lunadeer.dominion.utils.stui.ListView;
import cn.lunadeer.dominion.utils.stui.components.Line;
import cn.lunadeer.dominion.utils.stui.components.buttons.FunctionalButton;
import cn.lunadeer.dominion.utils.stui.components.buttons.ListViewButton;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.Dominion.defaultPermission;
import static cn.lunadeer.dominion.misc.Converts.toIntegrity;
import static cn.lunadeer.dominion.misc.Converts.toPlayer;

public class TemplateSetting {

    public static class TemplateSettingText extends ConfigurationPart {
        public String title = "Template Setting";
        public String button = "SETTING";
        public String notFound = "Template {0} not found.";
    }

    public static ListViewButton button(CommandSender sender, String templateName) {
        return (ListViewButton) new ListViewButton(Language.templateSettingText.button) {
            @Override
            public void function(String pageStr) {
                show(sender, templateName, pageStr);
            }
        }.needPermission(defaultPermission);
    }

    public static void show(CommandSender sender, String templateName, String pageStr) {
        try {
            Player player = toPlayer(sender);
            TemplateDOO template = TemplateDOO.select(player.getUniqueId(), templateName);
            if (template == null) {
                Notification.error(sender, Language.templateSettingText.notFound, templateName);
                return;
            }
            ListView view = ListView.create(10, button(sender, templateName));
            view.title(Language.templateSettingText.title);
            view.navigator(Line.create()
                    .append(MainMenu.button(sender).build())
                    .append(TemplateList.button(sender).build())
                    .append(Language.templateSettingText.button)
            );
            // /dominion template_set_flag <模板名称> <权限名称> <true/false> [页码]
            for (PriFlag flag : Flags.getAllPriFlagsEnable()) {
                view.add(createOption(sender, flag, template.getFlagValue(flag), template.getName(), pageStr));
            }
            view.showOn(player, toIntegrity(pageStr));
        } catch (Exception e) {
            Notification.error(sender, e.getMessage());
        }
    }

    private static Line createOption(CommandSender sender, PriFlag flag, boolean value, String templateName, String pageStr) {
        if (value) {
            return Line.create()
                    .append(new FunctionalButton("☑") {
                        @Override
                        public void function() {
                            TemplateCommand.setTemplateFlag(sender, templateName, flag.getFlagName(), "false", pageStr);
                        }
                    }.needPermission(defaultPermission).green().build())
                    .append(Component.text(flag.getDisplayName()).hoverEvent(Component.text(flag.getDescription())));
        } else {
            return Line.create()
                    .append(new FunctionalButton("☐") {
                        @Override
                        public void function() {
                            TemplateCommand.setTemplateFlag(sender, templateName, flag.getFlagName(), "true", pageStr);
                        }
                    }.needPermission(defaultPermission).red().build())
                    .append(Component.text(flag.getDisplayName()).hoverEvent(Component.text(flag.getDescription())));
        }
    }
}
