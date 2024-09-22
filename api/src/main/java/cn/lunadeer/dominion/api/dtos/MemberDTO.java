package cn.lunadeer.dominion.api.dtos;

import java.util.UUID;

public interface MemberDTO {
    Integer getId();

    UUID getPlayerUUID();

    Boolean getAdmin();

    Integer getDomID();

    Integer getGroupId();

    Boolean getFlagValue(Flag flag);
}
