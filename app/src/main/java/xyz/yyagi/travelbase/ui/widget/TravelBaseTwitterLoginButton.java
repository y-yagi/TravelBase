package xyz.yyagi.travelbase.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import xyz.yyagi.travelbase.R;

/**
 * Created by yaginuma on 15/06/30.
 */
public class TravelBaseTwitterLoginButton extends TwitterLoginButton{
    public TravelBaseTwitterLoginButton(Context context) {
        super(context);
        init();
    }

    public TravelBaseTwitterLoginButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TravelBaseTwitterLoginButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        if (isInEditMode()){
            return;
        }
        setText(R.string.twitter_login);
    }
}
