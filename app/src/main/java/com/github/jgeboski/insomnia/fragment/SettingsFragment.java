package com.github.jgeboski.insomnia.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.github.jgeboski.insomnia.R;

public class SettingsFragment
    extends PreferenceFragment
{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
