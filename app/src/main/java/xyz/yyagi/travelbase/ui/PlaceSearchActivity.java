package xyz.yyagi.travelbase.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.orhanobut.wasp.CallBack;
import com.orhanobut.wasp.WaspError;


import java.util.HashMap;

import io.realm.Realm;
import xyz.yyagi.travelbase.R;
import xyz.yyagi.travelbase.ui.widget.ProgressDialogBuilder;
import xyz.yyagi.travelbase.service.TravelBaseService;
import xyz.yyagi.travelbase.service.TravelBaseServiceBuilder;
import xyz.yyagi.travelbase.util.LogUtil;

public class PlaceSearchActivity extends BaseActivity implements PlaceSelectionListener, View.OnClickListener {
    private static final String TAG = LogUtil.makeLogTag(PlaceSearchActivity.class);

    private Place mPlace;
    private Context mContext;
    private ProgressDialog mProgressDialog;
    private ButtonRectangle mRegistrationButton;

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

        mContext = this;
        mProgressDialog = ProgressDialogBuilder.build(this, getString(R.string.loading), getResources().getColor(R.color.primary));

        mRegistrationButton = (ButtonRectangle) findViewById(R.id.registration_place_button);
        mRegistrationButton.setOnClickListener(this);

        setupDrawer();
    }

    /**
     * Callback invoked when a place has been selected from the PlaceAutocompleteFragment.
     */
    @Override
    public void onPlaceSelected(Place place) {
        mPlace = place;
        mRegistrationButton.setVisibility(View.VISIBLE);
    }

    /**
     * Callback invoked when PlaceAutocompleteFragment encounters an error.
     */
    @Override
    public void onError(Status status) {
        Log.e(TAG, "onError: Status = " + status.toString());

        Toast.makeText(this, getString(R.string.system_error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        if (mPlace == null) return;

        mProgressDialog.show();
        TravelBaseService service = TravelBaseServiceBuilder.build(this);
        String authHeader = TravelBaseServiceBuilder.makeBearerAuthHeader();
        HashMap<String, String> query = TravelBaseServiceBuilder.makeResourceOwnerInfo();
        if (query != null) {
            query.put("name", (String)mPlace.getName());
            // TODO: Placeから正しいaddressが取得出来ない場合があるので送信しない。正しいaddresが取得出来るようになったらコメントを外す。
            // query.put("address", (String)mPlace.getAddress());
            query.put("latitude", String.valueOf(mPlace.getLatLng().latitude));
            query.put("longitude", String.valueOf(mPlace.getLatLng().longitude));
        }

        service.addPlace(authHeader, "v1", query, new CallBack<xyz.yyagi.travelbase.model.Place>() {
            @Override
            public void onSuccess(xyz.yyagi.travelbase.model.Place place) {
                savePlace(place);
                mProgressDialog.dismiss();
                Toast.makeText(mContext, getString(R.string.registration_place_success), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(mContext, PlaceListActivity.class);
                startActivity(intent);
            }

            @Override
            public void onError(WaspError waspError) {
                mProgressDialog.dismiss();
                Toast.makeText(mContext, getString(R.string.system_error), Toast.LENGTH_LONG).show();
            }

            public void savePlace(xyz.yyagi.travelbase.model.Place place) {
                Realm realm = Realm.getInstance(mContext);

                realm.beginTransaction();
                realm.copyToRealm(place);
                realm.commitTransaction();
            }
        });
    }
}
