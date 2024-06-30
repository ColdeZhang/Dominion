package cn.lunadeer.dominion.tuis;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.minecraftpluginutils.stui.ListView;
import cn.lunadeer.minecraftpluginutils.stui.components.Button;
import cn.lunadeer.minecraftpluginutils.stui.components.Line;
import cn.lunadeer.minecraftpluginutils.stui.components.NumChanger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.commands.Apis.playerOnly;
import static cn.lunadeer.dominion.tuis.Apis.getPage;
import static cn.lunadeer.dominion.tuis.Apis.notOp;

public class SysConfig {
    public static void show(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        if (notOp(player)) return;
        int page = getPage(args, 1);
        ListView view = ListView.create(10, "/dominion sys_config");
        view.title("系统配置");
        view.navigator(Line.create().append(Button.create("主菜单").setExecuteCommand("/dominion menu").build()).append("系统配置"));

        Line limitSize = Line.create()
                .append(Component.text("领地尺寸限制"));
        view.add(limitSize);
        Line limitSizeX = Line.create()
                .append(Component.text("    X轴(东西)"));
        if (Dominion.config.getLimitSizeX() == -1) {
            limitSizeX.append(Component.text("无限制")).append(Button.create("设置数值").setExecuteCommand("/dominion set_config limit_size_x 64 " + page).build());
        } else {
            limitSizeX.append(NumChanger.create(Dominion.config.getLimitSizeX(), "/dominion set_config limit_size_x").setPageNumber(page).build());
            limitSizeX.append(Button.create("设置无限制").setExecuteCommand("/dominion set_config limit_size_x -1 " + page).build());
        }
        view.add(limitSizeX);
        Line limitSizeZ = Line.create()
                .append(Component.text("    Z轴(南北)"));
        if (Dominion.config.getLimitSizeZ() == -1) {
            limitSizeZ.append(Component.text("无限制")).append(Button.create("设置数值").setExecuteCommand("/dominion set_config limit_size_z 64 " + page).build());
        } else {
            limitSizeZ.append(NumChanger.create(Dominion.config.getLimitSizeZ(), "/dominion set_config limit_size_z").setPageNumber(page).build());
            limitSizeZ.append(Button.create("设置无限制").setExecuteCommand("/dominion set_config limit_size_z -1 " + page).build());
        }
        view.add(limitSizeZ);
        Line limitSizeY = Line.create()
                .append(Component.text("    Y轴(垂直)"));
        if (!Dominion.config.getLimitVert()) {
            if (Dominion.config.getLimitSizeY() == -1) {
                limitSizeY.append(Component.text("无限制")).append(Button.create("设置数值").setExecuteCommand("/dominion set_config limit_size_y 64 " + page).build());
            } else {
                limitSizeY.append(NumChanger.create(Dominion.config.getLimitSizeY(), "/dominion set_config limit_size_y").setPageNumber(page).build());
                limitSizeY.append(Button.create("设置无限制").setExecuteCommand("/dominion set_config limit_size_y -1 " + page).build());
            }
        } else {
            limitSizeY.append(Component.text(Dominion.config.getLimitSizeY())
                    .style(Style.style(TextDecoration.STRIKETHROUGH))
                    .hoverEvent(Component.text("因为垂直自动延伸已开启，此设置不可手动修改")));
        }
        view.add(limitSizeY);
        if (Dominion.config.getLimitVert()) {
            view.add(Line.create()
                    .append("垂直自动延伸")
                    .append(Button.createGreen("☑").setExecuteCommand("/dominion set_config limit_vert false " + page).build()));
        } else {
            view.add(Line.create()
                    .append("垂直自动延伸")
                    .append(Button.createRed("☐").setExecuteCommand("/dominion set_config limit_vert true " + page).build()));
        }
        Line limitMaxY = Line.create()
                .append(Component.text("最高Y坐标限制"));
        limitMaxY.append(NumChanger.create(Dominion.config.getLimitMaxY(), "/dominion set_config limit_max_y").setPageNumber(page).build());
        view.add(limitMaxY);
        Line limitMinY = Line.create()
                .append(Component.text("最低Y坐标限制"));
        limitMinY.append(NumChanger.create(Dominion.config.getLimitMinY(), "/dominion set_config limit_min_y").setPageNumber(page).build());
        view.add(limitMinY);
        Line limitAmount = Line.create()
                .append(Component.text("每个玩家领地数量限制"));
        if (Dominion.config.getLimitAmount() == -1) {
            limitAmount.append(Component.text("无限制")).append(Button.create("设置数值").setExecuteCommand("/dominion set_config limit_amount 3 " + page).build());
        } else {
            limitAmount.append(NumChanger.create(Dominion.config.getLimitAmount(), "/dominion set_config limit_amount").setPageNumber(page).build());
            limitAmount.append(Button.create("设置无限制").setExecuteCommand("/dominion set_config limit_amount -1 " + page).build());
        }
        view.add(limitAmount);
        Line limitDepth = Line.create()
                .append(Component.text("领地深度限制"));
        if (Dominion.config.getLimitDepth() == -1) {
            limitDepth.append(Component.text("无限制")).append(Button.create("设置数值").setExecuteCommand("/dominion set_config limit_depth 64 " + page).build());
        } else {
            limitDepth.append(NumChanger.create(Dominion.config.getLimitDepth(), "/dominion set_config limit_depth").setPageNumber(page).build());
            limitDepth.append(Button.create("设置无限制").setExecuteCommand("/dominion set_config limit_depth -1 " + page).build());
        }
        view.add(limitDepth);

        if (Dominion.config.getLimitOpBypass()) {
            view.add(Line.create()
                    .append("OP是否可以无视限制")
                    .append(Button.createGreen("☑").setExecuteCommand("/dominion set_config limit_op_bypass false " + page).build()));
        } else {
            view.add(Line.create()
                    .append("OP是否可以无视限制")
                    .append(Button.createRed("☐").setExecuteCommand("/dominion set_config limit_op_bypass true " + page).build()));
        }

        Line autoCreateRadius = Line.create()
                .append(Component.text("自动创建半径"))
                .append(NumChanger.create(Dominion.config.getAutoCreateRadius(), "/dominion set_config auto_create_radius").setPageNumber(page).build());
        view.add(autoCreateRadius);

        if (Dominion.config.getTpEnable()) {
            view.add(Line.create()
                    .append("领地传送功能")
                    .append(Button.createGreen("☑").setExecuteCommand("/dominion set_config tp_enable false " + page).build()));
        } else {
            view.add(Line.create()
                    .append("领地传送功能")
                    .append(Button.createRed("☐").setExecuteCommand("/dominion set_config tp_enable true " + page).build()));
        }
        Line tpDelay = Line.create()
                .append(Component.text("    传送延迟(秒)"))
                .append(NumChanger.create(Dominion.config.getTpDelay(), "/dominion set_config tp_delay").setPageNumber(page).build());
        view.add(tpDelay);
        Line tpCoolDown = Line.create()
                .append(Component.text("    传送冷却(秒)"))
                .append(NumChanger.create(Dominion.config.getTpCoolDown(), "/dominion set_config tp_cool_down").setPageNumber(page).build());
        view.add(tpCoolDown);
        if (Dominion.config.getEconomyEnable()) {
            Line economy = Line.create()
                    .append("经济系统");
            view.add(economy);
            Line price = Line.create()
                    .append(Component.text("    每方块单价"))
                    .append(NumChanger.create(Dominion.config.getEconomyPrice(), "/dominion set_config economy_price", 0.1).setPageNumber(page).build());
            view.add(price);
            if (Dominion.config.getEconomyOnlyXZ()) {
                view.add(Line.create()
                        .append("   仅计价平面积")
                        .append(Button.createGreen("☑").setExecuteCommand("/dominion set_config economy_only_xz false " + page).build()));
            } else {
                view.add(Line.create()
                        .append("   仅计价平面积")
                        .append(Button.createRed("☐").setExecuteCommand("/dominion set_config economy_only_xz true " + page).build()));
            }
            Line refund = Line.create()
                    .append(Component.text("    删除/缩小领地退还比例"))
                    .append(NumChanger.create(Dominion.config.getEconomyRefund(), "/dominion set_config economy_refund", 0.01).setPageNumber(page).build());
            view.add(refund);
        }
        if (Dominion.config.getResidenceMigration()) {
            view.add(Line.create()
                    .append("是否允许从Residence迁移数据")
                    .append(Button.createGreen("☑").setExecuteCommand("/dominion set_config residence_migration false " + page).build()));
        } else {
            view.add(Line.create()
                    .append("是否允许从Residence迁移数据")
                    .append(Button.createRed("☐").setExecuteCommand("/dominion set_config residence_migration true " + page).build()));
        }
        view.showOn(player, page);
    }
}
