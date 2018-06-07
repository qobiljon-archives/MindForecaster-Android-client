package kr.ac.inha.nsl.mindnavigator;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class AlarmReceiverIntervention extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent notificationIntent = new Intent(context, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notificationIntent);

        int notificaiton_id = (int) intent.getLongExtra("notification_id", 0);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle("Intervention");
            inboxStyle.addLine(intent.getStringExtra("Content1"));
            inboxStyle.addLine(intent.getStringExtra("Content2"));

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel_for_intervention")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Intervention")
                .setTicker("New Message Alert!")
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentText(intent.getStringExtra("Content1")).setStyle(inboxStyle);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(notificaiton_id, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = builder.setContentIntent(pendingIntent).build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(notificaiton_id, notification);
        }
    }
}
