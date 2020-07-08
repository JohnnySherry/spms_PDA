package com.example.spms.check.services;

import com.example.spms.check.entity.CheckJobs;
import com.example.spms.check.entity.CheckResults;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface CheckStatus {

    @GET("/getGoods")
    Call<ResponseBody> getParts();

    @GET("/getCheckResults")
    Call<List<CheckResults>> getCheckResults();

//    @POST("/getCheckJobs")
//    Call<CheckJobs>getCheckJobs(@Body QueryObject query);

    @GET("/getCheckJobs")
    Call<CheckJobs>getCheckJobs();
}
