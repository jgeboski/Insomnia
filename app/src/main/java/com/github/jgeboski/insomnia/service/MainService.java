package com.github.jgeboski.insomnia.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Notification;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;

import com.github.jgeboski.insomnia.Insomnia;
import com.github.jgeboski.insomnia.model.AppItem;
import com.github.jgeboski.insomnia.receiver.ScreenReceiver;

public class MainService
    extends Service
    implements OnSharedPreferenceChangeListener
{
    public MainObserver observer;
    public MainThread thread;
    public Map<String, AppItem> items;
    public ScreenReceiver screceiver;
    public SharedPreferences prefs;

    public long timeout;
    public boolean dimmable;

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
        items = new HashMap<>();
        screceiver = new ScreenReceiver(this);

        ContentResolver resolver = getContentResolver();
        String name = Settings.System.SCREEN_OFF_TIMEOUT;
        Uri uri = Settings.System.getUriFor(name);
        resolver.registerContentObserver(uri, true, observer);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
        reset();
        updateState();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if (thread != null) {
            thread.close();
        }

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

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key)
    {
        if (key.equals("active")) {
            updateState();
        } else {
            reset();
        }
    }

    public List<AppItem> getAppItems()
    {
        return Insomnia.getAppItems(this, items);
    }

    public boolean hasActiveAppItems()
    {
        return items.size() > 0;
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
        dimmable = prefs.getBoolean("dimmable", false);

        if (thread != null) {
            thread.reset();
        }
    }

    public void update(AppItem item)
    {
        if (item.active) {
            items.put(item.name, item);
        } else {
            items.remove(item.name);
        }

        updateState();
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

    private void updateState()
    {
        if (!prefs.getBoolean("active", true) || !hasActiveAppItems()) {
            if (thread == null) {
                return;
            }

            thread.close();
            stopForeground(true);
            thread = null;
            return;
        }

        if (thread != null) {
            return;
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);

        Notification notice = getNotification();
        startForeground(Insomnia.SERVICE_NOTIFICATION_ID, notice);
        registerReceiver(screceiver, filter);

        thread = new MainThread(this);
        thread.start();
    }
}
