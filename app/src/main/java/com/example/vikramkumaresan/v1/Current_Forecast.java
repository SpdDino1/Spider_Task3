package com.example.vikramkumaresan.v1;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

//Downloads current weather data

public class Current_Forecast extends AsyncTask<Void,Void,String> {
    String city;
    Context ctx;

    public Current_Forecast(String City,Context context) {
        city=City;
        ctx = context;
    }

    @Override
    protected String doInBackground(Void... params) {
        String JSONString="";

        try {
            URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q="+city+"&APPID=55f6f16837937f215174b6d18fbec555");
            HttpURLConnection connect = (HttpURLConnection)url.openConnection();
            connect.setRequestMethod("GET");
            connect.setDoOutput(true);

            InputStream instream = connect.getInputStream();
            InputStreamReader reader = new InputStreamReader(instream);
            BufferedReader buffread = new BufferedReader(reader);

            JSONString="";
            String line = buffread.readLine();
            while (line!=null){
                JSONString+=line;
                line=buffread.readLine();
            }

            return JSONString;
        } catch (MalformedURLException e) {
            Log.d("TAG","Malformed URL = "+e.toString());
        } catch (IOException e) {
            Log.d("TAG","IO = "+e.toString());
            JSONString = "Wrong City";
        }
        return JSONString;
    }

    @Override
    protected void onPostExecute(String s) {
        if(s.equals("Wrong City")){
            Toast.makeText(ctx,"City Doesn't Exist",Toast.LENGTH_LONG).show();
            Loading_Screen.ctx.finish();
    }
        else {
            ArrayList<String> current_data=new ArrayList<>();
            String city;
            Double temp_min;
            Double temp_max;
            String sky;
            try {
                //Parsing JSON String
                JSONObject parent = new JSONObject(s);
                city = parent.getString("name");
                temp_min=parent.getJSONObject("main").getDouble("temp_min");
                temp_max=parent.getJSONObject("main").getDouble("temp_max");
                sky=parent.getJSONArray("weather").getJSONObject(0).getString("description");

                current_data.add(city);
                current_data.add(""+temp_max+" K");
                current_data.add(""+temp_min+" K");
                current_data.add(sky);
                MainActivity.current_forecast_update(current_data);

                //Database Updation Mech............
                DB_Handler obj = new DB_Handler(ctx,null,null,1);

                if(!obj.isCityExist(city.toUpperCase(),"Current_Weather")){ //Insert as new city
                    obj.insertCurrent(city.toUpperCase(),""+temp_max,""+temp_min,sky);
                }
                else {  //Replace row
                    obj.delCity(city.toUpperCase(),"Current_Weather");
                    obj.insertCurrent(city.toUpperCase(),""+temp_max,""+temp_min,sky);
                }
                //.....................End of mech


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
