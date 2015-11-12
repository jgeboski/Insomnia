package com.github.jgeboski.insomnia.service;

import android.os.Binder;

public class MainServiceBinder
    extends Binder
{
    public MainService service;

    public MainServiceBinder(MainService service)
    {
        this.service = service;
    }
}
