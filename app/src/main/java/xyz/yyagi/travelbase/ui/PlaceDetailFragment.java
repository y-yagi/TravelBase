package xyz.yyagi.travelbase.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.realm.Realm;
import xyz.yyagi.travelbase.R;
import xyz.yyagi.travelbase.databinding.FragmentPlaceDetailBinding;
import xyz.yyagi.travelbase.model.Place;
import xyz.yyagi.travelbase.service.RealmBuilder;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link PlaceDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlaceDetailFragment extends Fragment {
    public static final String KEY_PLACE_ID = "place_id";
    private FragmentPlaceDetailBinding mBinding;
    private Place mPlace;

    public static PlaceDetailFragment newInstance(int placeId) {
        PlaceDetailFragment fragment = new PlaceDetailFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_PLACE_ID, placeId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setPlace();
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_place_detail, container, false);
        mBinding.setPlace(mPlace);

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        displayScheudle(view);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void setPlace() {
        int placeId = getArguments().getInt(KEY_PLACE_ID);
        Realm realm = RealmBuilder.getRealmInstance(getActivity());
        mPlace = realm.where(Place.class).equalTo("id", placeId).findFirst();
        realm.close();
    }

    private void displayScheudle(View view) {
        TextView textView;

        if (!mPlace.getUrl().isEmpty()) {
            textView = (TextView) view.findViewById(R.id.url);
            textView.setText(mPlace.getUrl().replace(",", "\n"));
            Linkify.addLinks(textView, Linkify.ALL);
        }
    }
}

