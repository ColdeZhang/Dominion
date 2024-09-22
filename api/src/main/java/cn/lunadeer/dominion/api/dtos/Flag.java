package cn.lunadeer.dominion.api.dtos;

public interface Flag {
    String getFlagName();

    String getDisplayName();

    String getDescription();

    Boolean getDefaultValue();

    Boolean getEnable();
}
