package com.chomp.wifistorymachine.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.chomp.wifistorymachine.R;
import com.chomp.wifistorymachine.application.MyApplication;
import com.chomp.wifistorymachine.constants.Constant;
import com.chomp.wifistorymachine.okhttp.OkHttpUtils;
import com.chomp.wifistorymachine.ui.chat.ConversationListFragment;
import com.chomp.wifistorymachine.util.Toaster;

import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

import static com.chomp.wifistorymachine.util.Utils.encrypt;
import static com.google.zxing.qrcode.decoder.ErrorCorrectionLevel.M;

public class HomeActivity extends BaseActivity {

    private final String MYDEVICE = "my_device";
    String TAG="HomeActivity";

    private RelativeLayout btn_story;
    private RelativeLayout btn_group;
    private RelativeLayout btn_Local_Story;
    private RelativeLayout btn_user;

    private FrameLayout layout_fragment;

    private int currentTabIndex = 0;
    private ConversationListFragment conversationListFragment;

    private SharedPreferences preferences;

//    EasemobHelper.InviteCallBack mInviteCallBack = new EasemobHelper.InviteCallBack() {
//        @Override
//        public void refresh() {
//            refreshUIWithMessage();
//        }
//    };

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
        if ((System.currentTimeMillis() - exitTime) > 1000) {
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            MyApplication.getInstance().exitApp();
        }
    }

    private String PINCODE_ID;
    private boolean isLoggedIn = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        MyApplication.getInstance().addActivity(this);
        setINt();
        initLayout();
        preferences = getSharedPreferences(Constant.SHARED_KARROBOT, Context.MODE_PRIVATE);

        PINCODE_ID = preferences.getString(Constant.EXTRA_PINCODE_ID, null);

        if (PINCODE_ID == null || ("0").equals(PINCODE_ID) || PINCODE_ID.equals("")) {
            isLoggedIn = false;
        }

        //更新
//        UpdateAgentUtils.getInstance().update(this);

        //注册一个环信监听连接状态的listener
//        EMClient.getInstance().addConnectionListener(EasemobHelper.getInstance().new MyConnectionListener(this));

//        SharedPreferences preferences = getSharedPreferences(Constant.SHARED_KARROBOT, Context.MODE_PRIVATE);;
//        String udid_str = preferences.getString(Constant.SHARED_UDID,"");
//        if(udid_str==null||udid_str.equals("")){
//         //   getMyBindDevice();
//        }
        Log.e("手机型号",android.os.Build.MODEL);
    }

    //获取我的设备列表
