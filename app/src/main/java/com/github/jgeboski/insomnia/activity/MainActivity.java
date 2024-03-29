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

package com.github.jgeboski.insomnia.activity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.github.jgeboski.insomnia.Insomnia;
import com.github.jgeboski.insomnia.model.AppItem;
import com.github.jgeboski.insomnia.R;
import com.github.jgeboski.insomnia.service.MainService;
import com.github.jgeboski.insomnia.Util;
import com.github.jgeboski.insomnia.service.MainBinder;

public class MainActivity
    extends AppCompatActivity
    implements DialogInterface.OnClickListener,
               OnClickListener,
               OnItemClickListener,
               OnItemLongClickListener,
               OnSharedPreferenceChangeListener,
               ServiceConnection
{
    public Button active;
    public ListView listv;
    public MainList list;
    public MainService service;
    public SharedPreferences prefs;
    public long tvalues[];
    public int itempos;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!Util.bindService(this, this, MainService.class)) {
            Util.toast(this, R.string.failed_service_bind);
            finish();
            return;
        }

        Resources res = getResources();
        tvalues = Util.getLongArray(res, R.array.timeout_values);

        listv = (ListView) findViewById(R.id.list_apps);
        listv.setOnItemClickListener(this);
        listv.setOnItemLongClickListener(this);

        active = (Button) findViewById(R.id.button_active);
        active.setOnClickListener(this);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
        updateActiveButton();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        prefs.unregisterOnSharedPreferenceChangeListener(this);
        Util.unbindService(this, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder ibinder)
    {
        MainBinder binder = (MainBinder) ibinder;
        service = binder.service;

        List<AppItem> items = service.getAppItems();
        Collections.sort(items);

        list = new MainList(this, R.layout.item_app, items);
        listv.setAdapter(list);
    }

    @Override
    public void onServiceDisconnected(ComponentName name)
    {
        this.service = null;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key)
    {
        if (key.equals(Insomnia.PREF_ACTIVE)) {
            updateActiveButton();
        }
    }

    @Override
    public void onItemClick(AdapterView parent, View view, int pos, long id)
    {
        AppItem item = list.getItem(pos);
        item.active = !item.active;
        service.update(item);
        list.notifyDataSetChanged();
    }

    @Override
    public boolean onItemLongClick(AdapterView parent, View view, int pos,
                                   long id)
    {
        AppItem item = list.getItem(pos);
        int i = Arrays.binarySearch(tvalues, item.timeout);
        itempos = pos;

        if (i < 0) {
            i = 0;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.timeout);
        builder.setSingleChoiceItems(R.array.timeout_items, i, this);
        builder.setNegativeButton(android.R.string.cancel, this);
        builder.show();
        return true;
    }

    @Override
    public void onClick(DialogInterface dialog, int which)
    {
        if (which == DialogInterface.BUTTON_NEGATIVE) {
            dialog.dismiss();
            return;
        }

        AppItem item = list.getItem(itempos);
        item.timeout = tvalues[which];
        service.update(item);
        list.notifyDataSetChanged();
        dialog.dismiss();
    }

    @Override
    public void onClick(View v)
    {
        Editor editor = prefs.edit();
        boolean active = prefs.getBoolean(Insomnia.PREF_ACTIVE, true);

        editor.putBoolean(Insomnia.PREF_ACTIVE, !active);
        editor.apply();
        updateActiveButton();
    }

    private void updateActiveButton()
    {
        int color;
        int text;

        if (prefs.getBoolean(Insomnia.PREF_ACTIVE, true)) {
            color = Color.GREEN;
            text = R.string.active;
        } else {
            color = Color.RED;
            text = R.string.inactive;
        }

        active.setText(text);
        active.setTextColor(color);
    }
}
