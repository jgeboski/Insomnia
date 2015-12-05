package com.github.jgeboski.insomnia.service;

import java.util.List;

import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import com.github.jgeboski.insomnia.Insomnia;
import com.github.jgeboski.insomnia.Log;
import com.github.jgeboski.insomnia.Util;
import com.github.jgeboski.insomnia.model.AppItem;

public class MainThread
    extends Thread
{
    public static final long TIMEOUT_MAX =
        /* The maximum time supported by Android */
        (((long) Integer.MAX_VALUE) * 1000) - 1;

    public MainService service;
    public boolean running;
    public WakeLock block;
    public WakeLock dlock;

    public MainThread(MainService service)
    {
        this.service = service;
        this.running = false;

        PowerManager pm = (PowerManager)
            service.getSystemService(Context.POWER_SERVICE);
        int appid = service.getApplicationInfo().labelRes;
        String tag = service.getString(appid);

        block = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, tag);
        dlock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, tag);
    }

    @Override
    public void run()
    {
        long timeout;
        running = true;

        Log.info("Service thread started");

        while (running) {
            if (Util.isScreenOn(service)) {
                List<AppItem> items = service.getRunningAppItems();
                timeout = Insomnia.getTimeout(items, service.timeout);

                if (!items.isEmpty()) {
                    acquireLock();
                } else {
                    releaseLock();
                }
            } else {
                timeout = TIMEOUT_MAX;
                releaseLock();
            }

            try {
                Log.debug("Sleeping for %d ms", timeout);
                sleep(timeout);
            } catch (InterruptedException e) {
            }
        }

        Log.info("Service thread stopped");
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

        releaseLock();
    }

    private void acquireLock()
    {
        if (!service.dimmable && !block.isHeld()) {
            releaseLock();
            Log.info("Acquiring bright wake lock");
            block.acquire();
        } else if (service.dimmable && !dlock.isHeld()) {
            releaseLock();
            Log.info("Acquiring dim wake lock");
            dlock.acquire();
        }
    }

    private void releaseLock()
    {
        if (block.isHeld()) {
            Log.info("Releasing bright wake lock");
            block.release();
        }

        if (dlock.isHeld()) {
            Log.info("Releasing dim wake lock");
            dlock.release();
        }
    }
}
