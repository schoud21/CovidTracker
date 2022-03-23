package com.example.covidtracker.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.covidtracker.data.api.UploadApi;
import com.example.covidtracker.data.model.UploadResponse;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

public class UploadRepo {

    private UploadApi uploadVideoApi;
    private MutableLiveData<UploadResponse> responseLiveData;

    public UploadRepo() {
        responseLiveData = new MutableLiveData<>();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        uploadVideoApi = new retrofit2.Retrofit.Builder()
                .baseUrl("http://192.168.0.98:5500")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(UploadApi.class);

    }

    public void uploadFile(File file) {
//        File file = new File(path);
        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("filename", file.getName(), requestBody);
        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());

        uploadVideoApi.uploadDb(fileToUpload, filename)
                .enqueue(new Callback<UploadResponse>() {
                    @Override
                    public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                        if (response.body() != null) {
                            responseLiveData.postValue(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<UploadResponse> call, Throwable t) {
                        String error = t.getMessage();
                        UploadResponse response = new UploadResponse();
                        response.setSuccess(0);
                        response.setMessage(error);
                        responseLiveData.postValue(response);
                    }
                });
    }

    public LiveData<UploadResponse> getUploadResponseLiveData() {
        return responseLiveData;
    }
}