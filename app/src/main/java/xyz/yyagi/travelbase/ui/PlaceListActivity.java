package xyz.yyagi.travelbase.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;

import io.realm.Realm;
import io.realm.RealmResults;
import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;
import xyz.yyagi.travelbase.R;
import xyz.yyagi.travelbase.model.Place;
import xyz.yyagi.travelbase.model.User;
import xyz.yyagi.travelbase.service.RealmBuilder;
import xyz.yyagi.travelbase.util.LogUtil;

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
            TextView textView;
            cardView = (CardView) getLayoutInflater().inflate(R.layout.card_place, layout, false);

            textView = (TextView) cardView.findViewById(R.id.name);
            textView.setText(place.getName());
            textView.setTag(place.getId());
            textView = (TextView) cardView.findViewById(R.id.address);
            textView.setText(place.getAddress());

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
