package com.example.weatherapi;

import com.example.weatherapi.weather.Weather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherCall {
    //  http://api.weatherstack.com/current?access_key=7d080bae003e42b27f12b699325fc779&query=New York


    @GET("current?")
    Call<Weather> getCurrent(@Query("access_key") String apiKey, @Query("query") String city);
}
