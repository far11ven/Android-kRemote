package com.kushal.kremote.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kushal.kremote.R;


public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    TextView siteLinkTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        siteLinkTextView = findViewById(R.id.tv_website);
        siteLinkTextView.setClickable(true);
        siteLinkTextView.setMovementMethod(LinkMovementMethod.getInstance());
        String myWebURL = "https://iamkushal.tumblr.com/android/kremote";
        siteLinkTextView.setText(myWebURL);

        siteLinkTextView.setOnClickListener(this);

        ImageView fbImgView = findViewById(R.id.img_fb);
        ImageView instaImgView = findViewById(R.id.img_insta);
        ImageView twitterImgView = findViewById(R.id.img_twitter);
        ImageView whatsappImgView = findViewById(R.id.img_whatsapp);
        CardView cv_openPlayStore = findViewById(R.id.cv_store);

        fbImgView.setOnClickListener(this);
        instaImgView.setOnClickListener(this);
        twitterImgView.setOnClickListener(this);
        whatsappImgView.setOnClickListener(this);
        cv_openPlayStore.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        Intent intent = new Intent();

        switch (v.getId()) {
            case R.id.img_fb:

                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://www.facebook.com/sharer/sharer.php?u=https%3A%2F%2Fplay.google.com%2Fstore%2Fapps%2Fdetails%3Fid%3Dcom.kushal.kremote&src=sdkpreparse"));
                startActivity(intent);

                break;

            case R.id.img_insta:

                shareToInstagram();

                break;

            case R.id.img_twitter:

                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://twitter.com/share?hashtags=kRemote,vlc&text=Hey%21+try+out%3A kRemote - https://play.google.com/store/apps/details?id=com.kushal.kremote"));
                startActivity(intent);
                break;

            case R.id.img_whatsapp:

                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://api.whatsapp.com/send?text=Hey%21%20try%20out%3A%20https://play.google.com/store/apps/details?id=com.kushal.kremote"));
                startActivity(intent);
                break;

            case R.id.cv_store:

                openAppInPlayStore();
                break;


            case R.id.tv_website:

                intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.kushal.kremote"));
                startActivity(intent);
                break;

        }




    }

    public void openAppInPlayStore() {
        System.out.println("Package === " + this.getPackageName());

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);

        } catch (ActivityNotFoundException e) {

            Toast toast = Toast.makeText(this,
                    "You device don't have any app which can open this link",
                    Toast.LENGTH_LONG);
            toast.setGravity(Gravity.BOTTOM, 0, 100);
            toast.show();


        }
    }



    public void shareToInstagram() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Hey! try out: kRemote - https://play.google.com/store/apps/details?id=com.kushal.kremote");
            shareIntent.setPackage("com.instagram.android");

            Toast.makeText(this, "Message copied, you can now share it to your friends on instagram", Toast.LENGTH_SHORT).show();
            startActivity(shareIntent);

        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Message copied, you can now share it to your friends on instagram", Toast.LENGTH_SHORT).show();
        }
    }

}
