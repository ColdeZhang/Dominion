package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.configuration.Configuration;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.managers.DatabaseTables;
import cn.lunadeer.dominion.misc.DominionException;
import cn.lunadeer.dominion.uis.tuis.MainMenu;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.Scheduler;
import cn.lunadeer.dominion.utils.command.Option;
import cn.lunadeer.dominion.utils.command.SecondaryCommand;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import cn.lunadeer.dominion.utils.stui.components.buttons.FunctionalButton;
import cn.lunadeer.dominion.utils.stui.components.buttons.PermissionButton;
import cn.lunadeer.dominion.utils.webMap.MapRender;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;

import static cn.lunadeer.dominion.Dominion.adminPermission;
import static cn.lunadeer.dominion.misc.Converts.toWorld;

public class AdministratorCommand {

    public static class AdministratorCommandText extends ConfigurationPart {
        public String reloadCacheButton = "RELOAD CACHE";
        public String reloadCacheDescription = "Reload the cache (dont do this frequently).";
        public String reloadConfigButton = "RELOAD CONFIG";
        public String reloadConfigDescription = "Reload the configuration.";

        public String reloadingDominionCache = "Reloading dominion cache...";
        public String reloadedDominionCache = "Reload dominion cache success!";
        public String reloadingMemberCache = "Reloading member privilege cache...";
        public String reloadedMemberCache = "Reload member privilege cache success!";
        public String reloadingGroupCache = "Reloading group cache...";
        public String reloadedGroupCache = "Reload group cache success!";

        public String reloadingConfig = "Reloading configuration...";

        public String exportingMcaList = "Exporting MCA list...";
        public String createMcaFolderFailed = "Failed to create mca list folder.";
        public String writingMcaList = "Writing list of world {0}...";
        public String createMcaFileFailed = "Failed to create mca list file {0}.";
        public String exportMCAListFailed = "Failed to export MCA list of world {0}, reason: {1}.";
        public String exportedMCAList = "Exported MCA list to {0} successfully.";

        public String importHint = "Import database is only for migration or restore-backup, don't use it to merge two databases.";
        public String importInfo = "If current database is not empty, it will throw some errors and may cause data loss or corruption.";
        public String importConfirm = "Please confirm the import operation by adding 'confirm' at the end of the command.";
    }

    private enum RELOAD_TYPE {
        CONFIG,
        CACHE,
        ALL
    }

    public static SecondaryCommand reloadCache = new SecondaryCommand("reload", List.of(
            new Option(Arrays.stream(RELOAD_TYPE.values()).map(Enum::name).map(String::toLowerCase).toList(), "all")
    )) {
        @Override
        public void executeHandler(CommandSender sender) {
            RELOAD_TYPE type;
            try {
                type = RELOAD_TYPE.valueOf(getArgumentValue(0).toUpperCase());
            } catch (Exception e) {
                type = RELOAD_TYPE.ALL;
            }
            switch (type) {
                case CONFIG:
                    reloadConfig(sender);
                    break;
                case CACHE:
                    reloadCache(sender);
                    break;
                default:
                    reloadConfig(sender);
                    reloadCache(sender);
                    break;
            }
        }
    }.needPermission(adminPermission).register();

    public static PermissionButton reloadCacheButton(CommandSender sender) {
        return new FunctionalButton(Language.administratorCommandText.reloadCacheButton) {
            @Override
            public void function() {
                reloadCache(sender);
            }
        }.needPermission(adminPermission);
    }

    public static void reloadCache(CommandSender sender) {
        Scheduler.runTaskAsync(() -> {
            Notification.info(sender, Language.administratorCommandText.reloadingDominionCache);
            Cache.instance.loadDominions();
            Notification.info(sender, Language.administratorCommandText.reloadedDominionCache);
        });
        Scheduler.runTaskAsync(() -> {
            Notification.info(sender, Language.administratorCommandText.reloadingMemberCache);
            Cache.instance.loadMembers();
            Notification.info(sender, Language.administratorCommandText.reloadedMemberCache);
        });
        Scheduler.runTaskAsync(() -> {
            Notification.info(sender, Language.administratorCommandText.reloadingGroupCache);
            Cache.instance.loadGroups();
            Notification.info(sender, Language.administratorCommandText.reloadedGroupCache);
        });
        if (sender instanceof Player) MainMenu.show(sender, "1");
    }

