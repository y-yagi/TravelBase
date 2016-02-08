package xyz.yyagi.travelbase.ui.widget;

import android.content.Context;

/**
 * Created by yaginuma on 15/05/14.
 */
public class ProgressDialogBuilder {
    public static TravelBaseProgressDialog build(Context context, String message, int color) {
        TravelBaseProgressDialog dialog = new TravelBaseProgressDialog(context, message, color);
        dialog.setCancelable(false);
        return dialog;
    }
}
