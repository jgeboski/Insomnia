package com.github.jgeboski.insomnia.service;

import android.database.ContentObserver;
import android.os.Handler;

public class MainObserver
    extends ContentObserver
{
    public MainService service;

    public MainObserver(MainService service)
    {
        super(new Handler());
        this.service = service;
    }

    @Override
    public void onChange(boolean selfChange)
    {
        service.rehashSettings();
    }

    @Override
    public boolean deliverSelfNotifications()
    {
        return true;
    }
}
