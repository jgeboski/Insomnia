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

        while (running) {
            if (Util.isScreenOn(service)) {
                if (service.hasRunningAppItems()) {
                    acquireLock();
                } else {
                    releaseLock();
                }

                /* Reset before the screen sleeps */
                timeout = service.timeout - 1500;
            } else {
                timeout = Long.MAX_VALUE;
                releaseLock();
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

        releaseLock();
    }

    private void acquireLock()
    {
        if (service.dimmable) {
            if (block.isHeld()) {
                block.release();
            }

            if (!dlock.isHeld()) {
                dlock.acquire();
            }
        } else {
            if (dlock.isHeld()) {
                dlock.release();
            }

            if (!block.isHeld()) {
                block.acquire();
            }
        }
    }

    private void releaseLock()
    {
        if (block.isHeld()) {
            block.release();
        }

        if (dlock.isHeld()) {
            dlock.release();
        }
    }
}
