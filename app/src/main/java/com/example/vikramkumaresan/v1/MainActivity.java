package com.example.vikramkumaresan.v1;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    static TextView city;
    static TextView temp_max;
    static TextView temp_min;
    static TextView sky;
    static Button future_forecast;

    static Context ctx;

    boolean isConnected;

    public static Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DB_Handler db_handler= new DB_Handler(this,null,null,1);
        ctx=this;
        
        isInternetConnected();  //internet Check

        city=(TextView)findViewById(R.id.city);
        temp_max=(TextView)findViewById(R.id.max_temp);
        temp_min=(TextView)findViewById(R.id.min_temp);
        sky=(TextView)findViewById(R.id.sky);
        future_forecast=(Button)findViewById(R.id.future_forecast);

    }

    public static void current_forecast_update(ArrayList<String> current_data){
        //To update the UI with current data
        city.setText(current_data.get(0));
        temp_max.setText(current_data.get(1));
        temp_min.setText(current_data.get(2));
        sky.setText(current_data.get(3));

        try{
            Loading_Screen.ctx.finish();
        }catch (Exception e){
        }
    }

    public static void fiveday_forecast_update(String name,ArrayList<ArrayList<String>> data_incoming){
        //To Launch new activity and display the forecast data
        Intent go = new Intent(ctx,Five_Day_Data.class);
        Five_Day_Data.city_name=name;
        Five_Day_Data.data=data_incoming;

        try {
            Loading_Screen.ctx.finish();
        }catch (Exception e){

        }

        ctx.startActivity(go);
    }

    public void get_current(View view){
        //On Get Forecast Click
        intent = new Intent(this,City_Entry.class);
        startActivityForResult(intent,102);
    }

    public void get_future(View view){
        //When 5 day button clicked
        intent = new Intent(this,City_Entry.class);
        startActivityForResult(intent,100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        isInternetConnected();  //Internet Check
        if(isConnected){
            if(resultCode==RESULT_OK && requestCode==102){
                //Getting Name after get forecast is clicked
                Current_Forecast obj = new Current_Forecast(intent.getStringExtra("City"),this);

                Intent loading = new Intent(this,Loading_Screen.class);
                startActivity(loading);

                obj.execute();
            }
            else if(resultCode==RESULT_OK && requestCode==100){
                //Getting Name after 5 day clicked
                Five_Day_Forecast obj = new Five_Day_Forecast(intent.getStringExtra("City"),this);

                Intent loading = new Intent(this,Loading_Screen.class);
                startActivity(loading);

                obj.execute();
            }
        }
        else {
            if(resultCode==RESULT_OK && requestCode==102){  //Current Forecast Clicked
                DB_Handler db_handler= new DB_Handler(ctx,null,null,1);
                if(db_handler.isCityExist(intent.getStringExtra("City").toUpperCase(),"Current_Weather")){    //City is in database
                    current_forecast_update(db_handler.getCurrentWeather(intent.getStringExtra("City").toUpperCase())); //Extracts from db
                }
                else {
                    Toast.makeText(ctx,"Device Offline. Enable Network",Toast.LENGTH_LONG).show();
                }
            }
            else if(resultCode==RESULT_OK && requestCode==100){ //5 Day Forecast Clicked
                DB_Handler db_handler= new DB_Handler(ctx,null,null,1);
                if(db_handler.isCityExist(intent.getStringExtra("City").toUpperCase(),"Forecast_Weather")){ //City in DB
                    db_handler.getForecastWeather(intent.getStringExtra("City").toUpperCase());
                }
                else {
                    Toast.makeText(ctx,"Device Offline. Enable Network",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private class CheckInternet extends AsyncTask<Void,Void,Void>{

        public CheckInternet() {
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                urlc.connect();
                isConnected=true;

            } catch (MalformedURLException e) {
                Log.d("TAG","Malformed URL");
            } catch (IOException e) {
                isConnected=false;
            }
            return null;
        }
    }

    private void isInternetConnected(){
        CheckInternet obj = new CheckInternet();
        obj.execute();
        SystemClock.sleep(2000);
    }

}
