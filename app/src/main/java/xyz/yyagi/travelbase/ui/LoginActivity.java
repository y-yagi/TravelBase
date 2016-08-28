package xyz.yyagi.travelbase.ui;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.crash.FirebaseCrash;
import com.orhanobut.wasp.Response;
import com.orhanobut.wasp.WaspError;
import com.orhanobut.wasp.WaspRequest;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import xyz.yyagi.travelbase.BuildConfig;
import xyz.yyagi.travelbase.R;
import xyz.yyagi.travelbase.model.Authorization;
import xyz.yyagi.travelbase.model.DeletedData;
import xyz.yyagi.travelbase.model.Event;
import xyz.yyagi.travelbase.model.Login;
import xyz.yyagi.travelbase.model.Place;
import xyz.yyagi.travelbase.model.SystemData;
import xyz.yyagi.travelbase.model.Travel;
import xyz.yyagi.travelbase.model.User;
import xyz.yyagi.travelbase.ui.widget.ProgressDialogBuilder;
import xyz.yyagi.travelbase.service.RealmBuilder;
import xyz.yyagi.travelbase.service.TravelBaseService;
import xyz.yyagi.travelbase.service.TravelBaseServiceBuilder;
import xyz.yyagi.travelbase.ui.widget.TravelBaseProgressDialog;
import xyz.yyagi.travelbase.util.CryptoUtil;
import xyz.yyagi.travelbase.util.DateUtil;
import xyz.yyagi.travelbase.util.LogUtil;

public class LoginActivity extends Activity implements View.OnClickListener {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = BuildConfig.TWITTER_KEY;
    private static final String TWITTER_SECRET = BuildConfig.TWITTER_SECRET;

