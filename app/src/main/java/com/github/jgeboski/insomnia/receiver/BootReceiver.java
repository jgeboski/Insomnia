package com.github.jgeboski.insomnia.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.github.jgeboski.insomnia.service.MainService;

public class BootReceiver
    extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        SharedPreferences prefs =
            PreferenceManager.getDefaultSharedPreferences(context);

        if (prefs.getBoolean("boot", true)) {
            intent = new Intent(context, MainService.class);
            context.startService(intent);
        }
    }
}
