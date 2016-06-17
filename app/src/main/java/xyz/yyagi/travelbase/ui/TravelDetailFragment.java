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
import android.databinding.DataBindingUtil;

import io.realm.Realm;
import xyz.yyagi.travelbase.R;
import xyz.yyagi.travelbase.model.Schedule;
import xyz.yyagi.travelbase.model.TravelDate;
import xyz.yyagi.travelbase.service.RealmBuilder;
import xyz.yyagi.travelbase.util.LogUtil;
import xyz.yyagi.travelbase.databinding.CardRouteBinding;
import xyz.yyagi.travelbase.databinding.CardScheduleBinding;
import xyz.yyagi.travelbase.util.StringUtil;

public class TravelDetailFragment extends Fragment {
    private Realm mRealm;
    private LinearLayout mTravelDetailLayout;
    private LinearLayout mMainLayout;
    private LayoutInflater mInflater;
    private TextView mMapTextLink;
    private TextView mNoticeTextView;
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
        mMainLayout = (LinearLayout) view.findViewById(R.id.fragmentTravelDetail);
        mRealm = RealmBuilder.getRealmInstance(getActivity());
        mMapTextLink = (TextView) view.findViewById(R.id.mapLinkText);
        mNoticeTextView = (TextView)view.findViewById(R.id.noticeText);
        displayScheudle();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void displayScheudle() {
        int travelDateId = getArguments().getInt(KEY_TRAVEL_DATE_ID);
        TravelDate travelDate = mRealm.where(TravelDate.class).equalTo("id", travelDateId).findFirst();

        if (travelDate.getSchedules().isEmpty()) {
            mNoticeTextView.setText(getString(R.string.no_schedules));
            mNoticeTextView.setVisibility(View.VISIBLE);
            return;
        }

        CardView scheduleView;
        CardView routeView;
        TextView textView;
        for (final Schedule schedule : travelDate.getSchedules()) {
            if (schedule.getRoute() != null) {
                CardRouteBinding binding = DataBindingUtil.inflate(mInflater, R.layout.card_route, mTravelDetailLayout, false);
                binding.setRoute(schedule.getRoute());
                routeView = (CardView) binding.getRoot();

                textView = (TextView) routeView.findViewById(R.id.title);
                String routeTitle = getString(R.string.route_title, schedule.getPlace().getName());
                textView.setText(routeTitle);

                routeView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int id = Integer.parseInt(((TextView) v.findViewById(R.id.title)).getTag().toString());
                        RouteDetailActivity.startActivity(getActivity(), id, schedule.getPlace().getName());
                    }
                });
                mTravelDetailLayout.addView(routeView);
            }

            CardScheduleBinding binding = DataBindingUtil.inflate(mInflater, R.layout.card_schedule, mTravelDetailLayout, false);
            binding.setSchedule(schedule);
            scheduleView= (CardView) binding.getRoot();

            textView = (TextView) scheduleView.findViewById(R.id.date);
            if (schedule.getFormatted_start_time() != null || schedule.getFormatted_end_time() != null) {
                String text = String.format("%s〜%s\n",
                        StringUtil.emptyStringOrValue(schedule.getFormatted_start_time()),
                        StringUtil.emptyStringOrValue(schedule.getFormatted_end_time())
                );
                textView.setText(text);
            }

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
        mMapTextLink.setVisibility(View.VISIBLE);
        mMapTextLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mTravelDetailLayout.removeAllViews();
                mMainLayout.removeAllViews();
                PlaceMapFragment fragment = PlaceMapFragment.newInstance(travelDateId,
                        PlaceMapFragment.ID_TYPE_TRAVEL_DATE, PlaceMapFragment.LIST_ZOOM);
                fragmentManager.beginTransaction().replace(R.id.fragmentTravelDetail, fragment).commit();
            }
        });
    }
}

