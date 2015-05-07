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

import java.util.HashMap;
import java.util.Map;

import xyz.yyagi.travelbase.R;
import xyz.yyagi.travelbase.model.Authorization;
import xyz.yyagi.travelbase.service.TravelBaseService;
import xyz.yyagi.travelbase.service.TravelBaseServiceBuilder;
import xyz.yyagi.travelbase.util.LogUtil;

public class LoginActivity extends Activity implements View.OnClickListener {

    private static final int REQUEST_CODE_SIGN_IN_GOOGLE = 1;
    private SignInButton mSignInButton;
    private Activity mActivity;
    private ProgressDialog mLoginDialog;
    private static final String TAG = LogUtil.makeLogTag(LoginActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getActionBar().hide();

        mSignInButton = (SignInButton) findViewById(R.id.google_sign_in_button);
        mSignInButton.setOnClickListener(this);
        mActivity = this;
        mLoginDialog = new ProgressDialog(this);
        mLoginDialog.setMessage(getString(R.string.logged_in));
        mLoginDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mLoginDialog.setCancelable(false);
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
        mLoginDialog.show();
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
        String authHeader = TravelBaseServiceBuilder.makeBasicAuthHeader(userId, provider);

        Map mapBody = new HashMap<>();
        mapBody.put("grant_type", "password");
        mapBody.put("id", userId);
        mapBody.put("provider", provider);

        service.authenticate(authHeader, mapBody, new CallBack<Authorization>() {
            @Override
            public void onSuccess(Authorization authorization) {
                mLoginDialog.dismiss();
                TravelBaseServiceBuilder.authorization = authorization;
                Intent intent = new Intent(mActivity, TravelListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                startActivity(intent);
            }

            @Override
            public void onError(WaspError waspError) {
                mLoginDialog.dismiss();
                Toast.makeText(mActivity, getString(R.string.login_faiure), Toast.LENGTH_LONG).show();
                Log.d(TAG, waspError.getErrorMessage());
            }
        });
    }
}

