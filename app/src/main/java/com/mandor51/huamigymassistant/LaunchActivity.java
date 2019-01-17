package com.mandor51.huamigymassistant;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        //TODO: Sync data start
        startActivity(new Intent(this, WorkoutPlanListActivity.class));
        finish();
    }
}
