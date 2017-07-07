package com.example.vikramkumaresan.v1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class Five_Day_Data extends AppCompatActivity {
    public static String city_name;
    public static ArrayList<ArrayList<String>> data;
    ListView list;
    ListAdapter adapt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_five__day__data);

        TextView name = (TextView)findViewById(R.id.name);
        name.setText(city_name);

        list=(ListView)findViewById(R.id.list);
        adapt = new Custom_Adapter(this,data);
        list.setAdapter(adapt);
    }
}
