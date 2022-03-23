package com.example.covidtracker.data.api;

import com.example.covidtracker.data.model.UploadResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UploadApi {
    @Multipart
    @POST("/upload_video-2.php/")
    Call<UploadResponse> uploadDb(@Part MultipartBody.Part file, @Part("filename") RequestBody name);
}
