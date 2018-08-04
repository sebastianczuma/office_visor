package com.example.sebastianczuma.officevisor.NotificationUpload;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by sebastianczuma on 18.08.2016.
 */
public final class UploadServiceStarterReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        UploadEventReceiver.setupAlarm(context);
    }
}