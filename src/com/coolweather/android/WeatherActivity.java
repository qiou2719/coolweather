package com.coolweather.android;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import com.bumptech.glide.Glide;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ScrollingView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.android.gson.*;

public class WeatherActivity extends AppCompatActivity
{
	private ScrollView weatherLayout;
	
	private TextView titleCity;
	
	private TextView titleUpdateTime;
	
	private TextView degreeText;
	
	private TextView weatherInfoText;
	
	private LinearLayout forecastLayout;
	
	private TextView aqiText;
	
	private TextView pm25Text;
	
	private TextView comfortText;

    private TextView carWashText;

    private TextView sportText;

    private ImageView bingPicImg;
    
    @SuppressLint("NewApi") @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
    	super.onCreate(savedInstanceState);
    	
    	if (Build.VERSION.SDK_INT >= 21) 
    	{
    		View decorView = getWindow().getDecorView();
    		decorView.setSystemUiVisibility(
    				View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    				);
    		getWindow().setStatusBarColor(Color.TRANSPARENT);
		}
    	
    	setContentView(R.layout.activity_weather);
    	//初始化条件
    	bingPicImg = (ImageView)findViewById(R.id.bing_pic_img);
    	weatherLayout = (ScrollView)findViewById(R.id.weather_layout);
    	titleCity = (TextView)findViewById(R.id.title_city);
    	titleUpdateTime = (TextView)findViewById(R.id.title_update_time);
    	degreeText = (TextView)findViewById(R.id.degree_text);
    	weatherInfoText = (TextView)findViewById(R.id.weather_info_text);
    	forecastLayout = (LinearLayout)findViewById(R.id.forecast_layout);
    	aqiText = (TextView)findViewById(R.id.aqi_text);
    	pm25Text = (TextView)findViewById(R.id.pm25_text);
    	comfortText = (TextView) findViewById(R.id.comfort_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        sportText = (TextView) findViewById(R.id.sport_text);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        
        String weatherString = prefs.getString("weather", null);
        if (weatherString != null) 
        {
			Weather weather = Utility.handleWeatherResponse(weatherString);
			String oldWeatherId = weather.basic.weatherId;
			String newWeatherId = getIntent().getStringExtra("weather_id");
			if (oldWeatherId.equals(newWeatherId)) {
				showWeatherInfo(weather);
			}
			else {
				requestWeather(newWeatherId);
			}
			
		}
        else {
			String weatherId = getIntent().getStringExtra("weather_id");
			weatherLayout.setVisibility(View.INVISIBLE);
			requestWeather(weatherId);
		}
        
        String bingPic = prefs.getString("bing_pic", null);
        if (bingPic != null) {
			Glide.with(this).load(bingPic).into(bingPicImg);
		}
        else
        {
        	loadBingPic();
        }
    }
    
    /**
     * 根据城市请求天气数据
     * @param weatherId 城市ID
     */
    public void requestWeather(final String weatherId)
    {
    	String weatherUrl = "https://free-api.heweather.com/v5/weather?city=" + 
    weatherId + "&key=27d0746f0d79431d90765bc970a90fa9";
    	
    	HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
			
			@Override
			public void onResponse(Call call, Response response) throws IOException {
				// TODO Auto-generated method stub
				final String responseText = response.body().string();
				final Weather weather = Utility.handleWeatherResponse(responseText);
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (weather!=null && "ok".equals(weather.status)) 
						{
							SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
							editor.putString("weather", responseText);
							editor.apply();
							showWeatherInfo(weather);
						}
						else {
							Toast.makeText(WeatherActivity.this, "获取天气信息失败", 
									Toast.LENGTH_SHORT).show();
						}
					}
				});
			}
			
			@Override
			public void onFailure(Call arg0, IOException arg1) {
				// TODO Auto-generated method stub
				Toast.makeText(WeatherActivity.this, "获取天气信息Failure", 
						Toast.LENGTH_SHORT).show();
			}
		});
    	
    	loadBingPic();
    }
    
    /**
     * 处理并展示Weather实体类中的数据
     * @param weather 天气数据类
     */
    public void showWeatherInfo(Weather weather)
    {
    	String cityName = weather.basic.cityName;
    	String updateTime = weather.basic.update.updateTime.split(" ")[1];
    	String degree = weather.now.temperature + "℃";
    	String weatherInfo = weather.now.more.info;
    	titleCity.setText(cityName);
    	titleUpdateTime.setText(updateTime);
    	degreeText.setText(degree);
    	weatherInfoText.setText(weatherInfo);
    	forecastLayout.removeAllViews();
    	
    	for(Forecast forecast : weather.forecastList)
    	{
    		View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
    		TextView dateText = (TextView)view.findViewById(R.id.date_text);
    		TextView infoText = (TextView)view.findViewById(R.id.info_text);
    		 TextView maxText = (TextView) view.findViewById(R.id.max_text);
             TextView minText = (TextView) view.findViewById(R.id.min_text);
             dateText.setText(forecast.date);
             infoText.setText(forecast.more.info);
             maxText.setText(forecast.temperature.max);
             minText.setText(forecast.temperature.min);
             forecastLayout.addView(view);
    	}
    	
    	if (weather.aqi!=null) 
    	{
    		aqiText.setText(weather.aqi.city.aqi);
    		pm25Text.setText(weather.aqi.city.pm25);
		}
    	
    	String comfort = "舒适度:" + weather.suggestion.comfort.info;
    	String carWash = "洗车指数：" + weather.suggestion.carWash.info;
        String sport = "运行建议：" + weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 加载必应每日一图
     */
    private void loadBingPic()
    {
    	String requestBingPic = "http://guolin.tech/api/bing_pic";
    	HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
			
			@Override
			public void onResponse(Call call, Response response) throws IOException {
				// TODO Auto-generated method stub
				final String bingPic = response.body().string();
				SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
				editor.putString("bing_pic", bingPic);
				editor.apply();
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
					}
				});
			}
			
			@Override
			public void onFailure(Call arg0, IOException e) {
				// TODO Auto-generated method stub
				e.printStackTrace();
			}
		});
    }
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, MainActivity.class);
    	startActivity(intent);
    	finish();
    	super.onBackPressed();
	}
	
    
}

