//    private void getMyBindDevice(){
//        HttpUtils.getMyBindDevice(this,MYDEVICE,okCallBack);
//    }

    private void initLayout() {
        layout_fragment = (FrameLayout) findViewById(R.id.layout_fragment);

        btn_story = (RelativeLayout) findViewById(R.id.btn_story);
        btn_story.setOnClickListener(this);

        btn_group = (RelativeLayout) findViewById(R.id.btn_group);
        btn_group.setOnClickListener(this);

        btn_Local_Story = (RelativeLayout) findViewById(R.id.btn_Local_Story);
        btn_Local_Story.setOnClickListener(this);

        btn_user = (RelativeLayout) findViewById(R.id.btn_user);
        btn_user.setOnClickListener(this);

        StoryHomeFragment storyHomeFragment = new StoryHomeFragment();
        updateFragment(storyHomeFragment);
        btn_story.setSelected(true);

      //  EasemobHelper.getInstance().setmInviteCallBack(mInviteCallBack);
    }


    private static final long INTERVAL_SECONDS = 500;//完成所有点击事件的时间需求
    private static final int INTERVAL_TIMES = 4;//完成所有点击事件的次数

    long[] mHits = new long[INTERVAL_TIMES];

    @Override
    public void onClick(View v) {
        System.arraycopy(mHits, 1, mHits, 0, mHits.length-1);
        mHits[mHits.length-1] = SystemClock.uptimeMillis();
        if (mHits[0] >= (SystemClock.uptimeMillis()-INTERVAL_TIMES)) {
            Toast.makeText(HomeActivity.this, "按钮被点击了四次", Toast.LENGTH_SHORT).show();
        }
            switch (v.getId()) {
                case R.id.btn_story:
                    currentTabIndex = 0;
                    StoryHomeFragment storyHomeFragment = new StoryHomeFragment();
                    updateFragment(storyHomeFragment);
                    btn_group.setSelected(false);
                    btn_user.setSelected(false);
                    btn_Local_Story.setSelected(false);
                    btn_story.setSelected(true);
                    break;
                case R.id.btn_group://家庭圈
//                currentTabIndex = 1;
//                conversationListFragment = new ConversationListFragment();
//                updateFragment(conversationListFragment);
//                btn_story.setSelected(false);
//                btn_user.setSelected(false);
//                btn_Local_Story.setSelected(false);
//                btn_group.setSelected(true);
                    if (isLoggedIn) {
                        Intent in = new Intent();
                        in.setClass(HomeActivity.this, ConversationListFragment.class);
                        startActivity(in);
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    }else{
                        Toaster.showLongToast(HomeActivity.this, getString(R.string.un_binding_family_circle));
                    }

                    break;
                case R.id.btn_Local_Story://本地故事
//                currentTabIndex = 2;
//                UserCenterFragment userCenterFragment = new UserCenterFragment();
//                updateFragment(userCenterFragment);
//                btn_story.setSelected(false);
//                btn_group.setSelected(false);
//                btn_user.setSelected(false);
//                btn_Local_Story.setSelected(true);
                    break;
                case R.id.btn_user://更多
                    currentTabIndex = 3;
                    UserCenterFragment userCenterFragment = new UserCenterFragment();
                    updateFragment(userCenterFragment);
                    btn_story.setSelected(false);
                    btn_group.setSelected(false);
                    btn_user.setSelected(true);

                    break;

                default:
                    break;
            }



    }

    private void updateFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.layout_fragment, fragment);
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        EMClient.getInstance().chatManager().addMessageListener(messageListener);
    }

    @Override
    protected void onStop() {
//        EMClient.getInstance().chatManager().removeMessageListener(messageListener);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
 //       OkHttpUtils.getInstance().cancelTag(MYDEVICE);
    }

    //环信消息监听
//    EMMessageListener messageListener = new EMMessageListener() {
//
//        @Override
//        public void onMessageReceived(List<EMMessage> messages) {
//            // notify new message
//            refreshUIWithMessage();
//        }
//
//        @Override
//        public void onCmdMessageReceived(List<EMMessage> messages) {
//            refreshUIWithMessage();
//        }
//
//        @Override
//        public void onMessageReadAckReceived(List<EMMessage> messages) {
//        }
//
//        @Override
//        public void onMessageDeliveryAckReceived(List<EMMessage> message) {
//        }
//
//        @Override
//        public void onMessageChanged(EMMessage message, Object change) {
//        }
//    };
//
//    //更新联系列表
//    public void refreshUIWithMessage() {
//        runOnUiThread(new Runnable() {
//            public void run() {
//                // refresh unread count
//                if (currentTabIndex == 1 && conversationListFragment != null) {
//                    // refresh conversation list
//                    if (conversationListFragment != null) {
//                        conversationListFragment.refresh();
//                    }
//                }
//            }
//        });
//    }

    private OkHttpUtils.OkCallBack okCallBack = new OkHttpUtils.OkCallBack(){
        @Override
        public void onBefore(Request request, Object tag) {

        }

        @Override
        public void onAfter(Object tag) {

        }

        @Override
        public void onError(Call call, Response response, Exception e, Object tag) {

        }

        @Override
        public void onResponse(Call call, Response response,Object tag) {

        }
        @Override
        public void onResponse(Object response, Object tag) {
//            if(tag.toString().equals(MYDEVICE)){
//                MyDeviceBean myDeviceBean = JsonParseUtil.json2Object(response.toString(),MyDeviceBean.class);
//                if(myDeviceBean!=null&&myDeviceBean.getSet()!=null){
//                    if(myDeviceBean.getSet().size()>0){
//                        SharedPreferences preferences  = getSharedPreferences(Constant.SHARED_KARROBOT, Context.MODE_PRIVATE);;
//                        preferences.edit().putString(Constant.SHARED_UDID, myDeviceBean.getSet().get(0).getUdid()).commit();
//                    }
//                }
//            }
        }
    };
}
