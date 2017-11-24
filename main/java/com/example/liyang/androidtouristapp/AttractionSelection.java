package com.example.liyang.androidtouristapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Li Yang on 21/11/2017.
 */

public class AttractionSelection extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_attraction_selection);
        this.setTitle("Select Your Budget");
        final TextView text = (TextView) findViewById(R.id.calculate);
        final Button calroute = (Button) findViewById(R.id.go_button);

        EditText eText = (EditText) findViewById(R.id.entercost);
        String cost = eText.getText().toString();

        calroute.setTag(1);
        calroute.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                final int status =(Integer) v.getTag();
                if(status == 1) {
                    showroute(v);
                    text.setText("Show Route");
                    v.setTag(0); //pause
                } else {
                    cal_route(v);
                    v.setTag(1); //pause
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //handle presses on the action bar items
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showroute(View view){
        Intent ShowRoute = new Intent(this, ShowRoute.class);
        startActivity(ShowRoute);
    }
    public void cal_route(View view){
        //calculate price
    }
}
