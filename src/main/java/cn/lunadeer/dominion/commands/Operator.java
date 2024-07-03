package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.BlueMapConnect;
import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.minecraftpluginutils.Notification;
import cn.lunadeer.minecraftpluginutils.Scheduler;
import cn.lunadeer.minecraftpluginutils.XLogger;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.lunadeer.dominion.commands.Apis.notOpOrConsole;

public class Operator {

    public static void reloadCache(CommandSender sender, String[] args) {
        if (notOpOrConsole(sender)) return;
        Scheduler.runTaskAsync(() -> {
            Notification.info(sender, "正在从数据库重新加载领地缓存...");
            Cache.instance.loadDominions();
            Notification.info(sender, "领地缓存已重新加载");
        });
        Scheduler.runTaskAsync(() -> {
            Notification.info(sender, "正在从数据库重新加载玩家权限缓存...");
            Cache.instance.loadMembers();
            Notification.info(sender, "玩家权限缓存已重新加载");
        });
        Scheduler.runTaskAsync(() -> {
            Notification.info(sender, "正在从数据库重新加载权限组缓存...");
            Cache.instance.loadGroups();
            Notification.info(sender, "权限组缓存已重新加载");
        });
    }

    public static void exportMca(CommandSender sender, String[] args) {
        if (notOpOrConsole(sender)) return;
        Scheduler.runTaskAsync(() -> {
            Notification.info(sender, "正在导出拥有领地的MCA文件列表...");
            Map<String, List<String>> mca_cords = new HashMap<>();
            List<DominionDTO> doms = Cache.instance.getDominions();
            for (DominionDTO dom : doms) {
                if (!mca_cords.containsKey(dom.getWorld())) {
                    mca_cords.put(dom.getWorld(), new ArrayList<>());
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
                        if (mca_cords.get(dom.getWorld()).contains(file_name)) {
                            continue;
                        }
                        mca_cords.get(dom.getWorld()).add(file_name);
                    }
                }
            }
            File folder = new File(Dominion.instance.getDataFolder(), "ExportedMCAList");
            if (!folder.exists()) {
                boolean success = folder.mkdirs();
                if (!success) {
                    Notification.error(sender, "创建导出文件夹失败");
                    return;
                }
            }
            for (String world : mca_cords.keySet()) {
                File file = new File(folder, world + ".txt");
                Notification.info(sender, "正在导出 %s 的MCA文件列表...", world);
                try {
                    if (file.exists()) {
                        boolean success = file.delete();
                        if (!success) {
                            Notification.error(sender, "删除 %s 的MCA文件列表失败", world);
                            continue;
                        }
                    }
                    boolean success = file.createNewFile();
                    if (!success) {
                        Notification.error(sender, "创建 %s 的MCA文件列表失败", world);
                        continue;
                    }
                    List<String> cords = mca_cords.get(world);
                    for (String cord : cords) {
                        XLogger.debug("正在写入 %s...", cord);
                        try {
                            java.nio.file.Files.write(file.toPath(), (cord + "\n").getBytes(), java.nio.file.StandardOpenOption.APPEND);
                        } catch (Exception e) {
                            Notification.error(sender, "写入 %s 失败", cord);
                        }
                    }
                } catch (Exception e) {
                    Notification.error(sender, "导出 %s 的MCA文件列表失败", world);
                    Notification.error(sender, e.getMessage());
                }
            }
            BlueMapConnect.renderMCA(mca_cords);
            Notification.info(sender, "MCA文件列表已导出到 %s", folder.getAbsolutePath());
        });
    }

    public static void reloadConfig(CommandSender sender, String[] args) {
        if (notOpOrConsole(sender)) return;
        Scheduler.runTaskAsync(() -> {
            Notification.info(sender, "正在重新加载配置文件...");
            Dominion.config.reload();
            Notification.info(sender, "配置文件已重新加载");
        });
    }

    private static int convertWorld2Mca(int world) {
        return world < 0 ? world / 512 - 1 : world / 512;
    }

}
