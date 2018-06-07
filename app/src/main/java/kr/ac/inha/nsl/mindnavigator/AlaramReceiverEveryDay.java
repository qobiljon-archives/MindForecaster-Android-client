package kr.ac.inha.nsl.mindnavigator;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class AlaramReceiverEveryDay extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent notificationIntent = new Intent(context, SignInActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(SignInActivity.class);
        stackBuilder.addNextIntent(notificationIntent);

        int notificaiton_id = (int)intent.getLongExtra("notification_id", 0);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(notificaiton_id, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel");

        Notification notification = builder.setContentTitle(context.getString(R.string.app_name))
                .setContentText(intent.getStringExtra("Content"))
                .setTicker("New Message Alert!")
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent).build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(notificaiton_id, notification);
        }
    }
}