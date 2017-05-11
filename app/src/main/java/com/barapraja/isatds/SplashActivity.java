package com.barapraja.isatds;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.barapraja.isatds.config.AppPref;

import java.util.Objects;

/**
 * Created by Tri Sutrisno on 21/10/2016. h
 */

public class SplashActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    public static final String LOGIN_PREFERENCES = AppPref.LOGIN_PREF;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        sharedPreferences = getApplicationContext().getSharedPreferences(LOGIN_PREFERENCES, Context.MODE_PRIVATE);
        final String isLogged = sharedPreferences.getString("logged",null);
        final String email = sharedPreferences.getString("email",null);

        int SPLASH_TIME_OUT = 1000;
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                if(Objects.equals(isLogged, "true")){
                    Intent loginAct = new Intent(SplashActivity.this, MainActivity.class);
                    loginAct.putExtra("EMAIL", email);
                    startActivity(loginAct);
                    overridePendingTransition(R.transition.fade_in, R.transition.fade_out);

                    finish();
                }else{
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.transition.fade_in, R.transition.fade_out);

                    finish();
                }
            }
        }, SPLASH_TIME_OUT);
    }

}