package com.chomp.wifistorymachine.ui.chat;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chomp.wifistorymachine.R;
import com.chomp.wifistorymachine.constants.Constant;
import com.chomp.wifistorymachine.model.BaseResponseBean;
import com.chomp.wifistorymachine.model.ResponseBean;
import com.chomp.wifistorymachine.okhttp.HttpUtils;
import com.chomp.wifistorymachine.okhttp.OkHttpUtils;
import com.chomp.wifistorymachine.ui.BaseActivity;
import com.chomp.wifistorymachine.ui.chat.AudioRecorderButton.AudioFinishRecorderListenter;
import com.chomp.wifistorymachine.util.CurrentTime;
import com.chomp.wifistorymachine.util.JsonParseUtil;
import com.chomp.wifistorymachine.util.Toaster;
import com.zhy.m.permission.MPermissions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.util.UUID;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;



public class ConversationListFragment extends BaseActivity {

    String TAG = "UserCenterFragment";
    private final String TAG_CHAT = "chatwith";


    private LinearLayout btn_back;
    private TextView text_title;

    private ListView mListView;
    private ArrayAdapter<Recorder> mAdapter;
    private List<Recorder> mDatas = new ArrayList<Recorder>();
    private AudioRecorderButton mAudioRecorderButton;
    private View right_mAnimView;

    private View left_mAnimView;

    private View rightView;

    private View leftView;

    private View topView;
    private boolean flag = false;
    public String currentTimeString = "";

    private InetAddress iaIP = null;
    private DatagramSocket dscmd = null;

    protected Activity mActivity;

    String pcode;
    private Context tcontext;

//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.em_chat_neterror_item, container, false);
//        initLayout(view);
//        return view;
//    }


    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.em_chat_neterror_item);
        tcontext = this;
        setTitle(getString(R.string.chat_with));
        setBack();
        initLayout();

        SharedPreferences preferences = getSharedPreferences(Constant.SHARED_KARROBOT, Context.MODE_PRIVATE);
        pcode = preferences.getString(Constant.EXTRA_PINCODE_ID, null);
    }

    /**
     * 录音记录类（备忘信息）
     *
     * @author songshi
     */
    public class Recorder {
        float time; // 备忘录音时间长度
        String filePath; // 备忘录音文件路径
        String mCurrentTime; // 备忘录音时的系统时间

        private String left_text;
        private String right_text;


        public String getLeft_text() {
            return left_text;
        }

        public String getRight_text() {
            return right_text;
        }

        public Recorder(float time, String filePath, String currentTime) {
            super();
            this.time = time;
            this.filePath = filePath;
            this.mCurrentTime = currentTime;

        }

        public float getTime() {
            return time;
        }

        public void setTime(float time) {
            this.time = time;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public String getmCurrentTime() {
            return mCurrentTime;
        }

        public void setmCurrentTime(String mCurrentTime) {
            this.mCurrentTime = mCurrentTime;
        }

    }


    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        MediaManage.pause();
    }


    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        MediaManage.resume();
    }

    //    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        this.mActivity = activity;
