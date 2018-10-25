package com.pastweather.pastweather;

import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WeatherActivity extends AppCompatActivity implements WeatherApplication.DoneGetListener {
    private Location lastLocation;
    private boolean locationWarning = false;
    private static final String key = "92d16368d0abe79a5842b05e27c9e606";
    public Button refreshLocation;
    //@BindView(R.id.butRefresh)
    public FrameLayout mainFrame;
    private double defaultLat = 33.6363, defaultLng = -117.6767;
    private WeatherApplication app;
    private DailyWeather[] listDaily = new DailyWeather[8];
    private DailyListAdapter adapter = null;
    private RecyclerView recyclerView;
    @BindView(R.id.tilLat)
    TextInputEditText tilLat;
    @BindView(R.id.tilLng)
    TextInputEditText tilLng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (WeatherApplication) getApplication();
        setContentView(R.layout.activity_weather);
        try {
            ButterKnife.bind(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FrameLayout mainFrame = (FrameLayout)findViewById(R.id.mainFrame);
        LinearLayout mainLayout = (LinearLayout)findViewById(R.id.mainLayout);
        refreshLocation = findViewById(R.id.butRefresh);
        recyclerView = findViewById(R.id.weather_days);

        try {
            lastLocation = app.getLastLocation();
        } catch (Exception e) {
            locationWarning = true;
            Toast.makeText(this, "Please enable the Location service to get GPS location",
                    Toast.LENGTH_LONG).show();
        }
        app.setDoneGetListener(this);
        refreshLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshData();
            }
        });
    }

    private void refreshData() {
        for(int i=0; i<8; i++){
            listDaily[i] = null;
        }
        retrieveWeatherData();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }
    private void retrieveWeatherData() {
        double lat, lng;
        if (lastLocation == null) {
            lat = defaultLat;
            lng = defaultLng;
        } else {
            lat = lastLocation.getLatitude();
            lng = lastLocation.getLongitude();
        }
        double lat1=0.0, lng1=0.0;
        String strText = tilLat.getText().toString();
        lat1 = Double.parseDouble(strText);
        if (lat1 != lat && lat1 != 0.0) {
            lat = lat1;
        }
        strText = tilLng.getText().toString();
        lng1 = Double.parseDouble(strText);
        if (lng1 != lng && lng1 != 0.0) {
            lng = lng1;
        }
        String queryParams = null;
        String queryResult = null;
        for (int i= 0; i< 8; i++) {
            queryParams = app.composeQueryParams(i, lat, lng);
            app.startSendHttpsRequestThread(queryParams, i, listDaily);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_weather, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onGetComplete() {
        //Now it is time to
        this.runOnUiThread(new Runnable() {
            public void run() {
                adapter = new DailyListAdapter(listDaily);
                recyclerView.setLayoutManager(new LinearLayoutManager(WeatherActivity.this));
                recyclerView.setAdapter(adapter);
            }
        });
    }
}
