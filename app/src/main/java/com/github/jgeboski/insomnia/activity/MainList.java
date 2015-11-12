package com.github.jgeboski.insomnia.activity;

import java.util.List;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.jgeboski.insomnia.model.AppItem;
import com.github.jgeboski.insomnia.R;

public class MainList
    extends ArrayAdapter<AppItem>
{
    public MainList(Context context, int resource, List<AppItem> items)
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

        if (item.active) {
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
        } catch (PackageManager.NameNotFoundException e) {
            iview.setImageDrawable(null);
        }

        return view;
    }
}
