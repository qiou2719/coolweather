package com.coolweather.android.util;


import okhttp3.OkHttpClient;

public class HttpUtil 
{
	public static void sendOkHttpRequest(String address, okhttp3.Callback callback)
	{
		OkHttpClient client = new OkHttpClient();
		okhttp3.Request request = new okhttp3.Request.Builder().url(address).build();
		client.newCall(request).enqueue(callback);
	}
}
