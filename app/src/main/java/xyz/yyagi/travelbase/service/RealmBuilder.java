package xyz.yyagi.travelbase.service;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by yaginuma on 15/07/04.
 */
public class RealmBuilder {
    public static Realm getRealmInstance(Context context) {
        Realm.init(context);
        return Realm.getDefaultInstance();
    }

    public static Realm getRealmInstance(RealmConfiguration realmConfiguration) {
        return Realm.getInstance(realmConfiguration);
    }
}
