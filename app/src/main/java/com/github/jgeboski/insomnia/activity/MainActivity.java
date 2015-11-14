package com.github.jgeboski.insomnia.activity;

import java.util.Collections;
import java.util.List;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
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
import android.widget.Button;
import android.widget.ListView;

import com.github.jgeboski.insomnia.model.AppItem;
import com.github.jgeboski.insomnia.R;
import com.github.jgeboski.insomnia.service.MainService;
import com.github.jgeboski.insomnia.Util;
import com.github.jgeboski.insomnia.service.MainBinder;

public class MainActivity
    extends AppCompatActivity
    implements OnClickListener,
               OnItemClickListener,
               OnSharedPreferenceChangeListener,
               ServiceConnection
{
    public Button active;
    public ListView listv;
    public MainList list;
    public MainService service;
    public SharedPreferences prefs;

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

        listv = (ListView) findViewById(R.id.list_apps);
        listv.setOnItemClickListener(this);

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
        if (key.equals("active")) {
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
    public void onClick(View v)
    {
        Editor editor = prefs.edit();
        boolean active = prefs.getBoolean("active", true);

        editor.putBoolean("active", !active);
        editor.apply();
        updateActiveButton();
    }

    private void updateActiveButton()
    {
        int color;
        int text;

        if (prefs.getBoolean("active", true)) {
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
