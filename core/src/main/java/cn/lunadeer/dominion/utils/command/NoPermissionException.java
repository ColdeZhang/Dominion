package cn.lunadeer.dominion.utils.command;

public class NoPermissionException extends RuntimeException {
    public static String MSG = "You do not have permission {0} to do this.";

    public NoPermissionException(String permission) {
        super(MSG.replace("{0}", permission));
    }
}
