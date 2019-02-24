package com.example.kyon.myapplication;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        JuliaSetTestView view = new JuliaSetTestView(this);
        setContentView(view);
    }
}
