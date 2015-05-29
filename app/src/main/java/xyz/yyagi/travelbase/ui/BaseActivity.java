package xyz.yyagi.travelbase.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import io.realm.Realm;
import io.realm.RealmResults;
import xyz.yyagi.travelbase.R;
import xyz.yyagi.travelbase.model.User;

public class BaseActivity extends AppCompatActivity {
    private Activity mActivity;
    private static final int DRAWER_TRAVEL_LIST = 1;
    private static final int DRAWER_LOGOUT = 2;

    protected  void setupDrawer() {
        mActivity = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final IProfile profile = new ProfileDrawerItem().withName("Mike Penz").withEmail("mikepenz@gmail.com");
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .build();


        Drawer drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .withTranslucentStatusBar(false)
                .withActionBarDrawerToggle(true)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(getString(R.string.title_activity_travel_list))
                                .withIcon(FontAwesome.Icon.faw_calendar).withIdentifier(DRAWER_TRAVEL_LIST),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(getString(R.string.action_logout)).
                                withIcon(FontAwesome.Icon.faw_sign_out).withIdentifier(DRAWER_LOGOUT)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        int identifier = drawerItem.getIdentifier();
                        switch (identifier) {
                            case DRAWER_TRAVEL_LIST:
                                Intent intent = new Intent(mActivity, TravelListActivity.class);
                                startActivity(intent);
                                return true;
                            case DRAWER_LOGOUT:
                                logout();
                                return true;
                        }
                        return false;
                    }
                })
                .build();
    }

    protected  void logout() {
        Realm realm = Realm.getInstance(this);
        realm.where(User.class).findFirst().removeFromRealm();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(intent);
    }
}
