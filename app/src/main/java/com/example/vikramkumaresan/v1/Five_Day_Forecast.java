package com.example.vikramkumaresan.v1;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;


//Downloads 5 day weather forecast

public class Five_Day_Forecast extends AsyncTask<Void,Void,String> {
    String city;
    Context ctx;

    public Five_Day_Forecast(String City,Context context) {
        city=City;
        ctx=context;
    }

    @Override
    protected String doInBackground(Void... params) {
        String JSONString = "";
        try {
            URL url = new URL("http://api.openweathermap.org/data/2.5/forecast?q="+city+"&APPID=55f6f16837937f215174b6d18fbec555");
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
        } catch (ProtocolException e) {
            Log.d("TAG","Protocol Error = "+e.toString());
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
        else{
            try {
                ArrayList<ArrayList<String>> final_data = new ArrayList<>();
                ArrayList<String> data= new ArrayList<>(); //Date,Temp Min, Temp Max,Sky

                //Database Variables
                String City;
                String Date="";
                String Temp_Min="";
                String Temp_Max="";
                String Sky="";
                //..................

                JSONObject parent = new JSONObject(s);
                JSONArray parent_array = parent.getJSONArray("list");

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  //To compare dates
                Date reference = sdf.parse(parent_array.getJSONObject(0).getString("dt_txt").split(" ")[0]);

                data.add(parent_array.getJSONObject(0).getString("dt_txt").split(" ")[0]);
                Date+=parent_array.getJSONObject(0).getString("dt_txt").split(" ")[0]; //Db variable Updation
                data.add(""+parent_array.getJSONObject(0).getJSONObject("main").getDouble("temp_min"));
                Temp_Min+=""+parent_array.getJSONObject(0).getJSONObject("main").getDouble("temp_min"); //Db variable Updation
                data.add(""+parent_array.getJSONObject(0).getJSONObject("main").getDouble("temp_max"));
                Temp_Max+=""+parent_array.getJSONObject(0).getJSONObject("main").getDouble("temp_max"); //Db variable Updation
                data.add(parent_array.getJSONObject(0).getJSONArray("weather").getJSONObject(0).getString("description"));
                Sky+=parent_array.getJSONObject(0).getJSONArray("weather").getJSONObject(0).getString("description");   //Db variable Updation
                final_data.add(data);

                JSONObject element;
                String dt_txt;
                City=parent.getJSONObject("city").getString("name");


                for (int i = 1; i < parent_array.length(); i++) {
                    element = parent_array.getJSONObject(i);
                    dt_txt = element.getString("dt_txt").split(" ")[0];

                    if (sdf.parse(dt_txt).compareTo(reference) > 0) {
                        data= new ArrayList<>();
                        data.add(element.getString("dt_txt").split(" ")[0]);
                        data.add(""+element.getJSONObject("main").getDouble("temp_min"));
                        data.add(""+element.getJSONObject("main").getDouble("temp_max"));
                        data.add(element.getJSONArray("weather").getJSONObject(0).getString("description"));
                        final_data.add(data);

                        //Database Variable Updation
                        Date=Date+"+"+element.getString("dt_txt").split(" ")[0];
                        Temp_Min=Temp_Min+"+"+""+element.getJSONObject("main").getDouble("temp_min");
                        Temp_Max=Temp_Max+"+"+""+element.getJSONObject("main").getDouble("temp_max");
                        Sky=Sky+"+"+element.getJSONArray("weather").getJSONObject(0).getString("description");
                        //..........................

                        reference=sdf.parse(dt_txt);
                    }
                }

                MainActivity.fiveday_forecast_update(parent.getJSONObject("city").getString("name"),final_data);

                //Database Updation Mech
                DB_Handler obj = new DB_Handler(ctx,null,null,1);
                if(!obj.isCityExist(City.toUpperCase(),"Forecast_Weather")){ //Insert as new city
                    obj.insertForecast(City.toUpperCase(),Date,Temp_Max,Temp_Min,Sky);
                }
                else{   //Replace the data
                    obj.delCity(City.toUpperCase(),"Forecast_Weather");
                    obj.insertForecast(City.toUpperCase(),Date,Temp_Max,Temp_Min,Sky);
                    Log.d("TAG","Replaced!");
                }

            } catch (ParseException e) {
                Log.d("TAG","Date Error = "+e.toString());
            } catch (JSONException e) {
                Log.d("TAG","JSON Error = "+e.toString());
            }
        }
    }
}