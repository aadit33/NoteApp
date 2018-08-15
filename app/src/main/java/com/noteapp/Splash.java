package com.noteapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;


public class Splash extends AppCompatActivity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;

    private SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);


        prefs = PreferenceManager.getDefaultSharedPreferences(Splash.this);
        final boolean Islogin = prefs.getBoolean("Islogin", false);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                if (Islogin) {
                    Intent i = new Intent(Splash.this, NoteListActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Intent goToLogin = new Intent(Splash.this, Login.class);
                    startActivity(goToLogin);
                    finish();
                }


            }
        }, SPLASH_TIME_OUT);
    }
}

