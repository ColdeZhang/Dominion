package cn.lunadeer.dominion.api.dtos;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface DominionDTO {
    // getters and setters
    Integer getId();

    UUID getOwner();

    String getName();

    @Nullable World getWorld();

    UUID getWorldUid();

    Integer getX1();

    Integer getY1();

    Integer getZ1();

    Integer getX2();

    Integer getY2();

    Integer getZ2();

    Integer getSquare();

    Integer getVolume();

    Integer getWidthX();

    Integer getHeight();

    Integer getWidthZ();

    Integer getParentDomId();

    String getJoinMessage();

    String getLeaveMessage();

    Boolean getFlagValue(Flag flag);

    Location getTpLocation();

    Location getLocation1();

    Location getLocation2();

    int getColorR();

    int getColorG();

    int getColorB();

    String getColor();

    int getColorHex();
}
