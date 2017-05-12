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
    @POST("user/auth/")
    Call<ResponseBody> authenticate(
            @Field("username") String email,
            @Field("password") String password);


    @POST("user/listStock")
    Call<ResponseBody> listStock(

    );

    @POST("user/list/")
    Call<ResponseBody> listHotspot(

    );

    @FormUrlEncoded
    @POST("user/checkin/")
    Call<ResponseBody> checkin(
            @Field("userId") String userId,
            @Field("hotspotId") String hotspotId,
            @Field("checkInTime") String checkinTime
    );

    @POST("user/checkout/")
    Call<ResponseBody> checkout(

    );



}
