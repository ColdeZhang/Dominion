package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.tuis.SysConfig;
import cn.lunadeer.minecraftpluginutils.Notification;
import org.bukkit.command.CommandSender;

import static cn.lunadeer.dominion.commands.Apis.notOpOrConsole;

public class SetConfig {

    public static void handler(CommandSender sender, String[] args) {
        if (notOpOrConsole(sender)) return;
        if (args.length < 2) {
            Notification.error(sender, "参数错误");
            return;
        }
        switch (args[1]) {
            case "auto_create_radius":
                setAutoCreateRadius(sender, args);
                break;
            case "limit_max_y":
                setLimitMaxY(sender, args);
                break;
            case "limit_min_y":
                setLimitMinY(sender, args);
                break;
            case "limit_size_x":
                setLimitSizeX(sender, args);
                break;
            case "limit_size_z":
                setLimitSizeZ(sender, args);
                break;
            case "limit_size_y":
                setLimitSizeY(sender, args);
                break;
            case "limit_amount":
                setLimitAmount(sender, args);
                break;
            case "limit_depth":
                setLimitDepth(sender, args);
                break;
            case "limit_vert":
                setLimitVert(sender, args);
                break;
            case "limit_op_bypass":
                setLimitOpBypass(sender, args);
                break;
            case "tp_enable":
                setTpEnable(sender, args);
                break;
            case "tp_delay":
                setTpDelay(sender, args);
                break;
            case "tp_cool_down":
                setTpCoolDown(sender, args);
                break;
            case "economy_enable":
                setEconomyEnable(sender, args);
                break;
            case "economy_price":
                setEconomyPrice(sender, args);
                break;
            case "economy_only_xz":
                setEconomyOnlyXZ(sender, args);
                break;
            case "economy_refund":
                setEconomyRefund(sender, args);
                break;
            case "residence_migration":
                setResidenceMigration(sender, args);
                break;
            default:
                Notification.error(sender, "未知参数");
        }
    }

    public static void refreshPageOrNot(CommandSender sender, String[] args) {
        if (args.length == 4) {
            int page = Integer.parseInt(args[3]);
            String[] newArgs = new String[2];
            newArgs[0] = "config";
            newArgs[1] = String.valueOf(page);
            SysConfig.show(sender, newArgs);
        }
    }

    private static void setAutoCreateRadius(CommandSender sender, String[] args) {
        int size = Integer.parseInt(args[2]);
        if (size < 2) {
            Dominion.config.setAutoCreateRadius(2);
            Notification.error(sender, "自动创建半径不能小于2");
        } else {
            Dominion.config.setAutoCreateRadius(size);
        }
        refreshPageOrNot(sender, args);
    }

    private static void adjustSizeY() {
        if (Dominion.config.getLimitVert()) {
            Dominion.config.setLimitSizeY(Dominion.config.getLimitMaxY() - Dominion.config.getLimitMinY() + 1);
        }
    }

    private static void setLimitMaxY(CommandSender sender, String[] args) {
        int maxY = Integer.parseInt(args[2]);
        if (maxY <= Dominion.config.getLimitMinY()) {
            Notification.error(sender, "最高Y坐标限制不能小于最低Y坐标限制");
            return;
        }
        Dominion.config.setLimitMaxY(maxY);
        adjustSizeY();
        refreshPageOrNot(sender, args);
    }

    private static void setLimitMinY(CommandSender sender, String[] args) {
        int minY = Integer.parseInt(args[2]);
        if (minY >= Dominion.config.getLimitMaxY()) {
            Notification.error(sender, "最低Y坐标限制不能大于最高Y坐标限制");
            return;
        }
        Dominion.config.setLimitMinY(minY);
        adjustSizeY();
        refreshPageOrNot(sender, args);
    }

    private static void setLimitSizeX(CommandSender sender, String[] args) {
        int sizeX = Integer.parseInt(args[2]);
        if (sizeX != -1 && sizeX < 4) {
            Dominion.config.setLimitSizeX(4);
            Notification.error(sender, "X轴(东西)最大尺寸不能小于4");
        } else {
            Dominion.config.setLimitSizeX(sizeX);
        }
        refreshPageOrNot(sender, args);
    }

    private static void setLimitSizeZ(CommandSender sender, String[] args) {
        int sizeZ = Integer.parseInt(args[2]);
        if (sizeZ != -1 && sizeZ < 4) {
            Dominion.config.setLimitSizeZ(4);
            Notification.error(sender, "Z轴(南北)最大尺寸不能小于4");
            return;
        } else {
            Dominion.config.setLimitSizeZ(sizeZ);
        }
        refreshPageOrNot(sender, args);
    }

