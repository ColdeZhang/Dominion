package cn.lunadeer.dominion.cuis;

import cn.lunadeer.dominion.controllers.BukkitPlayerOperator;
import cn.lunadeer.dominion.controllers.TemplateController;
import cn.lunadeer.dominion.tuis.TemplateList;
import cn.lunadeer.minecraftpluginutils.XLogger;
import cn.lunadeer.minecraftpluginutils.scui.CuiTextInput;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.commands.Apis.playerOnly;

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
            operator.getResponse().thenAccept(result -> {
                TemplateList.show(sender, new String[]{"template_list"});
            });
            TemplateController.createTemplate(operator, input);
        }
    }

    public static void open(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        CuiTextInput.InputCallback createTemplateCB = new createTemplateCB(player);
        CuiTextInput view = CuiTextInput.create(createTemplateCB).setText("未命名模板").title("输入模板名称");
        view.setSuggestCommand("/dominion template_create <模板名称>");
        view.open(player);
    }

}
