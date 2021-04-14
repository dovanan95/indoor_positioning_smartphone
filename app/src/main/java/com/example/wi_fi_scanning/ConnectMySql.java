package com.example.wi_fi_scanning;

import android.os.AsyncTask;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ConnectMySql extends AsyncTask<String, Void, String> {
    private static final String url = "jdbc:mysql://us-cdbr-east-03.cleardb.com:3306/heroku_0aefc07ce57e397";
    private static final String user = "b001be2b5d7012";
    private static final String password = "e795d093";
    Connection con = null;
    PreparedStatement pst = null;
    @Override
    protected void onPreExecute(){
        super.onPreExecute();
    }
    @Override
    protected String doInBackground(String...params){
        try{
            String sqlMesure = "insert into idls_api_measurement(SSID, RSSI, BSSID, Location_ID, Time_stamp) values (?,?,?,?,?)";
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(url, user, password);
            pst = con.prepareStatement(sqlMesure);
            //st.executeUpdate("insert into table A values (?,?,?,?)");
            pst.setString(1, params[0]);
            pst.setFloat(2, Float.parseFloat(params[1]));
            pst.setString(3, params[2]);
            pst.setString(4, params[3]);
            pst.setString(5, params[4]);
            return null;

        }catch (Exception e){
            e.printStackTrace();
            return e.toString();
        }
        finally {
            if(con != null)
            {
                try {
                    con.close();
                }
                catch (SQLException e)
                {

                }
            }
            if(pst != null)
            {
                try {
                    pst.close();
                }
                catch (SQLException e)
                {

                }
            }
        }


    }
    @Override
    protected void onPostExecute(String result){
        super.onPostExecute(result);
    }
}
