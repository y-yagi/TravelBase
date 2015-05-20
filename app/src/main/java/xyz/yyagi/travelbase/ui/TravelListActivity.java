package xyz.yyagi.travelbase.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;
import xyz.yyagi.travelbase.R;
import xyz.yyagi.travelbase.util.LogUtil;

public class TravelListActivity extends BaseActivity implements MaterialTabListener {
    private Activity mActivity;
    private static final String TAG = LogUtil.makeLogTag(TravelListActivity.class);
    private MaterialTabHost mTabHost;
    private ViewPager mPager;
    private PagerAdapter mPagerAdaper;
    private String[] mPageTitles = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_list);
        mActivity = this;

        mTabHost = (MaterialTabHost) this.findViewById(R.id.materialTabHost);
        mPager = (ViewPager) this.findViewById(R.id.viewpager);
        mPageTitles = getResources().getStringArray(R.array.travel_list_tab_titles);

        // init view pager
        mPagerAdaper = new ViewPagerAdapter(getFragmentManager());
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
        private TravelListFragment mPastTravelListFragment = new PastTravelListFragment();
        private TravelListFragment mFutureTravelListFragment = new FutureTravelListFragment();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int num) {
            if (num == 0) {
                return mFutureTravelListFragment;
            } else {
                return mPastTravelListFragment;
            }
        }

        @Override
        public int getCount() {
            return TAB_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (mPageTitles == null) {
                mPageTitles = getApplicationContext().getResources().getStringArray(R.array.travel_list_tab_titles);
            }
            return mPageTitles[position];
        }
    }
}
