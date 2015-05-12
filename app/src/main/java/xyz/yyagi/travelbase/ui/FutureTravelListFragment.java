package xyz.yyagi.travelbase.ui;

import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import io.realm.RealmResults;
import xyz.yyagi.travelbase.R;
import xyz.yyagi.travelbase.model.Travel;
import xyz.yyagi.travelbase.util.LogUtil;

/**
 * Created by yaginuma on 15/05/08.
 */
public class FutureTravelListFragment extends TravelListFragment {
    private static final String TAG = LogUtil.makeLogTag(FutureTravelListFragment.class);

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        displayTravels();
    }

    private void displayTravels() {
        Calendar calendar = Calendar.getInstance();
        RealmResults<Travel> travelList = mRealm.where(Travel.class)
                .greaterThanOrEqualTo("end_date", calendar.getTime())
                .findAll();

        if (travelList.size() == 0) {
            mNoticeTextView.setText(getString(R.string.no_plan_of_travel));
            mNoticeTextView.setVisibility(View.VISIBLE);
            return;
        }

        for (Travel travel : travelList) {
            CardView cardView;
            TextView textView;
            cardView = (CardView) mInflater.inflate(R.layout.travel_card, null, false);
            textView = (TextView) cardView.findViewById(R.id.name);
            textView.setText(travel.getName());
            textView = (TextView) cardView.findViewById(R.id.date);
            textView.setText(travel.getFormatted_start_date() + "〜" + travel.getFormatted_end_date() + "\n");
            textView = (TextView) cardView.findViewById(R.id.memo);
            textView.setText(travel.getMemo());

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), String.valueOf(v.getTag()) + "番目のCardViewがクリックされました", Toast.LENGTH_SHORT).show();
                }
            });
            mTravelListLayout.addView(cardView);
        }
    }
}