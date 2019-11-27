package com.example.weatherapi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weatherapi.weather.Current;
import com.example.weatherapi.weather.Location;
import com.example.weatherapi.weather.Request;
import com.example.weatherapi.weather.Weather;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private OkHttpClient.Builder builder;
    private   ImageView mImageView;
    private  TextView mCurrentWeatherReport, mCurrentWeatherTemperature, mPlaceName;
    private CoordinatorLayout mCoordinateLayout;
    private   String acess_key = "7d080bae003e42b27f12b699325fc779";
    private WeatherCall weatherCall;
    private   Call<Weather> call;
    private  String serviceName = " http://api.weatherstack.com/";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        isInternetConnection();
    }
    private void initView() {
        mCurrentWeatherTemperature = findViewById(R.id.current_weather_temp);
        mCurrentWeatherReport = findViewById(R.id.weather_report);
        mImageView = findViewById(R.id.current_weather_status);
        mPlaceName = findViewById(R.id.place_name);
        mCoordinateLayout = findViewById(R.id.coordinate_layout);
    }
    private void callServices() {
        builder = getHttpClient();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(serviceName).addConverterFactory(GsonConverterFactory.create()).client(builder.build()).build();
        weatherCall = retrofit.create(WeatherCall.class);
        call = weatherCall.getCurrent(acess_key, "Mumbai");
        call.enqueue(new Callback<Weather>() {
            @Override
            public void onResponse(Call<Weather> call, Response<Weather> response) {
                if (response != null) {
                    Weather weather = response.body();
                    if (weather != null) {
                        Toast.makeText(MainActivity.this, "weather not null", Toast.LENGTH_SHORT).show();
                        Current current = weather.getCurrent();
                        if (current != null) {
                            Toast.makeText(MainActivity.this, "current not null", Toast.LENGTH_SHORT).show();
                            int temp = current.getTemperature();
                            String icon = current.getWeatherIcons().get(0);
                            String description = current.getWeatherDescriptions().get(0);
                            String location = weather.getLocation().getName();
                            if (description != null) {
                                mCurrentWeatherTemperature.setText(temp + "");
                                mCurrentWeatherReport.setText(description);
                                Picasso.get()
                                        .load(icon)
                                        .placeholder(R.drawable.ic_launcher_background)
                                        .error(R.drawable.ic_launcher_background)
                                        .resize(200, 200)
                                        .into(mImageView);
                                mPlaceName.setText(location);
                                Toast.makeText(MainActivity.this, "weather report", Toast.LENGTH_SHORT).show();
                            }
                            //       Request request=weather.getRequest();
                            //      String unit=request.getUnit();
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<Weather> call, Throwable t) {
                Toast.makeText(MainActivity.this, "failure", Toast.LENGTH_SHORT).show();
                showSnackBar();
            }
        });
    }
    private void showSnackBar() {
        Snackbar snackbar = Snackbar.make(mCoordinateLayout, "Something Went Wrong", Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
    }
    public void isInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            callServices();
        }
        else
        {
            Snackbar snackbar = Snackbar.make(mCoordinateLayout, "No Internet", Snackbar.LENGTH_INDEFINITE);
            snackbar.show();
        }
    }
    //  http://api.weatherstack.com/current?access_key=7d080bae003e42b27f12b699325fc779&query=New York
    public OkHttpClient.Builder getHttpClient() {
        if (builder == null) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder client = new OkHttpClient.Builder();
            client.addInterceptor(loggingInterceptor);
            client.writeTimeout(60000, TimeUnit.MILLISECONDS);
            client.readTimeout(60000, TimeUnit.MILLISECONDS);
            client.connectTimeout(60000, TimeUnit.MILLISECONDS);
            return client;
        }
        return builder;
    }
}
