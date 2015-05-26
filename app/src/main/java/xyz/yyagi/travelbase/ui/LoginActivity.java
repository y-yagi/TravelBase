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

import java.util.ArrayList;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;
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

    private static final int REQUEST_CODE_SIGN_IN_GOOGLE = 1;
    private SignInButton mSignInButton;
    private Activity mActivity;
    private ProgressDialog mProgressDialog;
    private static final String TAG = LogUtil.makeLogTag(LoginActivity.class);
    private Realm mRealm;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: remove after. use only test.
//        Realm.deleteRealmFile(this);
        mRealm = Realm.getInstance(this);
        mActivity = this;

        setContentView(R.layout.activity_login);

        mProgressDialog = ProgressDialogBuilder.build(this, getString(R.string.loading));
        if (isLogined()) {
            mProgressDialog.show();
            fetchTravelList();
            return;
        }

        mSignInButton = (SignInButton) findViewById(R.id.google_sign_in_button);
        mSignInButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.google_sign_in_button:
                Intent intent = AccountManager.get(this).newChooseAccountIntent(null, null, new String[]{"com.google"
                        }, false, null,
                        null, null, null);
                startActivityForResult(intent, REQUEST_CODE_SIGN_IN_GOOGLE);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mProgressDialog.show();
        if (requestCode == REQUEST_CODE_SIGN_IN_GOOGLE) {
            if (resultCode == RESULT_OK) {
                authenticate(data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME), TravelBaseService.PROVIDER_GOOGLE);
            } else {
                // TODO: Do nothing?
            }
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
                Log.d(TAG, waspError.getErrorMessage());
            }
        });
    }

    private void saveTravelList(ArrayList<Travel> travelList) {
        mRealm.beginTransaction();
        for (Travel travel : travelList) {
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
}

