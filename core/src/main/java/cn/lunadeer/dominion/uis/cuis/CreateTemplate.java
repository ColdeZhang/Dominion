package cn.lunadeer.dominion.uis.cuis;

import cn.lunadeer.dominion.commands.TemplateCommand;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.uis.tuis.template.TemplateList;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import cn.lunadeer.dominion.utils.scui.CuiTextInput;
import cn.lunadeer.dominion.utils.stui.components.buttons.FunctionalButton;
import cn.lunadeer.dominion.utils.stui.components.buttons.PermissionButton;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.Dominion.defaultPermission;
import static cn.lunadeer.dominion.misc.Converts.toPlayer;


public class CreateTemplate {

    public static class CreateTemplateCuiText extends ConfigurationPart {
        public String title = "Create Template";
        public String input = "Enter new template name";
        public String button = "CREATE";
    }

    public static PermissionButton button(CommandSender sender) {
        return new FunctionalButton(Language.createTemplateCuiText.button) {
            @Override
            public void function() {
                open(sender);
            }
        }.needPermission(defaultPermission);
    }

    private record createTemplateCB(Player sender) implements CuiTextInput.InputCallback {
        @Override
        public void handleData(String input) {
            TemplateCommand.createTemplate(sender, input);
            TemplateList.show(sender, "1");
        }
    }

    public static void open(CommandSender sender) {
        try {
            Player player = toPlayer(sender);
            CuiTextInput.InputCallback createTemplateCB = new createTemplateCB(player);
            CuiTextInput view = CuiTextInput.create(createTemplateCB)
                    .setText(Language.createTemplateCuiText.input)
                    .title(Language.createTemplateCuiText.title);
            view.setSuggestCommand(TemplateCommand.createTemplate.getUsage());
            view.open(player);
        } catch (Exception e) {
            Notification.error(sender, e.getMessage());
        }
    }

}