    private static void setLimitSizeY(CommandSender sender, String[] args) {
        int sizeY = Integer.parseInt(args[2]);
        if (sizeY != -1 && sizeY < 4) {
            Dominion.config.setLimitSizeY(4);
            Notification.error(sender, "Y轴(垂直)最大尺寸不能小于4");
        } else {
            Dominion.config.setLimitSizeY(sizeY);
        }
        refreshPageOrNot(sender, args);
    }

    private static void setLimitAmount(CommandSender sender, String[] args) {
        int amount = Integer.parseInt(args[2]);
        if (amount != -1 && amount < 0) {
            Dominion.config.setLimitAmount(0);
            Notification.error(sender, "每个玩家领地数量限制不能小于0");
        } else {
            Dominion.config.setLimitAmount(amount);
        }
        refreshPageOrNot(sender, args);
    }

    private static void setLimitDepth(CommandSender sender, String[] args) {
        int depth = Integer.parseInt(args[2]);
        if (depth != -1 && depth < 0) {
            Dominion.config.setLimitDepth(0);
            Notification.error(sender, "领地深度限制不能小于0");
        } else {
            Dominion.config.setLimitDepth(depth);
        }
        refreshPageOrNot(sender, args);
    }

    private static void setLimitVert(CommandSender sender, String[] args) {
        boolean limitVert = Boolean.parseBoolean(args[2]);
        Dominion.config.setLimitVert(limitVert);
        adjustSizeY();
        refreshPageOrNot(sender, args);
    }

    private static void setLimitOpBypass(CommandSender sender, String[] args) {
        boolean limitOpBypass = Boolean.parseBoolean(args[2]);
        Dominion.config.setLimitOpBypass(limitOpBypass);
        refreshPageOrNot(sender, args);
    }

    private static void setTpEnable(CommandSender sender, String[] args) {
        boolean tpEnable = Boolean.parseBoolean(args[2]);
        Dominion.config.setTpEnable(tpEnable);
        refreshPageOrNot(sender, args);
    }

    private static void setTpDelay(CommandSender sender, String[] args) {
        int tpDelay = Integer.parseInt(args[2]);
        if (tpDelay < 0) {
            Dominion.config.setTpDelay(0);
            Notification.error(sender, "传送延迟不能小于0");
        } else {
            Dominion.config.setTpDelay(tpDelay);
        }
        refreshPageOrNot(sender, args);
    }

    private static void setTpCoolDown(CommandSender sender, String[] args) {
        int tpCoolDown = Integer.parseInt(args[2]);
        if (tpCoolDown < 0) {
            Dominion.config.setTpCoolDown(0);
            Notification.error(sender, "传送冷却时间不能小于0");
        } else {
            Dominion.config.setTpCoolDown(tpCoolDown);
        }
        refreshPageOrNot(sender, args);
    }

    private static void setEconomyEnable(CommandSender sender, String[] args) {
        boolean economyEnable = Boolean.parseBoolean(args[2]);
        Dominion.config.setEconomyEnable(economyEnable);
        refreshPageOrNot(sender, args);
    }

    private static void setEconomyPrice(CommandSender sender, String[] args) {
        float economyPrice = Float.parseFloat(args[2]);
        if (economyPrice < 0) {
            Dominion.config.setEconomyPrice(0.0f);
            Notification.error(sender, "每方块单价不能小于0");
        } else {
            Dominion.config.setEconomyPrice(economyPrice);
        }
        refreshPageOrNot(sender, args);
    }

    private static void setEconomyOnlyXZ(CommandSender sender, String[] args) {
        boolean economyOnlyXZ = Boolean.parseBoolean(args[2]);
        Dominion.config.setEconomyOnlyXZ(economyOnlyXZ);
        refreshPageOrNot(sender, args);
    }

    private static void setEconomyRefund(CommandSender sender, String[] args) {
        float economyRefund = Float.parseFloat(args[2]);
        if (economyRefund < 0) {
            Dominion.config.setEconomyRefund(0.0f);
            Notification.error(sender, "领地退款比例不能小于0");
        } else {
            Dominion.config.setEconomyRefund(economyRefund);
        }
    }

    private static void setResidenceMigration(CommandSender sender, String[] args) {
        boolean residenceMigration = Boolean.parseBoolean(args[2]);
        Dominion.config.setResidenceMigration(residenceMigration);
        refreshPageOrNot(sender, args);
    }
}
