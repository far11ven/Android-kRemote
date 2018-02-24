package com.kushal.vlcremotecontrol;

import android.os.Bundle;
import android.os.StrictMode;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;

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


 /*@TargetApi(Build.VERSION_CODES.HONEYCOMB)
     public class Settings extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_settings);

        }

}*/
