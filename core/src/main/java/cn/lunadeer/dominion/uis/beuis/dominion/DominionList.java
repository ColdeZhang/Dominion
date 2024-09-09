package cn.lunadeer.dominion.uis.beuis.dominion;

import cn.lunadeer.dominion.DominionNode;
import cn.lunadeer.dominion.dtos.DominionDTO;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.geyser.api.GeyserApi;

import java.util.ArrayList;
import java.util.List;

import static cn.lunadeer.dominion.commands.Helper.playerAdminDominions;
import static cn.lunadeer.dominion.uis.beuis.Menu.sendMainMenu;

public class DominionList {
    public static void sendDominionMenu(Player player) {
        List<DominionNode> my_Dominions = DominionNode.BuildNodeTree(-1, DominionDTO.selectByOwner(player.getUniqueId()));
        List<String> admin_dominions = playerAdminDominions(player);

        if (admin_dominions.isEmpty()) {
            if (my_Dominions.isEmpty()) {
                SimpleForm.Builder emptyMenu = SimpleForm.builder()
                        .title("我的领地")
                        .content("您还未有任何领地")
                        .closedOrInvalidResultHandler(response -> sendMainMenu(player));
                GeyserApi.api().sendForm(player.getUniqueId(), emptyMenu);
                return;
            }
            CheckListMenu(player, my_Dominions, 0);
            return;
        }
        if (my_Dominions.isEmpty()) {
            sendAdminDominionMenu(player, admin_dominions);
            return;
        }

        SimpleForm.Builder dominionMenu = SimpleForm.builder()
                .title("我的领地")
                .content("请选择一个操作：")
                .optionalButton("我创建的领地", !my_Dominions.isEmpty())
                .optionalButton("可管理的领地", !admin_dominions.isEmpty())
                .closedOrInvalidResultHandler(response -> sendMainMenu(player))
                .validResultHandler(response -> {
                    switch (response.clickedButtonId()) {
                        case 1:
                            CheckListMenu(player, my_Dominions, 0);
                            return;
                        case 2:
                            sendAdminDominionMenu(player, admin_dominions);
                            break;
                    }
                });
        GeyserApi.api().sendForm(player.getUniqueId(), dominionMenu);
    }

    private static void sendAdminDominionMenu(Player player, List<String> dominions) {
        SimpleForm.Builder adminDominionMenu = SimpleForm.builder()
                .title("领地查询")
                .content("查询到以下领地：")
                .closedOrInvalidResultHandler(response -> sendMainMenu(player));
        for (String dominion : dominions) {
            adminDominionMenu.button(dominion);
        }
        adminDominionMenu.validResultHandler(response -> {
            player.performCommand("dominion manage " + response.clickedButton().text());
        });
        GeyserApi.api().sendForm(player.getUniqueId(), adminDominionMenu);
    }

    public static void CheckListMenu(Player player, List<DominionNode> node, int depth) {
        SimpleForm.Builder checkListMenu = SimpleForm.builder()
                .title("领地查询")
                .content("查询到以下领地:")
                .closedOrInvalidResultHandler(response -> {
                    if (depth < 1) {
                        sendDominionMenu(player);
                        return;
                    }
                    CheckListMenu(player, node, depth - 1);
                });

        List<DominionNode> lines = new ArrayList<>();
        for (DominionNode line : node) {
            checkListMenu.button(line.getDominion().getName());
            lines.add(line);
        }
        checkListMenu.validResultHandler(response -> {
            ControlDominionMenu(player, node, lines.get(response.clickedButtonId() - 1), depth);
        });
        GeyserApi.api().sendForm(player.getUniqueId(), checkListMenu);
    }

    public static void ControlDominionMenu(Player player, List<DominionNode> node, DominionNode child, int depth) {
        if (child.getChildren().isEmpty()) {
            player.performCommand("dominion manager " + child.getDominion().getName());
            return;
        }
        SimpleForm.Builder ControlDominionMenu = SimpleForm.builder()
                .title("领地操作 - " + child.getDominion().getName())
                .content("请选择一个操作：")
                .button("管理该领地")
                .button("查询子领地")
                .closedOrInvalidResultHandler(response -> {
                    CheckListMenu(player, node, depth);
                })
                .validResultHandler(response -> {
                    switch (response.clickedButtonId()) {
                        case 1:
                            player.performCommand("dominion manager " + child.getDominion().getName());
                            return;
                        case 2:
                            CheckListMenu(player, child.getChildren(), depth + 1);
                            break;
                    }
                });
        GeyserApi.api().sendForm(player.getUniqueId(), ControlDominionMenu);
    }
}
