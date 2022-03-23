package com.example.covidtracker.data.model;

import com.google.gson.annotations.SerializedName;

public class UploadResponse {

	@SerializedName("success")
	private int success;

	@SerializedName("message")
	private String message;

	public int getSuccess(){
		return success;
	}

	public String getMessage(){
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setSuccess(int success) {
		this.success = success;
	}

	@Override
 	public String toString(){
		return 
			"Response{" + 
			"success = '" + success + '\'' + 
			",message = '" + message + '\'' + 
			"}";
		}
}