package cn.lunadeer.dominion.uis.beuis;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.GroupDTO;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.geyser.api.GeyserApi;

import java.util.ArrayList;
import java.util.List;

import static cn.lunadeer.dominion.uis.beuis.Menu.sendMainMenu;

public class TitleList {
    public static void sendTitleMenu(Player player) {

        if (Cache.instance.getBelongGroupsOf(player.getUniqueId()).isEmpty()) {
            SimpleForm.Builder emptyMenu = SimpleForm.builder()
                    .title("称号系统")
                    .content("您还未有称号！")
                    .closedOrInvalidResultHandler(response -> sendMainMenu(player));
            GeyserApi.api().sendForm(player.getUniqueId(), emptyMenu);
            return;
        }

        StringBuilder content = new StringBuilder();
        GroupDTO using = Cache.instance.getPlayerUsingGroupTitle(player.getUniqueId());
        if (using != null) {
            content.append("您当前称号为：").append(using.getNameColoredBukkit()).append("\n请选择一个操作：");
        }

        SimpleForm.Builder titleMenu = SimpleForm.builder()
                .title("称号系统")
                .content(content.toString())
                .optionalButton("卸下称号", using != null)
                .button("切换称号")
                .closedOrInvalidResultHandler(response -> {
                    sendMainMenu(player);
                })
                .validResultHandler(response -> {
                    switch (response.clickedButtonId()) {
                        case 1:
                            player.performCommand("dominion use_title -1");
                            return;
                        case 2:
                            sendTitleListMenu(player, using);
                            break;
                    }
                });
        GeyserApi.api().sendForm(player.getUniqueId(), titleMenu);
    }

    private static void sendTitleListMenu(Player player, GroupDTO using) {

        List<GroupDTO> groups = Cache.instance.getBelongGroupsOf(player.getUniqueId());
        List<DominionDTO> dominions = DominionDTO.selectByOwner(player.getUniqueId());
        for (DominionDTO dominion : dominions) {
            List<GroupDTO> groupsOfDom = GroupDTO.selectByDominionId(dominion.getId());
            groups.addAll(groupsOfDom);
        }
        List<String> titles = new ArrayList<>();
        int useTitle = 0;
        for (int i = 0; i < groups.size(); i++) {
            GroupDTO group = groups.get(i);
            if (using != null && using.getId().equals(group.getId())) {
                useTitle = i;
            }
            DominionDTO dominion = Cache.instance.getDominion(group.getDomID());
            titles.add(group.getNameColoredBukkit() + " - " + dominion.getName());
        }

        CustomForm.Builder titleListMenu = CustomForm.builder()
                .title("切换称号")
                .dropdown("可使用的权限组称号：", titles, useTitle)
                .closedOrInvalidResultHandler(response -> {
                    sendTitleMenu(player);
                })
                .validResultHandler(response -> {
                    int choose = response.asDropdown();
                    player.performCommand("dominion use_title " + groups.get(choose).getId());
                });
        GeyserApi.api().sendForm(player.getUniqueId(), titleListMenu);
    }

}
