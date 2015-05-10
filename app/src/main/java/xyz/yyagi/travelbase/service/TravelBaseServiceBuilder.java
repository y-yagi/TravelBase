package xyz.yyagi.travelbase.service;

import android.content.Context;
import android.util.Base64;

import com.orhanobut.wasp.Wasp;
import com.orhanobut.wasp.parsers.GsonParser;
import com.orhanobut.wasp.utils.LogLevel;
import com.orhanobut.wasp.utils.NetworkMode;

import java.net.CookiePolicy;
import java.util.Map;

import xyz.yyagi.travelbase.BuildConfig;
import xyz.yyagi.travelbase.model.Authorization;

/**
 * Created by yaginuma on 15/05/06.
 */
public class TravelBaseServiceBuilder {
    public static Authorization authorization = null;

    public static TravelBaseService build(Context context) {
        return new Wasp.Builder(context)
                .setEndpoint(TravelBaseService.endPoint)
//                .setLogLevel(LogLevel.FULL)              // TODO: remove
                .setParser(new GsonParser())
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
