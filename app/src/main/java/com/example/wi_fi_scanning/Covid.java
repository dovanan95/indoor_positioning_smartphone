package com.example.wi_fi_scanning;

import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

}
/*
public class ConnectMySql extends AsyncTask<String, Void, String>{
    private static final String url = "jdbc://us-cdbr-east-03.cleardb.com:3306/heroku_0aefc07ce57e397";
    private static final String user = "b001be2b5d7012";
    private static final String password = "e795d093";
    @Override
    protected void onPreExecute(){
        super.onPreExecute();
    }
    @Override
    protected String doInBackground(String...param){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, user, password);
            return null;

        }catch (Exception e){
            return null;

        }

    }
    @Override
    protected void onPostExecute(){
        super.onPostExecute();
    }
}*/


