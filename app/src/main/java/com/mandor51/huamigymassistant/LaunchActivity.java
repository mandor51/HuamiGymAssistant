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

import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LaunchActivity extends AppCompatActivity {

    TextView textView;
    TextView textView2;
    TextView textView3;
    SensorManager sensorManager;
    SensorEventListener heartRateSensorListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);

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

                int status = event.accuracy;
                String accurancy;

                switch (status){
                    case SensorManager.SENSOR_STATUS_NO_CONTACT:
                        accurancy = "NO CONTACT";
                        break;
                    case SensorManager.SENSOR_STATUS_UNRELIABLE:
                        accurancy = "UNRELIABLE";
                        break;
                    case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
                        accurancy = "LOW";
                        break;
                    case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                        accurancy = "MEDIUM";
                        break;
                    case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                        accurancy = "HIGH";
                        break;
                    default:
                        accurancy = "" + status;
                }


                Log.d("newnew", "" + event.accuracy);
                Log.d("newnew", "" + event.timestamp);
                textView.setText("" + event.values[0]);
                textView2.setText(accurancy);

                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(TimeUnit.MILLISECONDS.convert(event.timestamp, TimeUnit.NANOSECONDS));
                textView3.setText(sf.format(date));
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
