package com.example.sebastianczuma.officevisor.NotificationUpload;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.example.sebastianczuma.officevisor.DataKeepers.Devices;
import com.example.sebastianczuma.officevisor.Database.DbHandlerDevices;
import com.example.sebastianczuma.officevisor.WorkerClasses.DownloadDeviceDataForAlarms;

public class UploadIntentService extends IntentService {

    private static final String ACTION_START = "ACTION_START";
    private static final String ACTION_DELETE = "ACTION_DELETE";

    public UploadIntentService() {
        super(UploadIntentService.class.getSimpleName());
    }

    public static Intent createIntentStartNotificationService(Context context) {
        Intent intent = new Intent(context, UploadIntentService.class);
        intent.setAction(ACTION_START);
        return intent;
    }

    public static Intent createIntentDeleteNotification(Context context) {
        Intent intent = new Intent(context, UploadIntentService.class);
        intent.setAction(ACTION_DELETE);
        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            String action = intent.getAction();
            if (ACTION_START.equals(action)) {
                processStartNotification();
            }
            if (ACTION_DELETE.equals(action)) {
                processDeleteNotification(intent);
            }
        } finally {
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
    }

    private void processDeleteNotification(Intent intent) {
    }

    private void processStartNotification() {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String token = preferences.getString("token", "");

        final DbHandlerDevices dbHandlerDevices = new DbHandlerDevices(getApplicationContext());
        for (Devices r : dbHandlerDevices.returnAllDevicesForBackgroundService()) {
            DownloadDeviceDataForAlarms downloadDeviceDataForUser =
                    new DownloadDeviceDataForAlarms(getApplicationContext(),
                            token,
                            r.getNazwaBudynku(),
                            r.getNumerPoziomu(),
                            r.getPomieszczenie(),
                            r.getNazwa(),
                            r.getTyp()
                    );
            downloadDeviceDataForUser.getData();
        }

        dbHandlerDevices.close();



        /*DbHandler dbHandler = new DbHandler(this);
        UploadInBackground uploadInBackground = new UploadInBackground(this, dbHandler);
        uploadInBackground.execute("");




        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("Scheduled Notification")
                .setAutoCancel(true)
                .setColor(getResources().getColor(R.color.colorAccent))
                .setContentText("This notification has been triggered by Notification Service")
                .setSmallIcon(R.drawable.notification_icon);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                NOTIFICATION_ID,
                new Intent(this, NotificationActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        builder.setDeleteIntent(UploadEventReceiver.getDeleteIntent(this));

        final NotificationManager manager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, builder.build());*/
    }
}