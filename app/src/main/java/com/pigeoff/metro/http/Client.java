package com.pigeoff.metro.http;

import com.pigeoff.metro.data.Station;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface Client {
    @Headers("key: a96fgd4jf45dw2")
    @GET("/path/{from}/{to}")
    Call<ArrayList<Station>> getPath(@Path("from") String from, @Path("to") String to);

    @Headers("key: a96fgd4jf45dw2")
    @GET("/station/{from}")
    Call<ArrayList<Station>> getStation(@Path("from") String from);
}
