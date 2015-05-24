package xyz.yyagi.travelbase.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

import java.util.Map;

import io.realm.Realm;
import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;
import xyz.yyagi.travelbase.R;
import xyz.yyagi.travelbase.model.Place;
import xyz.yyagi.travelbase.util.LogUtil;


public class PlaceDetailActivity extends BaseActivity implements MaterialTabListener {
    private static final String TAG = LogUtil.makeLogTag(PlaceDetailActivity.class);
    private static final String EXTRA_PLACE_ID = "id";

    private Activity mActivity;
    private MaterialTabHost mTabHost;
    private ViewPager mPager;
    private PagerAdapter mPagerAdaper;
    private String[] mPageTitles = null;

    public static void startActivity(Context context, int id) {
        Intent intent = new Intent(context, PlaceDetailActivity.class);
        intent.putExtra(EXTRA_PLACE_ID, id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_detail);
        mActivity = this;

        Bundle extras = getIntent().getExtras();
        int id = extras.getInt(EXTRA_PLACE_ID);

        Realm realm = Realm.getInstance(this);
        Place place = realm.where(Place.class).equalTo("id", id).findFirst();
        setTitle(place.getName());

        mTabHost = (MaterialTabHost) this.findViewById(R.id.materialTabHost);
        mPager = (ViewPager) this.findViewById(R.id.viewpager);
        mPageTitles = getResources().getStringArray(R.array.place_detail_tab_titles);

        // init view pager
        mPagerAdaper = new ViewPagerAdapter(getFragmentManager(), id);
        mPager.setAdapter(mPagerAdaper);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mTabHost.setSelectedNavigationItem(position);
            }
        });

        for (int i = 0; i < mPagerAdaper.getCount(); i++) {
            mTabHost.addTab(mTabHost.newTab().setText(mPageTitles[i]).setTabListener(this));
        }
    }

    @Override
    public void onTabSelected(MaterialTab tab) {
        mPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(MaterialTab tab) {
    }

    @Override
    public void onTabUnselected(MaterialTab tab) {
    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final int TAB_COUNT = 2;
        private String[] mPageTitles = null;
        private PlaceDetailFragment mPlaceDetailFragment;
        private PlaceMapFragment mPlaceMapFragment;
        private GoogleMap mMap;
        private int mPlaceId;

        public ViewPagerAdapter(FragmentManager fm, int placeId) {
            super(fm);
            mPlaceId = placeId;
            mPlaceDetailFragment = PlaceDetailFragment.newInstance(mPlaceId);
            mPlaceMapFragment = PlaceMapFragment.newInstance(mPlaceId);
        }

        @Override
        public Fragment getItem(int num) {
            if (num == 0) {
                return mPlaceDetailFragment;
            } else {
                return mPlaceMapFragment;
            }
        }

        @Override
        public int getCount() {
            return TAB_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (mPageTitles == null) {
                mPageTitles = getApplicationContext().getResources().getStringArray(R.array.place_detail_tab_titles);
            }
            return mPageTitles[position];
        }
    }
}

