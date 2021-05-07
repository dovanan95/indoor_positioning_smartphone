package com.example.wi_fi_scanning;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
    public float x_gyro, y_gyro, z_gyro;

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
        ScanResultText.setText("Is Recording...");
        Timestamp timestamp = new Timestamp(datetime);
        ScanResultList = wifiManager.getScanResults();
        if (ScanResultList != null) {
            ++mRSSICount;
            Covid covid = new Covid();
            if(MODE==2)
            {
                covid.DBHelper_Mode_Sensor(mEditTextLocation.getText().toString(),timestamp.getTime(),IMEI,
                        x,y,z,x_lin_acc,y_lin_acc,z_lin_acc,x_ori,y_ori,z_ori,x_grav,y_grav,z_grav,x_magnet,y_magnet,
                        z_magnet,x_gyro,y_gyro,z_gyro);
            }
            for (int i = 0; i < ScanResultList.size(); i++) {
                ScanResult result = ScanResultList.get(i);
                Float RSSI = new Float(result.level);

                aSwitch = (Switch) findViewById(R.id.app_bar_switch);
                if(MODE==3)
                {
                    covid.DBHelper(result.BSSID, result.SSID, RSSI, mEditTextLocation.getText().toString(),
                            timestamp.getTime(), x, y, z, x_lin_acc, y_lin_acc, z_lin_acc, x_ori, y_ori,
                            z_ori, x_grav, y_grav, z_grav, x_magnet, y_magnet, z_magnet,
                            x_gyro, y_gyro, z_gyro, IMEI);
                }
                else if(MODE==1)
                {
                    covid.DBHelper_Mode_WIFI(result.BSSID, result.SSID, RSSI, mEditTextLocation.getText().toString(),
                            timestamp.getTime(), IMEI);
                }
            }
        } else if (ScanResultList == null) {
            Toast.makeText(this, "Data unavailable!", Toast.LENGTH_LONG).show();
            ScanResultText.append("No value detected. Try again!");
        }
        if (mRSSICount < 12)
        {
            if(aSwitch.isChecked()) {
                wifiManager.startScan();
                mRSSICount = 0;
            }
            else
            {
                ScanResultText.setText("Finished");
            }
        }
        //ScanResultText.setText("Finished");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestRuntimePermission();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAcceleration = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mOrientation = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        //sensorAll = sensorManager.getDefaultSensor(Sensor.TYPE_ALL);

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
            else if(event.sensor.getType()==Sensor.TYPE_GYROSCOPE)
            {
                x_gyro = event.values[0];
                y_gyro = event.values[1];
                z_gyro = event.values[2];
            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };
    String[] listItem;
    public int MODE = 1;
    public void onOptionClick(View view){
        listItem = new  String[]{"Record only Wifi", "Record only Sensor data", "Record All"};
        AlertDialog.Builder mbuilder = new AlertDialog.Builder(MainActivity.this);
        mbuilder.setTitle("Choose Record Mode!");
        mbuilder.setSingleChoiceItems(listItem, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(listItem[which]=="Record only Wifi")
                {
                    MODE=1;
                    ScanResultText.setText("Mode is:"+MODE);
                }
                else if(listItem[which]=="Record only Sensor data")
                {
                    MODE=2;
                    ScanResultText.setText("Mode is:"+MODE);
                }
                else if(listItem[which]=="Record All")
                {
                    MODE=3;
                    ScanResultText.setText("Mode is:"+MODE);
                }
            }
        });
        AlertDialog mDialog = mbuilder.create();
        mDialog.show();
    }

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
                sensorManager.registerListener(getmSensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                        SensorManager.SENSOR_DELAY_NORMAL);
                sensorManager.registerListener(getmSensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
                        SensorManager.SENSOR_DELAY_NORMAL);
                sensorManager.registerListener(getmSensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
                        SensorManager.SENSOR_DELAY_NORMAL);
                sensorManager.registerListener(getmSensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                        SensorManager.SENSOR_DELAY_NORMAL);

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

