package com.github.jgeboski.insomnia.activity;

import java.util.Collections;
import java.util.List;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.github.jgeboski.insomnia.model.AppItem;
import com.github.jgeboski.insomnia.R;
import com.github.jgeboski.insomnia.service.MainService;
import com.github.jgeboski.insomnia.Util;
import com.github.jgeboski.insomnia.service.MainServiceBinder;

public class MainActivity
    extends AppCompatActivity
    implements OnItemClickListener,
               ServiceConnection
{
    public ListView listv;
    public MainList list;
    public MainService service;

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
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Util.unbindService(this, this);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder ibinder)
    {
        MainServiceBinder binder = (MainServiceBinder) ibinder;
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
    public void onItemClick(AdapterView parent, View view, int pos, long id)
    {
        AppItem item = list.getItem(pos);
        item.active = !item.active;
        service.update(item);
        list.notifyDataSetChanged();
    }
}
