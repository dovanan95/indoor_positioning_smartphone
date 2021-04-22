package com.example.wi_fi_scanning;

import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

public class Covid extends AppCompatActivity {
    public static String myURL = "http://idls-server.herokuapp.com/idls_api/api/mes/mobile_handler";

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.mesure:
                Toast.makeText(getApplicationContext(), "measure", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.test:
                Toast.makeText(getApplicationContext(), "Test", Toast.LENGTH_LONG).show();
                Intent intent_2 = new Intent(this, Test_Covid_19.class);
                startActivity(intent_2);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static boolean CheckAPIConnection(String url, int timeout) {
        try {
            URL myUrl = new URL(url);
            URLConnection connection = myUrl.openConnection();
            connection.setConnectTimeout(timeout);
            connection.connect();
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public Void POST(String url, JSONObject obj) {
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;
        String result = "";
        if (CheckAPIConnection(url, 5) == true) {
            try {
                URL urll = new URL(url);
                urlConnection = (HttpURLConnection) urll.openConnection();
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setChunkedStreamingMode(0);
                OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                writer.write(obj.toString());
                writer.flush();

                int code = urlConnection.getResponseCode();
                if (code != 201) {
                    throw new IOException("Invalid response from server: " + code);
                }
                BufferedReader rd = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    Log.i("data", line);

                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            //code

        } else {
            Toast.makeText(getApplicationContext(), "URL Down", Toast.LENGTH_LONG).show();
        }
        return null;
    }

    public void DBHelper (String BSSID, String SSID, Float RSSI, String Loc_ID, long time_stmp,
                          Float x_acc, Float y_acc, Float z_acc, Float x_lnac, Float y_lnac, Float z_lnac,
                          Float x_ori, Float y_ori, Float z_ori, Float x_grav, Float y_grav, Float z_grav,
                          Float x_magnet, Float y_magnet, Float z_magnet)
    {
        DatabaseReference DBHelper = null;
        DBHelper = FirebaseDatabase.getInstance().getReference().child("data");
        Wifi_Measurement wifi_measurement = new Wifi_Measurement();
        wifi_measurement.setBSSID(BSSID);
        wifi_measurement.setSSID(SSID);
        wifi_measurement.setRSSI(RSSI);
        wifi_measurement.setLocation_ID(Loc_ID);
        wifi_measurement.setTime_stamp(time_stmp);

        HashMap Acce = new HashMap();
        Acce.put("x_acce", x_acc);
        Acce.put("y_acce", y_acc);
        Acce.put("z_acce", z_acc);
        HashMap Linear_Acceleration = new HashMap();
        Linear_Acceleration.put("x_lnac", x_lnac);
        Linear_Acceleration.put("y_lnac", y_lnac);
        Linear_Acceleration.put("z_lnac", z_lnac);
        HashMap Orient = new HashMap();
        Orient.put("x_ori", x_ori);
        Orient.put("y_ori", y_ori);
        Orient.put("z_ori", z_ori);
        HashMap Grav = new HashMap();
        Grav.put("x_grav", x_grav);
        Grav.put("y_grav", y_grav);
        Grav.put("z_grav", z_grav);
        HashMap Magnet = new HashMap();
        Magnet.put("x_magnet", x_magnet);
        Magnet.put("y_magnet", y_magnet);
        Magnet.put("z_magnet", z_magnet);
        HashMap Sens = new HashMap();
        Sens.put("Acceleration", Acce);
        Sens.put("Linear_Acceleration", Linear_Acceleration);
        Sens.put("Orientation", Orient);
        Sens.put("Gravity", Grav);
        Sens.put("Magnetic", Magnet);

        wifi_measurement.setSensors(Sens);
        DBHelper.push().setValue(wifi_measurement);

    }

}


