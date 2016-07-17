package xyz.yyagi.travelbase.model;

import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;
import xyz.yyagi.travelbase.service.RealmBuilder;
import xyz.yyagi.travelbase.service.TravelBaseServiceBuilder;
import xyz.yyagi.travelbase.util.CryptoUtil;

/**
 * Created by yaginuma on 16/02/21.
 */
public class Login {
    private Context mContext;
    private Calendar mCalendar;
    private Realm mRealm;

    public User user;
    public SystemData placeSystemData;
    public SystemData travelSystemData;
    public SystemData eventSystemData;

    public Login(Context context) {
        this.mContext = context;
    }

    public void init() {
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this.mContext)
                .deleteRealmIfMigrationNeeded()
                .build();

        Realm.setDefaultConfiguration(realmConfiguration);
        mRealm = Realm.getDefaultInstance();

        mCalendar = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));
        // FIXME: timezoneにUTCを指定しているが、実際取得出来る値がJSTになってしまっている為9マイナス
        mCalendar.add(Calendar.HOUR, -9);
        placeSystemData = mRealm.where(SystemData.class).equalTo("table_name", Place.class.toString()).findFirst();
        travelSystemData = mRealm.where(SystemData.class).equalTo("table_name", Travel.class.toString()).findFirst();
        travelSystemData = mRealm.where(SystemData.class).equalTo("table_name", Event.class.toString()).findFirst();
        user = mRealm.where(User.class).findFirst();
    }

    public void saveTravelList(ArrayList<Travel> travelList) {
        User user = mRealm.where(User.class).findFirst();
        mRealm.beginTransaction();
        for (Travel travel : travelList) {
            travel.setUser_id(user.getUid());
            mRealm.copyToRealmOrUpdate(travel);
        }
        updateApiLastAcquisitionTime(travelSystemData, Travel.class.toString());
        mRealm.commitTransaction();
    }

    public void savePlaceList(ArrayList<Place> placeList) {
        User user = mRealm.where(User.class).findFirst();
        mRealm.beginTransaction();
        for (Place place : placeList) {
            place.setUser_id(user.getUid());
            mRealm.copyToRealmOrUpdate(place);
        }
        updateApiLastAcquisitionTime(placeSystemData, Place.class.toString());
        mRealm.commitTransaction();
    }

    public void saveEventList(ArrayList<Event> eventList) {
        User user = mRealm.where(User.class).findFirst();
        mRealm.beginTransaction();
        for (Event event: eventList) {
            event.setUser_id(user.getUid());
            mRealm.copyToRealmOrUpdate(event);
        }
        updateApiLastAcquisitionTime(eventSystemData, Event.class.toString());
        mRealm.commitTransaction();
    }

    public void saveUser(User user) {
        mRealm.beginTransaction();
        mRealm.copyToRealm(user);
        mRealm.commitTransaction();
    }

    public boolean isLogined() {
        if (user != null) {
            return true;
        } else {
            return  false;
        }
    }

    private void updateApiLastAcquisitionTime(SystemData systemData, String table) {
        if (systemData == null) {
            systemData = mRealm.createObject(SystemData.class);
            systemData.setTable_name(table);
        }
        systemData.setApi_last_acquisition_time(mCalendar.getTime());
    }

    public void removeDeletedData(ArrayList<DeletedData> deletedDataList) {
        mRealm.beginTransaction();
        for (DeletedData deletedData : deletedDataList)  {
            if (deletedData.table_name.equals("places")) {
                mRealm.where(Place.class).equalTo("id", Integer.valueOf(deletedData.datum_id)).findAll().deleteAllFromRealm();
            } else if (deletedData.table_name.equals("travesl")) {
                mRealm.where(Travel.class).equalTo("id", Integer.valueOf(deletedData.datum_id)).findAll().deleteAllFromRealm();
            } else if (deletedData.table_name.equals("events")) {
                mRealm.where(Event.class).equalTo("id", Integer.valueOf(deletedData.datum_id)).findAll().deleteAllFromRealm();
            }
        }
        mRealm.commitTransaction();

    }

    public void term() {
        mRealm.close();
    }
}
