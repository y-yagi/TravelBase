package xyz.yyagi.travelbase.ui;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import io.realm.Realm;
import io.realm.RealmResults;
import xyz.yyagi.travelbase.R;
import xyz.yyagi.travelbase.model.User;

public class BaseActivity extends Activity {
    private Realm mRealm;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_common, menu);
        mRealm = Realm.getInstance(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_logout:
                logout();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        mRealm.where(User.class).findFirst().removeFromRealm();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(intent);
    }
}
