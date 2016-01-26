package xyz.yyagi.travelbase.ui.widget;

import android.content.Context;

import com.gc.materialdesign.widgets.ProgressDialog;

/**
 * Created by yaginuma on 15/05/14.
 */
public class ProgressDialogBuilder {

    public static ProgressDialog build(Context context, String message, int color) {
        ProgressDialog dialog = new ProgressDialog(context, message, color);
        dialog.setCancelable(false);
        return dialog;
    }
}
