package xyz.yyagi.travelbase.ui;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.orhanobut.wasp.CallBack;
import com.orhanobut.wasp.WaspError;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.OAuthSigning;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;
import xyz.yyagi.travelbase.BuildConfig;
import xyz.yyagi.travelbase.R;
import xyz.yyagi.travelbase.model.Authorization;
import xyz.yyagi.travelbase.model.Travel;
import xyz.yyagi.travelbase.model.User;
import xyz.yyagi.travelbase.service.ProgressDialogBuilder;
import xyz.yyagi.travelbase.service.TravelBaseService;
import xyz.yyagi.travelbase.service.TravelBaseServiceBuilder;
import xyz.yyagi.travelbase.util.CryptoUtil;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        // TODO: remove after. use only test.
//        Realm.deleteRealmFile(this);
        mRealm = Realm.getInstance(this);
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.google_sign_in_button:
                Intent intent = AccountManager.get(this).newChooseAccountIntent(null, null, new String[]{"com.google"
                        }, false, null,
                        null, null, null);
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

                mProgressDialog.dismiss();
                Intent intent = new Intent(mActivity, TravelListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                startActivity(intent);
            }

            @Override
            public void onError(WaspError waspError) {
                mProgressDialog.dismiss();
                Toast.makeText(mActivity, getString(R.string.login_faiure), Toast.LENGTH_LONG).show();
                setGoogleLoginButton();
                setTwitterLoginButton();
                Log.d(TAG, waspError.getErrorMessage());
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
}

