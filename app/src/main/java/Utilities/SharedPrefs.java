package Utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

public class SharedPrefs extends AppCompatActivity {

    Context mContext;
    public SharedPrefs(Context context)
    {
        mContext = context;
    }

    public void setDataInSharedPref(String MY_PREFS_NAME, String key , String value){

        SharedPreferences.Editor editor = mContext.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        //editor.apply();
        editor.commit();


    }

    public String getDataFromSharedPref(String MY_PREFS_NAME, String key){

        SharedPreferences prefs = mContext.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        String restoredText = prefs.getString(key, null);
        if (restoredText != null) {

            return restoredText;
        }else{
            String defValue = prefs.getString(key, "No value defined");//"No name defined" is the default value.
            return defValue;
        }
    }

}