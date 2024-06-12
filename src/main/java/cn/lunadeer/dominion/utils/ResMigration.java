package cn.lunadeer.dominion.utils;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.dtos.PlayerDTO;
import cn.lunadeer.dominion.utils.Residence.Residence;
import cn.lunadeer.dominion.utils.Residence.SaveFile;
import cn.lunadeer.minecraftpluginutils.XLogger;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ResMigration {


    /*
Residences:
  '03':
    TPLoc: -3967.62:48.0:-1988.87:4.8:-5.08
    Messages: 1
    Permissions:
      PlayerFlags:
        3244b8fb-3cf9-433e-8f4a-bb90bf6d4f54: 1
        8075e1ae-62fc-482f-b3af-3fc5b2770a2c: 2
        0a23b4e5-f18e-4f8b-947d-558444bb27ca: 3
      AreaFlags: 4
      OwnerUUID: 8075e1ae-62fc-482f-b3af-3fc5b2770a2c
      OwnerLastKnownName: guajn
    CreatedOn: 1630491137410
    Areas:
      main: -3998:0:-2014:-3939:255:-1955
  '04':
    TPLoc: -5640.7:189.0:-2029.38:40.35:-96.3
    Messages: 1
    Permissions:
      PlayerFlags:
        8075e1ae-62fc-482f-b3af-3fc5b2770a2c: 2
        0a23b4e5-f18e-4f8b-947d-558444bb27ca: 1
      AreaFlags: 5
      OwnerUUID: 8075e1ae-62fc-482f-b3af-3fc5b2770a2c
      OwnerLastKnownName: guajn
    CreatedOn: 1641656609270
    Areas:
      main: -5673:0:-2055:-5609:255:-2005
     */
    public static class ResidenceNode {
        public UUID owner;
        public World world;
        public String name;
        public Location loc1;
        public Location loc2;
        public Location tpLoc;
        public String joinMessage;
        public String leaveMessage;
        public List<ResidenceNode> children = new ArrayList<>();
    }

    public static List<ResidenceNode> extractFromResidence(JavaPlugin plugin) {
        List<ResidenceNode> dominions = new ArrayList<>();
        File resSave = new File(plugin.getDataFolder().getParent(), "Residence");
        resSave = new File(resSave, "Save");
        resSave = new File(resSave, "Worlds");
        if (!resSave.exists()) {
            XLogger.info("Residence Save not found");
            return dominions;
        }
        // list .yml files
        File[] files = resSave.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null || files.length == 0) {
            XLogger.info("No save files found");
            return dominions;
        }
        for (File file : files) {
            try {
                dominions.addAll(processWorld(file));
            } catch (Exception e) {
                XLogger.err("Failed to process file: %s, %s", file.getName(), e.getMessage());
            }
        }
        return dominions;
    }

    private static ResidenceNode parseDominion(String name, World world, Residence res, SaveFile save) {
        Player bukkitOwner = Dominion.instance.getServer().getPlayer(res.Permissions.OwnerUUID);
        if (bukkitOwner == null) {
            XLogger.warn("Owner not found: " + res.Permissions.OwnerUUID);
            return null;
        }
        PlayerDTO owner = PlayerDTO.get(bukkitOwner);
        String[] loc = res.Areas.get("main").split(":");
        if (loc.length != 6) {
            XLogger.warn("Invalid location: " + res.Areas.get("main"));
            return null;
        }
        ResidenceNode dominionNode = new ResidenceNode();
        dominionNode.owner = owner.getUuid();
        dominionNode.world = world;
        dominionNode.name = name;
        dominionNode.joinMessage = save.Messages.get(res.Messages).EnterMessage;
        dominionNode.leaveMessage = save.Messages.get(res.Messages).LeaveMessage;
        dominionNode.loc1 = new Location(world, Double.parseDouble(loc[0]), Double.parseDouble(loc[1]), Double.parseDouble(loc[2]));
        dominionNode.loc2 = new Location(world, Double.parseDouble(loc[3]), Double.parseDouble(loc[4]), Double.parseDouble(loc[5]));
        String[] tpLocStr = res.TPLoc.split(":");
        if (tpLocStr.length == 3) {
            dominionNode.tpLoc = new Location(world, Double.parseDouble(tpLocStr[0]), Double.parseDouble(tpLocStr[1]), Double.parseDouble(tpLocStr[2]));
        }
        if (res.Subzones != null) {
            for (Map.Entry<String, Residence> entry : res.Subzones.entrySet()) {
                ResidenceNode sub = parseDominion(entry.getKey(), world, entry.getValue(), save);
                if (sub != null) {
                    dominionNode.children.add(sub);
                }
            }
        }
        return dominionNode;
    }

    private static List<ResidenceNode> processWorld(File saveFile) throws Exception {
        String worldName = saveFile.getName().replace("res_", "").replace(".yml", "");
        World world = Dominion.instance.getServer().getWorld(worldName);
        Yaml yaml = new Yaml();
        InputStream inputStream = Files.newInputStream(saveFile.toPath());
        SaveFile save = yaml.loadAs(inputStream, SaveFile.class);
        inputStream.close();
        List<ResidenceNode> dominions = new ArrayList<>();
        for (Map.Entry<String, Residence> entry : save.Residences.entrySet()) {
            String name = entry.getKey();
            Residence residence = entry.getValue();
            ResidenceNode dominion = parseDominion(name, world, residence, save);
            dominions.add(dominion);
        }
        return dominions;
    }

}
