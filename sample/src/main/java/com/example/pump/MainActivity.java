package com.example.pump;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pump.databinding.ActivityMainBinding;
import com.shenghaiyang.pump.Pump;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding mainBinding = Pump.inflate(ActivityMainBinding.class, getLayoutInflater());
        setContentView(mainBinding.getRoot());
    }
}
