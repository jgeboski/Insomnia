package com.github.jgeboski.insomnia.model;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;

public class AppItem
    implements Comparable<AppItem>
{
    public String name;
    public String label;
    public long timeout;

    public AppItem(Context context, String name)
        throws NameNotFoundException
    {
        PackageManager pm = context.getPackageManager();
        ApplicationInfo info = pm.getApplicationInfo(name, 0);

        this.name = name;
        this.label = info.loadLabel(pm).toString();
        this.timeout = -1;
    }

    public Drawable getIcon(Context context)
        throws NameNotFoundException
    {
        PackageManager pm = context.getPackageManager();
        return pm.getApplicationIcon(name);
    }

    @Override
    public int compareTo(AppItem item)
    {
        if ((label == null) && (item.label == null)) {
            return name.compareTo(item.name);
        }

        if (label == null) {
            return -1;
        }

        if (item.label == null) {
            return 1;
        }

        return label.compareTo(item.label);
    }

    @Override
    public String toString()
    {
        return name;
    }
}
