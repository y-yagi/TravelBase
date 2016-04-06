package xyz.yyagi.travelbase.util;

/**
 * Created by yaginuma on 16/04/07.
 */
public class StringUtil {
    public static String emptyStringOrValue(String value) {
        if (value == null) {
            return "";
        } else {
            return value;
        }
    }
}
