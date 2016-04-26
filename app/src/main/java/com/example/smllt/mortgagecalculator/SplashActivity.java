package com.example.smllt.mortgagecalculator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Krystle on 4/26/2016.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(SplashActivity.this, MortgageActivity.class);
        startActivity(intent);
        finish();
    }
}