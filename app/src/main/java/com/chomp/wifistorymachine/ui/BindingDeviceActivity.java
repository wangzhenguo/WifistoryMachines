package com.chomp.wifistorymachine.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
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
import com.chomp.wifistorymachine.zxing.activity.CaptureActivity;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;



public class BindingDeviceActivity extends BaseActivity {
    private EditText network_pincord_view;
    private SharedPreferences preferences;
    private FrameLayout network_pincord_btn;
    private String PINCODE_ID;
    private Button Binding_btn;

    private LinearLayout btn_back;
    private final String BINDINGACCOUNT = "binding_account";

    private AlertDialog dialog;
    private LinearLayout layout_loading;
    private TextView text_loading;
    private TextView text_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binding_device);
        MyApplication.getInstance().addActivity(this);
        setTitle(getString(R.string.binding_toy));
        initView();
    }

    private void initView(){
        network_pincord_view=(EditText)findViewById(R.id.network_pincord_view);
        network_pincord_btn=(FrameLayout)findViewById(R.id.network_pincord_btn);
        network_pincord_btn.setOnClickListener(this);

        Binding_btn=(Button)findViewById(R.id.Binding_btn);
        Binding_btn.setOnClickListener(this);

        btn_back=(LinearLayout)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);

        preferences = getSharedPreferences(Constant.SHARED_KARROBOT, Context.MODE_PRIVATE);

        PINCODE_ID = preferences.getString(Constant.EXTRA_PINCODE_ID, null);

        boolean isLoggedIn=true;
        if(PINCODE_ID ==null  || ("0").equals(PINCODE_ID)){
            isLoggedIn = false;
        }
        if(isLoggedIn){
            network_pincord_view.setText(PINCODE_ID);
        }

        network_pincord_view.addTextChangedListener(new TextWatcher() {
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

    private void checkInputState() {
        boolean clickAble = true;

        if (TextUtils.isEmpty(network_pincord_view.getText().toString().trim()) || network_pincord_view.getText().length() < 1) {
            clickAble = false;
        }

        if (clickAble) {
            Binding_btn.setEnabled(true);
        } else {
            Binding_btn.setEnabled(false);

        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()){
            case R.id.Binding_btn:
                BindingAccount(network_pincord_view.getText().toString());
                break;
            case R.id.network_pincord_btn:
                Intent resultIntent=new Intent(BindingDeviceActivity.this,CaptureActivity.class);
                startActivityForResult(resultIntent, 0);
                break;

            case R.id.btn_back:
                onBackPressed();
                //handler.sendEmptyMessage(SENDING_ok);
                break;
        }

    }

    /**
     * 複寫onActivityResult，這個方法
     * 是要等到SimpleTaskActivity點了提交過後才會執行的
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //可以根據多個請求代碼來作相應的操作
        if(RESULT_OK==resultCode)
        {
            String result=data.getExtras().getString("result");
            network_pincord_view.setText(result);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    private void showLoginDialog(String info) {
        dialog = new AlertDialog.Builder(BindingDeviceActivity.this).create();
        dialog.show();
        Window window = dialog.getWindow();
        window.setGravity(Gravity.TOP);
        window.setContentView(R.layout.dialog_loading);
        layout_loading = (LinearLayout) window.findViewById(R.id.layout_loading);
        ImageView img_loading = (ImageView) window.findViewById(R.id.img_loading);
        text_loading = (TextView) window.findViewById(R.id.text_loading);
        text_result = (TextView) window.findViewById(R.id.text_result);
        Animation rotateAnimation = AnimationUtils.loadAnimation(BindingDeviceActivity.this, R.anim.rotate_360_anim);
        img_loading.startAnimation(rotateAnimation);
        text_loading.setText(info);
    }

    //绑定设备
    private void BindingAccount(String veriCode) {
        HttpUtils.bindDevice(veriCode,this,BINDINGACCOUNT,okCallBack);
    }

    private OkHttpUtils.OkCallBack okCallBack = new OkHttpUtils.OkCallBack(){
        public void onBefore(Request request, Object tag) {
            if (tag.toString().equals(BINDINGACCOUNT)) {
                showLoginDialog(getString(R.string.Toylink_loading3));
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
                if (tag.toString().equals(BINDINGACCOUNT)) {
                    ResponseBean responseBean = JsonParseUtil.json2Object(response.toString(),ResponseBean.class);
                    if (responseBean != null) {
                        if(responseBean.getCode() !=null){
                            if(responseBean.getCode().equals(Constant.CODE_REQUEST_SUCC)){
                                try {
                                    JSONObject json = new JSONObject(response.toString());
                                    String sets = json.getString("data");
                                    String psets = json.getString("pid");
                                    Log.d("zcw","==psets=="+psets);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                SharedPreferences.Editor editor=preferences.edit();
                                editor.putString(Constant.EXTRA_PINCODE_ID,network_pincord_view.getText().toString());
                                boolean b= editor.commit();
                                showResult(getString(R.string.binding_succ), true);
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
                    Intent intent = new Intent(BindingDeviceActivity.this, HomeActivity.class);
                    startActivity(intent);
                    MyApplication.getInstance().exitActivity();
                    overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                }
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
            Toaster.showShortToast(BindingDeviceActivity.this,"请求失败");
        }else {
            Toaster.showShortToast(BindingDeviceActivity.this,baseResponseBean.getMessage());
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(BINDINGACCOUNT);
    }


}
