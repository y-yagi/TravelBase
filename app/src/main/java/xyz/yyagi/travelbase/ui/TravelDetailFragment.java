package xyz.yyagi.travelbase.ui;

import android.app.FragmentManager;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.realm.Realm;
import xyz.yyagi.travelbase.R;
import xyz.yyagi.travelbase.model.Schedule;
import xyz.yyagi.travelbase.model.TravelDate;
import xyz.yyagi.travelbase.util.LogUtil;

public class TravelDetailFragment extends Fragment {
    private Realm mRealm;
    private LinearLayout mTravelDetailLayout;
    private LayoutInflater mInflater;
    private TextView mMapTextLink;
    public static final String KEY_TRAVEL_DATE_ID = "travel_date_id";
    private static final String TAG = LogUtil.makeLogTag(PlaceDetailActivity.class);

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
        mMapTextLink = (TextView) view.findViewById(R.id.mapLinkText);
        displayScheudle();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void displayScheudle() {
        int travelDateId = getArguments().getInt(KEY_TRAVEL_DATE_ID);
        TravelDate travelDate = mRealm.where(TravelDate.class).equalTo("id", travelDateId).findFirst();

        CardView scheduleView;
        CardView routeView;
        TextView textView;
        for (final Schedule schedule : travelDate.getSchedules()) {
            if (schedule.getRoute() != null) {
                routeView = (CardView) mInflater.inflate(R.layout.card_route, mTravelDetailLayout, false);

                textView = (TextView) routeView.findViewById(R.id.title);
                String routeTitle = getString(R.string.route_title, schedule.getPlace().getName());
                textView.setText(routeTitle);
                textView.setTag(schedule.getRoute().getId());
                textView = (TextView) routeView.findViewById(R.id.detail);
                textView.setText(schedule.getRoute().getDetail());
                routeView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int id = Integer.parseInt(((TextView) v.findViewById(R.id.title)).getTag().toString());
                        RouteDetailActivity.startActivity(getActivity(), id, schedule.getPlace().getName());
                    }
                });
                mTravelDetailLayout.addView(routeView);
            }

            scheduleView = (CardView) mInflater.inflate(R.layout.card_schedule, mTravelDetailLayout, false);
            textView = (TextView) scheduleView.findViewById(R.id.name);
            textView.setText(schedule.getPlace().getName());
            textView.setTag(schedule.getPlace().getId());
            textView = (TextView) scheduleView.findViewById(R.id.date);
            if (!schedule.getFormatted_start_time().isEmpty() || !schedule.getFormatted_end_time().isEmpty()) {
                textView.setText(schedule.getFormatted_start_time() + "〜" + schedule.getFormatted_end_time() + "\n");
            }
            textView = (TextView) scheduleView.findViewById(R.id.memo);
            textView.setText(schedule.getMemo());

            scheduleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = Integer.parseInt(((TextView) v.findViewById(R.id.name)).getTag().toString());
                    PlaceDetailActivity.startActivity(getActivity(), id);
                }
            });
            mTravelDetailLayout.addView(scheduleView);
        }
        setPlacesMap(travelDate.getId());
    }

    private void setPlacesMap(final int travelDateId) {
        final FragmentManager fragmentManager = getFragmentManager();
        mMapTextLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlaceMapFragment fragment = PlaceMapFragment.newInstance(travelDateId, PlaceMapFragment.ID_TYPE_TRAVEL_DATE);
                fragmentManager.beginTransaction().replace(R.id.content_layout, fragment).commit();
            }
        });
    }
}

