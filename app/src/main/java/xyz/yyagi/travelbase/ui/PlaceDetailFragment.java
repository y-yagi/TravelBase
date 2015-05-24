package xyz.yyagi.travelbase.ui;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.realm.Realm;
import xyz.yyagi.travelbase.R;
import xyz.yyagi.travelbase.model.Place;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link PlaceDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlaceDetailFragment extends Fragment {
    private Realm mRealm;
    public static final String KEY_PLACE_ID = "place_id";

    public static PlaceDetailFragment newInstance(int placeId) {
        PlaceDetailFragment fragment = new PlaceDetailFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_PLACE_ID, placeId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_place_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mRealm = Realm.getInstance(getActivity());
        displayScheudle(view);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void displayScheudle(View view) {
        TextView textView;
        int placeId = getArguments().getInt(KEY_PLACE_ID);
        Place place = mRealm.where(Place.class).equalTo("id", placeId).findFirst();

        textView = (TextView) view.findViewById(R.id.address);
        textView.setText(place.getAddress());
        if (!place.getStation_info().isEmpty()) {
            textView = (TextView) view.findViewById(R.id.station);
            textView.setText(place.getStation_info());
        }
        if (!place.getName().isEmpty()) {
            textView = (TextView) view.findViewById(R.id.memo);
            textView.setText(place.getMemo());
        }
    }
}

