package com.barapraja.isatds.config;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Tri Sutrisno on 11/05/2017.
 */

public interface AppService {

    @FormUrlEncoded
    @POST("login/")
    Call<ResponseBody> authenticate(
            @Field("email") String email,
            @Field("password") String password);

}
