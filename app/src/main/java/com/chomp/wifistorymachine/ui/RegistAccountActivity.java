package com.chomp.wifistorymachine.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chomp.wifistorymachine.R;
import com.chomp.wifistorymachine.application.MyApplication;
import com.chomp.wifistorymachine.constants.Constant;
import com.chomp.wifistorymachine.model.BaseResponseBean;
import com.chomp.wifistorymachine.model.ResponseBean;
import com.chomp.wifistorymachine.okhttp.HttpUtils;
import com.chomp.wifistorymachine.okhttp.OkHttpUtils;
import com.chomp.wifistorymachine.util.JsonParseUtil;
import com.chomp.wifistorymachine.util.Toaster;
import com.chomp.wifistorymachine.util.Utils;
import com.chomp.wifistorymachine.view.TimeButton;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;


public class RegistAccountActivity extends BaseActivity {


	LinearLayout father;
	private EditText edit_name;
	private EditText edit_account;
	private EditText edit_code;
	private TimeButton btn_get_code;
	private EditText edit_password;
	private ImageView btn_delete_name;
	private ImageView btn_delete;
	private ImageView btn_show_passwd;

	private Button btn_regist;
	private Button btn_login;

	private final String REGISTACCOUNT = "regist_account";
	private final String CHECKACCOUNT = "check_account";
	private final String CODESENDED = "code_sended";

	private AlertDialog dialog;
	private LinearLayout layout_loading;
	private TextView text_loading;
	private TextView text_result;

