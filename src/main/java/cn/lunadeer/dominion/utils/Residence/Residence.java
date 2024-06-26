package cn.lunadeer.dominion.utils.Residence;

import java.util.Map;

public class Residence {
    public String TPLoc;
    public Map<String, Residence> Subzones;
    public int Messages;
    public Permission Permissions;
    public Map<String, String> Areas;

    // getters and setters

    public String getTPLoc() {
        return TPLoc;
    }

    public void setTPLoc(String TPLoc) {
        this.TPLoc = TPLoc;
    }

    public Map<String, Residence> getSubzones() {
        return Subzones;
    }

    public void setSubzones(Map<String, Residence> subzones) {
        Subzones = subzones;
    }

    public int getMessages() {
        return Messages;
    }

    public void setMessages(int messages) {
        Messages = messages;
    }

    public Permission getPermissions() {
        return Permissions;
    }

    public void setPermissions(Permission permissions) {
        Permissions = permissions;
    }

    public Map<String, String> getAreas() {
        return Areas;
    }

    public void setAreas(Map<String, String> areas) {
        Areas = areas;
    }
}