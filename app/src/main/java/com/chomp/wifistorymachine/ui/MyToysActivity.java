package com.chomp.wifistorymachine.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.chomp.wifistorymachine.R;
import com.chomp.wifistorymachine.application.MyApplication;

import static com.chomp.wifistorymachine.R.id.btn_back;
import static com.chomp.wifistorymachine.R.id.config_network_ll;
import static com.chomp.wifistorymachine.R.string.add_device;
import static com.chomp.wifistorymachine.R.string.find;


public class MyToysActivity extends BaseActivity implements OnClickListener{

	private LinearLayout config_network_ll;
	private LinearLayout btn_back;

	private ImageView add_device;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bm_setting_toy_bound_ui);
		MyApplication.getInstance().addActivity(this);
		setTitle(getString(R.string.setting_toy_info));

		config_network_ll=(LinearLayout) findViewById(R.id.config_network_ll);
		config_network_ll.setOnClickListener(this);
		btn_back=(LinearLayout) findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);

		add_device=(ImageView) findViewById(R.id.add_device);
		add_device.setOnClickListener(this);
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()){
			case R.id.config_network_ll:
				Intent in=new Intent();
				in.setClass(MyToysActivity.this,WifiNetworkingActivity.class);
				startActivity(in);
				break;
			case R.id.add_device:
				Intent in2=new Intent();
				in2.setClass(MyToysActivity.this,AddDeviceActivity.class);
				startActivity(in2);
				break;
			case R.id.btn_back:
				onBackPressed();
				break;
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}




}
