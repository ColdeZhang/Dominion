package cn.lunadeer.dominion.utils.command;

public class InvalidArgumentException extends RuntimeException {
    public static String MSG = "Invalid arguments, usage e.g. {0}.";

    public InvalidArgumentException(String usage) {
        super(MSG.replace("{0}", usage));
    }
}