//    }
    float seconds_s = 0;
    String FilePath_s = null;
    private AnimationDrawable anim = null;

    private void initLayout() {
        topView = findViewById(R.id.topView);
        mListView = (ListView) findViewById(R.id.voiceNotesListView);
        mAudioRecorderButton = (AudioRecorderButton) findViewById(R.id.recorderButton);

        // 回调AudioRecorderButton的录音结束接口
        mAudioRecorderButton
                .setAudioFinishRecorderListenter(new AudioFinishRecorderListenter() {
                    public void onFinish(float seconds, String FilePath) {
                        if (!flag) { // 第一次录音结束后，设置显示顶部灰度条
                            // topView.setVisibility(View.VISIBLE);
                            flag = true;
                        }
                        seconds_s = seconds;
                        FilePath_s = FilePath;
                        //上传录音到服务器上
//                        StringBuilder result = Hservice.httpPost(FilePath, "12345673");// pinCode: 12345671
//                        Log.d("zcw","上传result=="+result);
//                        String dir = getResources().getString(R.string.recorder_path);                             // 此处需要判断是否有存储卡(外存)
//                        String fileName = GenerateFileNameMp3(); // 文件名字
//                        File file = new File(dir, fileName);  // 路径+文件名字
//                        String mCurrentFilePath = file.getAbsolutePath();
//                        FLameUtils lameUtils = new FLameUtils(1, 8000, 96);
//                        lameUtils.raw2mp3(FilePath, mCurrentFilePath);
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                        }

                        /// File FilePath1 = new File(FilePath);
                        //FilePath1.delete();    //删除录音文件

                        if (pcode == null || pcode.equals("")) {
                            Toaster.showLongToast(tcontext, getString(R.string.un_binding_chat));
                        } else {
                            sendVoiceMessage(FilePath);

                        }

                    }
                });

        GetFiles(getFilesDir().toString(), "amr", false);

        mAdapter = new AudioRecorderAdapter(this, mDatas); // mDatas在上面回调“录音完成”接口时动态增加
        mListView.setAdapter(mAdapter);

        // mListView.getChildAt(index)

        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                leftView = view.findViewById(R.id.left);
                rightView = view.findViewById(R.id.right);
                if (anim != null) {
                    anim.stop();
                    anim = null;
                }
                if (rightView.getVisibility() == View.VISIBLE) {

                    if (right_mAnimView != null) { // 处理如果有一个Item正在播放，然后用户又重新开启了另外一个Item的情况
                        right_mAnimView.setBackgroundResource(R.drawable.voice_right3); // 直接设置成R.drawable.voice_right3
                        right_mAnimView = null; // 然后置成空
                    }

                    // 播放动画
                    right_mAnimView = view.findViewById(R.id.right_recorder_anim);
                    right_mAnimView.setBackgroundResource(R.drawable.audio_play_anim);

                    anim = (AnimationDrawable) right_mAnimView
                            .getBackground();
                    anim.start();


                    // 播放音频
                    MediaManage.playSound(mDatas.get(position).filePath,
                            new MediaPlayer.OnCompletionListener() {

                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    // 在播放结束以后，将动画取消
                                    right_mAnimView
                                            .setBackgroundResource(R.drawable.voice_right3);
                                }
                            });
                }

                if (leftView.getVisibility() == View.VISIBLE) {

                    if (left_mAnimView != null) { // 处理如果有一个Item正在播放，然后用户又重新开启了另外一个Item的情况
                        left_mAnimView.setBackgroundResource(R.drawable.voice_right3); // 直接设置成R.drawable.voice_right3
                        left_mAnimView = null; // 然后置成空
                    }

                    // 播放动画
                    left_mAnimView = view.findViewById(R.id.left_recorder_anim);
                    left_mAnimView.setBackgroundResource(R.drawable.audio_play_anim);

                    anim = (AnimationDrawable) left_mAnimView
                            .getBackground();
                    anim.start();

                    // 播放音频
                    MediaManage.playSound(mDatas.get(position).filePath,
                            new MediaPlayer.OnCompletionListener() {

                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    // 在播放结束以后，将动画取消
                                    left_mAnimView
                                            .setBackgroundResource(R.drawable.voice_right3);
                                }
                            });
                }
            }

        });
