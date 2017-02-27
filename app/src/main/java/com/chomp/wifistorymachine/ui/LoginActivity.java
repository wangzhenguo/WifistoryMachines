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
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
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

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;



public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private final String LOGINREQUEST = "login_request";
    private final String GETTOKENREQUEST = "get_token_request";
    private final String GETKAR = "get_kar";
    private final String GETUSERINFO = "get_user_info";
    private SharedPreferences preferences;

    LinearLayout father;


    private EditText edit_account;
    private EditText edit_password;

    private ImageView btn_delete;
    private ImageView btn_show_passwd;

    private Button btn_login;
    private Button btn_regist;

    private TextView text_reset_password;

    private AlertDialog dialog;
    private LinearLayout layout_loading;
    private TextView text_loading;
    private TextView text_result;

    private long key_flush_token_time_stamp;
    private String key_flush_token;
    private String key_access_token;
    private String phone_num;

 //   private LinearLayout btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        MyApplication.getInstance().addActivity(this);
        setTitle(getString(R.string.login));

        preferences = getSharedPreferences(Constant.SHARED_KARROBOT, Context.MODE_PRIVATE);
        initView();
        setBack();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(LOGINREQUEST);
        OkHttpUtils.getInstance().cancelTag(GETTOKENREQUEST);
        OkHttpUtils.getInstance().cancelTag(GETKAR);
    }

    public void initView() {

        father= (LinearLayout) findViewById(R.id.father);
        father.setOnClickListener(this);

        edit_account = (EditText) findViewById(R.id.edit_account);
        edit_password = (EditText) findViewById(R.id.edit_password);

        btn_delete = (ImageView) findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(this);

        btn_show_passwd = (ImageView) findViewById(R.id.btn_show_passwd);
        btn_show_passwd.setOnClickListener(this);

        btn_regist = (Button) findViewById(R.id.btn_regist);
        btn_regist.setOnClickListener(this);

        text_reset_password = (TextView) findViewById(R.id.text_reset_password);
        text_reset_password.setOnClickListener(this);

      //  btn_back=(LinearLayout) findViewById(R.id.btn_back);
      //  btn_back.setOnClickListener(this);

        btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);
        btn_login.setEnabled(false);

        edit_account.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
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
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.btn_delete:
                edit_account.setText("");
                break;
            case R.id.btn_regist:
                Intent regist_intent = new Intent(LoginActivity.this, RegistAccountActivity.class);
                startActivity(regist_intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.text_reset_password:
                intent.setClass(LoginActivity.this, ResetPasswordActivity.class);
                intent.putExtra("type",0);//0 重置密码 1 修改密码
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
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
            case R.id.btn_login:
                Utils.hideInput(LoginActivity.this);
                String account = edit_account.getText().toString().trim();
                String passwd = edit_password.getText().toString().trim();
                if (Utils.checkPhoneNum(account)) {
                    login(account, passwd);
                    hideKeyboard();
                } else {
                    Toaster.showShortToast(LoginActivity.this, R.string.phone_num_wrong);
                }
                break;
            case R.id.father:
                hideKeyboard();
                break;
            case R.id.btn_back:
                onBackPressed();
                break;
            default:
                break;
        }
    }



    //隐藏虚拟键盘
    public  void hideKeyboard() {
        try{
            View currentFocus=LoginActivity.this.getCurrentFocus();
            if(currentFocus!=null){
                ((InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(currentFocus.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        super.onBackPressed();
    }
    //登录
    private void login(String account, String password) {
        phone_num = account;
        HttpUtils.login(this,LOGINREQUEST,account,password,okCallBack);
    }
    //获取token
    private void getAccessToken(String flushToken) {
        HttpUtils.getAccessToken(flushToken,this,GETTOKENREQUEST,okCallBack);
    }
    //创建账号
    private void  getKar(String accessToken) {
        HttpUtils.getKar(accessToken,this,GETKAR,okCallBack);
    }

    //获取用户信息
    private void getUserInfo(){
        HttpUtils.getUserInfo(this,GETUSERINFO,okCallBack);
    }


    public void checkInputState() {
        boolean clickAble = true;
        if (TextUtils.isEmpty(edit_account.getText().toString()) || edit_account.getText().length() < 11) {
            clickAble = false;
        }
        if (TextUtils.isEmpty(edit_password.getText().toString()) || edit_password.getText().length() < 6) {
            clickAble = false;
        }
        if (clickAble) {
            btn_login.setEnabled(true);
        } else {
            btn_login.setEnabled(false);
        }
    }

    private void showLoginDialog(String info) {
        if(dialog!=null){
            dialog.dismiss();
            dialog = null;
        }
        dialog = new AlertDialog.Builder(LoginActivity.this).create();
        dialog.show();
        Window window = dialog.getWindow();
        window.setGravity(Gravity.TOP);
        window.setContentView(R.layout.dialog_loading);
        layout_loading = (LinearLayout) window.findViewById(R.id.layout_loading);
        ImageView img_loading = (ImageView) window.findViewById(R.id.img_loading);
        text_loading = (TextView) window.findViewById(R.id.text_loading);
        text_result = (TextView) window.findViewById(R.id.text_result);
        Animation rotateAnimation = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.rotate_360_anim);
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
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                    rightToLeft();
                }
            }
        }, 10);
    }

    private OkHttpUtils.OkCallBack okCallBack = new OkHttpUtils.OkCallBack(){

        @Override
        public void onBefore(Request request, Object tag) {
            Log.i("TAG","loginLog  onBefore:"+tag.toString());
            if(tag.toString().equals(LOGINREQUEST)){
                showLoginDialog(getString(R.string.logining));
            }
        }

        @Override
        public void onAfter(Object tag) {
            Log.i("TAG","loginLog onAfter:"+tag.toString());
            if(tag.toString().equals(GETKAR)){
                dismissDialog();
            }
        }

        @Override
        public void onError(Call call, Response response, Exception e, Object tag) {
//			Log.i("TAG","onAfter:"+tag.toString()+"-----response:"+response.toString()+"----e:"+e.toString());
            Log.i("TAG","loginLog onError:"+tag.toString());
            if(tag.toString().equals(LOGINREQUEST)||tag.toString().equals(GETTOKENREQUEST)){
                dismissDialog();
            }
        }

        @Override
        public void onResponse(Call call, Response response,Object tag) {

        }

        @Override
        public void onResponse(Object response, Object tag) {
            Log.i("TAG","loginLog onResponse:"+tag.toString());
            Log.i("TAG","onResponse:"+tag.toString()+"-----response:"+response.toString());
            if(response!=null) {
                if (tag.toString().equals(LOGINREQUEST)) {

//                    ResponseBean responseBean = JsonParseUtil.json2Object(response.toString(),ResponseBean.class);
//                    if(responseBean != null&&responseBean.getCode().equals(Constant.CODE_REQUEST_SUCC)){
//                        key_flush_token_time_stamp =  System.currentTimeMillis() + responseBean.getResult().getValidTime();
//                        key_flush_token = responseBean.getResult().getFlushToken();
//                        getAccessToken(responseBean.getResult().getFlushToken());
//                    }else {
//                        dismissDialog();
//                        showMessage(responseBean);
//                    }

                    ResponseBean responseBean = JsonParseUtil.json2Object(response.toString(),ResponseBean.class);
                    if(responseBean != null&&responseBean.getCode().equals(Constant.CODE_REQUEST_SUCC)){
                        showResult(getString(R.string.login_succ), true);
                        SharedPreferences.Editor editor=preferences.edit();
                        Log.d("zcw","==Userid="+responseBean.getUserid());
                        Log.d("zcw","==Pcode="+responseBean.getPcode());
                        editor.putString(Constant.EXTRA_USER_ID,responseBean.getUserid());
                        editor.putString(Constant.EXTRA_PINCODE_ID,responseBean.getPcode());
                        boolean b= editor.commit();
                        showResult(getString(R.string.login_succ), true);

                    }else {
                        dismissDialog();
                        showMessage(responseBean);
                    }
                }else if (tag.toString().equals(GETTOKENREQUEST)){
                    ResponseBean responseBean = JsonParseUtil.json2Object(response.toString(),ResponseBean.class);
                    if(responseBean != null&&responseBean.getCode().equals(Constant.CODE_REQUEST_SUCC)){
                        key_access_token =  responseBean.getResult().getAccessToken();
                        getKar(responseBean.getResult().getAccessToken());
                    }else {
                        dismissDialog();
                        showMessage(responseBean);
                    }
                }else if(tag.toString().equals(GETKAR)){
//                    KarAccountBean karAccountBean = JsonParseUtil.json2Object(response.toString(),KarAccountBean.class);
//                    if(karAccountBean!=null){
//                        if(karAccountBean.getErr() == 0){
//                            SharedPreferences.Editor editor=preferences.edit();
//                            editor.putLong(Constant.KEY_FLUSH_TOKEN_TIME_STAMP,key_flush_token_time_stamp);
//                            editor.putString(Constant.KEY_FLUSH_TOKEN, key_flush_token);
//                            editor.putString(Constant.SHARED_USER_TOKEN,key_access_token);
//                            editor.putString(Constant.SHARED_USER_PASSPORT,String.valueOf(karAccountBean.getAccount()));
//                            editor.putString(Constant.SHARED_PHONE_NUM,phone_num);
//                            boolean b= editor.commit();
//                            getUserInfo();
//                            showResult(getString(R.string.login_succ), true);
//
//                            EasemobHelper.getInstance().registEasemobAccount(LoginActivity.this,String.valueOf(karAccountBean.getAccount()));
//                        }else {
//                            dismissDialog();
//                        }
//                    }else {
//                        dismissDialog();
//                        Toaster.showShortToast(LoginActivity.this,"请求失败");
//                    }

                }else if(tag.toString().equals(GETUSERINFO)){
//                    UserInfoBean userInfoBean = JsonParseUtil.json2Object(response.toString(),UserInfoBean.class);
//                    if(userInfoBean!=null&&userInfoBean.getUserInfo()!=null&&userInfoBean.getUserInfo().size()>0){
//                        UserInfoUtils.setUserInfo(LoginActivity.this,userInfoBean.getUserInfo().get(0));
//                    }
                }
            }else{
                dismissDialog();
            }
        }
    };
    private void showMessage(Object object){
        BaseResponseBean baseResponseBean = (BaseResponseBean)object;
        if(baseResponseBean==null){
            Toaster.showShortToast(LoginActivity.this,"请求失败");
        }else {
            Toaster.showShortToast(LoginActivity.this,baseResponseBean.getMessage());
        }
    }

}

