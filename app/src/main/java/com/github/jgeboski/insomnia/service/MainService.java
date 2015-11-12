package com.github.jgeboski.insomnia.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import com.github.jgeboski.insomnia.Insomnia;
import com.github.jgeboski.insomnia.model.AppItem;

public class MainService
    extends Service
{
    public MainThread thread;
    public Map<String, AppItem> items;

    @Override
    public IBinder onBind(Intent intent)
    {
        return new MainServiceBinder(this);
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        thread = new MainThread(this);
        items = new HashMap<>();

        Notification notice = getNotification();
        startForeground(Insomnia.SERVICE_NOTIFICATION_ID, notice);
        thread.start();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        thread.close();
        stopForeground(true);
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
