package cn.lunadeer.dominion.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Time {

    public static String nowStr() {
        // yyyy-MM-dd HH:mm:ss
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
}
