package com.barapraja.isatds.checkin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.barapraja.isatds.R;

import butterknife.ButterKnife;

/**
 * Created by Tri S on 5/12/2017. da
 */

public class checkin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_addstock);
        ButterKnife.bind(this);
    }

}
