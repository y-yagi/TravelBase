package xyz.yyagi.travelbase.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.google.android.gms.maps.GoogleMap;

import io.realm.Realm;
import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;
import xyz.yyagi.travelbase.R;
import xyz.yyagi.travelbase.model.Event;
import xyz.yyagi.travelbase.service.RealmBuilder;
import xyz.yyagi.travelbase.util.LogUtil;


public class EventDetailActivity extends BaseActivity implements MaterialTabListener {
    private static final String TAG = LogUtil.makeLogTag(EventDetailActivity.class);
    private static final String EXTRA_EVENT_ID = "id";

    private MaterialTabHost mTabHost;
    private ViewPager mPager;
    private String[] mPageTitles = null;

    public static void startActivity(Context context, int id) {
        Intent intent = new Intent(context, EventDetailActivity.class);
        intent.putExtra(EXTRA_EVENT_ID, id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        Bundle extras = getIntent().getExtras();
        int id = extras.getInt(EXTRA_EVENT_ID);

        Realm realm = RealmBuilder.getRealmInstance(this);
        Event event = realm.where(Event.class).equalTo("id", id).findFirst();
        realm.close();
        setTitle(event.getName());

        mTabHost = (MaterialTabHost) this.findViewById(R.id.materialTabHost);
        mPager = (ViewPager) this.findViewById(R.id.viewpager);
        mPageTitles = getResources().getStringArray(R.array.place_detail_tab_titles);

        // init view pager
        PagerAdapter pagerAdaper = new ViewPagerAdapter(getFragmentManager(), id, event.getPlace_id());
        mPager.setAdapter(pagerAdaper);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mTabHost.setSelectedNavigationItem(position);
            }
        });

        for (int i = 0; i < pagerAdaper.getCount(); i++) {
            mTabHost.addTab(mTabHost.newTab().setText(mPageTitles[i]).setTabListener(this));
        }
        setupDrawer();
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
        private EventDetailFragment mEventDetailFragment;
        private PlaceMapFragment mEventMapFragment;
        private GoogleMap mMap;
        private int mEventId;
        private int mPlaceId;

        public ViewPagerAdapter(FragmentManager fm, int eventId, int placeId) {
            super(fm);
            mEventId = eventId;
            mPlaceId = placeId;
        }

        @Override
        public Fragment getItem(int num) {
            if (num == 0) {
                return EventDetailFragment.newInstance(mEventId);
            } else {
                return PlaceMapFragment.newInstance(mPlaceId, PlaceMapFragment.ID_TYPE_PLACE, PlaceMapFragment.DEFAULT_ZOOM);
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

