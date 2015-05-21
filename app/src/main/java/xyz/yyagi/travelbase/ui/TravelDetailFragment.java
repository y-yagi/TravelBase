package xyz.yyagi.travelbase.ui;

import android.app.Activity;
import android.app.ListFragment;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import xyz.yyagi.travelbase.R;
import xyz.yyagi.travelbase.adapter.ScheduleTableAdapter;
import xyz.yyagi.travelbase.model.Schedule;
import xyz.yyagi.travelbase.model.Travel;
import xyz.yyagi.travelbase.model.TravelDate;

public class TravelDetailFragment extends Fragment {
    private Realm mRealm;
    private int mTravelDateId;
    private LinearLayout mTravelDetailLayout;
    private LayoutInflater mInflater;
    public static final String KEY_TRAVEL_DATE_ID = "travel_date_id";

    public static TravelDetailFragment newInstance(int travelDateId) {
        TravelDetailFragment fragment = new TravelDetailFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_TRAVEL_DATE_ID, travelDateId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mInflater = inflater;
        return inflater.inflate(R.layout.fragment_travel_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mTravelDetailLayout = (LinearLayout) view.findViewById(R.id.travelDetail);
        mRealm = Realm.getInstance(getActivity());
        displayScheudle();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void displayScheudle() {
        int travelDateId = getArguments().getInt(KEY_TRAVEL_DATE_ID);
        TravelDate travelDate = mRealm.where(TravelDate.class).equalTo("id", travelDateId).findFirst();

        for (Schedule schedule : travelDate.getSchedules()) {
            CardView cardView;
            TextView textView;
            cardView = (CardView) mInflater.inflate(R.layout.schedule_card, mTravelDetailLayout, false);
            textView = (TextView) cardView.findViewById(R.id.name);
            textView.setText(schedule.getPlace().getName());
            textView.setTag(schedule.getPlace().getId());
            textView = (TextView) cardView.findViewById(R.id.date);
            textView.setText(schedule.getFormatted_start_time() + "〜" + schedule.getFormatted_end_time() + "\n");
            textView = (TextView) cardView.findViewById(R.id.memo);
            textView.setText(schedule.getMemo());

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO:place link
                }
            });
            mTravelDetailLayout.addView(cardView);
        }
    }
}

