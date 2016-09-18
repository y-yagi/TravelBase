package xyz.yyagi.travelbase.service;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.orhanobut.wasp.Response;
import com.orhanobut.wasp.WaspError;

import java.util.HashMap;

import xyz.yyagi.travelbase.model.User;
import xyz.yyagi.travelbase.util.LogUtil;

public class NotificationIDService extends FirebaseInstanceIdService {
    private static final String TAG = LogUtil.makeLogTag(NotificationIDService.class);
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "Refreshed token: " + refreshedToken);

        sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        TravelBaseService service = TravelBaseServiceBuilder.build(this);
        String authHeader = TravelBaseServiceBuilder.makeBearerAuthHeader();
        if (authHeader.isEmpty()) {
            // NOTE: まだログインが行われていない状態
            return;
        }
        HashMap<String, String> query = TravelBaseServiceBuilder.makeResourceOwnerInfo();
        HashMap<String, String> body = new HashMap<String, String>();
        body.put("token", token);

        service.registrateToken(authHeader, "v1", query, body, new com.orhanobut.wasp.Callback<User>() {
            @Override
            public void onSuccess(Response response, User user) {
                // Do nothing
            }

            @Override
            public void onError(WaspError waspError) {
                Log.d(TAG, waspError.getErrorMessage());
            }
        });
    }
}

