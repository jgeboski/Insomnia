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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.pm.PackageManager;

import com.github.jgeboski.insomnia.model.AppItem;

public class Insomnia
{
    public static final String PREF_ACTIVE = "active";
    public static final String PREF_BOOT = "boot";
    public static final String PREF_DIMMABLE = "dimmable";
    public static final int SERVICE_NOTIFICATION_ID = R.string.app_name;

    public static List<AppItem> getAppItems(Context context,
                                            Map<String, AppItem> items)
    {
        Set<String> apps = Util.getApplications(context);
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

    public static List<AppItem> getRunningAppItems(Context context,
                                                   Map<String, AppItem> items)
    {
        Set<String> rapps = Util.getRunningApps(context);
        List<AppItem> ret = new ArrayList<>();
        long time = System.currentTimeMillis();

        for (AppItem i : items.values()) {
            if (!rapps.contains(i.name)) {
                i.reset();
                continue;
            }

            if (i.timeout > 0) {
                if (i.seen < 1) {
                    i.seen = time;
                }

                if ((i.seen + i.timeout) < time) {
                    continue;
                }
            }

            ret.add(i);
        }

        return ret;
    }

    public static long getTimeout(List<AppItem> items, long stimeout)
    {
        long time = System.currentTimeMillis();
        long timeout = stimeout - 1500;

        for (AppItem i : items) {
            if (i.timeout < 1) {
                continue;
            }

            long itimeout = (i.seen + i.timeout) - time;

            if ((itimeout > 0) && (itimeout < timeout)) {
                timeout = itimeout;
            }
        }

        return timeout;
    }

    public static void resetAppItems(Map<String, AppItem> items)
    {
        for (AppItem i : items.values()) {
            i.reset();
        }
    }
}
