package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.managers.DatabaseTables;
import cn.lunadeer.dominion.managers.Translation;
import cn.lunadeer.dominion.utils.map.MapRender;
import cn.lunadeer.minecraftpluginutils.GiteaReleaseCheck;
import cn.lunadeer.minecraftpluginutils.Notification;
import cn.lunadeer.minecraftpluginutils.Scheduler;
import cn.lunadeer.minecraftpluginutils.XLogger;
import cn.lunadeer.minecraftpluginutils.databse.DatabaseManager;
import cn.lunadeer.minecraftpluginutils.databse.DatabaseType;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.lunadeer.dominion.utils.CommandUtils.hasPermission;


public class Operator {

    public static void reloadCache(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "dominion.admin")) {
            return;
        }
        Scheduler.runTaskAsync(() -> {
            Notification.info(sender, Translation.Commands_Operator_ReloadingDominionCache);
            Cache.instance.loadDominions();
            Notification.info(sender, Translation.Commands_Operator_ReloadedDominionCache);
        });
        Scheduler.runTaskAsync(() -> {
            Notification.info(sender, Translation.Commands_Operator_ReloadingPrivilegeCache);
            Cache.instance.loadMembers();
            Notification.info(sender, Translation.Commands_Operator_ReloadedPrivilegeCache);
        });
        Scheduler.runTaskAsync(() -> {
            Notification.info(sender, Translation.Commands_Operator_ReloadingGroupCache);
            Cache.instance.loadGroups();
            Notification.info(sender, Translation.Commands_Operator_ReloadedGroupCache);
        });
    }

    public static void exportMca(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "dominion.admin")) {
            return;
        }
        Scheduler.runTaskAsync(() -> {
            Notification.info(sender, Translation.Commands_Operator_ExportingMCAList);
            Map<String, List<String>> mca_cords = new HashMap<>();
            List<DominionDTO> doms = Cache.instance.getAllDominions();
            for (DominionDTO dom : doms) {
                if (dom.getWorld() == null) {
                    continue;
                }
                if (!mca_cords.containsKey(dom.getWorld().getName())) {
                    mca_cords.put(dom.getWorld().getName(), new ArrayList<>());
                }
                Integer world_x1 = dom.getX1();
                Integer world_x2 = dom.getX2();
                Integer world_z1 = dom.getZ1();
                Integer world_z2 = dom.getZ2();
                int mca_x1 = convertWorld2Mca(world_x1) - 1;
                int mca_x2 = convertWorld2Mca(world_x2) + 1;
                int mca_z1 = convertWorld2Mca(world_z1) - 1;
                int mca_z2 = convertWorld2Mca(world_z2) + 1;
                for (int x = mca_x1; x <= mca_x2; x++) {
                    for (int z = mca_z1; z <= mca_z2; z++) {
                        String file_name = "r." + x + "." + z + ".mca";
                        if (mca_cords.get(dom.getWorld().getName()).contains(file_name)) {
                            continue;
                        }
                        mca_cords.get(dom.getWorld().getName()).add(file_name);
                    }
                }
            }
            File folder = new File(Dominion.instance.getDataFolder(), "ExportedMCAList");
            if (!folder.exists()) {
                boolean success = folder.mkdirs();
                if (!success) {
                    Notification.error(sender, Translation.Commands_Operator_CreateExportFolderFailed);
                    return;
                }
            }
            for (String world : mca_cords.keySet()) {
                File file = new File(folder, world + ".txt");
                Notification.info(sender, Translation.Commands_Operator_ExportingMCAListForWorld, world);
                try {
                    if (file.exists()) {
                        boolean success = file.delete();
                        if (!success) {
                            Notification.error(sender, Translation.Commands_Operator_DeleteMCAListFailed, world);
                            continue;
                        }
                    }
                    boolean success = file.createNewFile();
                    if (!success) {
                        Notification.error(sender, Translation.Commands_Operator_CreateMCAListFailed, world);
                        continue;
                    }
                    List<String> cords = mca_cords.get(world);
                    for (String cord : cords) {
                        XLogger.debug("Writing %s...", cord);
                        try {
                            java.nio.file.Files.write(file.toPath(), (cord + "\n").getBytes(), java.nio.file.StandardOpenOption.APPEND);
                        } catch (Exception e) {
                            Notification.error(sender, Translation.Commands_Operator_WriteMCAListFailed, cord);
                        }
                    }
                } catch (Exception e) {
                    Notification.error(sender, Translation.Commands_Operator_ExportMCAListFailed, world);
                    Notification.error(sender, e.getMessage());
                }
            }
            MapRender.renderMCA(mca_cords);
            Notification.info(sender, Translation.Commands_Operator_ExportedMCAList, folder.getAbsolutePath());
        });
    }

    public static void reloadConfig(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "dominion.admin")) {
            return;
        }
        Scheduler.runTaskAsync(() -> {
            Notification.info(sender, Translation.Commands_Operator_ReloadingConfig);
            Dominion.config.reload();
            DatabaseManager.instance.reConnection(
                    DatabaseType.valueOf(Dominion.config.getDbType().toUpperCase()),
                    Dominion.config.getDbHost(),
                    Dominion.config.getDbPort(),
                    Dominion.config.getDbName(),
                    Dominion.config.getDbUser(),
                    Dominion.config.getDbPass()
            );
            Notification.info(sender, Translation.Commands_Operator_ReloadedConfig);
        });
    }

    /**
     * 导出数据库
     * /dominion export_db [confirm]
     *
     * @param sender 发送者
     * @param args   参数
     */
    public static void exportDatabase(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "dominion.admin")) {
            return;
        }
        if (args.length != 2 || !args[1].equals("confirm")) {
            Notification.warn(sender, Translation.Commands_Operator_ExportDBConfirm);
            return;
        }
        DatabaseTables.Export(sender);
    }

    /**
     * 导入数据库
     * /dominion import_db [confirm]
     *
     * @param sender 发送者
     * @param args   参数
     */
    public static void importDatabase(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "dominion.admin")) {
            return;
        }
        if (args.length != 2 || !args[1].equals("confirm")) {
            Notification.warn(sender, Translation.Commands_Operator_ImportDBConfirm);
            return;
        }
        DatabaseTables.Import(sender);
    }

    public static void version(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "dominion.admin")) {
            return;
        }
        GiteaReleaseCheck.instance.getLatestRelease();
        Notification.info(sender, Dominion.instance.getDescription().getVersion());
    }

    private static int convertWorld2Mca(int world) {
        return world < 0 ? world / 512 - 1 : world / 512;
    }

}
