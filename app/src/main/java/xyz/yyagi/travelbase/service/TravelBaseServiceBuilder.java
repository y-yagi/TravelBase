package xyz.yyagi.travelbase.service;

import android.content.Context;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.orhanobut.wasp.Wasp;
import com.orhanobut.wasp.parsers.GsonParser;
import com.orhanobut.wasp.utils.LogLevel;

import java.net.CookiePolicy;
import java.util.HashMap;

import io.realm.RealmObject;
import xyz.yyagi.travelbase.BuildConfig;
import xyz.yyagi.travelbase.model.User;

/**
 * Created by yaginuma on 15/05/06.
 */
public class TravelBaseServiceBuilder {
    public static User user;

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
                .build()
                .create(TravelBaseService.class);
    }

    public static HashMap<String, String> makeClientCredentials() {
        HashMap<String, String> mapBody = new HashMap<>();
        mapBody.put("grant_type", "client_credentials");
        mapBody.put("client_id", BuildConfig.TRAVEL_BASE_API_ID);
        mapBody.put("client_secret", BuildConfig.TRAVEL_BASE_API_SECRET);
        return mapBody;
    }

    public static HashMap<String, String> makeResourceOwnerInfo() {
        if (user == null) {
            return null;
        }
        HashMap<String, String> mapBody = new HashMap<String, String>();
        mapBody.put("user_id", user.getUid());
        mapBody.put("user_provider", user.getProvider());
        return mapBody;
    }

    public static String makeBearerAuthHeader() {
        if (user == null) {
            return "";
        } else {
            return "Bearer " + user.getAccessToken();
        }
    }
}
