package cn.lunadeer.dominion.cuis;

import cn.lunadeer.dominion.controllers.BukkitPlayerOperator;
import cn.lunadeer.dominion.controllers.TemplateController;
import cn.lunadeer.dominion.managers.Translation;
import cn.lunadeer.dominion.tuis.template.TemplateList;
import cn.lunadeer.minecraftpluginutils.XLogger;
import cn.lunadeer.minecraftpluginutils.scui.CuiTextInput;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.utils.CommandUtils.playerOnly;

public class CreateTemplate {

    private static class createTemplateCB implements CuiTextInput.InputCallback {
        private final Player sender;

        public createTemplateCB(Player sender) {
            this.sender = sender;
        }

        @Override
        public void handleData(String input) {
            XLogger.debug("createTemplateCB.run: %s", input);
            BukkitPlayerOperator operator = BukkitPlayerOperator.create(sender);
            TemplateController.createTemplate(operator, input);
            TemplateList.show(sender);
        }
    }

    public static void open(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        CuiTextInput.InputCallback createTemplateCB = new createTemplateCB(player);
        CuiTextInput view = CuiTextInput.create(createTemplateCB).setText(Translation.Commands_Template_NewTemplateName.trans()).title(Translation.CUI_Input_CreateTemplate.trans());
        view.setSuggestCommand(Translation.Commands_Template_CreateTemplateUsage.trans());
        view.open(player);
    }

}
