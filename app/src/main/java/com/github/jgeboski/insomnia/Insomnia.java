package com.github.jgeboski.insomnia;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import java.util.ArrayList;
import java.util.List;

public class Insomnia
{
    public static List<AppItem> getAppItems(Context context)
    {
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> infos = pm.getInstalledApplications(0);
        List<AppItem> items = new ArrayList<>(infos.size());
        String pname = context.getPackageName();

        for (ApplicationInfo info : infos) {
            if (pname.equals(info.packageName)) {
                continue;
            }

            Intent intent =  pm.getLaunchIntentForPackage(info.packageName);

            if (intent != null) {
                AppItem item = new AppItem(info.packageName);
                items.add(item);
            }
        }

        return items;
    }
}
