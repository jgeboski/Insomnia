package com.github.jgeboski.insomnia.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.github.jgeboski.insomnia.service.MainService;

public class BootReceiver
    extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        intent = new Intent(context, MainService.class);
        context.startService(intent);
    }
}
