package com.golriz.locationtracker;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.golriz.gpstracker.LocationTracker;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        findViewById(R.id.btnStop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocationTracker locationTracker = new LocationTracker(getApplicationContext());
                locationTracker.stopTracking();
            }
        });

        findViewById(R.id.btnStart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocationTracker locationTracker = new LocationTracker(getApplicationContext());
                locationTracker.startTracking();

            }
        });
    }


}
