package cn.lunadeer.dominion.api.dtos;

import net.kyori.adventure.text.Component;

public interface GroupDTO {
    Integer getId();

    Integer getDomID();

    String getName();

    Component getNameColoredComponent();

    String getNameColoredBukkit();

    Boolean getAdmin();

    Boolean getFlagValue(Flag flag);
}
