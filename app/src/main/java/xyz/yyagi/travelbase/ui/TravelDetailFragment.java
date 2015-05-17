package xyz.yyagi.travelbase.ui;

import android.app.Activity;
import android.app.ListFragment;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import io.realm.Realm;
import xyz.yyagi.travelbase.R;
import xyz.yyagi.travelbase.adapter.ScheduleTableAdapter;
import xyz.yyagi.travelbase.model.Schedule;
import xyz.yyagi.travelbase.model.Travel;
import xyz.yyagi.travelbase.model.TravelDate;

public class TravelDetailFragment extends ListFragment {
    private Realm mRealm;
    private int mTravelDateId;
    public static final String KEY_NUMBER = "number";

    public interface Listener {
        public void onFragmentViewCreated(ListFragment fragment);
        public void onFragmentAttached(TravelDetailFragment fragment);
        public void onFragmentDetached(TravelDetailFragment fragment);
    }

    public static TravelDetailFragment newInstance(int number) {
        TravelDetailFragment fragment = new TravelDetailFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_NUMBER, number);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_travel_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mRealm = Realm.getInstance(getActivity());
        if (getActivity() instanceof Listener) {
            ((Listener) getActivity()).onFragmentViewCreated(this);
        }
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (getActivity() instanceof Listener) {
            ((Listener) getActivity()).onFragmentAttached(this);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (getActivity() instanceof Listener) {
            ((Listener) getActivity()).onFragmentDetached(this);
        }
    }
}

