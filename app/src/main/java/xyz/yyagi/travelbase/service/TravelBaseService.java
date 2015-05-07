package xyz.yyagi.travelbase.service;

import com.orhanobut.wasp.CallBack;
import com.orhanobut.wasp.http.BodyMap;
import com.orhanobut.wasp.http.GET;
import com.orhanobut.wasp.http.Header;
import com.orhanobut.wasp.http.Mock;
import com.orhanobut.wasp.http.POST;
import com.orhanobut.wasp.http.Path;
import com.orhanobut.wasp.http.RetryPolicy;

import java.util.ArrayList;
import java.util.Map;

import xyz.yyagi.travelbase.model.Authorization;
import xyz.yyagi.travelbase.model.Travel;

/**
 * Created by yaginuma on 15/05/06.
 */
public interface TravelBaseService {
    static final String endPoint = "https://travel-base.herokuapp.com";
    static final String PROVIDER_GOOGLE = "google_oauth2";
    static final String API_VERSION = "v1";

    @RetryPolicy(timeout = 5000)
    @POST("/oauth/token")
    void authenticate(
            @Header("Authorization") String authToken,
            @BodyMap Map body,
            CallBack<Authorization> callBack
    );

    @RetryPolicy(timeout = 5000)
    @GET("/api/{version}/travels")
    void fetchTravels(
            @Header("Authorization") String authToken,
            @Path("version") String version,
            CallBack<ArrayList<Travel>> callBack
    );

}

