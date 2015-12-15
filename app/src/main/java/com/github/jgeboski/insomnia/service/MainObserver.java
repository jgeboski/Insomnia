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

package com.github.jgeboski.insomnia.service;

import android.database.ContentObserver;
import android.os.Handler;

public class MainObserver
    extends ContentObserver
{
    public MainService service;

    public MainObserver(MainService service)
    {
        super(new Handler());
        this.service = service;
    }

    @Override
    public void onChange(boolean selfChange)
    {
        service.rehashSettings();
    }

    @Override
    public boolean deliverSelfNotifications()
    {
        return true;
    }
}
