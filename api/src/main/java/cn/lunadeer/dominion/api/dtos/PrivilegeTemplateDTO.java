package cn.lunadeer.dominion.api.dtos;

import java.util.UUID;

public interface PrivilegeTemplateDTO {
    Integer getId();

    UUID getCreator();

    String getName();

    Boolean getAdmin();

    Boolean getFlagValue(Flag flag);
}
