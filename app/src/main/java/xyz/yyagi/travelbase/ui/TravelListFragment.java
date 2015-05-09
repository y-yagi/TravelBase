package xyz.yyagi.travelbase.ui;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import xyz.yyagi.travelbase.R;
import xyz.yyagi.travelbase.model.Travel;
import xyz.yyagi.travelbase.util.LogUtil;

/**
 * Created by yaginuma on 15/05/08.
 */
public class TravelListFragment extends Fragment {
    private static final String TAG = LogUtil.makeLogTag(TravelListFragment.class);
    public ArrayList<Travel> travelList;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return  inflater.inflate(R.layout.fragment_travel_list, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        for (Travel travel : travelList) {
            CardView cardView;
            TextView textView;
            cardView = (CardView) view.findViewById(R.id.card_view);

            textView = (TextView) view.findViewById(R.id.name);
            textView.setText(travel.name);
            textView = (TextView) view.findViewById(R.id.date);
            textView.setText(travel.formatted_start_date + "〜" + travel.formatted_end_date + "\n");
            textView = (TextView) view.findViewById(R.id.memo);
            textView.setText(travel.memo);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), String.valueOf(v.getTag()) + "番目のCardViewがクリックされました", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}

