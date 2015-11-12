package com.github.jgeboski.insomnia.service;

import android.content.ContentResolver;
import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.Settings;

import com.github.jgeboski.insomnia.Util;

public class MainThread
    extends Thread
{
    public MainService service;
    public boolean running;

    public MainThread(MainService service)
    {
        this.service = service;
        this.running = true;
    }

    @Override
    public void run()
    {
        ContentResolver resolver = service.getContentResolver();
        int appid = service.getApplicationInfo().labelRes;
        String tag = service.getString(appid);

        PowerManager pm = (PowerManager)
            service.getSystemService(Context.POWER_SERVICE);
        WakeLock lock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, tag);

        while (running) {
            if (Util.isScreenOn(service)) {
                if (service.hasRunningAppItems()) {
                    if (!lock.isHeld()) {
                        lock.acquire();
                    }
                } else {
                    if (lock.isHeld()) {
                        lock.release();
                    }
                }
            } else {
                if (lock.isHeld()) {
                    lock.release();
                }
            }

            /* Always refetch the timeout in case of updates */
            String id = Settings.System.SCREEN_OFF_TIMEOUT;
            long timeout = Settings.System.getLong(resolver, id, 60000);

            try {
                sleep(timeout - 1500);
            } catch (InterruptedException e) {
            }
        }
    }

    public void close()
    {
        if (!isAlive()) {
            return;
        }

        running = false;
        interrupt();

        try {
            join();
        } catch (InterruptedException e) {
        }
    }
}