	private String username;
	private String phoneNum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_regist);
		MyApplication.getInstance().addActivity(this);
		setTitle(getString(R.string.regist));
		initView();
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		OkHttpUtils.getInstance().cancelTag(REGISTACCOUNT);
		OkHttpUtils.getInstance().cancelTag(CHECKACCOUNT);
		OkHttpUtils.getInstance().cancelTag(CODESENDED);
	}
	private void initView() {

		father= (LinearLayout) findViewById(R.id.father);
		father.setOnClickListener(this);
		
		edit_name = (EditText) findViewById(R.id.edit_name);
		edit_account = (EditText) findViewById(R.id.edit_account);
		edit_code = (EditText) findViewById(R.id.edit_code);
		edit_password = (EditText) findViewById(R.id.edit_password);

		btn_get_code = (TimeButton) findViewById(R.id.btn_get_code);
		btn_get_code.setOnClickListener(this);

		btn_regist = (Button) findViewById(R.id.btn_regist);
		btn_regist.setOnClickListener(this);
		btn_regist.setEnabled(false);

		btn_login = (Button) findViewById(R.id.btn_login);
		btn_login.setOnClickListener(this);

		btn_delete_name = (ImageView) findViewById(R.id.btn_delete_name);
		btn_delete_name.setOnClickListener(this);

		btn_delete = (ImageView) findViewById(R.id.btn_delete);
		btn_delete.setOnClickListener(this);

		btn_show_passwd = (ImageView) findViewById(R.id.btn_show_passwd);
		btn_show_passwd.setOnClickListener(this);

		edit_name.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				checkInputState();
				if (s.length() > 0) {
					btn_delete_name.setVisibility(View.VISIBLE);
				} else {
					btn_delete_name.setVisibility(View.GONE);
				}
			}
		});

		
		edit_account.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				checkInputState();
				if (s.length() > 0) {
					btn_delete.setVisibility(View.VISIBLE);
				} else {
					btn_delete.setVisibility(View.GONE);
				}
			}
		});

		edit_password.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				checkInputState();
				if (s.length() > 0) {
					btn_show_passwd.setVisibility(View.VISIBLE);
				} else {
					btn_show_passwd.setVisibility(View.GONE);
				}
			}
		});

		edit_code.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				checkInputState();
			}
		});
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		
		case R.id.btn_delete_name:
			edit_name.setText("");
		break;
		
		case R.id.btn_delete:
			edit_account.setText("");
		break;
		case R.id.btn_show_passwd:
			if (edit_password.getTransformationMethod() == HideReturnsTransformationMethod.getInstance()) {
				btn_show_passwd.setSelected(false);
				edit_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
				edit_password.setSelection(edit_password.getText().toString().length());
			} else {
				btn_show_passwd.setSelected(true);
				edit_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
				edit_password.setSelection(edit_password.getText().toString().length());
			}
		break;
		case R.id.btn_get_code:
			username = edit_account.getText().toString().trim();
			phoneNum = edit_account.getText().toString().trim();
			
			if (TextUtils.isEmpty(username)) {
				Toaster.showShortToast(RegistAccountActivity.this, R.string.name_num_empty);
			} else if (TextUtils.isEmpty(phoneNum)) {
				Toaster.showShortToast(RegistAccountActivity.this, R.string.phone_num_empty);
			}
			
			else {
				if (Utils.checkPhoneNum(phoneNum)) {
					checkAccount(phoneNum);
				} else {
					Toaster.showShortToast(RegistAccountActivity.this, R.string.phone_num_wrong);
				}
			}
		break;
		case R.id.btn_regist:
			username = edit_name.getText().toString().trim();
			phoneNum = edit_account.getText().toString().trim();
			String veriCode = edit_code.getText().toString().trim();
			String password = edit_password.getText().toString().trim();
			Utils.hideInput(RegistAccountActivity.this);
			registAccount(username,phoneNum, veriCode, password);
		break;
		case R.id.btn_login:
			intent.setClass(RegistAccountActivity.this, LoginActivity.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
		break;
		case R.id.father:
			 hideKeyboard();
			break;
		default:
		break;
		}
	}


	//隐藏虚拟键盘
	public  void hideKeyboard() {
		try{
			View currentFocus=RegistAccountActivity.this.getCurrentFocus();
			if(currentFocus!=null){
				((InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(currentFocus.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}


	//注册账号
	private void registAccount(String username,String phoneNum, String veriCode, String password) {
		HttpUtils.registAccount(username,phoneNum,veriCode,password,this,REGISTACCOUNT,okCallBack);
	}
	//获取验证码
	private void getVeriCode(String phoneNum) {
		HttpUtils.getVeriCode(phoneNum,this,CODESENDED,okCallBack);
	}
	//检测账号是否存在
	private void checkAccount(String account) {
		HttpUtils.checkAccount(account,this,CHECKACCOUNT,okCallBack);
	}

	private void showLoginDialog(String info) {
		dialog = new AlertDialog.Builder(RegistAccountActivity.this).create();
		dialog.show();
		Window window = dialog.getWindow();
		window.setGravity(Gravity.TOP);
		window.setContentView(R.layout.dialog_loading);
		layout_loading = (LinearLayout) window.findViewById(R.id.layout_loading);
		ImageView img_loading = (ImageView) window.findViewById(R.id.img_loading);
		text_loading = (TextView) window.findViewById(R.id.text_loading);
		text_result = (TextView) window.findViewById(R.id.text_result);
		Animation rotateAnimation = AnimationUtils.loadAnimation(RegistAccountActivity.this, R.anim.rotate_360_anim);
		img_loading.startAnimation(rotateAnimation);
		text_loading.setText(info);
	}

	private void dismissDialog() {
		if (dialog != null) {
			dialog.dismiss();
		}
	}

	private void showResult(String info, final boolean skip) {
		if (dialog != null) {
			layout_loading.setVisibility(View.GONE);
			text_result.setText(info);
			text_result.setVisibility(View.VISIBLE);
		}
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				dismissDialog();
				if (skip) {
					Intent intent = new Intent(RegistAccountActivity.this, LoginActivity.class);
					startActivity(intent);
					finish();
					overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
				}
			}
		}, 1000);
	}

	private void checkInputState() {
		boolean clickAble = true;
		
		if (TextUtils.isEmpty(edit_name.getText().toString().trim()) || edit_name.getText().length() < 1) {
			clickAble = false;
		}
		if (TextUtils.isEmpty(edit_account.getText().toString().trim()) || edit_account.getText().length() < 11) {
			clickAble = false;
		}
		if (TextUtils.isEmpty(edit_code.getText().toString().trim()) || edit_code.getText().length() < 4) {
			clickAble = false;
		}
		if (TextUtils.isEmpty(edit_password.getText().toString().trim()) || edit_password.getText().length() < 6) {
			clickAble = false;
		}
		if (clickAble) {
			btn_regist.setEnabled(true);
		} else {
			btn_regist.setEnabled(false);

		}
	}

	private OkHttpUtils.OkCallBack okCallBack = new OkHttpUtils.OkCallBack(){
		public void onBefore(Request request, Object tag) {
			if (tag.toString().equals(REGISTACCOUNT)) {
				showLoginDialog(getString(R.string.registing));
			}
		}
		public void onAfter(Object tag) {

		}


		public void onError(Call call, Response response, Exception e, Object tag) {
			dismissDialog();
		}



		@Override
		public void onResponse(Call call, Response response,Object tag) {

		}

		public void onResponse(Object response, Object tag) {
			if(response!=null) {
				if (tag.toString().equals(REGISTACCOUNT)) {
					ResponseBean responseBean = JsonParseUtil.json2Object(response.toString(),ResponseBean.class);
					if (responseBean != null) {
						if(responseBean.getCode() !=null){
							if(responseBean.getCode().equals(Constant.CODE_REQUEST_SUCC)){
								showResult(getString(R.string.regist_succ), true);
							}else{
								showResult(getString(R.string.regist_fail), true);
							}
						}

					}else {
						showMessage(responseBean);
					}
				}else if(tag.toString().equals(CHECKACCOUNT)){

					ResponseBean responseBean = JsonParseUtil.json2Object(response.toString(),ResponseBean.class);
					if (responseBean != null&&responseBean.getCode().equals(Constant.CODE_ACCOUNT_UNEXIT)) {
						btn_get_code.startCountdown();
						getVeriCode(phoneNum);
						edit_code.setText("8888");//目前写死验证码 ，服务器端还未实现动态验证码
					}else {
						showMessage(responseBean);
					}
				}else if(tag.toString().equals(CODESENDED)){
					ResponseBean responseBean = JsonParseUtil.json2Object(response.toString(),ResponseBean.class);
					showMessage(responseBean);
				}
			}else {
				dismissDialog();
			}
		}
	};


	@Override
	public void onBackPressed() {
		super.onBackPressed();
		leftToRight();
	}

	private void showMessage(Object object){
		BaseResponseBean baseResponseBean = (BaseResponseBean)object;
		dismissDialog();
		if(baseResponseBean==null){
			Toaster.showShortToast(RegistAccountActivity.this,"请求失败");
		}else {
			Toaster.showShortToast(RegistAccountActivity.this,baseResponseBean.getMessage());
		}
	}
}
