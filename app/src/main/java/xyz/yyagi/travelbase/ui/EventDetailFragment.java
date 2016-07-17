package xyz.yyagi.travelbase.ui;

import android.app.Fragment;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.realm.Realm;
import xyz.yyagi.travelbase.R;
import xyz.yyagi.travelbase.databinding.FragmentEventDetailBinding;
import xyz.yyagi.travelbase.databinding.FragmentPlaceDetailBinding;
import xyz.yyagi.travelbase.model.Event;
import xyz.yyagi.travelbase.model.Place;
import xyz.yyagi.travelbase.service.RealmBuilder;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link EventDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventDetailFragment extends Fragment {
    public static final String KEY_EVENT_ID = "event_id";
    private FragmentEventDetailBinding mBinding;
    private Event mEvent;
    private Place mPlace;

    public static EventDetailFragment newInstance(int eventId) {
        EventDetailFragment fragment = new EventDetailFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setEventAndPlace();
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_event_detail, container, false);
        mBinding.setEvent(mEvent);
        mBinding.setPlace(mPlace);

        return mBinding.getRoot();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void setEventAndPlace() {
        int placeId = getArguments().getInt(KEY_EVENT_ID);
        Realm realm = RealmBuilder.getRealmInstance(getActivity());
        mEvent = realm.where(Event.class).equalTo("id", placeId).findFirst();
        mPlace = realm.where(Place.class).equalTo("id", mEvent.getPlace_id()).findFirst();
        realm.close();
    }
}

