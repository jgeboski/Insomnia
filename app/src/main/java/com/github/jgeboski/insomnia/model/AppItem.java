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
    public boolean active;
    public long timeout;
    public long seen;

    public AppItem(Context context, String name)
        throws NameNotFoundException
    {
        PackageManager pm = context.getPackageManager();
        ApplicationInfo info = pm.getApplicationInfo(name, 0);

        this.name = name;
        this.label = info.loadLabel(pm).toString();
        this.active = false;
        this.timeout = 0;
        this.seen = 0;
    }

    public Drawable getIcon(Context context)
        throws NameNotFoundException
    {
        PackageManager pm = context.getPackageManager();
        return pm.getApplicationIcon(name);
    }

    public void reset()
    {
        seen = 0;
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
