package com.kushal.kremote.activities;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.kushal.kremote.R;

public  class SettingsFragment extends PreferenceFragmentCompat  {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        setPreferencesFromResource(R.xml.pref_settings, rootKey);
    }
}