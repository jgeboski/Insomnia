/*
 * Copyright 2015 James Geboski <jgeboski@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.jgeboski.insomnia;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.PowerManager;
import android.widget.Toast;

public class Util
{
    public static boolean bindService(Context context, ServiceConnection conn,
                                      Class klass)
    {
        context = context.getApplicationContext();
        Intent intent = new Intent(context, klass);

        if (!isServiceRunning(context, klass)) {
            context.startService(intent);
        }

        return context.bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    public static void unbindService(Context context, ServiceConnection conn)
    {
        context = context.getApplicationContext();
        context.unbindService(conn);
    }

    public static String getApplicationFromPid(int pid)
    {
        String path = String.format("/proc/%d/cmdline", pid);
        File file = new File(path);

        try {
            return readFile(file).trim();
        } catch (IOException e) {
            return null;
        }
    }

    public static Set<String> getApplications(Context context)
    {
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> infos = pm.getInstalledApplications(0);
        Set<String> apps = new HashSet<>();
        String pname = context.getPackageName();

        for (ApplicationInfo i : infos) {
            Intent intent = pm.getLaunchIntentForPackage(i.packageName);

            if (!pname.equals(i.packageName) && (intent != null)) {
                apps.add(i.packageName);
            }
        }

        return apps;
    }

    public static long[] getLongArray(Resources res, int resid)
    {
        String strs[] = res.getStringArray(resid);
        long longs[] = new long[strs.length];

        for (int i = 0; i < strs.length; i++) {
            try {
                longs[i] = Long.parseLong(strs[i]);
            } catch (NumberFormatException e) {
                longs[i] = 0;
            }
        }

        return longs;
    }

    public static Set<String> getRunningApps(Context context)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getRunningApps21(context);
        }

        return getRunningApps20(context);
    }

    public static boolean isApplicationPid(int pid)
    {
        String path = String.format("/proc/%d/cgroup", pid);
        File file = new File(path);
        String data;

        try {
            data = readFile(file).trim();
        } catch (IOException e) {
            return false;
        }

        String lines[] = data.split("\\n");
        boolean cpu = false;
        boolean cpuacct = false;

        for (String l : lines) {
            String toks[] = l.split(":");

            if (toks.length != 3) {
                continue;
            }

            String group = toks[2];
            String subs[] = toks[1].split(",");
            Arrays.sort(subs);

            if (!cpu && (Arrays.binarySearch(subs, "cpu") >= 0)) {
                cpu = group.matches("/(bg_non_interactive)?");
            }

            if (!cpuacct && (Arrays.binarySearch(subs, "cpuacct") >= 0)) {
                cpuacct = group.matches("/.+");
            }

            if (cpu && cpuacct) {
                return true;
            }
        }

        return false;
    }

    public static boolean isScreenOn(Context context)
    {
        PowerManager pm = (PowerManager)
            context.getSystemService(Context.POWER_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            return pm.isInteractive();
        }

        return pm.isScreenOn();
    }

    public static boolean isServiceRunning(Context context, Class klass)
    {
        ActivityManager manager = (ActivityManager)
            context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningServiceInfo> services =
            manager.getRunningServices(Integer.MAX_VALUE);
        String name = klass.getName();

        for (RunningServiceInfo s : services) {
            String sname = s.service.getClassName();

            if (name.equals(sname) && (s.pid > 0)) {
                return true;
            }
        }

        return false;
    }

    public static String readFile(File file)
        throws IOException
    {
        FileReader reader = new FileReader(file);
        StringBuilder data = new StringBuilder();
        char buf[] = new char[256];
        int n;

        while ((n = reader.read(buf)) > 0) {
            data.append(buf, 0, n);
        }

        return data.toString();
    }

    public static void toast(Context context, String text, Object ... args)
    {
        String format = String.format(text, args);
        Toast toast = Toast.makeText(context, format, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void toast(Context context, int id, Object ... args)
    {
        String format = context.getString(id);
        toast(context, format, args);
    }

    private static Set<String> getRunningApps20(Context context)
    {
        ActivityManager manager = (ActivityManager)
            context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> procs = manager.getRunningAppProcesses();
        Set<String> apps = new HashSet<>();

        for (RunningAppProcessInfo p : procs) {
            apps.add(p.processName);
        }

        return apps;
    }

    private static Set<String> getRunningApps21(Context context)
    {
        int appid = context.getApplicationInfo().labelRes;

        File file = new File("/proc");
        Set<String> apps = new HashSet<>();
        File files[];
        int pid;

        try {
            files = file.listFiles();
        } catch (SecurityException e) {
            Log.error("Failed to read procfs: %s", e.getMessage());
            return apps;
        }

        for (File f : files) {
            if (!f.isDirectory()) {
                continue;
            }

            try {
                String fname = f.getName();
                pid = Integer.parseInt(fname);
            } catch (NumberFormatException e) {
                continue;
            }

            if (!isApplicationPid(pid)) {
                continue;
            }

            String app = getApplicationFromPid(pid);

            if (!app.isEmpty()) {
                apps.add(app);
            }
        }

        return apps;
    }
}
