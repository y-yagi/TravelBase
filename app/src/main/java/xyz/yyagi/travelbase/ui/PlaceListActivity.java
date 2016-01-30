package xyz.yyagi.travelbase.ui;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.tool.Binding;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;

import io.realm.Realm;
import io.realm.RealmResults;
import xyz.yyagi.travelbase.R;
import xyz.yyagi.travelbase.model.Place;
import xyz.yyagi.travelbase.service.RealmBuilder;
import xyz.yyagi.travelbase.util.LogUtil;
import xyz.yyagi.travelbase.databinding.CardPlaceBinding;


public class PlaceListActivity extends BaseActivity implements View.OnClickListener {
    private Activity mActivity;
    private static final String TAG = LogUtil.makeLogTag(PlaceListActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_list);

        mActivity = this;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        displayPlaces();
        setupDrawer();
    }

    private void displayPlaces() {
        RealmResults<Place> places = getPlaces();
        LinearLayout layout = (LinearLayout) findViewById(R.id.placeList);
        for (Place place: places) {
            CardView cardView;
            CardPlaceBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.card_place, layout, false);
            binding.setPlace(place);
            cardView = (CardView) binding.getRoot();

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = Integer.parseInt(((TextView) v.findViewById(R.id.name)).getTag().toString());
                    PlaceDetailActivity.startActivity(mActivity, id);
                }
            });
            layout.addView(cardView);
        }
    }

    private RealmResults<Place> getPlaces() {
        Realm realm = RealmBuilder.getRealmInstance(this);
        RealmResults<Place> places = realm.where(Place.class).equalTo("status", "not_gone").findAll();
        places.sort("id", RealmResults.SORT_ORDER_DESCENDING);
        return places;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, PlaceSearchActivity.class);
        startActivity(intent);
    }
}