//        Permissions_Flag = new gx_PreferenServer(this);
//        int P_Flag = Permissions_Flag.Get_Hand_Flag();
//        if (P_Flag <= 0) {
//        }


        try {
            iaIP = InetAddress.getByName(VIDEO_IP_ADDRESS);
            dscmd = new DatagramSocket(PUB_PORT);
            dscmd.setReuseAddress(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        DRefresh = true;
        DataCmd.start();

       // setPermissions();
       //setPermissions(Manifest.permission.RECORD_AUDIO);

        //getVoiceMessage("http://www.bbgushiji.com:89/chatfile/201702091150098174.mp3");
        //http://www.bbgushiji.com:89/chatfile/201702150955359824.wav
      //  getVoiceMessage("http://www.bbgushiji.com:89/chatfile/201702150955359824.wav");
    }


    /**
     * 随机生成文件名称
     *
     * @return
     */
    private String GenerateFileNameMp3() {
        // TODO Auto-generated method stub
        return UUID.randomUUID().toString() + ".amr"; // 音频文件格式
    }


    private Socket socketTCP = null;
    private String VIDEO_IP_ADDRESS = "106.14.62.57";//106.14.62.57 接收数据的ip地址
    private int PUB_PORT = 8782;//公共端口号

    private boolean sendF = true;
    private int statem = 0;
    private boolean DRefresh = false;

    private Thread DataCmd = new Thread("tcpdatacmd") {
        @SuppressLint("NewApi")
        public void run() {
            byte[] packetbuf = new byte[4096];
            DataInputStream input = null;
            InputStream inputStream = null;
            int length = packetbuf.length, offset_s = 0, tmplen = 0;
            StringBuilder result = new StringBuilder();
            while (DRefresh)//请求数据 成功
            {
                try {
                    if (null == socketTCP) {
                        socketTCP = new Socket(VIDEO_IP_ADDRESS, PUB_PORT);
                        //	socketTCP.setSoTimeout(2000);
                        inputStream = socketTCP.getInputStream();
                        input = new DataInputStream(inputStream);
                        if (sendF) {
                          //  send();
                            new Thread(new MyThread()).start();
                            sendF = false;
                        }
                    }

                    if (sendF) {
                        send();
                        sendF = false;
                    }
                    result.replace(0, result.length(), "");
                    Log.d("zcw", "result1==" + result);
                    tmplen = input.read(packetbuf, offset_s, length);

                    String data = new String(packetbuf);

                    data = data.substring(0, data.indexOf("\0"));
                    result.append(data);
                    Log.d("wzg","data=="+data);
                    JSONObject jsonObject=new JSONObject(data);//我们需要把json串看成一个大的对象
                    String url=jsonObject.getString("url");//获取pet对象的参数
                    Log.d("zcw", "result2==" + result);
                    Log.d("zcw", "data==" + data);
                    Log.d("zcw", "url==" + url);
                    if (tmplen < 0) {
                        Thread.sleep(50);
                        statem++;
                        continue;
                    } else if (tmplen == 0) {
                        Thread.sleep(50);
                        continue;
                    }
                    statem = 0;
                    getVoiceMessage(url);
                    //boolean yes=Hservice.httpGet(result,CurrentTime.getCurrentTime());//下载
                } catch (Exception e) {
                    Log.d("wzg", "SocketTimeoutException...");
                    e.printStackTrace();
                }
            }

            try {
                if (socketTCP != null) {
                    Log.d("wzg","input.close");
                    input.close();
                    inputStream.close();
//                    socketTCP.close();
//                    socketTCP=null;
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    };






    /**
     * 发送消息
     */
    public void send() {
        new Thread() {
            @Override
            public void run() {
                try {
                    // {"cmd":"S01","mac":"","rollcode":"7"}
                    SharedPreferences preferences = getSharedPreferences(Constant.SHARED_KARROBOT, Context.MODE_PRIVATE);
                    String userid = preferences.getString(Constant.EXTRA_USER_ID, null);
                    JSONObject json = new JSONObject();
                    try {
                        json.put("cmd", "C01");
                        json.put("mac", "");
                        json.put("rollcode", userid);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String jsonString = json.toString();
                    if(socketTCP !=null) {
                        DataOutputStream writer = new DataOutputStream(socketTCP.getOutputStream());
                        writer.writeBytes(jsonString);
                    }
                    Log.d("zcw", "=jsonString=" + jsonString);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public class MyThread implements Runnable {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            while (DRefresh) {
                try {
                    Thread.sleep(2000);// 线程暂停2秒，单位毫秒
                    send();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                onBackPressed();
                break;
            default:
                break;
        }
    }

    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
        super.onBackPressed();
    }


    private void setPermissions() {
        if (getSDKVersionNumber()>=23) {

            prepareAudio();
        } else {
            prepareAudio();
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
    private void Check_Permissions(){
        if (!(ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.RECORD_AUDIO)) {
                //已经禁止提示了
                Toast.makeText(ConversationListFragment.this, "您已禁止该权限，需要重新开启。", Toast.LENGTH_SHORT).show();

                Log.i("wzg", "您已禁止该权限，需要重新开启");
            }
            requestCameraPermission();
        } else {
            Log.i("wzg", "onClick granted");
        }




    }

    private static final int REQUECT_CODE_SDCARD = 2;
    private static final int REQUECT_CODE_CALL_PHONE = 3;
    //申请授权
    private void requestCameraPermission() {
       // if (!MPermissions.shouldShowRequestPermissionRationale(ConversationListFragment.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUECT_CODE_SDCARD))
       // {
            MPermissions.requestPermissions(ConversationListFragment.this, REQUECT_CODE_SDCARD, Manifest.permission.RECORD_AUDIO);


        Log.i("wzg", "==MPermissions.requestPermissions==");

        // }
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
                Toast.makeText(ConversationListFragment.this, "您已禁止录音权限，不能录音", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void createMediaRecord(){
// ①Initial：实例化MediaRecorder对象
        MediaRecorder  mMediaRecorder = new MediaRecorder();
// mMediaRecorder = new MediaRecorder();
// setAudioSource/setVedioSource
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);//设置麦克风

/* 设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default
* THREE_GPP(3gp格式，H263视频/ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB)*/

        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);

// 设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

// 设置输出文件的路径
        File file = new File(getFilesDir().toString()+"/tmp.amr");
        if (file.exists()) {
            file.delete();
        }
        mMediaRecorder.setOutputFile(file.toString());
    }

    /**
     * 主动申请录音权限
     * */
    private void setRecordingPermissions() {
        String tmpPath = getFilesDir().toString()+"/tmp.amr";

        File file = new File(tmpPath);
        if (file.mkdirs()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        MediaRecorder mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); // 设置MediaRecorder的音频源为麦克风
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR); // 设置音频的格式
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB); // 设置音频的编码为AMR_NB
        mMediaRecorder.setOutputFile(tmpPath); // 设置输出文件
        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();// java.lang.IllegalStateException
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            throw new IllegalStateException();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mMediaRecorder.release();
        mMediaRecorder = null;
        if (file != null) {
          //  file.delete(); // 删除录音文件
//            Permissions_Flag.Set_Hand_Flag(1);
        }
    }

    /**
     * 准备录音
     */
    public void prepareAudio() {

        try {
            String tmpPath = getFilesDir().toString();

            File dir = new File(tmpPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String fileName = "tmp.amr"; // 文件名字
            File file = new File(dir, fileName);  // 路径+文件名字


            //MediaRecorder可以实现录音和录像。需要严格遵守API说明中的函数调用先后顺序.
            MediaRecorder mMediaRecorder = new MediaRecorder();

            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);    // 设置MediaRecorder的音频源为麦克风
			/* 设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default
             * THREE_GPP(3gp格式，H263视频/ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB)
             * */
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);    // 设置音频的格式
            // 设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);    // 设置音频的编码为AMR_NB
            mMediaRecorder.setOutputFile(file.getAbsolutePath());    // 设置输出文件
            mMediaRecorder.prepare();
           // mMediaRecorder.start();

            if (file != null) {
                  file.delete(); // 删除录音文件
//            Permissions_Flag.Set_Hand_Flag(1);
            }

        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    Recorder recorder;
    MediaPlayer mdp;

    public void GetFiles(String Path, String Extension, boolean IsIterative) // 搜索目录，扩展名，是否进入子文件夹
    {
        File[] files = new File(Path).listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                if (f.isFile()) {
                    if (f.getPath()
                            .substring(
                                    f.getPath().length() - Extension.length())
                            .equals(Extension)) { // 判断扩展名
                        // lstFile.add(f.getPath());
                        // 1.5000002f,
                        // "/storage/emulated/0/VoiceRecorder/9df8a369-61c9-4aca-ba46-1d9e9b3a0878.amr",
                        // "2016年11月08日  12:05:40 星期二"
                        File file = new File(f.getPath());
                        long time = file.lastModified();


                        recorder = new Recorder(1.5000002f, f.getPath(),
                                getDate(time));



                        mDatas.add(recorder);
                    }

                } else if (f.isDirectory() && f.getPath().indexOf("/.") == -1) // 忽略点文件（隐藏文件/文件夹）
                    GetFiles(f.getPath(), Extension, IsIterative);
            }
        }
    }


    private String getDate(long time){
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date curDate=new Date(time);//获取当前时间
        String    str    = dateFormat.format(curDate);
        Log.d("wzg","==time=="+str);
        return  str;
    }


    //上传录音文件
    public void sendVoiceMessage(String mp3Path) {

        HttpUtils.sendVoiceMessage(mp3Path,this,TAG_CHAT,okCallBack);
    }
    File GS_mp3File;
    private void getVoiceMessage(String SaveMp3Path) {

        File directory = new File(getFilesDir().toString());
        if (!directory.exists()) {
              directory.mkdirs();
        }
        String fileName = GenerateFileName(); // 文件名字
        GS_mp3File = new File(directory, fileName);  // 路径+文件名字

        HttpUtils.saveVoiceMessage(GS_mp3File,SaveMp3Path,this,TAG_CHAT,SaveOKCallBack);
    }



    //下载回调函数
    OkHttpUtils.OkCallBack SaveOKCallBack=new OkHttpUtils.OkCallBack() {
        public void onBefore(Request request, Object tag) {

        }

        public void onAfter(Object tag) {

        }

        public void onError(Call call, Response response, Exception e, Object tag) {
            Log.e(TAG, e.toString());
        }


        @Override
        public void onResponse(Call call, Response response,Object tag) {
            if(response!=null) {
                if (tag.toString().equals(TAG_CHAT)) {
//                    InputStream inputStream = response.body().byteStream();
//                    int ddd=inputStream.toString().length();
//                    FileOutputStream fileOutputStream = null;
//
//                    File directory = new File(getResources().getString(R.string.recorder_path));
//                    if (!directory.exists()) {
//                        directory.mkdirs();
//                    }
//
//                    String fileName = GenerateFileName(); // 文件名字
//                    File GS_mp3File = new File(directory, fileName);  // 路径+文件名字
//
//                    try {
//                        fileOutputStream = new FileOutputStream(GS_mp3File);
//                        byte[] buffer = new byte[2048];
//                        int len = 0;
//                        while ((len = inputStream.read(buffer)) != -1) {
//                            fileOutputStream.write(buffer, 0, len);
//                        }
//                        fileOutputStream.flush();
//                    } catch (IOException e) {
//                        Log.i("wangshu", "IOException");
//                        e.printStackTrace();
//                    }
//
//                    Log.d("wangshu", "文件下载成功");

                    currentTimeString = CurrentTime.getCurrentTime();
                    Recorder recorder = new Recorder(seconds_s, GS_mp3File.getAbsolutePath(),
                            currentTimeString);
                    mDatas.add(recorder);
                    mAdapter.notifyDataSetChanged(); // 更新ListView
                    mListView.setSelection(mDatas.size() - 1); // 重新回到最后一个
                }
            }
        }

        public void onResponse(Object response, Object tag) {
            if(response!=null) {
                if (tag.toString().equals(TAG_CHAT)) {

                }
            }
        }
    };

    /**
     * 随机生成文件名称
     * @return
     */
    private String GenerateFileName() {
        // TODO Auto-generated method stub
        return "bb_"+UUID.randomUUID().toString() + ".amr"; // 音频文件格式
    }

    //上传录音回调函数
    OkHttpUtils.OkCallBack okCallBack=new OkHttpUtils.OkCallBack() {
        public void onBefore(Request request, Object tag) {

        }

        public void onAfter(Object tag) {

        }

        @Override
        public void onResponse(Call call, Response response,Object tag) {
            if(response!=null) {

            }
        }

        public void onError(Call call, Response response, Exception e, Object tag) {
            Log.e(TAG, e.toString());
        }


        public void onResponse(Object response, Object tag) {
            if(response!=null) {
                if (tag.toString().equals(TAG_CHAT)) {
                    ResponseBean responseBean = JsonParseUtil.json2Object(response.toString(),ResponseBean.class);
                    if(responseBean != null&&responseBean.getCode().equals(Constant.CODE_REQUEST_SUCC)){
                        //showMessage();
                        currentTimeString = CurrentTime.getCurrentTime();

                        Recorder recorder;
                        recorder = new Recorder(1.5000002f, FilePath_s,
                                currentTimeString);


                        mDatas.add(recorder);
                        mAdapter.notifyDataSetChanged(); // 更新ListView
                        mListView.setSelection(mDatas.size() - 1); // 重新回到最后一个
                    }else{
                        File file = new File(FilePath_s);
                        file.delete();
                        showMessage(responseBean);
                    }

                }
            }
        }
    };

    private void showMessage(Object object){
        BaseResponseBean baseResponseBean = (BaseResponseBean)object;
        if(baseResponseBean==null){
            Toaster.showShortToast(this,"请求失败");
        }else {
            Toaster.showShortToast(this,baseResponseBean.getMessage());
        }
    }



    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        DRefresh=false;
        try {
            if(socketTCP !=null){
                Log.d("wzg","onDestroy input.close");
                socketTCP.close();
                socketTCP=null;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        MediaManage.release();
        OkHttpUtils.getInstance().cancelTag(TAG_CHAT);
    }

}