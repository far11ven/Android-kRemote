package com.kushal.kremote.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.text.format.Formatter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;

import android.util.Base64;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.kushal.kremote.R;
import com.kushal.kremote.utils.IPAddressValidator;

import static com.google.android.gms.ads.AdSize.*;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button startButton;
    //private ProgressBar progressBar;

    private RelativeLayout mProgressBar;
    private static TextView connectionState;

    private WebView vlcWebView;

    private SharedPreferences sharedPref;
    static private String vlcConnectionURL;
    static private String authToken;
    private static final String PORT_NUMBER = "8080";
    private static String CURRENT_VLC_SERVER_IP;
    private AsyncTask searchVLCTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PreferenceManager.setDefaultValues(this, R.xml.pref_settings, false);  //sets user preferences to default at first time invocation
        // to get settings from Shared Preferences
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        //Display Logo in Action Bar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.smalllogo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        mProgressBar = findViewById(R.id.rl_progressBar);
        //progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, android.R.color.holo_green_light), android.graphics.PorterDuff.Mode.MULTIPLY);

        connectionState = findViewById(R.id.connection_State);

        vlcWebView = findViewById(R.id.vlc_mobileView);
        vlcWebView.setWebContentsDebuggingEnabled(false); // this command prevents debugging in WebView
        vlcWebView.loadDataWithBaseURL(null, getDefaultVLCPage(), "text/html", "UTF-8", null);  //load default grey VLC page on startup

        searchForVLCConnection(sharedPref.getBoolean(Settings.KEY_PREF_AUTOSEARCH_CHKBOX, false));

        startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.startButton:
                if (searchVLCTask != null) {
                    searchVLCTask.cancel(true);

                    System.out.println(" =====================================  Task has been closed");

                }
                searchForVLCConnection(true);

                //vlcWebView.loadUrl("http://:12345@" + "192.168.0.104 " + ":8080/mobile.html");  //replace IP
                break;
        }


    }

    public void searchForVLCConnection(boolean searchFlag) {

        if (searchFlag == true) {
            vlcConnectionURL = sharedPref.getString(Settings.KEY_PREF_IP_EDITTEXT, "");
            authToken = sharedPref.getString(Settings.KEY_PREF_AUTH_EDITTEXT, "");

            IPAddressValidator ipValidator = new IPAddressValidator();

            if (authToken != null && !authToken.isEmpty()) {

                if (vlcConnectionURL != null && !vlcConnectionURL.isEmpty()) {

                    if (ipValidator.validate(vlcConnectionURL) == true) {
                        //vlcWebView.loadUrl("about:blank");
                        vlcWebView.loadDataWithBaseURL(null, getDefaultVLCPage(), "text/html", "UTF-8", null);
                        connectionState.setText("Trying to connect to VLC running on : " + vlcConnectionURL);
                        searchVLCTask = new FindVLCServerTask().execute(vlcConnectionURL);

                    } else {
                        //vlcWebView.loadUrl("about:blank");
                        vlcWebView.loadDataWithBaseURL(null, getDefaultVLCPage(), "text/html", "UTF-8", null);
                        String toastMsg = "A valid IP address is not set \n Tip: You can get that by typing \"IPCONFIG\" on your PC's command prompt.";
                        Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_LONG).show();
                        vlcConnectionURL = null;    //setting null, as provided is invalid, hence vlc will try to search

                        searchVLCTask = new FindVLCServerTask().execute(vlcConnectionURL);
                    }

                } else {

                    //vlcWebView.loadUrl("about:blank");
                    vlcWebView.loadDataWithBaseURL(null, getDefaultVLCPage(), "text/html", "UTF-8", null);
                    String toastMsg = "No existing VLC IP found, Searching for local VLC server..";
                    Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_LONG).show();
                    searchVLCTask = new FindVLCServerTask().execute(vlcConnectionURL);

                }

            } else {
                //vlcWebView.loadUrl("about:blank");
                vlcWebView.loadDataWithBaseURL(null, getDefaultVLCPage(), "text/html", "UTF-8", null);
                connectionState.setText("Please first set your VLC password under " + "\n" + "\"Settings > VLC Password\"");
            }
        }
    }

    public class FindVLCServerTask extends AsyncTask<String, Void, String> {

        //Override onPreExecute to set the loading indicator to visible
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            String[] connectionParams = params;
            final String urlParam = connectionParams[0];
            String username = "";
            String password = authToken;

            String vlcServerURL = urlParam + ":" + PORT_NUMBER;
            String urlString = "http://" + vlcServerURL;
            int connectionCounter = 0;
            int responseCode = 0;

            if (testAServer(urlString, username, password) != 200) {
                vlcServerURL = null;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        connectionState.setText("Tip: To speed things up you can manually provide your VLC IP under \"Settings > VLC IP Address\" \n");
                    }
                });


                for (int i = 0; i <= 255; i++) {

                    do {
                        vlcServerURL = "192.168." + i + "." + connectionCounter + ":" + PORT_NUMBER;
                        urlString = "http://" + vlcServerURL;
                        responseCode = testAServer(urlString, username, password);

                        System.out.println(" VLC URL IS : " + vlcServerURL + " & response code is :" + responseCode);

                        if (responseCode == 200) {
                            break;
                        }

                        if (responseCode == 401) {
                            vlcServerURL = null;
                            break;
                        }

                        if (connectionCounter == 255 && responseCode != 200) {

                            vlcServerURL = null;
                        }

                        if (isCancelled())
                            break;


                        //increment counter
                        connectionCounter++;

                    } while (connectionCounter <= 255 || responseCode == 200);

                    connectionCounter = 0;

                    //when found a server
                    if (i <= 255 && vlcServerURL != null) {

                        break;
                    }
                }
            }

            return vlcServerURL;
        }

        @Override
        protected void onCancelled() {

        }

        @Override
        protected void onPostExecute(String vlcConnectionURL) {
            final String vlcServerURL = vlcConnectionURL;
            // As soon as the loading is complete, hide the loading indicator
            mProgressBar.setVisibility(View.INVISIBLE);

            if (vlcServerURL != null && !vlcServerURL.isEmpty()) {

                vlcWebView.setWebViewClient(new WebViewClient());  //opens all the links in same web view

                WebSettings vlcWebSettings = vlcWebView.getSettings();
                vlcWebSettings.setJavaScriptEnabled(true);
                vlcWebSettings.setLoadsImagesAutomatically(true);
                //vlcWebSettings.setBuiltInZoomControls(true);
                //vlcWebSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                //This the the enabling of the zoom controls
                //vlcWebSettings.setBuiltInZoomControls(true);

                vlcWebSettings.setUseWideViewPort(true);
                vlcWebSettings.setLoadWithOverviewMode(true);
                vlcWebView.setInitialScale(-1);                     //This will zoom out the WebView
                vlcWebView.loadUrl("http://:" + authToken + "@" + vlcServerURL + "/mobile.html");

                CURRENT_VLC_SERVER_IP = vlcServerURL;
                connectionState.setText("VLC Player is running on : " + vlcServerURL);
                Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();


            } else {

                //vlcWebView.loadUrl("about:blank");
                vlcWebView.loadDataWithBaseURL(null, getDefaultVLCPage(), "text/html", "UTF-8", null);
                String toastMsg = getString(R.string.messageOnConnectToVLCFailure);
                Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_LONG).show();

                connectionState.setText("VLC server was not found :( " + "\n" + "Please check your vlc player settings!");

            }


        }
    }


    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        WebView vlcWebView = findViewById(R.id.vlc_mobileView);
        TextView connectionState = findViewById(R.id.connection_State);

        switch (item.getItemId()) {
            case R.id.action_back:
                if (vlcWebView.canGoBack()) {
                    if (!vlcWebView.getUrl().contains("mobile.html")) {
                        vlcWebView.goBack();
                    }
                } else {
                    super.onBackPressed();
                    finish();
                }
                return true;

            case R.id.action_ad:

                // Initialize the Mobile Ads SDK
                MobileAds.initialize(this, getString(R.string.admob_app_id_test));   //replace with test account

                AdRequest adRequest = new AdRequest.Builder().addTestDevice("A70433FFEDBDE75343616C4719DBCC94").build();

                // Prepare the Interstitial Ad Activity
                final InterstitialAd mInterstitialAd = new InterstitialAd(this);

                // Insert the Ad Unit ID
                mInterstitialAd.setAdUnitId(getString(R.string.admob_interstitial_id_test));

                // Interstitial Ad load Request
                mInterstitialAd.loadAd(adRequest);

                // Prepare an Interstitial Ad Listener
                mInterstitialAd.setAdListener(new AdListener() {
                    public void onAdLoaded() {
                        // Call displayInterstitial() function when the Ad loads
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                        }
                    }
                });

                return true;

            case R.id.action_refresh:
                if (vlcConnectionURL != null && !vlcConnectionURL.isEmpty() && authToken != null && !authToken.isEmpty()) {
                    vlcWebView.loadUrl("http://:" + authToken + "@" + vlcConnectionURL + "/mobile.html");
                }
                Toast.makeText(getApplicationContext(), "Connection has been refreshed", Toast.LENGTH_LONG).show();
                return true;

            case R.id.menu_Item1:
                Intent intent_PreReq = new Intent(this, PreRequisites.class);
                startActivity(intent_PreReq);
                return true;

            case R.id.menu_Item2:
                Intent intent_Settings = new Intent(this, Settings.class);
                startActivity(intent_Settings);
                return true;

            case R.id.menu_Item3:
                Intent intent_About = new Intent(this, AboutActivity.class);
                startActivity(intent_About);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public static int testAServer(String urlString, String username, String password) {

        HttpURLConnection urlc = null;
        int responseCode = 0;
        try {
            URL url = new URL(urlString);
            urlc = (HttpURLConnection) url.openConnection();
            String userCredentials = username + ":" + password;
            String basicAuth = "Basic " + Base64.encodeToString(userCredentials.getBytes(), Base64.DEFAULT);
            urlc.setRequestProperty("Authorization", basicAuth);
            urlc.setRequestMethod("GET");

            urlc.setConnectTimeout(1 * 100);          //  in milliSeconds.
            urlc.connect();
            responseCode = urlc.getResponseCode();

        } catch (MalformedURLException e1) {
            //connectionState.setText("MalformedURLException nada");
        } catch (IOException e) {
            // connectionState.setText("IOException nada");
        }
        return responseCode;
    }

    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String ip = Formatter.formatIpAddress(inetAddress.hashCode());
                        return ip;
                    }
                }
            }
        } catch (SocketException ex) {

        }
        return null;

    }

    public String getDefaultVLCPage() {

        final String defaultHTMLPage = "<!DOCTYPE html>" +
                "<html lang=\"en-US\">" +
                "<head><meta name=\"viewport\" content=\"width=device-width, user-scalable=yes\" /></head>" +
                "<body style=\"font: 11pt Helvetica, Arial, sans-serif; background-color: #EEE; margin: 0px;\">" +
                "<div id=\"art\" style=\"top: 0px; width: 150px; height: 150px; box-sizing: border-box; margin: 10px auto;\">" +
                "<img id=\"albumArt\" src=\"file:///android_asset/vlc_trans_48.png\" width=\"128\" height=\"128\" style=\" -webkit-filter: grayscale(100%); filter: grayscale(100%); display: inline; margin-left: 11px; margin-top: 10px;\">" +
                "</div>" +
                "</body></html>";

        return defaultHTMLPage;
    }

}