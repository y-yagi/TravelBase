package xyz.yyagi.travelbase.ui;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import xyz.yyagi.travelbase.R;
import xyz.yyagi.travelbase.databinding.CardEventBinding;
import xyz.yyagi.travelbase.model.Event;
import xyz.yyagi.travelbase.service.RealmBuilder;
import xyz.yyagi.travelbase.util.LogUtil;


public class EventListActivity extends BaseActivity {
    private Activity mActivity;
    private static final String TAG = LogUtil.makeLogTag(EventListActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        mActivity = this;

        displayEvents();
        setupDrawer();
        drawer.setSelection(BaseActivity.DRAWER_EVENT_LIST, false);
    }

    private void displayEvents() {
        RealmResults<Event> events = getEvents();
        LinearLayout layout = (LinearLayout) findViewById(R.id.eventList);
        for (Event event: events) {
            CardView cardView;
            CardEventBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.card_event, layout, false);
            binding.setEvent(event);
            cardView = (CardView) binding.getRoot();

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = Integer.parseInt(((TextView) v.findViewById(R.id.name)).getTag().toString());
                    EventDetailActivity.startActivity(mActivity, id);
                }
            });
            layout.addView(cardView);
        }
    }

    private RealmResults<Event> getEvents() {
        Realm realm = RealmBuilder.getRealmInstance(this);
        RealmResults<Event> events = realm.where(Event.class)
                .findAllSorted("start_date", Sort.DESCENDING);
        return events;
    }
}
