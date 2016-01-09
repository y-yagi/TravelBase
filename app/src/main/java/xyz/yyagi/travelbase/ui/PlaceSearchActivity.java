package xyz.yyagi.travelbase.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.orhanobut.wasp.CallBack;
import com.orhanobut.wasp.WaspError;


import java.util.HashMap;

import xyz.yyagi.travelbase.R;
import xyz.yyagi.travelbase.service.ProgressDialogBuilder;
import xyz.yyagi.travelbase.service.TravelBaseService;
import xyz.yyagi.travelbase.service.TravelBaseServiceBuilder;
import xyz.yyagi.travelbase.util.LogUtil;

public class PlaceSearchActivity extends BaseActivity implements PlaceSelectionListener, View.OnClickListener {
    private static final String TAG = LogUtil.makeLogTag(PlaceSearchActivity.class);

    private TextView mPlaceDetailsText;
    private TextView mPlaceAttribution;
    private Place mPlace;
    private Context mContext;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_place_search);

        // Retrieve the PlaceAutocompleteFragment.
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Register a listener to receive callbacks when a place has been selected or an error has
        // occurred.
        autocompleteFragment.setOnPlaceSelectedListener(this);

        // Retrieve the TextViews that will display details about the selected place.
        mPlaceDetailsText = (TextView) findViewById(R.id.place_details);
        mPlaceAttribution = (TextView) findViewById(R.id.place_attribution);
        mContext = this;
        mProgressDialog = new ProgressDialog(this, getString(R.string.loading));

        ButtonRectangle button = (ButtonRectangle) findViewById(R.id.registration_place_button);
        button.setOnClickListener(this);

        setupDrawer();
    }

    /**
     * Callback invoked when a place has been selected from the PlaceAutocompleteFragment.
     */
    @Override
    public void onPlaceSelected(Place place) {
        Log.i(TAG, "Place Selected: " + place.getName());

        // Format the returned place's details and display them in the TextView.
        mPlace = place;
        mPlaceDetailsText.setText(formatPlaceDetails(getResources(), place));

        CharSequence attributions = place.getAttributions();
        if (!TextUtils.isEmpty(attributions)) {
            mPlaceAttribution.setText(Html.fromHtml(attributions.toString()));
        } else {
            mPlaceAttribution.setText("");
        }
    }

    /**
     * Callback invoked when PlaceAutocompleteFragment encounters an error.
     */
    @Override
    public void onError(Status status) {
        Log.e(TAG, "onError: Status = " + status.toString());

        Toast.makeText(this, "Place selection failed: " + status.getStatusMessage(),
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Helper method to format information about a place nicely.
     */
    private static Spanned formatPlaceDetails(Resources res, Place place) {
        return Html.fromHtml(res.getString(R.string.place_details, place.getName(), place.getId(),
                place.getAddress(), place.getLatLng().latitude, place.getLatLng().longitude));
    }

    @Override
    public void onClick(View v) {
        if (mPlace == null) {
            // TODO: とりあえずガード
            return;
        }

        mProgressDialog.show();
        TravelBaseService service = TravelBaseServiceBuilder.build(this);
        String authHeader = TravelBaseServiceBuilder.makeBearerAuthHeader();
        HashMap<String, String> query = TravelBaseServiceBuilder.makeResourceOwnerInfo();
        if (query != null) {
            query.put("name", (String)mPlace.getName());
            query.put("address", (String)mPlace.getAddress());
            query.put("latitude", String.valueOf(mPlace.getLatLng().latitude));
            query.put("longitude", String.valueOf(mPlace.getLatLng().longitude));
        }

        service.addPlace(authHeader, "v1", query, new CallBack<xyz.yyagi.travelbase.model.Place>() {
            @Override
            public void onSuccess(xyz.yyagi.travelbase.model.Place place) {
                mProgressDialog.dismiss();
                Toast.makeText(mContext, "success", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(WaspError waspError) {
                mProgressDialog.dismiss();
                Toast.makeText(mContext, "error", Toast.LENGTH_LONG).show();
            }
        });
    }
}
