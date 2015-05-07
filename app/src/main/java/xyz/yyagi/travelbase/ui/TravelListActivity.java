package xyz.yyagi.travelbase.ui;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.wasp.CallBack;
import com.orhanobut.wasp.WaspError;

import java.util.ArrayList;

import xyz.yyagi.travelbase.R;
import xyz.yyagi.travelbase.model.Travel;
import xyz.yyagi.travelbase.service.TravelBaseService;
import xyz.yyagi.travelbase.service.TravelBaseServiceBuilder;
import xyz.yyagi.travelbase.util.LogUtil;

public class TravelListActivity extends Activity {
    private Activity mActivity;
    private static final String TAG = LogUtil.makeLogTag(TravelListActivity.class);
    private LinearLayout mTravelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_list);
        mTravelList = (LinearLayout)findViewById(R.id.travelList);
        mActivity = this;
        fetchTravelList();
    }

    private void fetchTravelList() {
        TravelBaseService service = TravelBaseServiceBuilder.build(this);
        String authHeader = TravelBaseServiceBuilder.makeBearerAuthHeader();
        service.fetchTravels(authHeader, TravelBaseService.API_VERSION, new CallBack<ArrayList<Travel>>() {
            @Override
            public void onSuccess(ArrayList<Travel> travels) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                LinearLayout linearLayout;
                CardView cardView;
                TextView textView;

                for (Travel travel : travels) {
                    linearLayout = (LinearLayout) inflater.inflate(R.layout.travel_card, null);
                    cardView = (CardView) linearLayout.findViewById(R.id.card_view);

                    textView = (TextView) linearLayout.findViewById(R.id.name);
                    textView.setText(travel.name);
                    textView = (TextView) linearLayout.findViewById(R.id.date);
                    textView.setText(travel.formatted_start_date + "〜" + travel.formatted_end_date + "\n");
                    textView = (TextView) linearLayout.findViewById(R.id.memo);
                    textView.setText(travel.memo);

                    cardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(mActivity, String.valueOf(v.getTag()) + "番目のCardViewがクリックされました", Toast.LENGTH_SHORT).show();
                        }
                    });
                    mTravelList.addView(linearLayout);
                }
            }

            @Override
            public void onError(WaspError waspError) {
                Toast.makeText(mActivity, getString(R.string.system_error), Toast.LENGTH_LONG).show();
                Log.d(TAG, waspError.getErrorMessage());
            }
        });
    }
}
