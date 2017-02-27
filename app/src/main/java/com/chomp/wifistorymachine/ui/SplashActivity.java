package com.chomp.wifistorymachine.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.chomp.wifistorymachine.R;
import com.chomp.wifistorymachine.constants.Constant;

import static com.chomp.wifistorymachine.constants.Constant.EXTRA_USER_ID;


public class SplashActivity extends BaseActivity {
	private SharedPreferences preferences;
	private boolean isLoggedIn;
	private static int DELAYTIME = 1500;//延时时间

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		setINt();
		preferences = getSharedPreferences(Constant.SHARED_KARROBOT, Context.MODE_PRIVATE);
		isLoggedIn = true;
		pageJump();
	}

	private void pageJump() {
		//long flushTokenTimeStamp = preferences.getLong(Constant.KEY_FLUSH_TOKEN_TIME_STAMP, 0L);

		String EXTRA_USER_ID=preferences.getString(Constant.EXTRA_USER_ID, null);

		//if ( System.currentTimeMillis() > flushTokenTimeStamp) {
		//	preferences.edit().putString(Constant.KEY_FLUSH_TOKEN, "").commit();
		//	preferences.edit().putLong(Constant.KEY_FLUSH_TOKEN_TIME_STAMP, 0L).commit();
		if(EXTRA_USER_ID ==null  || ("0").equals(EXTRA_USER_ID)){
			isLoggedIn = false;
		}
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				Intent intent = null;
				if (!isLoggedIn) {
					intent = new Intent(SplashActivity.this, RegistAccountActivity.class);
				} else {
					intent = new Intent(SplashActivity.this, HomeActivity.class);
				}
				startActivity(intent);
				finish();
			}
		}, DELAYTIME);

	}
}