    public static PermissionButton reloadConfigButton(CommandSender sender) {
        return new FunctionalButton(Language.administratorCommandText.reloadConfigButton) {
            @Override
            public void function() {
                reloadConfig(sender);
            }
        }.needPermission(adminPermission);
    }

    public static void reloadConfig(CommandSender sender) {
        Scheduler.runTaskAsync(() -> {
            try {
                Notification.info(sender, Language.administratorCommandText.reloadingConfig);
                Configuration.loadConfigurationAndDatabase(sender);
            } catch (Exception e) {
                Notification.error(sender, e.getMessage());
            }
            if (sender instanceof Player) MainMenu.show(sender, "1");
        });
    }

    public static SecondaryCommand exportData = new SecondaryCommand("export", List.of(
            new Option(List.of("mca", "db"), "db")
    )) {
        @Override
        public void executeHandler(CommandSender sender) {
            if (getArgumentValue(0).toUpperCase().startsWith("M")) {
                exportMCA(sender);
            } else {
                DatabaseTables.Export(sender);
            }
        }
    }.needPermission(adminPermission).register();

    public static void exportMCA(CommandSender sender) {
        try {
            Scheduler.runTaskAsync(() -> {
                Notification.info(sender, Language.administratorCommandText.exportingMcaList);
                Map<String, List<String>> mca_cords = new HashMap<>();
                List<DominionDTO> doms = Cache.instance.getAllDominions();
                for (DominionDTO dom : doms) {
                    World world = toWorld(dom.getWorldUid());
                    if (!mca_cords.containsKey(world.getName())) {
                        mca_cords.put(world.getName(), new ArrayList<>());
                    }
                    int mca_x1 = convertWorld2Mca(dom.getCuboid().x1()) - 1;
                    int mca_x2 = convertWorld2Mca(dom.getCuboid().x2()) + 1;
                    int mca_z1 = convertWorld2Mca(dom.getCuboid().z1()) - 1;
                    int mca_z2 = convertWorld2Mca(dom.getCuboid().z2()) + 1;
                    for (int x = mca_x1; x <= mca_x2; x++) {
                        for (int z = mca_z1; z <= mca_z2; z++) {
                            String file_name = "r." + x + "." + z + ".mca";
                            if (mca_cords.get(world.getName()).contains(file_name)) {
                                continue;
                            }
                            mca_cords.get(world.getName()).add(file_name);
                        }
                    }
                }
                File folder = new File(Dominion.instance.getDataFolder(), "ExportedMCAList");
                if (!folder.exists()) {
                    boolean success = folder.mkdirs();
                    if (!success) {
                        throw new DominionException(Language.administratorCommandText.createMcaFolderFailed);
                    }
                }
                for (String world : mca_cords.keySet()) {
                    File file = new File(folder, world + ".txt");
                    Notification.info(sender, Language.administratorCommandText.writingMcaList, world);
                    try {
                        if (file.exists()) {
                            File backup = new File(folder, world + ".txt.bak");
                            boolean success = file.renameTo(backup);
                        }
                        if (!file.createNewFile()) {
                            throw new DominionException(Language.administratorCommandText.createMcaFileFailed, file.getName());
                        }
                        List<String> cords = mca_cords.get(world);
                        for (String cord : cords) {
                            Files.write(file.toPath(), (cord + "\n").getBytes(), StandardOpenOption.APPEND);
                        }
                    } catch (Exception e) {
                        Notification.error(sender, Language.administratorCommandText.exportMCAListFailed, world, e.getMessage());
                    }
                }
                MapRender.renderMCA(mca_cords);
                Notification.info(sender, Language.administratorCommandText.exportedMCAList, folder.getAbsolutePath());
            });
        } catch (Exception e) {
            Notification.error(sender, e.getMessage());
        }
    }

    public static SecondaryCommand importData = new SecondaryCommand("import", List.of(
            new Option(List.of("db")),
            new Option(List.of("confirm"), "")
    )) {
        @Override
        public void executeHandler(CommandSender sender) {
            if (!getArgumentValue(1).equals("confirm")) {
                Notification.warn(sender, Language.administratorCommandText.importHint);
                Notification.warn(sender, Language.administratorCommandText.importInfo);
                Notification.warn(sender, Language.administratorCommandText.importConfirm);
            }
            DatabaseTables.Import(sender);
        }
    }.needPermission(adminPermission).register();

    private static int convertWorld2Mca(int world) {
        return world < 0 ? world / 512 - 1 : world / 512;
    }

}
