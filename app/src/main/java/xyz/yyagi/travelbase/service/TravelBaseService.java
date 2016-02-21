package xyz.yyagi.travelbase.service;

import com.orhanobut.wasp.Callback;
import com.orhanobut.wasp.http.BodyMap;
import com.orhanobut.wasp.http.GET;
import com.orhanobut.wasp.http.Header;
import com.orhanobut.wasp.http.POST;
import com.orhanobut.wasp.http.Path;
import com.orhanobut.wasp.http.QueryMap;
import com.orhanobut.wasp.http.RetryPolicy;

import java.util.ArrayList;
import java.util.Map;

import xyz.yyagi.travelbase.model.Authorization;
import xyz.yyagi.travelbase.model.Place;
import xyz.yyagi.travelbase.model.Travel;

/**
 * Created by yaginuma on 15/05/06.
 */
public interface TravelBaseService {
    static final String endPoint = "https://travel-base.herokuapp.com";
    static final String PROVIDER_GOOGLE = "google_oauth2";
    static final String PROVIDER_TWITTER = "twitter";
    static final int TIME_OUT = 5000;

    @RetryPolicy(timeout = TIME_OUT)
    @POST("/oauth/token")
    void authenticate(
            @BodyMap Map body,
            Callback<Authorization> callBack
    );

    @RetryPolicy(timeout = TIME_OUT)
    @GET("/api/{version}/travels")
    void travels(
            @Header("Authorization") String authToken,
            @Path("version") String version,
            @QueryMap Map query,
            Callback<ArrayList<Travel>> callBack
    );

    @RetryPolicy(timeout = TIME_OUT)
    @GET("/api/{version}/travels/{id}")
    void travel(
            @Header("Authorization") String authToken,
            @Path("version") String version,
            @Path("id") int id,
            @QueryMap Map query,
            Callback<Travel> callBack
    );

    @RetryPolicy(timeout = TIME_OUT)
    @GET("/api/{version}/places")
    void places(
            @Header("Authorization") String authToken,
            @Path("version") String version,
            @QueryMap Map query,
            Callback<ArrayList<Place>> callBack
    );

    @RetryPolicy(timeout = TIME_OUT)
    @POST("/api/{version}/places")
    void addPlace(
            @Header("Authorization") String authToken,
            @Path("version") String version,
            @QueryMap Map query,
            Callback<Place> callBack
    );
}

