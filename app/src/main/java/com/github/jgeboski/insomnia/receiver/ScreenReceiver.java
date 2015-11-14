package com.github.jgeboski.insomnia.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.github.jgeboski.insomnia.Insomnia;
import com.github.jgeboski.insomnia.service.MainService;

public class ScreenReceiver
    extends BroadcastReceiver
{
    public MainService service;

    public ScreenReceiver(MainService service)
    {
        super();
        this.service = service;
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();

        if (action.equals(Intent.ACTION_SCREEN_ON)) {
            Insomnia.resetAppItems(service.items);
        }

        service.reset();
    }
}
