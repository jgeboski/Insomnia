package com.github.jgeboski.insomnia;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;

public class AppItem
{
    public String name;
    public long timeout;

    public AppItem(String name)
    {
        this.name = name;
        this.timeout = -1;
    }

    public String getLabel(Context context)
        throws NameNotFoundException
    {
        PackageManager pm = context.getPackageManager();
        ApplicationInfo info = pm.getApplicationInfo(name, 0);
        return info.loadLabel(pm).toString();
    }

    public Drawable getIcon(Context context)
        throws NameNotFoundException
    {
        PackageManager pm = context.getPackageManager();
        return pm.getApplicationIcon(name);
    }

    @Override
    public String toString()
    {
        return name;
    }
}
