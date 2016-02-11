package xyz.yyagi.travelbase.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.databinding.DataBindingUtil;

import io.realm.Realm;
import io.realm.RealmResults;
import xyz.yyagi.travelbase.R;
import xyz.yyagi.travelbase.model.Travel;
import xyz.yyagi.travelbase.service.RealmBuilder;
import xyz.yyagi.travelbase.util.LogUtil;
import xyz.yyagi.travelbase.databinding.CardTravelBinding;

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
        mRealm = RealmBuilder.getRealmInstance(getActivity());
    }

    protected void displayTravels(RealmResults<Travel> travels) {
        for (Travel travel : travels) {
            CardView cardView;
            TextView textView;

            CardTravelBinding binding = DataBindingUtil.inflate(mInflater, R.layout.card_travel, mTravelListLayout, false);
            binding.setTravel(travel);
            cardView = (CardView) binding.getRoot();

            textView = (TextView) cardView.findViewById(R.id.date);
            String text = "";
            if (travel.getFormatted_start_date().equals(travel.getFormatted_end_date())) {
                text = String.format("%s\n", travel.getFormatted_start_date());
            } else {
                text = String.format("%s〜%s\n", travel.getFormatted_start_date(), travel.getFormatted_end_date());
            }
            textView.setText(text);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = Integer.parseInt(((TextView) v.findViewById(R.id.name)).getTag().toString());
                    TravelDetailActivity.startActivity(getActivity(), id);
                }
            });
            mTravelListLayout.addView(cardView);
        }
    }

}
