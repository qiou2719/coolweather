package com.coolweather.android;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import org.litepal.crud.DataSupport;

import com.coolweather.android.db.City;
import com.coolweather.android.db.County;
import com.coolweather.android.db.Province;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import android.R.integer;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaFragment extends Fragment
{
	public static final int LEVEL_PROVINCE = 0;

    public static final int LEVEL_CITY = 1;

    public static final int LEVEL_COUNTY = 2;
	
    private ProgressDialog progressDialog;
    
    private TextView titleText;
    
	private Button backButton;
	
	private ListView listView;
	
	private ArrayAdapter<String> adapter;
	
	private List<String> dataList = new ArrayList<String>();
	
	/**
	 * ʡ�б�
	 */
	private List<Province> provincesList;
	/**
     * ���б�
     */
	private List<City> cityList;
	
	/**
     * ���б�
     */
    private List<County> countyList;

    /**
     * ѡ�е�ʡ��
     */
    private Province selectedProvince;

    /**
     * ѡ�еĳ���
     */
    private City selectedCity;

    /**
     * ��ǰѡ�еļ���
     */
    private int currentLevel;

	@Override
	public View onCreateView(LayoutInflater inflater,
			 ViewGroup container,  Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.choose_area, container, false);
		titleText = (TextView)view.findViewById(R.id.title_text);
		backButton = (Button)view.findViewById(R.id.back_button);
		listView = (ListView)view.findViewById(R.id.list_view);
		adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		return view;
		//return super.onCreateView(inflater, container, savedInstanceState);
		
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if(currentLevel == LEVEL_PROVINCE)
				{
					selectedProvince = provincesList.get(position);
					queryCities();
				}
				else if (currentLevel == LEVEL_CITY) {
					selectedCity = cityList.get(position);
					queryCounties();
				}
				else if(currentLevel == LEVEL_COUNTY )
				{
					String weatherId = countyList.get(position).getWeatherId();
					Intent intent = new Intent(getActivity(), WeatherActivity.class);
					intent.putExtra("weather_id", weatherId);
					startActivity(intent);
					getActivity().finish();
				}
			}
		});
		backButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (currentLevel == LEVEL_COUNTY) {
					queryCities();
				}
				else if (currentLevel == LEVEL_CITY) {
					queryProvinces();
				}
			}
		});
		queryProvinces();
	}
    
	/**
	 * ��ѯȫ�����е�ʡ�����ȴ����ݿ��в�ѯ��û�е��������ϲ�ѯ
	 */
	private void queryProvinces()
	{
		titleText.setText("�й�");
		backButton.setVisibility(View.GONE);
		provincesList = DataSupport.findAll(Province.class);
		if (provincesList.size()>0) 
		{
			dataList.clear();
			for(Province province : provincesList)
			{
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			currentLevel = LEVEL_PROVINCE;
		}
		else {
			String address = "http://guolin.tech/api/china";
			queryFromServer(address, "province");
		}
	}
	
	/**
	 * ��ѯѡ��ʡ�����е���
	 */
	private void queryCities()
	{
		titleText.setText(selectedProvince.getProvinceName());
		backButton.setVisibility(View.VISIBLE);
		cityList = DataSupport.where("provinceid = ?", 
				String.valueOf(selectedProvince.getId())).find(City.class);
		if (cityList.size() > 0) 
		{
			dataList.clear();
			for (City city : cityList) {
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			currentLevel = LEVEL_CITY;
		}
		else {
			int provinceCode = selectedProvince.getProvinceCode();
			String address = "http://guolin.tech/api/china/"+provinceCode;
			queryFromServer(address, "city");
		}
	}
	
	/**
	 * ��ѯѡ���������е���
	 */
	private void queryCounties()
	{
		titleText.setText(selectedCity.getCityName());
		backButton.setVisibility(View.VISIBLE);
		countyList = DataSupport.where("cityid = ?", String.valueOf(selectedCity.getId())).find(County.class);
		if (countyList.size() > 0) 
		{
			dataList.clear();
			for(County county : countyList)
			{
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			currentLevel = LEVEL_COUNTY;
		}
		else {
			int provinceCode = selectedProvince.getProvinceCode();
			int cityCode = selectedCity.getCityCode();
			String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
			queryFromServer(address, "county");
		}
	}
	
	private void queryFromServer(String address, final String type)
	{
		showProgressDialog();
		HttpUtil.sendOkHttpRequest(address, new Callback() {
			
			@Override
			public void onResponse(Call call, Response response) throws IOException {
				// TODO Auto-generated method stub
				String responseText = response.body().string();
				boolean result = false;
				if ("province".equals(type)) 
				{
					result = Utility.handleProvinceResponse(responseText);
				}
				else if ("city".equals(type)) 
				{
					result = Utility.handleCityResponse(responseText, selectedProvince.getId());
				}
				else if ("county".equals(type)) 
				{
					result = Utility.handleCountyResponse(responseText, selectedCity.getId());
				}
				
				if (result) {
					getActivity().runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							closeProgressDialog();
							if ("province".equals(type)) 
							{
								queryProvinces();
							}
							else if("city".equals(type))
							{
								queryCities();
							}
							else if ("county".equals(type)) {
								queryCounties();
							}
						}
					});
				}
			}
			
			@Override
			public void onFailure(Call arg0, IOException arg1) {
				// TODO Auto-generated method stub
				getActivity().runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						closeProgressDialog();
						Toast.makeText(getContext(), "����ʧ��", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}
	
	/**
	 * ��ʾ���ȶԻ���
	 */
	private void showProgressDialog()
	{
		if (progressDialog == null) 
		{
			progressDialog = new ProgressDialog(getActivity());
			progressDialog.setMessage("���ڼ���...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	
	private void closeProgressDialog()
	{
		if (progressDialog!=null) 
		{
			progressDialog.dismiss();
		}
	}
    
}

























