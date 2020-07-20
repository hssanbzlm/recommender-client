package com.example.gpslocation;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface recommenderApi {

@GET("recommend/{idUser}/{lat}/{longi}")
    Call<List<response>> getResponse(@Path("idUser") int idUser,@Path("lat") Double lat,@Path("longi") Double longi);

}
