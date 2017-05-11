package com.barapraja.isatds.barcode;


import android.content.Context;

import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;

/**
 * Created by Tri Sutrisno on 11/05/2017. gfgf
 */

public class BarcodeTrackerFactory implements  MultiProcessor.Factory<Barcode> {
    private Context mContext;

    BarcodeTrackerFactory(Context context) {
        mContext = context;
    }

    @Override
    public Tracker<Barcode> create(Barcode barcode) {
        return new BarcodeTracker(mContext);
    }
}
