package com.example.sebastianczuma.officevisor.WorkerClasses;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by sebastianczuma on 22.10.2016.
 */

public class DeleteUserData {
    Context ctx;

    public DeleteUserData(Context context) {
        this.ctx = context;
    }

    public void purgeAllData() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = pref.edit();

        editor.clear();
        editor.apply();
    }
}
