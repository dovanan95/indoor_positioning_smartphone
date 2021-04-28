package com.example.wi_fi_scanning;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MainActivity extends Covid {
    private TextView ScanResultText;
    private WifiManager wifiManager;
    private EditText mEditTextLocation;
    private List<ScanResult> ScanResultList;
    boolean isPermitted = false;
    final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private Map<String, Integer> wifiList = new TreeMap<>();
    int mRSSICount = 0;

    private Switch aSwitch;
    String IMEI;

    public SensorEventListener mSensorListener;
    public SensorManager sensorManager;
    public List<Sensor> listSensor;
    public Sensor mAcceleration;
    public Sensor mOrientation;
    public Sensor sensorAll;
    public float x, y, z, x_lin_acc, y_lin_acc, z_lin_acc;
    public float x_ori, y_ori, z_ori;
    public float x_grav, y_grav, z_grav;
    public float x_magnet, y_magnet, z_magnet;

    // BroadcastReceiver 정의
    // 여기서는 이전 예제에서처럼 별도의 Java class 파일로 만들지 않았는데, 어떻게 하든 상관 없음
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                getWifiInfo();
            }
        }
    };

    // wifi scan 결과를 얻어서 UI의 TextView에 표시하는 기능 수행
    public void getWifiInfo() {
        //Do Van An's development

        IMEI = com.example.wi_fi_scanning.IMEI.get_device_id(this);

        Date currentTime = Calendar.getInstance().getTime();
        long datetime = currentTime.getTime();
        Timestamp timestamp = new Timestamp(datetime);
        ScanResultText.setText("Is Recording...");
        ScanResultList = wifiManager.getScanResults();

        if (ScanResultList != null) {
            ++mRSSICount;
            for (int i = 0; i < ScanResultList.size(); i++) {
                ScanResult result = ScanResultList.get(i);
                /*
                ScanResultText.append("Start:"
                        + "Date_Time: " + currentTime + " || " + "Time stamp: " + timestamp.getTime() + "||"
                        + "Location code: " + mEditTextLocation.getText().toString()
                        + "||" + " BSSID: " + result.BSSID + "||" + " SSID: "
                        + result.SSID + "||" + " RSSI: " + result.level + "\n"
                        + "Acceleration: " + x + "; " + y + "; " + z + "\n"
                        + " 8===============D -----------End\n");*/
                Float RSSI = new Float(result.level);
                Covid covid = new Covid();
                aSwitch = (Switch) findViewById(R.id.app_bar_switch);
                if(aSwitch.isChecked()){
                    covid.DBHelper(result.BSSID, result.SSID, RSSI, mEditTextLocation.getText().toString(),
                            timestamp.getTime(), x, y, z, x_lin_acc, y_lin_acc, z_lin_acc, x_ori, y_ori,
                            z_ori, x_grav, y_grav, z_grav, x_magnet, y_magnet, z_magnet, IMEI);
                }
            }
        } else if (ScanResultList == null) {
            Toast.makeText(this, "Data unavailable!", Toast.LENGTH_LONG).show();
            ScanResultText.append("No value detected. Try again!");
        }
        if (mRSSICount < 12)
        {
            wifiManager.startScan();
            mRSSICount = 0;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestRuntimePermission();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAcceleration = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mOrientation = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        sensorAll = sensorManager.getDefaultSensor(Sensor.TYPE_ALL);

        ScanResultText = (TextView) findViewById((R.id.result));
        mEditTextLocation = (EditText) findViewById(R.id.activit_main_location_edittext);
        ScanResultText.setMovementMethod(new ScrollingMovementMethod());
        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        if (wifiManager.isWifiEnabled() == false) {
            wifiManager.setWifiEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        try {
            super.onPause();
            unregisterReceiver(mReceiver);
            sensorManager.unregisterListener(getmSensorListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        try {
            super.onDestroy();
            unregisterReceiver(mReceiver);
            sensorManager.unregisterListener(getmSensorListener);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private SensorEventListener getmSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                x = event.values[0];
                y = event.values[1];
                z = event.values[2];
            } else if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
                x_lin_acc = event.values[0];
                y_lin_acc = event.values[1];
                z_lin_acc = event.values[2];
            } else if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
                x_grav = event.values[0];
                y_grav = event.values[1];
                z_grav = event.values[2];
            } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                x_magnet = event.values[0];
                y_magnet = event.values[1];
                z_magnet = event.values[2];
            } else if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
                x_ori = event.values[0];
                y_ori = event.values[1];
                z_ori = event.values[2];
            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    public void onClick(View view) {
        if (view.getId() == R.id.start) {
            Toast.makeText(this, "Wi-Fi scanning start", Toast.LENGTH_LONG).show();
            if (isPermitted) {
                //mRSSICount=0;
                wifiManager.startScan();
                IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
                registerReceiver(mReceiver, filter);

                sensorManager.registerListener(getmSensorListener, mAcceleration, SensorManager.SENSOR_DELAY_NORMAL);
                sensorManager.registerListener(getmSensorListener, mOrientation, SensorManager.SENSOR_DELAY_NORMAL);
                sensorManager.registerListener(getmSensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL);
                sensorManager.registerListener(getmSensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_NORMAL);
                sensorManager.registerListener(getmSensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY), SensorManager.SENSOR_DELAY_NORMAL);

                //Tel.listen(myPhoneListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
                //IMEI = com.example.wi_fi_scanning.IMEI.get_device_id(this);
            } else {
                Toast.makeText(getApplicationContext(), "cannot access", Toast.LENGTH_LONG).show();
            }
        } else if (view.getId() == R.id.stop) {
            /*
            DatabaseReference DBHelper_2 = null;
            DBHelper_2 = FirebaseDatabase.getInstance().getReference().child("Stop");
            HashMap stop_record = new HashMap();
            stop_record.put("stop", true);
            DBHelper_2.push().setValue(stop_record);*/

            unregisterReceiver(mReceiver);
            sensorManager.unregisterListener(getmSensorListener);
            //mRSSICount=12;
            ScanResultText.setText("Stop");
            //Toast.makeText(this, "Stop", Toast.LENGTH_LONG).show();
        }
    }

    //허용하시겠습니까? 퍼미션 창 뜨게하는 것!
    private void requestRuntimePermission() {
        //*******************************************************************
        // Runtime permission check
        //*******************************************************************
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        } else {
            // ACCESS_FINE_LOCATION 권한이 있는 것
            isPermitted = true;
        }
    }
}

