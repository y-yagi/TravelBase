package xyz.yyagi.travelbase.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import io.realm.Realm;
import xyz.yyagi.travelbase.R;
import xyz.yyagi.travelbase.model.Place;

public class PlaceMapFragment extends MapFragment {

    private LatLng mLatLon;
    private Realm mRealm;
    private Activity mContext;
    public static final String KEY_PLACE_ID = "place_id";

    public static PlaceMapFragment newInstance(int placeId) {
        PlaceMapFragment fragment = new PlaceMapFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_PLACE_ID, placeId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        mRealm = Realm.getInstance(getActivity());
        int placeId = getArguments().getInt(KEY_PLACE_ID);
        Place place = mRealm.where(Place.class).equalTo("id", placeId).findFirst();
        mLatLon = new LatLng(place.getLatitude(), place.getLongitude());

        initMap();
        return v;
    }

    private void initMap() {
        UiSettings settings = getMap().getUiSettings();

        getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLon, 16));
        getMap().addMarker(new MarkerOptions().position(mLatLon).icon(BitmapDescriptorFactory.defaultMarker()));
    }
}

