package com.pastweather.pastweather;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.HttpsURLConnection;

public class WeatherApplication extends Application {
    private static final String LOG_TAG = "WeatherApplication";
    private LocationManager locationManager = null;
    private final String darkSkyKey = "92d16368d0abe79a5842b05e27c9e606";
    private final String domain = "https://api.darksky.net";
    private final String reqParams = "exclude=currently,minutely,hourly,alerts,flags";
    private DoneGetListener subscribe;
    private AtomicInteger getCount = new AtomicInteger();

    public void setDoneGetListener(DoneGetListener listener) {
        this.subscribe = listener;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
    }

    public Location getLastLocation() throws Exception {
        String locationProvider;
        if (isLocationEnabled()) {
            locationProvider = LocationManager.GPS_PROVIDER;
        } else {
            locationProvider = LocationManager.NETWORK_PROVIDER;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            throw new Exception("Need to enable GPS ang grant location permissions");
        }
        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
        return lastKnownLocation;
    }

    public boolean isLocationEnabled() {
        int locationMode = 0;
        String locationProviders;

        try {
            locationMode = Settings.Secure.getInt(this.getContentResolver(), Settings.Secure.LOCATION_MODE);

        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return locationMode != Settings.Secure.LOCATION_MODE_OFF;
    }

    public String composeQueryParams(int pastDay, double lat, double lng) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1 * pastDay);
        Date time = cal.getTime();
        time.setHours(12);
        time.setMinutes(0);
        time.setSeconds(0);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat dateFormat2 = new SimpleDateFormat("HH:mm:ss");
        String dateStr = dateFormat.format(time);
        String dateStr2 = dateFormat2.format(time);
        String params = domain + String.format("/forecast/%s/%.4f,%.4f,%sT%s", darkSkyKey, lat, lng, dateStr, dateStr2);
        return params;
    }

    public void startSendHttpsRequestThread(final String reqUrl, final int index, final DailyWeather[] listDaily) {
        Thread sendHttpRequestThread = new Thread() {
            @Override
            public void run() {
                // Maintain http url connection.
                HttpsURLConnection httpConn = null;

                // Read text input stream.
                InputStreamReader isReader = null;

                // Read text into buffer.
                BufferedReader bufReader = null;

                // Save server response text.
                StringBuffer readTextBuf = new StringBuffer();

                try {
                    // Create a URL object use page url.
                URL url = new URL(reqUrl + "?" + reqParams);

                    // Open http connection to web server.
                    httpConn = (HttpsURLConnection) url.openConnection();

                    // Set http request method to get.
                    httpConn.setRequestMethod("GET");
                    httpConn.setRequestProperty("User-Agent", "");
                    httpConn.setRequestProperty("Content-Type", "application/json");
                    httpConn.setRequestProperty("Accept", "application/json");

                    httpConn.setDoInput(true);

                    // Set connection timeout and read timeout value.
                    httpConn.setConnectTimeout(10000);
                    httpConn.setReadTimeout(10000);
                    int statusCode = httpConn.getResponseCode();

                    InputStream is = httpConn.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);

                    String inputLine;
                    StringBuilder sb = new StringBuilder();

                    while ((inputLine = br.readLine()) != null) {
                        sb.append(inputLine);
                    }
                    String result = sb.toString();
                    DailyWeather dailyWeather = DailyWeather.getObjectFromString(result);
                    if (dailyWeather != null) {
                        listDaily[index] =dailyWeather;
                    }

                    br.close();
                    httpConn.disconnect();

                } catch (MalformedURLException ex) {
                    Log.e(LOG_TAG, ex.getMessage(), ex);
                } catch (Exception ex) {
                    Log.e(LOG_TAG, ex.getMessage(), ex);
                    ex.printStackTrace();

                } finally {
                    try {
                        if (getCount.incrementAndGet()==8){
                            if (subscribe != null) {
                                subscribe.onGetComplete();
                                getCount.set(0);
                            }
                        }
                        if (bufReader != null) {
                            bufReader.close();
                            bufReader = null;
                        }
                        if (isReader != null) {
                            isReader.close();
                            isReader = null;
                        }
                        if (httpConn != null) {
                            httpConn.disconnect();
                            httpConn = null;
                        }
                    } catch (IOException ex) {
                    }

                }
            }

        };  //end of thread
        sendHttpRequestThread.start();
    }

    public static interface DoneGetListener {
        void onGetComplete();
    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException {

        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";

        while((line = bufferedReader.readLine()) != null){
            result += line;
        }

        /* Close Stream */
        if(null!=inputStream){
            inputStream.close();
        }

        return result;
    }

}