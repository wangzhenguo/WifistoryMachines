package com.chomp.wifistorymachine.ui;

import android.Manifest;
import android.R.color;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chomp.wifistorymachine.R;
import com.chomp.wifistorymachine.constants.Constant;
import com.chomp.wifistorymachine.model.BaseResponseBean;
import com.chomp.wifistorymachine.ui.chat.ConversationListFragment;
import com.chomp.wifistorymachine.util.Toaster;
import com.zhy.m.permission.MPermissions;

import static com.chomp.wifistorymachine.R.id.edit_account;

public class BaseActivity extends FragmentActivity implements OnClickListener {
	protected TextView title;

	protected  LinearLayout onBack;
	private static final int REQUECT_CODE_SDCARD = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(Constant.FINISH_APP);
		registerReceiver(finishReceiver, mFilter);

		initStatusBar();
	}

	BroadcastReceiver finishReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			switch (intent.getAction()) {
			case Constant.FINISH_APP:
				finish();
			break;
			}
		}
	};

	@SuppressLint("NewApi")
	public void initStatusBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {// 4.4 全透明状态栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {// 5.0 全透明实现
			Window window = getWindow();
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(getResources().getColor(color.transparent));
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			unregisterReceiver(finishReceiver);
		}catch (Exception e){

		}
	}

	@Override
	public void onClick(View v) {

		
	}

	public void setTitle(String titleStr) {
		Log.d("zcw", "===wzg===");
		title = (TextView) findViewById(R.id.text_title);
		title.setText(titleStr);
	}



	public void setBack() {
		onBack = (LinearLayout) findViewById(R.id.btn_back);
		onBack.setOnClickListener(this);

	}

	public void setINt() {

	}

	public void rightToLeft(){
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}

	public void leftToRight(){
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

	//隐藏虚拟键盘
	protected  void hideKeyboard(){
		try{
			View currentFocus= getCurrentFocus();
			if(currentFocus!=null){
				((InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(currentFocus.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	//显示虚拟键盘
	protected  void showKeyboard() {
		try{
			((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(null, InputMethodManager.SHOW_FORCED);
		}catch (Exception e){
			e.printStackTrace();
		}
	}


	public void setPermissions(String SetPermissions) {
		if (getSDKVersionNumber()>=23) {
			Check_Permissions(SetPermissions);//然后在回调中处理
			// setRecordingPermissions();
		}
	}



	private  int getSDKVersionNumber() {
		int sdkVersion;
		try {
			sdkVersion = Integer.valueOf(android.os.Build.VERSION.SDK);
			Log.d("wzg","sdkVersion=="+sdkVersion);
		} catch (NumberFormatException e) {
			sdkVersion = 0;
		}
		return sdkVersion;
	}

	//检查权限
	private void Check_Permissions(String SetPermissions){
		if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {
			if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.RECORD_AUDIO)) {
				//已经禁止提示了
				Toast.makeText(this, "您已禁止该权限，需要重新开启。", Toast.LENGTH_SHORT).show();

				Log.i("wzg", "您已禁止该权限，需要重新开启");
			}
			requestCameraPermission(SetPermissions);
		} else {
			Log.i("wzg", "onClick granted");
		}
	}

	//申请授权
	private void requestCameraPermission(String SetPermissions) {
		MPermissions.requestPermissions(this, REQUECT_CODE_SDCARD, SetPermissions);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
	{
		//   MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		if (requestCode == REQUECT_CODE_SDCARD) {
			int grantResult = grantResults[0];
			boolean granted = grantResult == PackageManager.PERMISSION_GRANTED;
			Log.i("wzg", "onRequestPermissionsResult granted=" + granted);
			if(!granted) {
				Toast.makeText(this, "您已禁止权限,请去管理app权限里面打开", Toast.LENGTH_SHORT).show();
			}
		}
	}

}
