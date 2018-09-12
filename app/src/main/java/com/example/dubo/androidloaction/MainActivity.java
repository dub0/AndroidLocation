package com.example.dubo.androidloaction;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getLocation();  //1
        //getLocation1();  //2
        LocationUtils.getInstance().getLocations(this);
        //getLocation3();  //4
    }
}
