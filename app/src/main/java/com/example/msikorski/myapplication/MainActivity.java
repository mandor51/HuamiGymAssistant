package com.example.msikorski.myapplication;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.wearable.view.GridViewPager;
import android.support.wearable.view.drawer.WearableActionDrawer;
import android.support.wearable.view.drawer.WearableDrawerLayout;
import android.support.wearable.view.drawer.WearableNavigationDrawer;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.huami.watch.transport.DataBundle;
import com.huami.watch.transport.DataTransportResult;
import com.huami.watch.transport.TransportDataItem;
import com.huami.watch.util.Log;
import com.kieronquinn.library.amazfitcommunication.Transporter;
import com.kieronquinn.library.amazfitcommunication.TransporterClassic;
import com.kieronquinn.library.amazfitcommunication.internet.LocalHTTPRequest;
import com.kieronquinn.library.amazfitcommunication.internet.LocalHTTPResponse;
import com.kieronquinn.library.amazfitcommunication.internet.LocalURLConnection;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements WearableActionDrawer.OnMenuItemClickListener{

    private TransporterClassic transporter;

    private WearableDrawerLayout mWearableDrawerLayout;
    private WearableNavigationDrawer mWearableNavigationDrawer;
    private WearableActionDrawer mWearableActionDrawer;

    private AppListAdapter appListCenterAdapter;
    SnapHelper snapHelperCenter;
    RecyclerView recyclerView;

    private int i = 0;

    Context mContext;

    SensorManager sensorManager;
    SensorEventListener gyroscopeSensorListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.snap_helper);

        mContext = this.getApplicationContext();

        recyclerView = findViewById(R.id.rec1);

        //LinearSnapHelper snapHelper = new LinearSnapHelper();

        LinearLayoutManager layoutManagerCenter = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManagerCenter);
        appListCenterAdapter = new AppListAdapter();


        recyclerView.setAdapter(appListCenterAdapter);
        snapHelperCenter = new LinearSnapHelper();/*{
                @Override
                public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
                    View centerView = findSnapView(layoutManager);
                    if (centerView == null)
                        return RecyclerView.NO_POSITION;

                    int position = layoutManager.getPosition(centerView);
                    int targetPosition = -1;
                    if (layoutManager.canScrollHorizontally()) {
                        if (velocityX < 0) {
                            targetPosition = position - 1;
                        } else {
                            targetPosition = position + 1;
                        }
                    }

                    if (layoutManager.canScrollVertically()) {
                        if (velocityY < 0) {
                            targetPosition = position - 1;
                        } else {
                            targetPosition = position + 1;
                        }
                    }

                    final int firstItem = 0;
                    final int lastItem = layoutManager.getItemCount() - 1;
                    targetPosition = Math.min(lastItem, Math.max(targetPosition, firstItem));
                    return targetPosition;
                }
        };*/
        snapHelperCenter.attachToRecyclerView(recyclerView);

        /* {

            private OrientationHelper mVerticalHelper, mHorizontalHelper;

            @Override
            public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager,
                                                      @NonNull View targetView) {
                Log.d("newnewnew", "calculateDistanceToFinalSnap");
                int[] out = new int[2];

                if (layoutManager.canScrollHorizontally()) {
                    out[0] = distanceToStart(targetView, getHorizontalHelper(layoutManager));
                } else {
                    out[0] = 0;
                }

                if (layoutManager.canScrollVertically()) {
                    out[1] = distanceToStart(targetView, getVerticalHelper(layoutManager));
                } else {
                    out[1] = 0;
                }
                return out;
            }

            @Override
            public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
                Log.d("newnewnew", "findTargetSnapPosition");
                View centerView = findSnapView(layoutManager);
                if (centerView == null)
                    return RecyclerView.NO_POSITION;

                int position = layoutManager.getPosition(centerView);
                int targetPosition = -1;
                if (layoutManager.canScrollHorizontally()) {
                    if (velocityX < 0) {
                        targetPosition = position - 1;
                    } else {
                        targetPosition = position + 1;
                    }
                }

                if (layoutManager.canScrollVertically()) {
                    if (velocityY < 0) {
                        targetPosition = position - 1;
                    } else {
                        targetPosition = position + 1;
                    }
                }

                final int firstItem = 0;
                final int lastItem = layoutManager.getItemCount() - 1;
                targetPosition = Math.min(lastItem, Math.max(targetPosition, firstItem));
                Log.d("newnewnew", String.valueOf(targetPosition));
                return targetPosition;
            }

            @Override
            public View findSnapView(RecyclerView.LayoutManager layoutManager) {

                Log.d("newnewnew", "findSnapView");
                if (layoutManager instanceof LinearLayoutManager) {

                    if (layoutManager.canScrollHorizontally()) {
                        return getStartView(layoutManager, getHorizontalHelper(layoutManager));
                    } else {
                        return getStartView(layoutManager, getVerticalHelper(layoutManager));
                    }
                }

                return super.findSnapView(layoutManager);
            }

            private View getStartView(RecyclerView.LayoutManager layoutManager,
                                      OrientationHelper helper) {

                Log.d("newnewnew", "getStartView");
                if (layoutManager instanceof LinearLayoutManager) {
                    int firstChild = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();

                    boolean isLastItem = ((LinearLayoutManager) layoutManager)
                            .findLastCompletelyVisibleItemPosition()
                            == layoutManager.getItemCount() - 1;

                    if (firstChild == RecyclerView.NO_POSITION || isLastItem) {
                        return null;
                    }

                    View child = layoutManager.findViewByPosition(firstChild);

                    if (helper.getDecoratedEnd(child) >= helper.getDecoratedMeasurement(child) / 2
                            && helper.getDecoratedEnd(child) > 0) {
                        return child;
                    } else {
                        if (((LinearLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition()
                                == layoutManager.getItemCount() - 1) {
                            return null;
                        } else {
                            return layoutManager.findViewByPosition(firstChild + 1);
                        }
                    }
                }

                return super.findSnapView(layoutManager);
            }

            private int distanceToStart(View targetView, OrientationHelper helper) {
                Log.d("newnewnew", "distanceToStart");
                return helper.getDecoratedStart(targetView) - helper.getStartAfterPadding();
            }

            private OrientationHelper getVerticalHelper(RecyclerView.LayoutManager layoutManager) {
                Log.d("newnewnew", "getVerticalHelper");
                if (mVerticalHelper == null) {
                    mVerticalHelper = OrientationHelper.createVerticalHelper(layoutManager);
                }
                return mVerticalHelper;
            }

            private OrientationHelper getHorizontalHelper(RecyclerView.LayoutManager layoutManager) {
                Log.d("newnewnew", "getHorizonalHelper");
                if (mHorizontalHelper == null) {
                    mHorizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager);
                }
                return mHorizontalHelper;
            }
        };*/
        //snapHelper.attachToRecyclerView(recyclerView);

        Log.d("brand=", Build.BRAND.toString() + " device=" + Build.DEVICE.toString() + " model=" + Build.MODEL + " display=" + Build.DISPLAY.toString() + " product=" + Build.PRODUCT.toString());

        //final GridViewPager mGridPager = findViewById(R.id.pager);
        //mGridPager.setAdapter(new SampleGridPagerAdapter(this, getFragmentManager()));


        //mWearableDrawerLayout = findViewById(R.id.drawer_layout);
        //mWearableNavigationDrawer = findViewById(R.id.top_navigation_drawer);
        //mWearableNavigationDrawer.setAdapter(new NavigationAdapter(this));

        //mWearableActionDrawer = findViewById(R.id.bottom_action_drawer);

        //mWearableActionDrawer.setOnMenuItemClickListener(this);

        //Create the transporter **WARNING** The second parameter MUST be the same on both your watch and phone companion apps!
        //Please change the module name to something unique, but keep it the same for both apps!
        transporter = (TransporterClassic) Transporter.get(this, "example_module");
        //Add a channel listener to listen for ready event
        transporter.addChannelListener(new Transporter.ChannelListener() {
            @Override
            public void onChannelChanged(boolean ready) {
                //Transporter is ready if ready is true, send an action now. This will **NOT** work before the transporter is ready!
                //You can change the action to whatever you want, there's also an option for a data bundle to be added (see below)
                if(ready)transporter.send("hello_world!");
            }
        });
        transporter.addDataListener(new Transporter.DataListener() {
            @Override
            public void onDataReceived(TransportDataItem transportDataItem) {
                Log.d("TransporterExample", "Item received action: " + transportDataItem.getAction());
                setText(transportDataItem.getAction().toString());
                if(transportDataItem.getAction().equals("hello_world")) {
                    DataBundle receivedData = transportDataItem.getData();
                    //Do whatever with your action & data. You can send data back in the same way using the same transporter
                }
            }
        });
        transporter.connectTransportService();

        /*Button button = findViewById(R.id.sampleButton);

        button.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v){
                transporter.send("" + i);
                i++;
            }
        });*/

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        Sensor lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if(lightSensor == null) {
            Log.d("newnew", "Light sensor not available!");
        }

        Sensor gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if(gyroSensor == null) {
            Log.d("newnew", "Gyro sensor not available!");
        }

        Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(accelerometerSensor == null) {
            Log.d("newnew", "Accelerometer not available!");
        }

        /*gyroscopeSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                Log.d("newnew", "On gyro sensor changed! x=" + sensorEvent.values[0]);

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
                Log.d("newnew", "On gyro accurancy changed!");
            }
        };

        // Register the listener
        sensorManager.registerListener(gyroscopeSensorListener, gyroSensor, SensorManager.SENSOR_DELAY_NORMAL);*/

    }

    class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.ViewHolder> {


        @Override
        public AppListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_app, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(AppListAdapter.ViewHolder holder, int position) {
            holder.onBind(position);
            Log.d("newnew", "onBind " + position);
        }

        @Override
        public int getItemCount() {
            return 200;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView textViewName;

            public ViewHolder(View itemView) {
                super(itemView);
                textViewName = itemView.findViewById(R.id.textSm);
            }

            public void onBind(final int position) {
                    textViewName.setText("" + position*2.5);
            }
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        View snapView = snapHelperCenter.findSnapView(recyclerView.getLayoutManager());
        int snapPosition = recyclerView.getLayoutManager().getPosition(snapView);

        Log.d("newnew", "SnapPostion=" + snapPosition*2.5);
    }

    @Override
    public void onStop(){
        super.onStop();
        transporter.removeAllChannelListeners();
        transporter.removeAllDataListeners();
        transporter.disconnectTransportService();

        //sensorManager.unregisterListener(gyroscopeSensorListener);
    }

    private void sendActionWithData(){
        //Create a bundle of data
        DataBundle dataBundle = new DataBundle();
        //Key value pair
        dataBundle.putString("hello", "world");
        //Send action
        transporter.send("hello_world_data", dataBundle);
    }

    private void sendActionWithDataAndCallback(){
        //Create a bundle of data
        DataBundle dataBundle = new DataBundle();
        //Key value pair
        dataBundle.putString("hello", "world");
        //Send action with a callback. This also works without the data bundle
        transporter.send("hello_world_data", dataBundle, new Transporter.DataSendResultCallback() {
            @Override
            public void onResultBack(DataTransportResult dataTransportResult) {
                Log.d("TransporterExample", "onResultBack result code " + dataTransportResult.getResultCode());
            }
        });
    }

    private void setText(final String text) {
        //Has to be run on the UI thread, a lot of receiver stuff isn't
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //TextView textView = findViewById(R.id.sampleText);
                //textView.setText(text);
            }
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }


}
