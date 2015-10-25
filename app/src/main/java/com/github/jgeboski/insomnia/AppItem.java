package com.github.jgeboski.insomnia;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import java.util.Comparator;

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

    public static Comparator getComparator(Context context)
    {
        return new AppItemComparator(context);
    }
}

class AppItemComparator
    implements Comparator<AppItem>
{
    private Context context;

    public AppItemComparator(Context context)
    {
        this.context = context;
    }

    @Override
    public int compare(AppItem i1, AppItem i2)
    {
        String l1;
        String l2;

        try {
            l1 = i1.getLabel(context);
        } catch (NameNotFoundException e) {
            l1 = null;
        }

        try {
            l2 = i2.getLabel(context);
        } catch (NameNotFoundException e) {
            l2 = null;
        }

        if ((l1 == null) && (l2 == null)) {
            return i1.name.compareTo(i2.name);
        }

        if (l1 == null) {
            return -1;
        }

        if (l2 == null) {
            return 1;
        }

        return l1.compareTo(l2);
    }
}
