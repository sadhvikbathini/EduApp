package com.example.eduapp; // your app package name

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    public static final String BASE_URL = "http://10.0.2.2:8080/";  // Emulator
    private static Retrofit retrofit = null;

    public static AuthAPI getAuthAPI() {
        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(AuthAPI.class);
    }
}
