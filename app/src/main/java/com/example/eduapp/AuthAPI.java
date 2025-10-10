package com.example.eduapp; // your app package name

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AuthAPI {
    @POST("/api/signup")
    Call<String> signup(@Body User user);

    @POST("/api/signin")
    Call<String> signin(@Body User user);

    @POST("/api/updateGoal")
    Call<String> updateGoal(@Query("email") String email, @Query("goal") String goal);
}
