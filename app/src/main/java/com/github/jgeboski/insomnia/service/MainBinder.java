package com.github.jgeboski.insomnia.service;

import android.os.Binder;

public class MainBinder
    extends Binder
{
    public MainService service;

    public MainBinder(MainService service)
    {
        this.service = service;
    }
}
