package com.github.jgeboski.insomnia.activity;

import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.github.jgeboski.insomnia.Insomnia;
import com.github.jgeboski.insomnia.model.AppItem;
import com.github.jgeboski.insomnia.R;

public class MainActivity
    extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView lview = (ListView) findViewById(R.id.list_apps);
        List<AppItem> items = Insomnia.getAppItems(this);
        Collections.sort(items);

        AppItemList list = new AppItemList(this, R.layout.item_app, items);
        lview.setAdapter(list);
    }
}

class AppItemList
    extends ArrayAdapter<AppItem>
{
    public AppItemList(Context context, int resource, List<AppItem> items)
    {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        AppItem item = getItem(position);
        Context context = getContext();

        if (view == null) {
            LayoutInflater vi = LayoutInflater.from(context);
            view = vi.inflate(R.layout.item_app, null);
        }

        ImageView iview = (ImageView) view.findViewById(R.id.item_app_status);
        TextView tview = (TextView) view.findViewById(R.id.item_app_name);
        tview.setText(item.name);

        if (item.timeout >= 0) {
            iview.setImageResource(R.drawable.enabled);
        } else {
            iview.setImageResource(R.drawable.disabled);
        }

        iview = (ImageView) view.findViewById(R.id.item_app_icon);
        tview = (TextView) view.findViewById(R.id.item_app_label);
        tview.setText(item.label);

        try {
            Drawable icon = item.getIcon(context);
            iview.setImageDrawable(icon);
        } catch (NameNotFoundException e) {
            iview.setImageDrawable(null);
        }

        return view;
    }
}
