package com.kushal.kremote.activities;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;
import android.widget.Toast;

import com.kushal.kremote.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class PreRequisites extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prerequisites);

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        //"all the steps will come here "
        TextView tView = (TextView) findViewById(R.id.instructions);
        tView.setText(getFileText());

        TextView helpLink =(TextView)findViewById(R.id.instructionsLink);
        helpLink.setClickable(true);
        helpLink.setMovementMethod(LinkMovementMethod.getInstance());
        String text = "<a href='http://www.iamkushal.tumblr.com/android/kremote'> Need more HELP? </a>";
        helpLink.setText(Html.fromHtml(text));

    }

    public String getFileText(){
        StringBuilder text = new StringBuilder();
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open("Pre-requistes.txt")));
            // do reading, usually loop until end of file reading
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                text.append(mLine);
                text.append('\n');
            }
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(),"Error reading file!",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }
        return text.toString();
    }
}
