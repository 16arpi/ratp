package com.pigeoff.metro;

import android.app.Application;
import android.net.Network;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pigeoff.metro.data.Station;
import com.pigeoff.metro.http.Client;
import com.pigeoff.metro.utils.ReadFile;
import com.pigeoff.metro.utils.Route;

import java.lang.reflect.Array;
import java.util.ArrayList;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MetroApp extends Application {

    public Client httpClient;

    @Override
    public void onCreate() {
        super.onCreate();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://pigeoff.pw:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        httpClient = retrofit.create(Client.class);
    }
}
