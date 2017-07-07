package com.example.vikramkumaresan.v1;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class Custom_Adapter extends ArrayAdapter{
    TextView date;
    TextView temp_min;
    TextView temp_max;
    TextView sky;

    public Custom_Adapter(@NonNull Context context, ArrayList<ArrayList<String>> forecast_data) {
        super(context,R.layout.custom_row,forecast_data);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View custom_view = inflater.inflate(R.layout.custom_row,parent,false);

        date=(TextView)custom_view.findViewById(R.id.date);
        temp_min=(TextView)custom_view.findViewById(R.id.temp_min);
        temp_max=(TextView)custom_view.findViewById(R.id.temp_max);
        sky=(TextView)custom_view.findViewById(R.id.sky);

        ArrayList<String> data = (ArrayList<String>) getItem(position);
        date.setText(date.getText()+data.get(0));
        temp_min.setText(temp_min.getText()+data.get(1)+" K");
        temp_max.setText(temp_max.getText()+data.get(2)+" K");
        sky.setText(sky.getText()+data.get(3));

        return custom_view;
    }
}
