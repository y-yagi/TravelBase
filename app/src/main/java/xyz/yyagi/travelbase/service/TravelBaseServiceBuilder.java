package xyz.yyagi.travelbase.service;

import android.content.Context;
import android.util.Base64;

import com.orhanobut.wasp.Wasp;
import com.orhanobut.wasp.parsers.GsonParser;
import com.orhanobut.wasp.utils.NetworkMode;

import java.net.CookiePolicy;
import java.util.Map;

import xyz.yyagi.travelbase.BuildConfig;

/**
 * Created by yaginuma on 15/05/06.
 */
public class TravelBaseServiceBuilder {
    public static TravelBaseService build(Context context) {
        return new Wasp.Builder(context)
                .setEndpoint(TravelBaseService.endPoint)
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
}
