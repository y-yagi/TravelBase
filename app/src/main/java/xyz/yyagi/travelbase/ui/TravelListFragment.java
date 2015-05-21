package xyz.yyagi.travelbase.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.wasp.CallBack;
import com.orhanobut.wasp.WaspError;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import xyz.yyagi.travelbase.R;
import xyz.yyagi.travelbase.model.Travel;
import xyz.yyagi.travelbase.service.TravelBaseService;
import xyz.yyagi.travelbase.service.TravelBaseServiceBuilder;
import xyz.yyagi.travelbase.util.LogUtil;

/**
 * Created by yaginuma on 15/05/08.
 */
public class TravelListFragment extends Fragment {
    private static final String TAG = LogUtil.makeLogTag(TravelListFragment.class);
    protected LinearLayout mTravelListLayout;
    protected TextView mNoticeTextView;
    protected LayoutInflater mInflater;
    protected Realm mRealm;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mInflater = inflater;
        return inflater.inflate(R.layout.fragment_travel_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mTravelListLayout = (LinearLayout) view.findViewById(R.id.travelList);
        mNoticeTextView = (TextView)view.findViewById(R.id.noticeText);
        mRealm = Realm.getInstance(getActivity());
    }

}
