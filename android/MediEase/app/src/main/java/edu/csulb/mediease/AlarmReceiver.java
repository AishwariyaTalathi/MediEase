package edu.csulb.mediease;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.WakefulBroadcastReceiver;

import static android.content.Context.POWER_SERVICE;

public class AlarmReceiver extends WakefulBroadcastReceiver {

    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder;
    private Context context;

    @Override
    public void onReceive(final Context context, Intent intent) {
        this.context = context;
        /*Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Ringtone ringtone = RingtoneManager.getRingtone(context, uri);
        ringtone.play();*/

        //start ringtone
        Intent startIntent = new Intent(context, RingtonePlayingService.class);
        context.startService(startIntent);

        int id = intent.getIntExtra("db_id", -1);
        int count = intent.getIntExtra("count", -1);

        MySQLiteHelper db = new MySQLiteHelper(context);
        Medicine medicine = db.getRow(id); //id starts from 0
        String name = medicine.getName();
        buildNotification(name, count);
        notificationManager.notify(1, builder.build());
        PowerManager pm = (PowerManager) context.getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "tag");
        wl.acquire();
    }

    private void buildNotification(String name, int count) {
        builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_action_alarm)
                .setContentTitle("Take medicine")
                .setContentText(name)
                .setAutoCancel(true);
        Intent resultIntent = new Intent(context, AlarmsActivity.class);
        resultIntent.putExtra("count", count);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addParentStack(AlarmsActivity.class);
        taskStackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                taskStackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }
} 