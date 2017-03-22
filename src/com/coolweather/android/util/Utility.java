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
	 * �����������Ӧ��JSON���ݣ�����Province��
	 * @param response ���������ص���Ӧ��Ϊjson����
	 * @return �ɹ�Ϊtrue
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
	 * �����ʹ�����������ص��м�����
	 * @param response ���������صĻ�Ӧ
	 * @param provinceId ������ʡ
	 * @return �����ǳɹ�
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
	 * �����ؼ���Ϣ
	 * @param response ��������Ӧ
	 * @param cityId ����id
	 * @return �����Ƿ���ɹ�
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
	 * �����ص�JSON���ݽ�����Weatherʵ����
	 * @param response ���������ص�JSON��Ӧ
	 * @return ������
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



















