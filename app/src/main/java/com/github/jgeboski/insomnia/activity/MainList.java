package com.github.jgeboski.insomnia.activity;

import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.jgeboski.insomnia.Util;
import com.github.jgeboski.insomnia.model.AppItem;
import com.github.jgeboski.insomnia.R;

public class MainList
    extends ArrayAdapter<AppItem>
{
    public String sitems[];
    public long tvalues[];

    public MainList(Context context, int resource, List<AppItem> items)
    {
        super(context, resource, items);

        Resources res = context.getResources();
        sitems = res.getStringArray(R.array.timeout_items_short);
        tvalues = Util.getLongArray(res, R.array.timeout_values);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        AppItem item = getItem(position);
        Context context = getContext();
        String text;

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

        if (item.timeout > 0) {
            int i = Arrays.binarySearch(tvalues, item.timeout);

            if (i < 0) {
                i = 0;
            }

            text = sitems[i];
        } else {
            text = new String();
        }

        tview = (TextView) view.findViewById(R.id.item_app_timeout);
        tview.setText(text);
        return view;
    }
}
