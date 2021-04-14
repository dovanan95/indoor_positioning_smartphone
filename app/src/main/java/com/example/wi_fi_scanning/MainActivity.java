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
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Comparator;
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

    Wifi_Measurement wifi_measurement = new Wifi_Measurement();
    DatabaseReference DBHelper;

    public SensorEventListener mSensorListener;
    public SensorManager sensorManager;
    public List<Sensor> listSensor;
    public Sensor mAcceleration;
    public Sensor mOrientation;
    public Sensor sensorAll;
    public float x,y,z, x_lin_acc, y_lin_acc, z_lin_acc;
    public float x_ori, y_ori, z_ori;

    // BroadcastReceiver 정의
    // 여기서는 이전 예제에서처럼 별도의 Java class 파일로 만들지 않았는데, 어떻게 하든 상관 없음
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
            {
                getWifiInfo();
            }
        }
    };
    // wifi scan 결과를 얻어서 UI의 TextView에 표시하는 기능 수행
    public void getWifiInfo()
    {
        //Do Van An's development

        Date currentTime = Calendar.getInstance().getTime();
        long datetime = currentTime.getTime();
        Timestamp timestamp = new Timestamp(datetime);
        ScanResultList = wifiManager.getScanResults();
        if(ScanResultList != null)
        {
            //Toast.makeText(this, "Scan finished", Toast.LENGTH_LONG).show();
            for (int i =0; i < ScanResultList.size(); i++)
            {
                ScanResult result = ScanResultList.get(i);
                ScanResultText.append("Start:"
                        +"Date_Time: " + currentTime + " || " + "Time stamp: "+ timestamp.getTime() + "||"
                        + "Location code: " + mEditTextLocation.getText().toString()
                        + "||" + " BSSID: " + result.BSSID + "||" +" SSID: "
                        + result.SSID + "||" + " RSSI: "+ result.level + "\n"
                        + "Acceleration: " + x + "; " + y+ "; " + z + "\n"
                        + "Orientation: " + x_ori + "; " +y_ori+"; " +z_ori+ "\n"
                        + "linear acceleration: " + x_lin_acc+ " ; " + y_lin_acc +" ; "+ z_lin_acc +"\n"
                        +" 8===============D -----------End\n");

                DBHelper = FirebaseDatabase.getInstance().getReference().child("wifi_measurement");
                Float RSSI = new Float(result.level);
                wifi_measurement.setBSSID(result.BSSID);
                wifi_measurement.setRSSI(RSSI);
                wifi_measurement.setSSID(result.SSID);
                wifi_measurement.setLocation_ID(mEditTextLocation.getText().toString());
                wifi_measurement.setTime_stamp(timestamp.getTime());
                DBHelper.push().setValue(wifi_measurement);
                /*
                ConnectMySql connectMySql = new ConnectMySql();
                String RSSI = String.valueOf(result.level);
                String tstmp = String.valueOf(timestamp.getTime());
                connectMySql.execute(result.SSID, RSSI, result.BSSID,
                        mEditTextLocation.getText().toString(), tstmp);*/
            }
            ++mRSSICount;
            //ScanResultText.setText("");
            if(mRSSICount<12)
            {
                wifiManager.startScan();
                mRSSICount = 0;
            }
            else
            {
                unregisterReceiver(mReceiver);
            }
        }
        else if(ScanResultList == null)
        {

            Toast.makeText(this, "Data unavailable!", Toast.LENGTH_LONG).show();
            ScanResultText.append("No value detected. Try again!");
        }

    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.mesure:
                Toast.makeText(getApplicationContext(),"measure",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.test:
                Toast.makeText(getApplicationContext(),"Test",Toast.LENGTH_LONG).show();
                Intent intent_2 = new Intent(this, Test_Covid_19.class);
                startActivity(intent_2);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestRuntimePermission();
        //onRequestPermissionResult();
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mAcceleration = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mOrientation = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        sensorAll = sensorManager.getDefaultSensor(Sensor.TYPE_ALL);

        ScanResultText = (TextView)findViewById((R.id.result));
        mEditTextLocation = (EditText)findViewById(R.id.activit_main_location_edittext);
        ScanResultText.setMovementMethod(new ScrollingMovementMethod());
        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        if(wifiManager.isWifiEnabled() == false)
        {
            wifiManager.setWifiEnabled(true);
        }
    }

    @Override
    protected void onResume() {

        super.onResume();
        //IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        //registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onPause(){
        try {
            super.onPause();
            unregisterReceiver(mReceiver);
            sensorManager.unregisterListener(getmSensorListener);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    @Override
    protected void onDestroy(){
        try {
            super.onDestroy();
            unregisterReceiver(mReceiver);
            sensorManager.unregisterListener(getmSensorListener);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    private SensorEventListener getmSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER)
            {
                x= event.values[0];
                y= event.values[1];
                z= event.values[2];
            }
            else if(event.sensor.getType()==Sensor.TYPE_LINEAR_ACCELERATION)
            {
                x_lin_acc=event.values[0];
                y_lin_acc=event.values[1];
                z_lin_acc=event.values[2];
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };
    private SensorEventListener getOrientation = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            x_ori = event.values[0];
            y_ori = event.values[1];
            z_ori = event.values[2];
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };


    public void onClick(View view){
        if(view.getId()== R.id.start)
        {
            Toast.makeText(this, "Wi-Fi scanning start", Toast.LENGTH_LONG).show();
            if(isPermitted)
            {
                //mRSSICount=0;
                wifiManager.startScan();
                IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
                registerReceiver(mReceiver, filter);

                sensorManager.registerListener(getmSensorListener, mAcceleration, SensorManager.SENSOR_DELAY_NORMAL);
                sensorManager.registerListener(getOrientation, mOrientation, SensorManager.SENSOR_DELAY_NORMAL);
                sensorManager.registerListener(getmSensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_NORMAL);
            }
            else
            {
                Toast.makeText(getApplicationContext(), "cannot access", Toast.LENGTH_LONG).show();
            }
        }
        else if(view.getId()==R.id.stop)
        {
            unregisterReceiver(mReceiver);
            sensorManager.unregisterListener(getmSensorListener);
            sensorManager.unregisterListener(getOrientation);
            //mRSSICount=12;
            ScanResultText.setText("");
            Toast.makeText(this, "Stop", Toast.LENGTH_LONG).show();
        }
    }
    //허용하시겠습니까? 퍼미션 창 뜨게하는 것!
    private void  requestRuntimePermission()
    {
        //*******************************************************************
        // Runtime permission check
        //*******************************************************************
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION))
            {

            }
            else
            {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        }
        else
        {
            // ACCESS_FINE_LOCATION 권한이 있는 것
            isPermitted = true;
        }
    }

    public void onRequestPermissionResult(int RequestCode, String Permissions[], int[] grantResults)
    {
        switch (RequestCode)
        {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
            {
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // ACCESS_FINE_LOCATION 권한을 얻음
                    isPermitted = true;
                }
                else
                {
                    isPermitted = false;
                }
                return;
            }
        }
    }
}

class ValueComparator implements Comparator<String>
        {
            Map<String, Integer> base;
            public ValueComparator(Map<String,Integer> base)
            {
                this.base = base;
            }

            @Override
            public int compare(String a, String b)
            {
                if(base.get(a)>base.get(b))
                {
                    return -1;
                }
                else
                {
                    return 1;
                }
            }
        }