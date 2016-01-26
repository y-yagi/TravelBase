package xyz.yyagi.travelbase.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import io.realm.Realm;
import xyz.yyagi.travelbase.R;
import xyz.yyagi.travelbase.model.Route;
import xyz.yyagi.travelbase.service.RealmBuilder;
import xyz.yyagi.travelbase.util.LogUtil;

public class RouteDetailActivity extends BaseActivity {
    private static final String TAG = LogUtil.makeLogTag(PlaceDetailActivity.class);
    private static final String EXTRA_ROUTE_ID = "id";
    private static final String EXTRA_PLACE_NAME = "place_name";

    public static void startActivity(Context context, int id, String placeName) {
        Intent intent = new Intent(context, RouteDetailActivity.class);
        intent.putExtra(EXTRA_ROUTE_ID, id);
        intent.putExtra(EXTRA_PLACE_NAME, placeName);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_detail);

        Bundle extras = getIntent().getExtras();
        int id = extras.getInt(EXTRA_ROUTE_ID);
        String placeName = extras.getString(EXTRA_PLACE_NAME);

        Realm realm = RealmBuilder.getRealmInstance(this);
        Route route = realm.where(Route.class).equalTo("id", id).findFirst();
        realm.close();

        TextView routeDetail = (TextView)findViewById(R.id.route_detail);
        routeDetail.setText(route.getDetail());

        String routeTitle = getString(R.string.route_title, placeName);
        setTitle(routeTitle);
        setupDrawer();
    }
}
