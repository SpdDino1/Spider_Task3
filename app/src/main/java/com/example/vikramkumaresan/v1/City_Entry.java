package com.example.vikramkumaresan.v1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class City_Entry extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city__entry);
    }

    public void give_city_name(View view){
        EditText city = (EditText)findViewById(R.id.city_name);
        MainActivity.intent.putExtra("City",city.getText().toString());
        setResult(RESULT_OK);
        finish();
    }
}
