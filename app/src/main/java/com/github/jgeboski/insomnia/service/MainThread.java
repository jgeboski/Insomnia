package com.github.jgeboski.insomnia.service;

import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

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
        int appid = service.getApplicationInfo().labelRes;
        String tag = service.getString(appid);
        long timeout;

        PowerManager pm = (PowerManager)
            service.getSystemService(Context.POWER_SERVICE);
        WakeLock lock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, tag);

        while (running) {
            if (Util.isScreenOn(service)) {
                boolean hasapps = service.hasRunningAppItems();

                if (hasapps && !lock.isHeld()) {
                    lock.acquire();
                } else if (!hasapps && lock.isHeld()) {
                    lock.release();
                }

                /* Reset before the screen sleeps */
                timeout = service.timeout - 1500;
            } else {
                if (lock.isHeld()) {
                    lock.release();
                }

                timeout = Long.MAX_VALUE;
            }

            try {
                sleep(timeout);
            } catch (InterruptedException e) {
            }
        }
    }

    public void reset()
    {
        if (isAlive()) {
            interrupt();
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
