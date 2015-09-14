package xyz.yyagi.travelbase.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import io.realm.Realm;
import xyz.yyagi.travelbase.R;
import xyz.yyagi.travelbase.model.Travel;
import xyz.yyagi.travelbase.model.User;
import xyz.yyagi.travelbase.service.RealmBuilder;
import xyz.yyagi.travelbase.util.HelpUtils;

public class BaseActivity extends AppCompatActivity {
    private Activity mActivity;
    private static final int DRAWER_TRAVEL_LIST = 1;
    private static final int DRAWER_LOGOUT = 2;
    private static final int DRAWER_DETAIL = 3;
    private static final int DRAWER_PLACE_LIST = 4;

    protected  void setupDrawer() {
        mActivity = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        Realm realm = Realm.getInstance(this);
        User user = realm.where(User.class).findFirst();
        final IProfile profile = new ProfileDrawerItem().withName(user.getUid());
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.color.primary_dark)
                .addProfiles(profile)
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
                        new PrimaryDrawerItem().withName(getString(R.string.title_activity_place_list))
                                .withIcon(FontAwesome.Icon.faw_map_marker).withIdentifier(DRAWER_PLACE_LIST),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(getString(R.string.action_detail)).
                                withIcon(FontAwesome.Icon.faw_info_circle).withIdentifier(DRAWER_DETAIL),
                        new DividerDrawerItem(),
                new PrimaryDrawerItem().withName(getString(R.string.action_logout)).
                        withIcon(FontAwesome.Icon.faw_sign_out).withIdentifier(DRAWER_LOGOUT)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        int identifier = drawerItem.getIdentifier();
                        Intent intent;
                        switch (identifier) {
                            case DRAWER_TRAVEL_LIST:
                                intent = new Intent(mActivity, TravelListActivity.class);
                                startActivity(intent);
                                return true;
                            case DRAWER_PLACE_LIST:
                                intent = new Intent(mActivity, PlaceListActivity.class);
                                startActivity(intent);
                                return true;
                            case DRAWER_LOGOUT:
                                logout();
                                return true;
                            case DRAWER_DETAIL:
                                HelpUtils.showAbout(mActivity);
                                return true;
                        }
                        return false;
                    }
                })
                .build();
    }

    protected  void logout() {
        Realm realm = RealmBuilder.getRealmInstance(this);

        realm.beginTransaction();
        realm.clear(User.class);
        // FIXME:remained other tables
        realm.clear(Travel.class);
        realm.commitTransaction();
        realm.close();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(intent);
    }
}
