package xyz.yyagi.travelbase.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import xyz.yyagi.travelbase.R;
import xyz.yyagi.travelbase.util.LogUtil;

public class MessagingService extends FirebaseMessagingService {
    private static final String TAG = LogUtil.makeLogTag(MessagingService.class);

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        Log.e(TAG, "From: " + remoteMessage.getFrom());
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder noti = new NotificationCompat.Builder(this);
        noti.setContentTitle("Travel Base");
        noti.setContentText(remoteMessage.getNotification().getBody());
        noti.setSmallIcon(R.mipmap.ic_launcher);

        notificationManager.notify(1000, noti.build());
    }
}
