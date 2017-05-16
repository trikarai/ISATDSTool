package com.barapraja.isatds;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.barapraja.isatds.config.AppPref;
import com.barapraja.isatds.config.AppService;
import com.barapraja.isatds.config.AppUri;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity{
    private final String TAG = "LoginActivity";
    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private TextView failedLoginMessage;
    View focusView = null;
    private String email;
    private String password;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public static String keyUserId = "userId";
    public static String keyName = "name";
    public static String keyAreaId = "areaId";
    public static String keyEmail = "email";
    public static String keyLogged = "logged";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Get the app's shared preferences
        sharedPreferences = getApplicationContext().getSharedPreferences(AppPref.LOGIN_PREF, Context.MODE_PRIVATE);

        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.username);
        //populateAutoComplete();
        failedLoginMessage = (TextView) findViewById(R.id.failed_login);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                failedLoginMessage.setText("");
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

    }

    private void attemptLogin(){
        boolean mCancel = this.loginValidation();
        if (mCancel) {
            focusView.requestFocus();
        } else {
            loginProcessWithRetrofit(email, password);
        }
    }

    private boolean loginValidation() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        // Store values at the time of the login attempt.
        email = mEmailView.getText().toString();
        password = mPasswordView.getText().toString();
        boolean cancel = false;
        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }
        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;}
//        } else if (!isEmailValid(email)) {
//            mEmailView.setError(getString(R.string.error_invalid_email));
//            focusView = mEmailView;
//            cancel = true;
//        }
        return cancel;
    }

//    private void populateAutoComplete(){
//        String[] countries = getResources().getStringArray(R.array.autocomplete);
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,countries);
//        mEmailView.setAdapter(adapter);
//    }
    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }
    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 1;
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private AppService getInterfaceService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppUri.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final AppService mInterfaceService = retrofit.create(AppService.class);
        return mInterfaceService;
    }
    private void loginProcessWithRetrofit(final String email, String password){
        showProgress(true);
        AppService mApiService = this.getInterfaceService();
        Call<ResponseBody> mService = mApiService.authenticate(email, password);
        mService.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    //Toast.makeText(LoginActivity.this, "R: " + response.body().string(), Toast.LENGTH_SHORT).show();
                    JSONObject jsonR = new JSONObject(response.body().string());
                    Log.d(TAG,"Respone :"+ jsonR);
                    boolean error = jsonR.getBoolean("status");
                    String err_msg = jsonR.optString("msg");
                    if(error) {

                        JSONObject data = jsonR.getJSONObject("data");

                        String userId = data.getString("id");
                        String name = data.getString("name");
                        String areaId = data.optString("areaId");

                        Log.e(TAG, "READ Object :" + userId + " " + name + " "+ areaId );

                        editor = sharedPreferences.edit();
                        editor.putString(keyUserId, userId);
                        editor.putString(keyName, name);
                        editor.putString(keyAreaId, areaId);
                        editor.putString(keyEmail, email);
                        editor.putString(keyLogged, "true");
                        editor.commit(); // Very important
                        showProgress(false);
                        Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
                        loginIntent.putExtra("EMAIL", email);
                        startActivity(loginIntent);
                        finish();

                    }else{
                        showProgress(false);
                        //failedLoginMessage.setText(getResources().getString(R.string.registration_message));
                        failedLoginMessage.setText(err_msg);
                        mPasswordView.requestFocus();
                        //String errorMessage = jsonR.getString("err_msg");
                        //Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException | IOException e) {
                    showProgress(false);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showProgress(false);
                call.cancel();
                Toast.makeText(LoginActivity.this, "Please check your network connection and internet permission", Toast.LENGTH_LONG).show();
            }
        });
    }
}

