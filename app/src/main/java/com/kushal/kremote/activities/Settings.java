package com.kushal.kremote.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class Settings extends AppCompatActivity {
    public static final String KEY_PREF_AUTOSEARCH_CHKBOX= "SEARCH_ON_STARTUP";
    public static final String KEY_PREF_IP_EDITTEXT= "IP_VALUE";
    public static final String KEY_PREF_AUTH_EDITTEXT= "PASS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

    }
}


