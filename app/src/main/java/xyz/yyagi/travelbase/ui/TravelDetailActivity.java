package xyz.yyagi.travelbase.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;

import io.realm.Realm;
import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;
import xyz.yyagi.travelbase.R;
import xyz.yyagi.travelbase.model.Travel;
import xyz.yyagi.travelbase.model.TravelDate;
import xyz.yyagi.travelbase.util.DateUtil;
import xyz.yyagi.travelbase.util.LogUtil;


public class TravelDetailActivity extends BaseActivity implements MaterialTabListener {
    private static final String TAG = LogUtil.makeLogTag(TravelDetailActivity.class);
    private Realm mRealm;
    private static final String EXTRA_TRAVEL_ID = "id";
    private MaterialTabHost mTabHost;
    private ViewPager mPager;
    private PagerAdapter mPagerAdaper;
    private String[] mPageTitles = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_detail);
        mRealm = Realm.getInstance(this);
        Bundle extras = getIntent().getExtras();
        int id = extras.getInt(EXTRA_TRAVEL_ID);
        Travel travel = mRealm.where(Travel.class).equalTo("id", id).findFirst();
        setTitle(travel.getName());

        mTabHost = (MaterialTabHost) this.findViewById(R.id.materialTabHost);
        mPager = (ViewPager) this.findViewById(R.id.viewpager);

        ArrayList<String> pageTitles = new ArrayList<String>();
        ArrayList<TravelDate> travelDates = new ArrayList<TravelDate>();
        for(TravelDate travelDate : travel.getTravel_dates()) {
            pageTitles.add(DateUtil.format(travelDate.getDate()));
            travelDates.add(travelDate);
        }

        mPagerAdaper = new ViewPagerAdapter(getFragmentManager(), travel, pageTitles, travelDates);
        mPager.setAdapter(mPagerAdaper);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mTabHost.setSelectedNavigationItem(position);
            }
        });

        for (int i = 0; i < mPagerAdaper.getCount(); i++) {
            mTabHost.addTab(mTabHost.newTab().setText(mPagerAdaper.getPageTitle(i)).setTabListener(this));
        }
    }

    public static void startActivity(Context context, int id) {
        Intent intent = new Intent(context, TravelDetailActivity.class);
        intent.putExtra(EXTRA_TRAVEL_ID, id);
        context.startActivity(intent);
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
        private Travel mTravel;
        private ArrayList<String> mPageTitles;
        private ArrayList<TravelDate> mTravelDates;

        public ViewPagerAdapter(FragmentManager fm, Travel travel, ArrayList<String> pageTitles, ArrayList<TravelDate> travelDates) {
            super(fm);
            mTravel = travel;
            mPageTitles = pageTitles;
            mTravelDates = travelDates;
        }

        @Override
        public Fragment getItem(int num) {
            TravelDetailFragment fragment = TravelDetailFragment.newInstance(mTravelDates.get(num).getId());
            return fragment;
        }

        @Override
        public int getCount() {
            return mTravelDates.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mPageTitles.get(position);
        }
    }
}
