package com.chomp.wifistorymachine.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.chomp.wifistorymachine.R;
import com.chomp.wifistorymachine.application.MyApplication;
import com.chomp.wifistorymachine.constants.Constant;
import com.chomp.wifistorymachine.zxing.activity.CaptureActivity;
import com.chomp.wifistorymachine.zxing.activity.SCCtlOps;
import com.realtek.simpleconfiglib.SCLibrary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WifiNetworkingActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout linear_pin;
    private SharedPreferences preferences;
    private EditText network_name_view;
    private EditText network_password_view;

    private FrameLayout network_connect_btn;
    private FrameLayout network_pincord_btn;
    private EditText network_pincord_view;
    private Button config_bt;
    private RelativeLayout RelativeL_wifi;
    private LinearLayout LinearL_top_wifi;
    private ImageView iv_link;
    private LinearLayout btn_back;

    private String PINCODE_ID;
    public static final int configTimeout = 75000;//60s
    private boolean TimesupFlag_cfg = false;
    private SCLibrary SCLib = new SCLibrary();
    private Animation operatingAnim=null;

    private String wifiName,wifiPasswd,pincode;
    private Thread mThread;
    private  final int SENDING_OVER = 1;
    private  final int SENDING_ok = 2;



    private int msg_sta=0;

    static {
        System.loadLibrary("simpleconfiglib");
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bm_airkiss_ui);
        setTitle(getString(R.string.wifi_airkiss_ready));
        MyApplication.getInstance().addActivity(this);
        initView();
        setPermissions(Manifest.permission.CAMERA);
        setPermissions("android.hardware.camera");
        setPermissions("android.hardware.camera.autofocus");
    }

    private void initView(){
        linear_pin=(LinearLayout)findViewById(R.id.linear_pin);
        network_name_view=(EditText)findViewById(R.id.network_name_view);
        network_password_view=(EditText)findViewById(R.id.network_password_view);
        network_pincord_view=(EditText)findViewById(R.id.network_pincord_view);

        network_connect_btn=(FrameLayout)findViewById(R.id.network_connect_btn);
        network_connect_btn.setOnClickListener(this);

        network_pincord_btn=(FrameLayout)findViewById(R.id.network_pincord_btn);
        network_pincord_btn.setOnClickListener(this);
        config_bt=(Button)findViewById(R.id.config_bt);
        config_bt.setOnClickListener(this);

        RelativeL_wifi=(RelativeLayout)findViewById(R.id.RelativeL_wifi);

        LinearL_top_wifi=(LinearLayout)findViewById(R.id.LinearL_top_wifi);

        iv_link=(ImageView)findViewById(R.id.iv_link);

        btn_back=(LinearLayout)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);

        preferences = getSharedPreferences(Constant.EXTRA_PINCODE_ID, Context.MODE_PRIVATE);
        PINCODE_ID = preferences.getString(Constant.EXTRA_PINCODE_ID, null);

        boolean isLoggedIn=true;
        if(PINCODE_ID ==null  || ("0").equals(PINCODE_ID)){
            isLoggedIn = false;
        }
        if(isLoggedIn){
            network_pincord_view.setText(PINCODE_ID);
        }

        SCLib.rtk_sc_init();
        //wifi manager init
        SCLib.WifiInit(this);
        SCLib.TreadMsgHandler = new MsgHandler();

        operatingAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_360_anim);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);

        network_name_view.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkInputState();

            }

        });

        network_password_view.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkInputState();

            }

        });

        network_pincord_view.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkInputState();

            }

        });
    }


    private String getConnectWifiSsid(){
        WifiManager wifiMgr = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        int wifiState = wifiMgr.getWifiState();
        WifiInfo info = wifiMgr.getConnectionInfo();
        String wifiname = info != null ? info.getSSID() : null;
        if(wifiname.indexOf("\"")==0) wifiname = wifiname.substring(1,wifiname.length());   //去掉第一个 "
        if(wifiname.lastIndexOf("\"")==(wifiname.length()-1)) wifiname = wifiname.substring(0,wifiname.length()-1);  //去掉最后一个 "
        return wifiname;
    }


    @Override
    protected void onResume() {
        super.onResume();
        String ddd=getConnectWifiSsid();
        network_name_view.setText(ddd);
    }




    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private long exitTime = 0;
    public void exit() {

//        if ((System.currentTimeMillis() - exitTime) > 1000) {
//            Toast.makeText(getApplicationContext(), "再按一次退出程序",
//                    Toast.LENGTH_SHORT).show();
//            exitTime = System.currentTimeMillis();
//        } else {
//            finish();
//        }
        if(LinearL_top_wifi.getVisibility()==View.VISIBLE){

            onBackPressed();
        }else if(RelativeL_wifi.getVisibility()==View.VISIBLE){
            msg_sta=0;
            SCLibrary.ProfileSendTimeIntervalMs = 200; //200ms
				/* Time interval(ms) between sending two packets. */
            SCLibrary.PacketSendTimeIntervalMs  = 10; //10ms
				/* Each packet sending counts. */
            SCLibrary.EachPacketSendCounts = 1;
            SCLib.rtk_sc_stop();
            handler.sendEmptyMessage(SENDING_ok);
        }
    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()){
            case R.id.network_connect_btn:
                Intent intent =  new Intent(Settings.ACTION_WIFI_SETTINGS);
                startActivity(intent);
                break;

            case R.id.network_pincord_btn:
                Intent resultIntent=new Intent(WifiNetworkingActivity.this,CaptureActivity.class);
                startActivityForResult(resultIntent, 0);
                break;

            case R.id.config_bt:
                wifiName= network_name_view.getText().toString();
                wifiPasswd=network_password_view.getText().toString();
                pincode=network_pincord_view.getText().toString();
                if(wifiName !=null && wifiPasswd !=null && pincode !=null){
                    mThread = new Thread(){
                        public void run() {

                            handler.sendEmptyMessage(SENDING_OVER);
                            Configure_action(wifiName, wifiPasswd, pincode);
                        }
                    }; /* 10ms */
                    mThread.start();
                }
                break;
            case R.id.btn_back:
                msg_sta=0;
                SCLibrary.ProfileSendTimeIntervalMs = 200; //200ms
				/* Time interval(ms) between sending two packets. */
                SCLibrary.PacketSendTimeIntervalMs  = 10; //10ms
				/* Each packet sending counts. */
                SCLibrary.EachPacketSendCounts = 1;
                SCLib.rtk_sc_stop();
                handler.sendEmptyMessage(SENDING_ok);
                break;
        }

    }

    public void checkInputState() {
        boolean clickAble = true;
        if (TextUtils.isEmpty(network_name_view.getText().toString())) {
            clickAble = false;
        }
        if (TextUtils.isEmpty(network_password_view.getText().toString())) {
            clickAble = false;
        }
        if (TextUtils.isEmpty(network_pincord_view.getText().toString())) {
            clickAble = false;
        }
        if (clickAble) {
            config_bt.setEnabled(true);
        } else {
            config_bt.setEnabled(false);
        }
    }



    private Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            if (msg.what == SENDING_OVER) {
              //  Toast.makeText(getApplicationContext(), "debug调试提示开始连接",Toast.LENGTH_SHORT).show();
                if (operatingAnim != null) {
                    iv_link.startAnimation(operatingAnim);
                }

                LinearL_top_wifi.setVisibility(View.GONE);
                RelativeL_wifi.setVisibility(View.VISIBLE);

            }

            if (msg.what == SENDING_ok) {
                if(LinearL_top_wifi.getVisibility()==View.GONE){
                    if( msg_sta==1){
                        Toast.makeText(getApplicationContext(), "联网超时,检查故事机是否进入联网状态，稍后重试",Toast.LENGTH_LONG).show();
                    }else if(msg_sta==2){
                        Toast.makeText(getApplicationContext(), "联网成功",Toast.LENGTH_SHORT).show();
                    }

                    if (operatingAnim != null) {
                        iv_link.clearAnimation();
                    }
                    RelativeL_wifi.setVisibility(View.GONE);
                    LinearL_top_wifi.setVisibility(View.VISIBLE);

                }else{
                    onBackPressed();
                }
            }
        };
    };



    private void Configure_action( String ssid, String password, String rollcode)
    {
        int stepOneTimeout = 30000;
        int connect_count = 200;

        //get wifi ip
        int wifiIP = SCLib.WifiGetIpInt();//55504122
        while(connect_count>0 && wifiIP==0){
            wifiIP = SCLib.WifiGetIpInt();
            connect_count--;
        }
        if(wifiIP == 0){
            //Toast.makeText(MyApplication.appContext, "Allocating IP, please wait a moment", Toast.LENGTH_SHORT).show();
            return;
        }



        SCLib.rtk_sc_reset();
        SCLib.rtk_sc_set_default_pin("");
        SCLib.rtk_sc_set_pin(rollcode);
        SCLib.rtk_sc_set_ssid(ssid);
        SCLib.rtk_sc_set_password(password);

        Log.d("zcw","rollcode="+rollcode + ",ssid="+ssid + ",password=="+password);

        TimesupFlag_cfg = false;

        SCLib.rtk_sc_set_ip(wifiIP);
        SCLib.rtk_sc_build_profile();

			/* Profile(SSID+PASSWORD, contain many packets) sending total time(ms). */
        SCLibrary.ProfileSendTimeMillis = configTimeout;
        //==================== 1 ========================= 30s
			/* Time interval(ms) between sending two profiles. */
        SCLibrary.ProfileSendTimeIntervalMs = 50; //50ms
			/* Time interval(ms) between sending two packets. */
        SCLibrary.PacketSendTimeIntervalMs  = 10; //0ms
			/* Each packet sending counts. */
        SCLibrary.EachPacketSendCounts = 1;

        //exception action
        exception_action();

        SCLib.rtk_sc_start();
        int watchCount = 0;
        try {
            do{
                Thread.sleep(1000);
                watchCount+=1000;
            }while(TimesupFlag_cfg==false && watchCount<stepOneTimeout);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //==================== 2 =========================
        if(TimesupFlag_cfg==false)
        {


            int count = 0;
				/* Time interval(ms) between sending two profiles. */
            SCLibrary.ProfileSendTimeIntervalMs = 200; //200ms
				/* Time interval(ms) between sending two packets. */
            SCLibrary.PacketSendTimeIntervalMs  = 10; //10ms
				/* Each packet sending counts. */
            SCLibrary.EachPacketSendCounts = 1;
            SCLib.rtk_sc_stop();
            msg_sta=1;
            handler.sendEmptyMessage(SENDING_ok);
            Log.d("zcw","传输时间到了");
        }
    }


    private void exception_action()
    {
        if(Build.MANUFACTURER.equalsIgnoreCase("Samsung")){
            //SCLibrary.PacketSendTimeIntervalMs  = 5;
            if (Build.MODEL.equalsIgnoreCase("G9008")){ //Samsung Galaxy S5 SM-G9008
                SCLibrary.PacketSendTimeIntervalMs  = 10;
            }else if(Build.MODEL.contains("SM-G9208")){ //samsun Galaxy S6
                SCLibrary.PacketSendTimeIntervalMs  = 10;
            }else if(Build.MODEL.contains("N900")){ //samsun Galaxy note 3
                SCLibrary.PacketSendTimeIntervalMs  = 5;
            }else if(Build.MODEL.contains("SM-N910U")){ //samsun Galaxy note 4
                SCLibrary.PacketSendTimeIntervalMs  = 5;
            }

        }else if(Build.MANUFACTURER.equalsIgnoreCase("Xiaomi")){//for MI
            if (Build.MODEL.equalsIgnoreCase("MI 4W")){
                SCLibrary.PacketSendTimeIntervalMs  = 5;	//MI 4
            }
        }else if(Build.MANUFACTURER.equalsIgnoreCase("Sony")){//for Sony
            if (Build.MODEL.indexOf("Xperia")>0){
                SCLibrary.PacketSendTimeIntervalMs  = 5;	//Z3
            }
        }else if(Build.MANUFACTURER.equalsIgnoreCase("HUAWEI")){//HUAWEI
            if (Build.MODEL.indexOf("GEM-702L")>0){
                SCLibrary.PacketSendTimeIntervalMs  = 10;	//GEM-702L
            }else{
                SCLibrary.PacketSendTimeIntervalMs  = 5;
            }
        }

        //check link rate
        WifiManager wifi_service = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiinfo = wifi_service.getConnectionInfo();
        if( wifiinfo.getLinkSpeed() > 78 ){//MCS8 , 20MHZ , NOSGI
            SCLibrary.ProfileSendTimeIntervalMs = 100; //50ms
            SCLibrary.PacketSendTimeIntervalMs  = 15;
        }
    }

    /** Handler class to receive send/receive message */
    private class MsgHandler extends Handler{

        @Override
        public void handleMessage(Message msg){
            Log.d("zcw", "msg.what: " + msg.what);
            switch(msg.what) {
                case ~SCCtlOps.Flag.CfgSuccessACK://Config Timeout
                    SCLib.rtk_sc_stop();
                    TimesupFlag_cfg = true;
                    break;
                case SCCtlOps.Flag.CfgSuccessACK: //Not Showable
                    SCLib.rtk_sc_stop();
                    TimesupFlag_cfg = true;
                    List<HashMap<String, Object>> InfoList = new ArrayList<HashMap<String, Object>>();
                    SCLib.rtk_sc_get_connected_sta_info(InfoList);
                    String ip = InfoList.get(0).get("IP").toString();
                    Log.i("zcw", "SUCCESS::ip="+ip);
                    msg_sta=2;
                    handler.sendEmptyMessage(SENDING_ok);

                    break;
                case SCCtlOps.Flag.DiscoverACK:
                    Log.d("MsgHandler","DiscoverACK");
                    break;
                case ~SCCtlOps.Flag.DiscoverACK:

                    break;
                case SCCtlOps.Flag.DelProfACK:

                    break;
                case SCCtlOps.Flag.RenameDevACK:

                    break;
                default:

                    break;
            }
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



    public void stopsendWifiInfoThread(){
        SCLib.rtk_sc_stop();
        TimesupFlag_cfg =true;
        Thread.currentThread().interrupt();
    }

    private void releaseAll() {
        stopsendWifiInfoThread();
        SCLib.rtk_sc_exit();
        SCCtlOps.ConnectedSSID = null;
        SCCtlOps.ConnectedPasswd = null;



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseAll();

    }
}
