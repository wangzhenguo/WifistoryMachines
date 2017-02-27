package com.chomp.wifistorymachine.ui;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chomp.wifistorymachine.R;
import com.chomp.wifistorymachine.application.MyApplication;
import com.chomp.wifistorymachine.constants.Constant;
import com.chomp.wifistorymachine.model.BaseResponseBean;
import com.chomp.wifistorymachine.model.ResponseBean;
import com.chomp.wifistorymachine.okhttp.HttpUtils;
import com.chomp.wifistorymachine.okhttp.OkHttpUtils;
import com.chomp.wifistorymachine.util.JsonParseUtil;
import com.chomp.wifistorymachine.util.Toaster;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;


import static com.chomp.wifistorymachine.constants.Constant.EXTRA_USER_ID;


public class AddDeviceActivity extends BaseActivity{

    private Button go_to_airkiss;
    private Button go_to_bind;

    private Button go_to_unbind;

    private LinearLayout btn_back;

    private SharedPreferences preferences;
    private String PINCODE_ID;

    private final String UNBINDINGACCOUNT = "unbinding_account";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);
        MyApplication.getInstance().addActivity(this);
        setTitle(getString(R.string.configure_toy));

        go_to_airkiss=(Button) findViewById(R.id.go_to_airkiss);
        go_to_airkiss.setOnClickListener(this);
        go_to_bind=(Button) findViewById(R.id.go_to_bind);
        go_to_bind.setOnClickListener(this);

        go_to_unbind=(Button) findViewById(R.id.go_to_unbind);
        go_to_unbind.setOnClickListener(this);

        btn_back=(LinearLayout) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);

        preferences = getSharedPreferences(Constant.SHARED_KARROBOT, Context.MODE_PRIVATE);

        PINCODE_ID = preferences.getString(Constant.EXTRA_PINCODE_ID, null);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.go_to_airkiss:
                Intent in=new Intent();
                in.setClass(AddDeviceActivity.this,WifiNetworkingActivity.class);
                startActivity(in);
                break;
            case R.id.go_to_bind:
                Intent in2=new Intent();
                in2.setClass(AddDeviceActivity.this,BindingDeviceActivity.class);
                startActivity(in2);
                break;
            case R.id.go_to_unbind:
                boolean isLoggedIn=true;
                if(EXTRA_USER_ID ==null  || ("0").equals(EXTRA_USER_ID)){
                    isLoggedIn = false;
                }
                 if(isLoggedIn){
                     unBindingAccount(PINCODE_ID);
                 }else{
                     Toast.makeText(getApplicationContext(),getString(R.string.wifi_name_airkiss_ready),Toast.LENGTH_SHORT).show();
                 }
                break;
            case R.id.btn_back:
                onBackPressed();
                break;

        }
    }


    //绑定设备
    private void unBindingAccount(String veriCode) {
        HttpUtils.unbindDevice(veriCode,this,UNBINDINGACCOUNT,okCallBack);
    }



    private OkHttpUtils.OkCallBack okCallBack = new OkHttpUtils.OkCallBack(){
        public void onBefore(Request request, Object tag) {
            if (tag.toString().equals(UNBINDINGACCOUNT)) {
                showLoginDialog(getString(R.string.Un_Toylink_loading3));
            }
        }
        public void onAfter(Object tag) {

        }



        @Override
        public void onResponse(Call call, Response response,Object tag) {

        }

        public void onError(Call call, Response response, Exception e, Object tag) {
            dismissDialog();
        }


        public void onResponse(Object response, Object tag) {
            if(response!=null) {
                if (tag.toString().equals(UNBINDINGACCOUNT)) {
                   ResponseBean responseBean = JsonParseUtil.json2Object(response.toString(),ResponseBean.class);
                    if (responseBean != null) {
                        if(responseBean.getCode() !=null){
                            if(responseBean.getCode().equals(Constant.CODE_REQUEST_SUCC)){
                                SharedPreferences.Editor editor=preferences.edit();
                                editor.putString(Constant.EXTRA_PINCODE_ID,"0");
                                boolean b= editor.commit();
                                showResult(getString(R.string.un_binding_succ), true);
                            }else{
                                showMessage(responseBean);
                            }
                        }

                    }else {
                        showMessage(responseBean);
                    }
                }
            }else {
                dismissDialog();
            }
        }
    };

    private AlertDialog dialog;
    private LinearLayout layout_loading;
    private TextView text_loading;
    private TextView text_result;

    private void showLoginDialog(String info) {
        dialog = new AlertDialog.Builder(AddDeviceActivity.this).create();
        dialog.show();
        Window window = dialog.getWindow();
        window.setGravity(Gravity.TOP);
        window.setContentView(R.layout.dialog_loading);
        layout_loading = (LinearLayout) window.findViewById(R.id.layout_loading);
        ImageView img_loading = (ImageView) window.findViewById(R.id.img_loading);
        text_loading = (TextView) window.findViewById(R.id.text_loading);
        text_result = (TextView) window.findViewById(R.id.text_result);
        Animation rotateAnimation = AnimationUtils.loadAnimation(AddDeviceActivity.this, R.anim.rotate_360_anim);
        img_loading.startAnimation(rotateAnimation);
        text_loading.setText(info);
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
//                if (skip) {
//                    Intent intent = new Intent(AddDeviceActivity.this, HomeActivity.class);
//                    startActivity(intent);
//                    finish();
//                    overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
//                }
            }
        }, 1000);
    }

    private void dismissDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        leftToRight();
    }

    private void showMessage(Object object){
        BaseResponseBean baseResponseBean = (BaseResponseBean)object;
        dismissDialog();
        if(baseResponseBean==null){
            Toaster.showShortToast(AddDeviceActivity.this,"请求失败");
        }else {
            Toaster.showShortToast(AddDeviceActivity.this,baseResponseBean.getMessage());
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(UNBINDINGACCOUNT);
    }
}
