package com.barapraja.isatds.stock;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;


import com.barapraja.isatds.R;
import com.barapraja.isatds.barcode.BarcodeCaptureActivity;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Tri Sutrisno on 11/05/2017. sds
 */

public class AddStock extends AppCompatActivity {


    @BindView(R.id.phone_number)
    EditText _phonenumber;
    @BindView(R.id.btn_scan)
    Button _btnscan;
    @BindView(R.id.btn_submit)
    Button _btnsubmit;

    @BindView(R.id.auto_focus) CompoundButton autoFocus;
    @BindView(R.id.use_flash) CompoundButton useFlash;

    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addstock);
        ButterKnife.bind(this);

        _btnscan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AddStock.this, "Call CR Scanner", Toast.LENGTH_SHORT).show();
                // launch barcode activity.
                Intent intent = new Intent(getApplicationContext(), BarcodeCaptureActivity.class);
                startActivityForResult(intent, RC_BARCODE_CAPTURE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    Point[] p = barcode.cornerPoints;
                    _phonenumber.setText(barcode.displayValue);
                } else _phonenumber.setText(R.string.no_barcode_captured);
            } else Log.e(TAG, String.format(getString(R.string.barcode_error_format),
                    CommonStatusCodes.getStatusCodeString(resultCode)));
        } else super.onActivityResult(requestCode, resultCode, data);
    }




}
