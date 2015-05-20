package xyz.yyagi.travelbase.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ListActivity;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import io.realm.Realm;
import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;
import xyz.yyagi.travelbase.R;
import xyz.yyagi.travelbase.adapter.ScheduleTableAdapter;
import xyz.yyagi.travelbase.model.Schedule;
import xyz.yyagi.travelbase.model.Travel;
import xyz.yyagi.travelbase.model.TravelDate;
import xyz.yyagi.travelbase.util.DateUtil;
import xyz.yyagi.travelbase.util.LogUtil;


public class TravelDetailActivity extends BaseActivity implements MaterialTabListener, TravelDetailFragment.Listener {
    private static final String TAG = LogUtil.makeLogTag(TravelDetailActivity.class);
    private Realm mRealm;
    private static final String EXTRA_TRAVEL_ID = "id";
    private MaterialTabHost mTabHost;
    private ViewPager mPager;
    private PagerAdapter mPagerAdaper;
    private ArrayList<ScheduleTableAdapter> mScheduleTableAdapters;
    private String[] mPageTitles = null;
    private Set<TravelDetailFragment> mTravelDetailFragments = new HashSet<TravelDetailFragment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_detail);
        mRealm = Realm.getInstance(this);
        Bundle extras = getIntent().getExtras();
        int id = extras.getInt(EXTRA_TRAVEL_ID);
        Travel travel = mRealm.where(Travel.class).equalTo("id", id).findFirst();

        mTabHost = (MaterialTabHost) this.findViewById(R.id.materialTabHost);
        mPager = (ViewPager) this.findViewById(R.id.viewpager);
        mScheduleTableAdapters = new ArrayList<ScheduleTableAdapter>();

        ArrayList<String> pageTitleList = new ArrayList<String>();
        for(TravelDate travelDate : travel.getTravel_dates()) {
            pageTitleList.add(DateUtil.format(travelDate.getDate()));
            setScheduleTableAdapters(travelDate.getId());
        }

        mPagerAdaper = new ViewPagerAdapter(getFragmentManager(), travel, pageTitleList);
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

    @Override
    public void onFragmentViewCreated(ListFragment fragment) {
        int number = fragment.getArguments().getInt(TravelDetailFragment.KEY_NUMBER, 0);
        fragment.setListAdapter(mScheduleTableAdapters.get(number));
    }

    @Override
    public void onFragmentAttached(TravelDetailFragment fragment) {
        mTravelDetailFragments.add(fragment);
    }

    @Override
    public void onFragmentDetached(TravelDetailFragment fragment) {
        mTravelDetailFragments.remove(fragment);
    }

    private void setScheduleTableAdapters(int travelDateId) {
        TravelDate travelDate = mRealm.where(TravelDate.class).equalTo("id", travelDateId).findFirst();
        ArrayList<Schedule> mItems = new ArrayList<Schedule>();
        for(Schedule schedule : travelDate.getSchedules()) {
            mItems.add(schedule);
        }
        ScheduleTableAdapter scheduleTableAdapter = new ScheduleTableAdapter(this, mItems);
        mScheduleTableAdapters.add(scheduleTableAdapter);
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private Travel mTravel;
        private ArrayList<String> mPageTitleList;

        public ViewPagerAdapter(FragmentManager fm, Travel travel, ArrayList<String> pageTitleList) {
            super(fm);
            mTravel = travel;
            mPageTitleList = pageTitleList;
        }

        @Override
        public Fragment getItem(int num) {
            TravelDetailFragment fragment = TravelDetailFragment.newInstance(num);
            return fragment;
        }

        @Override
        public int getCount() {
            return mTravel.getTravel_dates().size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mPageTitleList.get(position);
        }
    }
}
