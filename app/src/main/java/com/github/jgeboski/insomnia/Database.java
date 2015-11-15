package com.github.jgeboski.insomnia;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.github.jgeboski.insomnia.model.AppItem;

public class Database
    extends SQLiteOpenHelper
{
    public static final String NAME = "insomnia";
    public static final String TABLE_ITEMS = "items";
    public static final String COL_ITEMS_NAME = "name";
    public static final String COL_ITEMS_ACTIVE = "active";
    public static final String COL_ITEMS_TIMEOUT = "timeout";

    public static final String QUERY_CREATE_ITEMS = join(
        "CREATE TABLE", TABLE_ITEMS, "(",
            COL_ITEMS_NAME, "TEXT PRIMARY KEY,",
            COL_ITEMS_ACTIVE, "INTEGER,",
            COL_ITEMS_TIMEOUT, "INTEGER",
        ")"
    );

    public static final String WHERE_ITEMS_NAME = join(
        COL_ITEMS_NAME, "= ?"
    );

    public SQLiteDatabase db;

    public Database(Context context)
    {
        super(context, NAME, null, 1);
        this.db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(QUERY_CREATE_ITEMS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
    }

    public List<AppItem> getAppItems(Context context)
    {
        List<AppItem> items = new ArrayList<>();

        Cursor cursor = db.query(TABLE_ITEMS, null, null,
                                 null, null, null, null);

        while (cursor.moveToNext()) {
            try {
                AppItem item = getAppItem(context, cursor);
                items.add(item);
            } catch (NameNotFoundException e) {
            }
        }

        return items;
    }

    public void remove(AppItem item)
    {
        String args[] = {item.name};
        db.delete(TABLE_ITEMS, WHERE_ITEMS_NAME, args);
    }

    public void update(AppItem item)
    {
        ContentValues values = getValues(item);
        String args[] = {item.name};
        int rows = db.update(TABLE_ITEMS, values, WHERE_ITEMS_NAME, args);

        if (rows < 1) {
            db.insert(TABLE_ITEMS, null, values);
        }
    }

    public static String join(Object ... args)
    {
        StringBuilder sb = new StringBuilder();

        for (Object a : args) {
            if (sb.length() > 0) {
                sb.append(' ');
            }

            sb.append(a.toString());
        }

        return sb.toString();
    }

    private AppItem getAppItem(Context context, Cursor cursor)
        throws NameNotFoundException
    {
        String name = cursor.getString(0);
        AppItem item = new AppItem(context, name);
        item.active = cursor.getInt(1) != 0;
        item.timeout = cursor.getLong(2);
        return item;
    }

    private ContentValues getValues(AppItem item)
    {
        ContentValues values = new ContentValues();
        values.put(COL_ITEMS_NAME, item.name);
        values.put(COL_ITEMS_ACTIVE, item.active ? 1 : 0);
        values.put(COL_ITEMS_TIMEOUT, item.timeout);
        return values;
    }
}
