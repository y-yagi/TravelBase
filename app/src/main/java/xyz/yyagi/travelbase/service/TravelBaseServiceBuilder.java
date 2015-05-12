package xyz.yyagi.travelbase.service;

import android.content.Context;
import android.util.Base64;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.orhanobut.wasp.Wasp;
import com.orhanobut.wasp.parsers.GsonParser;

import java.net.CookiePolicy;

import io.realm.RealmObject;
import xyz.yyagi.travelbase.BuildConfig;
import xyz.yyagi.travelbase.model.Authorization;

/**
 * Created by yaginuma on 15/05/06.
 */
public class TravelBaseServiceBuilder {
    public static Authorization authorization = null;

    public static TravelBaseService build(Context context) {
        // @see http://realm.io/jp/docs/java/0.80.0/#gson
        Gson gson = new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .setDateFormat("yyyy-MM-dd")
                .create();

        return new Wasp.Builder(context)
                .setEndpoint(TravelBaseService.endPoint)
                .setParser(new GsonParser(gson))
                .trustCertificates()
                .enableCookies(CookiePolicy.ACCEPT_ALL)
//                .setNetworkMode(NetworkMode.MOCK)      // TODO: remove
                .build()
                .create(TravelBaseService.class);
    }

    public static String makeBasicAuthHeader(String userId, String provider) {
        return "Basic " + Base64.encode((
                BuildConfig.TRAVEL_BASE_API_ID + ":" + BuildConfig.TRAVEL_BASE_API_SECRET).getBytes(), Base64.DEFAULT);
    }

    public static String makeBearerAuthHeader() {
        if (authorization == null) {
            return "";
        } else {
            return "Bearer " + authorization.access_token;
        }
    }
}
