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

package com.github.jgeboski.insomnia.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.github.jgeboski.insomnia.Insomnia;
import com.github.jgeboski.insomnia.service.MainService;

public class ScreenReceiver
    extends BroadcastReceiver
{
    public MainService service;

    public ScreenReceiver(MainService service)
    {
        super();
        this.service = service;
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();

        if (action.equals(Intent.ACTION_SCREEN_ON)) {
            Insomnia.resetAppItems(service.items);
        }

        service.reset();
    }
}
