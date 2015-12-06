package xyz.yyagi.travelbase.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.annotation.NonNull;
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

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import xyz.yyagi.travelbase.R;
import xyz.yyagi.travelbase.model.Place;
import xyz.yyagi.travelbase.model.Schedule;
import xyz.yyagi.travelbase.model.TravelDate;
import xyz.yyagi.travelbase.service.RealmBuilder;

public class PlaceMapFragment extends MapFragment {

    public static final String KEY_ID = "id";
    public static final String KEY_ID_TYPE = "id_type";
    public static final String KEY_ZOOM = "zoom";
    public static final String ID_TYPE_PLACE = "id_type_place";
    public static final String ID_TYPE_TRAVEL_DATE = "id_type_travel_date";
    public static final int DEFAULT_ZOOM = 15;
    public static final int LIST_ZOOM = 13;

    public static PlaceMapFragment newInstance(int placeId, String idType, int zoom) {
        PlaceMapFragment fragment = new PlaceMapFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_ID, placeId);
        args.putString(KEY_ID_TYPE, idType);
        args.putInt(KEY_ZOOM, zoom);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        initMap();
        return v;
    }

    private void initMap() {
        LatLng latLng;
        int zoom = getArguments().getInt(KEY_ZOOM);
        UiSettings settings = getMap().getUiSettings();
        settings.setCompassEnabled(true);

        ArrayList<Place> places = getPlaces();

        if(!places.isEmpty()) {
            Place firstPlace = places.get(0);
            latLng = new LatLng(firstPlace.getLatitude(), firstPlace.getLongitude());
            getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        }

        for(Place place : places) {
            latLng = new LatLng(place.getLatitude(), place.getLongitude());
            getMap().addMarker(
                    new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory.defaultMarker())
                            .title(place.getName())
            );
        }
    }

    private ArrayList<Place> getPlaces() {
        String idType = getArguments().getString(KEY_ID_TYPE);
        if (idType == null) {
            return null;
        }

        int id = getArguments().getInt(KEY_ID);
        ArrayList<Place> places = new ArrayList<Place>();
        Realm realm = RealmBuilder.getRealmInstance(getActivity());

        if (idType.equals(ID_TYPE_PLACE)) {
            RealmResults<Place> results = realm.where(Place.class).equalTo("id", id).findAll();
            for (Place place : results) {
                places.add(place);
            }
        } else if(idType.equals(ID_TYPE_TRAVEL_DATE)) {
            TravelDate travelDate = realm.where(TravelDate.class).equalTo("id", id).findFirst();
            for (Schedule schedule : travelDate.getSchedules()) {
                places.add(schedule.getPlace());
            }
        }
        realm.close();
        return places;
    }
}

