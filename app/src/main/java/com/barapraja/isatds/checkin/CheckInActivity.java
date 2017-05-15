package com.barapraja.isatds.checkin;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.barapraja.isatds.R;
import com.barapraja.isatds.config.AppPref;
import com.barapraja.isatds.config.AppService;
import com.barapraja.isatds.config.AppUri;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Tri S on 5/12/2017. da
 */

public class CheckInActivity extends AppCompatActivity {
    private final String TAG = "Check In";

    private String userId;
    private String hotspotId;
    private String checkinTime;

    public static String keyCheckin = "checkin";

    @BindView(R.id.btn_checkin) Button _btncheckin;
    @BindView(R.id.btn_checkout) Button _btncheckout;

    ProgressDialog progressDialog;

    //SHARED LOGIN
    SharedPreferences sharedPreferences,checkinPreferences;
    SharedPreferences.Editor editor;
    //END SHARED LOGIN

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin);
        ButterKnife.bind(this);

        progressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);

        // Get the app's shared preferences
        sharedPreferences = getApplicationContext().getSharedPreferences(AppPref.LOGIN_PREF, Context.MODE_PRIVATE);
        checkinPreferences = getApplicationContext().getSharedPreferences(AppPref.CHECKIN_PREF,Context.MODE_PRIVATE);

        if(checkinPreferences.getBoolean(keyCheckin,false)){
            Toast.makeText(CheckInActivity.this, "local checkin pref detected " , Toast.LENGTH_SHORT).show();
            _btncheckin.setVisibility(View.INVISIBLE);
        }else{
            Toast.makeText(CheckInActivity.this, "local checkin pref not detected ", Toast.LENGTH_SHORT).show();
        }

        userId = sharedPreferences.getString("userId", null);

        //hardcode hotspot
        hotspotId = "1";

        Calendar c = Calendar.getInstance();
        @SuppressLint
        ("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        checkinTime = sdf.format(c.getTime());

        _btncheckin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callCheckIn(userId,hotspotId,checkinTime);
            }
        });
        _btncheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private AppService getInterfaceService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppUri.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final AppService mInterfaceService = retrofit.create(AppService.class);
        return mInterfaceService;
    }

    private void callCheckIn(String userId, String hotspotId, String checkinTime) {
        showDialog();
        AppService mApiService = this.getInterfaceService();
        retrofit2.Call<ResponseBody> mService = mApiService.checkin(userId,hotspotId,checkinTime);
        mService.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    JSONObject jsonR = new JSONObject(response.body().string());
                    boolean error = jsonR.getBoolean("status");
                    String err_msg = jsonR.optString("msg");
                    if (error){
                        Toast.makeText(CheckInActivity.this,"TRUE "+err_msg,Toast.LENGTH_LONG).show();
                        editor = checkinPreferences.edit();
                        editor.putBoolean(keyCheckin, true);
                        editor.commit();
                        hideDialog();
                    }else {
                        Toast.makeText(CheckInActivity.this,"FALSE "+err_msg,Toast.LENGTH_LONG).show();
                        hideDialog();
                    }
                }catch(JSONException | IOException e) {
                    hideDialog();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                hideDialog();
                call.cancel();
                Toast.makeText(CheckInActivity.this, "Please check your network connection and internet permission", Toast.LENGTH_LONG).show();
            }
        });


    }

    private void showDialog() {
        if(!progressDialog.isShowing()){
            progressDialog.show();
        }
    }

    private void hideDialog(){
        if(progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

}
