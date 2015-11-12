package com.github.jgeboski.insomnia;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.pm.PackageManager;

import com.github.jgeboski.insomnia.model.AppItem;

public class Insomnia
{
    public static int SERVICE_NOTIFICATION_ID = R.string.app_name;

    public static List<AppItem> getAppItems(Context context,
                                            Map<String, AppItem> items)
    {
        List<String> apps = Util.getApplications(context);
        List<AppItem> ret = new ArrayList<>();

        for (String a : apps) {
            AppItem item = items.get(a);

            if (item != null) {
                ret.add(item);
                continue;
            }

            try {
                item = new AppItem(context, a);
                ret.add(item);
            } catch (PackageManager.NameNotFoundException e) {
            }
        }

        return ret;
    }

    public static boolean hasRunningAppItems(Context context,
                                             Map<String, AppItem> items)
    {
        PackageManager pm = context.getPackageManager();
        List<String> running = Util.getRunningApps(context);
        Set<String> apps = items.keySet();

        for (String r : running) {
            if (apps.contains(r)) {
                return true;
            }
        }

        return false;
    }
}
