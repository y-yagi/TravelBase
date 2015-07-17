package xyz.yyagi.travelbase.ui;

import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import xyz.yyagi.travelbase.BuildConfig;
import xyz.yyagi.travelbase.R;

import static org.assertj.core.api.Assertions.assertThat;
import static org.robolectric.Shadows.shadowOf;

/**
 * Created by yaginuma on 15/07/09.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class LoginActivityTest {

    @Test
    public void onCreate_DisplayTitle() throws Exception {
        Activity activity = Robolectric.setupActivity(LoginActivity.class);
        TextView textView = (TextView)shadowOf(activity).findViewById(R.id.login_title);
        assertThat(textView.getText()).isEqualTo("TravelBase");
    }
}
