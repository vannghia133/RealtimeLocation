package com.nghiatv.realtimelocation.remote;

import com.nghiatv.realtimelocation.model.MyResponse;
import com.nghiatv.realtimelocation.model.Request;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAaDQIEj8:APA91bGK-JvgizhLvq1EfS1oChJJLLidIH8CetSST7_bPpY07jkTsJpPeXo96rU3wnJznC2OpWxj52px2Npq2rFZBqNU5k_Hwl0pmHTNuvkRK6QXZAUXtKfp8PgpaaOABOv43ZUPnS9P"
    })
    @POST("fcm/send")
    abstract Observable<MyResponse> sendFriendRequestToUser(@Body Request body);
}
