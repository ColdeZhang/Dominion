package cn.lunadeer.dominion.utils;

public class Misc {

    public static boolean isPaper() {
        try {
            Class.forName("io.papermc.paper.threadedregions.scheduler.ScheduledTask");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static String formatString(String str, Object... args) {
        String formatStr = str;
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) {
                args[i] = "[null for formatString (args[" + i + "])]";
            }
            formatStr = formatStr.replace("{" + i + "}", args[i].toString());
        }
        return formatStr;
    }

}
