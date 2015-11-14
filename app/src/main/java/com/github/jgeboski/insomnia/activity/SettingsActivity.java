package com.github.jgeboski.insomnia.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.jgeboski.insomnia.fragment.SettingsFragment;

public class SettingsActivity
    extends AppCompatActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentTransaction trans = getFragmentManager().beginTransaction();
        Fragment fragment = new SettingsFragment();

        trans.add(android.R.id.content, fragment);
        trans.commit();
    }
}
