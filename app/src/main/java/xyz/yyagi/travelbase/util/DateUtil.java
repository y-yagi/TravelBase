package xyz.yyagi.travelbase.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yaginuma on 15/05/18.
 */
public class DateUtil {
    public static String format(Date d) {
        return new SimpleDateFormat("yyyy/MM/dd").format(d);
    }
}