    private static final int REQUEST_CODE_GOOGLE_SIGN_IN = 1;
    // @see https://github.com/twitter/twitter-kit-android/blob/master/twitter-core/src/main/java/com/twitter/sdk/android/core/TwitterAuthConfig.java#L37
    private static final int REQUEST_CODE_TWITTER_LOGIN = 140;
    private SignInButton mGoogleSignInButton;
    private TwitterLoginButton mTwitterLoginButton;
    private LoginActivity mActivity;
    private TravelBaseProgressDialog mProgressDialog;
    private static final String TAG = LogUtil.makeLogTag(LoginActivity.class);
    private User mUser;
    private Login mLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Crashlytics(), new Twitter(authConfig));

        mActivity = this;
        mLogin = new Login(this);
        mLogin.init();

        setContentView(R.layout.activity_login);

        // To use when an error occurs, it is necessary to set previously
        mGoogleSignInButton = (SignInButton) findViewById(R.id.google_sign_in_button);
        mTwitterLoginButton = (TwitterLoginButton) findViewById(R.id.twitter_sign_in_button);

        mProgressDialog = ProgressDialogBuilder.build(this, getString(R.string.loading), getResources().getColor(R.color.primary));
        if (mLogin.isLogined()) {
            mLogin.user.setAccessToken(CryptoUtil.decrypt(this, mLogin.user.getEncryptedAccessToken()));
            TravelBaseServiceBuilder.user = mLogin.user;
            mProgressDialog.show();
            fetchTravelList();
            return;
        }

        setTwitterLoginButton();
        setGoogleLoginButton();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLogin.term();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.google_sign_in_button:
                Intent intent = AccountPicker.newChooseAccountIntent(null, null, new String[]{"com.google"},
                        false, null, null, null, null);
                startActivityForResult(intent, REQUEST_CODE_GOOGLE_SIGN_IN);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mProgressDialog.show();
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_GOOGLE_SIGN_IN:
                if (resultCode == RESULT_OK) {
                    String userData = data.getStringExtra(AccountManager.KEY_AUTHTOKEN);
                    authenticate(data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME), TravelBaseService.PROVIDER_GOOGLE);
                } else {
                    // TODO: Do nothing?
                }
            case REQUEST_CODE_TWITTER_LOGIN:
                mTwitterLoginButton.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void authenticate(String userId, String provider) {
        TravelBaseService service = TravelBaseServiceBuilder.build(this);
        Map credentials = TravelBaseServiceBuilder.makeClientCredentials();
        mUser = new User();
        mUser.setUid(userId);
        mUser.setProvider(provider);

        service.authenticate(credentials, new com.orhanobut.wasp.Callback<Authorization>() {
            @Override
            public void onSuccess(Response response, Authorization authorization) {
                mUser.setAccessToken(authorization.access_token);
                mUser.setEncryptedAccessToken(CryptoUtil.encrypt(mActivity, authorization.access_token));

                mLogin.saveUser(mUser);
                TravelBaseServiceBuilder.user = mUser;
                fetchTravelList();
            }

            @Override
            public void onError(WaspError waspError) {
                mProgressDialog.dismiss();
                Toast.makeText(mActivity, getString(R.string.login_faiure), Toast.LENGTH_LONG).show();
                Log.d(TAG, waspError.getErrorMessage());
            }
        });
    }

    private void fetchTravelList() {
        TravelBaseService service = TravelBaseServiceBuilder.build(this);
        String authHeader = TravelBaseServiceBuilder.makeBearerAuthHeader();
        HashMap<String, String> query = TravelBaseServiceBuilder.makeResourceOwnerInfo();
        if (query != null) {
            query.put("fields", "*");
            if (mLogin.travelSystemData != null) {
                query.put("updated_at", DateUtil.formatWithTime(mLogin.travelSystemData.getApi_last_acquisition_time()));
            }
        }

        service.travels(authHeader, "v1", query, new com.orhanobut.wasp.Callback<ArrayList<Travel>>() {
            @Override
            public void onSuccess(Response response, ArrayList<Travel> travelList) {
                mLogin.saveTravelList(travelList);
                fetchPlaceList();
            }

            @Override
            public void onError(WaspError waspError) {
                mProgressDialog.dismiss();
                Log.d(TAG, waspError.getErrorMessage());
                // failure to get the latest data, the process continues with past data
                startStartPointActivity();
            }
        });
    }

    private void fetchPlaceList() {
        TravelBaseService service = TravelBaseServiceBuilder.build(this);
        String authHeader = TravelBaseServiceBuilder.makeBearerAuthHeader();
        HashMap<String, String> query = TravelBaseServiceBuilder.makeResourceOwnerInfo();
        if (query != null) {
            query.put("fields", "*");

            if (mLogin.placeSystemData != null) {
                query.put("updated_at", DateUtil.formatWithTime(mLogin.placeSystemData.getApi_last_acquisition_time()));
            }
        }

        service.places(authHeader, "v1", query, new com.orhanobut.wasp.Callback<ArrayList<Place>>() {
            @Override
            public void onSuccess(Response response, ArrayList<Place> placeList) {
                mLogin.savePlaceList(placeList);
                fetchEventList();
            }

            @Override
            public void onError(WaspError waspError) {
                mProgressDialog.dismiss();
                Log.d(TAG, waspError.getErrorMessage());
                // failure to get the latest data, the process continues with past data
                startStartPointActivity();
            }
        });
    }

    private void fetchEventList() {
        TravelBaseService service = TravelBaseServiceBuilder.build(this);
        String authHeader = TravelBaseServiceBuilder.makeBearerAuthHeader();
        HashMap<String, String> query = TravelBaseServiceBuilder.makeResourceOwnerInfo();
        if (query != null) {
            query.put("fields", "*");

            if (mLogin.eventSystemData != null) {
                query.put("updated_at", DateUtil.formatWithTime(mLogin.eventSystemData.getApi_last_acquisition_time()));
            }
        }

        service.events(authHeader, "v1", query, new com.orhanobut.wasp.Callback<ArrayList<Event>>() {
            @Override
            public void onSuccess(Response response, ArrayList<Event> eventList) {
                mLogin.saveEventList(eventList);
                fetchDeletedData();
            }

            @Override
            public void onError(WaspError waspError) {
                mProgressDialog.dismiss();
                Log.d(TAG, waspError.getErrorMessage());
                // failure to get the latest data, the process continues with past data
                startStartPointActivity();
            }
        });
    }

    private void fetchDeletedData() {
        TravelBaseService service = TravelBaseServiceBuilder.build(this);
        String authHeader = TravelBaseServiceBuilder.makeBearerAuthHeader();
        HashMap<String, String> query = TravelBaseServiceBuilder.makeResourceOwnerInfo();
        if (query != null) {
            query.put("fields", "*");
            // TODO: add updated_at parameter
        }

        service.deletedData(authHeader, "v1", query, new com.orhanobut.wasp.Callback<ArrayList<DeletedData>>() {
            @Override
            public void onSuccess(Response response, ArrayList<DeletedData> deletedDataList) {
                mLogin.removeDeletedData(deletedDataList);
                mProgressDialog.dismiss();
                startStartPointActivity();
            }

            @Override
            public void onError(WaspError waspError) {
                mProgressDialog.dismiss();
                Log.d(TAG, waspError.getErrorMessage());
                // failure to get the latest data, the process continues with past data
                startStartPointActivity();
            }
        });
    }

    private void startStartPointActivity() {
        Intent intent = new Intent(mActivity, TravelListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(intent);
    }

    private void setTwitterLoginButton() {
        mTwitterLoginButton.setVisibility(View.VISIBLE);
        mTwitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                TwitterSession session = Twitter.getSessionManager().getActiveSession();
                authenticate(String.valueOf(session.getUserId()), TravelBaseService.PROVIDER_TWITTER);
            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(mActivity, getString(R.string.login_faiure), Toast.LENGTH_LONG).show();
                Log.d(TAG, "Twitter Login fail: " + exception.getMessage());
            }
        });
    }

    private void setGoogleLoginButton() {
        mGoogleSignInButton.setOnClickListener(this);
        mGoogleSignInButton.setVisibility(View.VISIBLE);
    }
}

