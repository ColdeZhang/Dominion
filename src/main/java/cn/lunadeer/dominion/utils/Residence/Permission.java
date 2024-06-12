package cn.lunadeer.dominion.utils.Residence;

public class Permission {
    public String OwnerUUID;
    public String OwnerLastKnownName;

    // getters and setters

    public String getOwnerUUID() {
        return OwnerUUID;
    }

    public void setOwnerUUID(String ownerUUID) {
        OwnerUUID = ownerUUID;
    }

    public String getOwnerLastKnownName() {
        return OwnerLastKnownName;
    }

    public void setOwnerLastKnownName(String ownerLastKnownName) {
        OwnerLastKnownName = ownerLastKnownName;
    }
}