package xyz.yyagi.travelbase.ui;

import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;

import io.realm.RealmResults;
import xyz.yyagi.travelbase.R;
import xyz.yyagi.travelbase.model.Travel;
import xyz.yyagi.travelbase.model.User;
import xyz.yyagi.travelbase.util.LogUtil;

/**
 * Created by yaginuma on 15/05/08.
 */
public class FutureTravelListFragment extends TravelListFragment {
    private static final String TAG = LogUtil.makeLogTag(FutureTravelListFragment.class);

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        displayTravels();
    }

    private void displayTravels() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, -1);
        User user = mRealm.where(User.class).findFirst();
        RealmResults<Travel> travels = mRealm.where(Travel.class)
                .greaterThanOrEqualTo("end_date", calendar.getTime())
                .equalTo("user_id", user.getUid())
                .findAll();
        travels.sort("start_date", RealmResults.SORT_ORDER_DESCENDING);

        if (travels.size() == 0) {
            mNoticeTextView.setText(getString(R.string.no_plan_of_travel));
            mNoticeTextView.setVisibility(View.VISIBLE);
            return;
        }
        displayTravels(travels);
    }
}
