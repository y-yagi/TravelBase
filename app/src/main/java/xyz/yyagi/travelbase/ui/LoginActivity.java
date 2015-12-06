package xyz.yyagi.travelbase.ui;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.SignInButton;
import com.orhanobut.wasp.CallBack;
import com.orhanobut.wasp.WaspError;

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
import java.util.Map;
import java.util.TimeZone;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import xyz.yyagi.travelbase.BuildConfig;
import xyz.yyagi.travelbase.R;
import xyz.yyagi.travelbase.model.Authorization;
import xyz.yyagi.travelbase.model.Place;
import xyz.yyagi.travelbase.model.SystemData;
import xyz.yyagi.travelbase.model.Travel;
import xyz.yyagi.travelbase.model.User;
import xyz.yyagi.travelbase.service.ProgressDialogBuilder;
import xyz.yyagi.travelbase.service.RealmBuilder;
import xyz.yyagi.travelbase.service.TravelBaseService;
import xyz.yyagi.travelbase.service.TravelBaseServiceBuilder;
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
    private ProgressDialog mProgressDialog;
    private static final String TAG = LogUtil.makeLogTag(LoginActivity.class);
    private Realm mRealm;
    private User mUser;
    private SystemData mSystemData;
    Calendar mCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        RealmConfiguration realmConfiguration = RealmBuilder.getRealmConfiguration(this);

        // TODO: remove after. use only test.
//        Realm.deleteRealm(realmConfiguration);

        mRealm = RealmBuilder.getRealmInstance(realmConfiguration);
        mSystemData = mRealm.where(SystemData.class).findFirst();
        mActivity = this;

        setContentView(R.layout.activity_login);

        // To use when an error occurs, it is necessary to set previously
        mGoogleSignInButton = (SignInButton) findViewById(R.id.google_sign_in_button);
        mTwitterLoginButton = (TwitterLoginButton)findViewById(R.id.twitter_sign_in_button);

        mProgressDialog = ProgressDialogBuilder.build(this, getString(R.string.loading));
        if (isLogined()) {
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
        if (mRealm != null) {
            mRealm.close();
        }
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

        service.authenticate(credentials, new CallBack<Authorization>() {
            @Override
            public void onSuccess(Authorization authorization) {
                mUser.setAccessToken(authorization.access_token);
                mUser.setEncryptedAccessToken(CryptoUtil.encrypt(mActivity, authorization.access_token));

                mRealm.beginTransaction();
                mRealm.copyToRealm(mUser);
                mRealm.commitTransaction();

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
        Map query = TravelBaseServiceBuilder.makeResourceOwnerInfo();
        query.put("fields", "*");

        service.travels(authHeader, "v1", query, new CallBack<ArrayList<Travel>>() {
            @Override
            public void onSuccess(ArrayList<Travel> travelList) {
                saveTravelList(travelList);
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
        Map query = TravelBaseServiceBuilder.makeResourceOwnerInfo();
        query.put("fields", "*");
        if (mSystemData != null) {
            query.put("updated_at", DateUtil.formatWithTime(mSystemData.getApi_last_acquisition_time()));
        }

        mCalendar = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));
        // FIXME: timezoneにUTCを指定しているが、実際取得出来る値がJSTになってしまっている為9マイナス
        mCalendar.add(Calendar.HOUR, -9);

        service.places(authHeader, "v1", query, new CallBack<ArrayList<Place>>() {
            @Override
            public void onSuccess(ArrayList<Place> placeList) {
                savePlaceList(placeList);
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

    private void saveTravelList(ArrayList<Travel> travelList) {
        User user = mRealm.where(User.class).findFirst();
        mRealm.beginTransaction();
        for (Travel travel : travelList) {
            travel.setUser_id(user.getUid());
            mRealm.copyToRealmOrUpdate(travel);
        }
        mRealm.commitTransaction();
    }

    private void savePlaceList(ArrayList<Place> placeList) {
        User user = mRealm.where(User.class).findFirst();
        mRealm.beginTransaction();
        for (Place place : placeList) {
            place.setUser_id(user.getUid());
            mRealm.copyToRealmOrUpdate(place);
        }
        updateApiLastAcquisitionTime();
        mRealm.commitTransaction();
    }

    private void startStartPointActivity() {
        Intent intent = new Intent(mActivity, TravelListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(intent);
    }

    private boolean isLogined() {
        RealmResults<User> results = mRealm.where(User.class).findAll();
        if (results.size() == 0) {
            return  false;
        }
        User user = results.first();
        user.setAccessToken(CryptoUtil.decrypt(this, user.getEncryptedAccessToken()));
        TravelBaseServiceBuilder.user = user;
        return true;
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

    private void updateApiLastAcquisitionTime() {
        if (mSystemData == null) {
            mSystemData = mRealm.createObject(SystemData.class);
        }
        mSystemData.setApi_last_acquisition_time(mCalendar.getTime());
    }
}

