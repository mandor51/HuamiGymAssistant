package com.mandor51.huamigymassistant;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import com.huami.watch.util.Log;

import java.util.List;

public class LaunchActivity extends AppCompatActivity {

    TextView textView;
    SensorManager sensorManager;
    SensorEventListener heartRateSensorListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        textView = findViewById(R.id.textView);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor sensor1 : sensors) {
            Log.d("Sensor list", sensor1.getName() + ": " + sensor1.getType());
        }

        Sensor heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        if(heartRateSensor == null) {
            Log.d("newnew", "Heart rate sensor not available!");
        }else{
            Log.d("newnew", "Heart rate sensor acquired!");
        }

        heartRateSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                Log.d("newnew", "" + event.values[0]);
                textView.setText("" + event.values[0]);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
                Log.d("newnew", "accurancy changed");
            }
        };

        // Register the listener
        if(sensorManager.registerListener(heartRateSensorListener, heartRateSensor, SensorManager.SENSOR_DELAY_FASTEST)){
            Log.d("newnew", "sensor registered");
        }else {
            Log.d("newnew", "sensor not registered");
        }

        //TODO: Sync data start
        //startActivity(new Intent(this, WorkoutPlanListActivity.class));
        //finish();
    }

    @Override
    public void onStop() {
        super.onStop();
        sensorManager.unregisterListener(heartRateSensorListener);
    }

    private void setText(final String text) {
        //Has to be run on the UI thread, a lot of receiver stuff isn't
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(text);
            }
        });
    }
}
