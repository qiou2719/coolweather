package com.coolweather.android.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.coolweather.android.db.City;
import com.coolweather.android.db.County;
import com.coolweather.android.db.Province;
import com.coolweather.android.gson.Weather;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import android.text.TextUtils;

public class Utility 
{
	/**
	 * 处理服务器响应的JSON数据，生成Province表
	 * @param response 服务器发回的响应，为json数据
	 * @return 成功为true
	 */
	public static boolean handleProvinceResponse(String response)
	{
		if (!TextUtils.isEmpty(response)) 
		{
			try {
				JSONArray allProvinces = new JSONArray(response);
				for (int i = 0; i < allProvinces.length(); i++) 
				{
					JSONObject provinceObject = allProvinces.getJSONObject(i);
					Province province = new Province();
					province.setProvinceName(provinceObject.getString("name"));
					province.setProvinceCode(provinceObject.getInt("id"));
					province.save();
				}
				return true;
			} catch (JSONException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		
		return false;
	}
	
	/**
	 * 解析和处理服务器返回的市级数据
	 * @param response 服务器返回的回应
	 * @param provinceId 所属的省
	 * @return 处理是成功
	 */
	public static boolean handleCityResponse(String response, int provinceId)
	{
		if (!TextUtils.isEmpty(response)) 
		{
			try {
				JSONArray allCities = new JSONArray(response);
				for (int i = 0; i < allCities.length(); i++) {
					JSONObject cityObject = allCities.getJSONObject(i);
					City city= new City();
					city.setCityName(cityObject.getString("name"));
					city.setCityCode(cityObject.getInt("id"));
					city.setProvinceId(provinceId);
					city.save();
				}
				return true;
			} catch (JSONException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		return false;
	}
	
	/**
	 * 处理县级消息
	 * @param response 服务器响应
	 * @param cityId 城市id
	 * @return 返回是否处理成功
	 */
	public static boolean handleCountyResponse(String response, int cityId)
	{
		if (!TextUtils.isEmpty(response)) 
		{
			try {
				JSONArray allCounties = new JSONArray(response);
				for (int i = 0; i < allCounties.length(); i++) {
					JSONObject countyObject = allCounties.getJSONObject(i);
					County county = new County();
					county.setCountyName(countyObject.getString("name"));
					county.setWeatherId(countyObject.getString("weather_id"));
					county.setCityId(cityId);
					county.save();
				}
				return true;
			} catch (JSONException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		return false;
	}
	
	/**
	 * 将返回的JSON数据解析成Weather实体类
	 * @param response 服务器返回的JSON响应
	 * @return 天气类
	 */
	public static Weather handleWeatherResponse(String response)
	{
		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONArray jsonArray = jsonObject.getJSONArray("HeWeather5");
			String weatherContent = jsonArray.getJSONObject(0).toString();
			return new Gson().fromJson(weatherContent, Weather.class);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return null;
	}
}



















