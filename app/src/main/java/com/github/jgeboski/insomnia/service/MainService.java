package com.github.jgeboski.insomnia.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Notification;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;

import com.github.jgeboski.insomnia.Insomnia;
import com.github.jgeboski.insomnia.model.AppItem;
import com.github.jgeboski.insomnia.receiver.ScreenReceiver;

public class MainService
    extends Service
{
    public MainObserver observer;
    public MainThread thread;
    public Map<String, AppItem> items;
    public ScreenReceiver screceiver;

    public long timeout;

    @Override
    public IBinder onBind(Intent intent)
    {
        return new MainBinder(this);
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        observer = new MainObserver(this);
        thread = new MainThread(this);
        items = new HashMap<>();
        screceiver = new ScreenReceiver(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);

        ContentResolver resolver = getContentResolver();
        String name = Settings.System.SCREEN_OFF_TIMEOUT;
        Uri uri = Settings.System.getUriFor(name);
        resolver.registerContentObserver(uri, true, observer);
        reset();

        Notification notice = getNotification();
        startForeground(Insomnia.SERVICE_NOTIFICATION_ID, notice);
        registerReceiver(screceiver, filter);
        thread.start();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        thread.close();
        unregisterReceiver(screceiver);
        stopForeground(true);

        ContentResolver resolver = getContentResolver();
        resolver.unregisterContentObserver(observer);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        return START_STICKY;
    }

    public List<AppItem> getAppItems()
    {
        return Insomnia.getAppItems(this, items);
    }

    public boolean hasRunningAppItems()
    {
        return Insomnia.hasRunningAppItems(this, items);
    }

    public void reset()
    {
        ContentResolver resolver = getContentResolver();
        String name = Settings.System.SCREEN_OFF_TIMEOUT;
        timeout = Settings.System.getLong(resolver, name, 60000);

        thread.reset();
    }

    public void update(AppItem item)
    {
        if (item.active) {
            items.put(item.name, item);
        } else {
            items.remove(item.name);
        }
    }

    private Notification getNotification()
    {
        Notification.Builder notice = new Notification.Builder(this);
        notice.setOngoing(true);
        notice.setWhen(0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            notice.setShowWhen(false);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notice.setPriority(Notification.PRIORITY_MIN);
            return notice.build();
        }

        return notice.getNotification();
    }
}
