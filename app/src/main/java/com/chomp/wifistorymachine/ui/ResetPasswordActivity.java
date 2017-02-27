package com.chomp.wifistorymachine.ui;

import android.app.Activity;
import android.os.Bundle;

import com.chomp.wifistorymachine.R;
import com.chomp.wifistorymachine.application.MyApplication;

public class ResetPasswordActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_passwd);
        MyApplication.getInstance().addActivity(this);

    }
}
