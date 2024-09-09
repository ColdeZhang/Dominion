package cn.lunadeer.dominion.uis.beuis;

import cn.lunadeer.dominion.Dominion;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.geyser.api.GeyserApi;

public class Menu {
    public static void sendMainMenu(Player player) {
        if (player.isOp()) {
            sendOPMenu(player);
            return;
        }
        SimpleForm.Builder mainMenu = SimpleForm.builder()
                .title("Dominion 领地系统")
                .content("请选择一个操作:")
                .button("创建领地")
                .button("我的领地")
                .button("模板列表")
                .optionalButton("称号列表", Dominion.config.getGroupTitleEnable())
                .optionalButton("迁移数据", Dominion.config.getResidenceMigration())
                .validResultHandler(response -> {
                    switch (response.clickedButtonId()) {
                        case 1:
                            player.performCommand("dominion cui_create");
                            return;
                        case 2:
                            player.performCommand("dominion list");
                            return;
                        case 3:
                            player.performCommand("dominion template list");
                            return;
                        case 4:
                            player.performCommand("dominion title_list");
                            return;
                        case 5:
                            player.performCommand("dominion migrate_list");
                            break;
                    }
                });
        GeyserApi.api().sendForm(player.getUniqueId(), mainMenu);

    }

    public static void sendOPMenu(Player player) {
        SimpleForm.Builder OPMenu = SimpleForm.builder()
                .title("Dominion 管理员菜单")
                .content("这个菜单只有管理员可以打开\n请选择一个操作:")
                .button("切换普通菜单")
                .button("所有领地")
                .button("系统配置")
                .button("重载缓存")
                .button("重载配置")
                .validResultHandler(response -> {
                    switch (response.clickedButtonId()) {
                        case 1:
                            sendMainMenu(player);
                            return;
                        case 2:
                            player.performCommand("dominion all_dominion");
                            return;
                        case 3:
                            player.performCommand("dominion sys_config");
                            return;
                        case 4:
                            player.performCommand("dominion reload_cache");
                            return;
                        case 5:
                            player.performCommand("dominion reload_config");
                            break;
                    }
                });
        GeyserApi.api().sendForm(player.getUniqueId(), OPMenu);
    }
}
