package cn.lunadeer.dominion.utils.stui.components.buttons;

import java.util.ArrayList;
import java.util.List;

public abstract class PermissionButton extends Button {

    protected final List<String> permissions = new ArrayList<>();

    public PermissionButton(String text) {
        super(text);
    }

    public PermissionButton needPermission(String permission) {
        permissions.add(permission);
        return this;
    }
}
