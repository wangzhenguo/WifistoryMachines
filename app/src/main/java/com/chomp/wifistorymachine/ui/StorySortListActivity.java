package com.chomp.wifistorymachine.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chomp.wifistorymachine.R;


import com.chomp.wifistorymachine.adapter.ViewHolder;
import com.chomp.wifistorymachine.application.MyApplication;
import com.chomp.wifistorymachine.constants.Constant;
import com.chomp.wifistorymachine.dao.AudiobooksSort;
import com.chomp.wifistorymachine.model.BaseResponseBean;
import com.chomp.wifistorymachine.model.ResponseBean;
import com.chomp.wifistorymachine.okhttp.HttpUtils;
import com.chomp.wifistorymachine.okhttp.OkHttpUtils;
import com.chomp.wifistorymachine.ui.chat.MediaManage;
import com.chomp.wifistorymachine.util.JsonParseUtil;
import com.chomp.wifistorymachine.util.Toaster;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

import static android.R.attr.key;
import static com.chomp.wifistorymachine.R.id.btn_audition_item;
import static com.chomp.wifistorymachine.R.id.img_arrow1;


/**
 * Created by ronglinbo on 2016/10/31.
 */
public class StorySortListActivity extends BaseActivity{

    String TAG="StorySortListActivity";

    private final String GET_STORY_QUERY_SF="get_story_querySF";


    private final String GET_STORY_QUERY_FL="get_story_queryFL";

    private final int HANDLER_LOAD_AFTER=101;

    private Context mContext;

    private ListView listView;
    //private StorySortAdapter adapter;
    private LvAdapter lvAdapter_s;
    /*  title */
    private LinearLayout btn_back;
    private TextView text_title;
    private ImageView img_right;

    private SharedPreferences preferences;

//    List<String> list=new ArrayList<String>();

    ArrayList<AudiobooksSort> audiobooksSorts=null;

    private String sortid;

    private String sortname;

    private int[] storyPictures = new int[] { R.drawable.chengyugushi, R.drawable.guanchasikao, R.drawable.guoxuejindian, R.drawable.jiankanganquan,
            R.drawable.lishigushi, R.drawable.shenghuoxiguan, R.drawable.shuimianyinyue, R.drawable.tonghuagushi,
            R.drawable.xinggepeiyang, R.drawable.yingyuxuexi, R.drawable.yingyuyuandi, R.drawable.yuyanqimeng, R.drawable.zhihuigushi,R.drawable.zhishiwenda,
            R.drawable.zhonghuatongyao,R.drawable.zhongwenerge,R.drawable.zirankepu,
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_sort_list);
        MyApplication.getInstance().addActivity(this);
        setINt();
        mContext=StorySortListActivity.this;

        Bundle bundle = this.getIntent().getExtras();
        sortid= bundle.getString("sortid");
        //接收name值
        sortname= bundle.getString("sortname");

        Log.i(TAG,"sortname:"+sortname);


        preferences = getSharedPreferences(Constant.SHARED_KARROBOT, Context.MODE_PRIVATE);

        audiobooksSorts=new ArrayList<AudiobooksSort>();
        loadData();
        setTitleName();
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaManage.release();
        handler.removeMessages(HANDLER_LOAD_AFTER);
        OkHttpUtils.getInstance().cancelTag(GET_STORY_QUERY_SF);

    }
    //加载试听列表数据
    public void loadData() {
        if(!TextUtils.isEmpty(sortname)){
//            audiobooksSorts = KARDBHelper.newInstance(mContext).queryAudiobooksSort(sortname);
//            if(audiobooksSorts==null||audiobooksSorts.size()<=0){
            Log.i(TAG,"有声读物分类列表：没有缓存");
            HttpUtils.getDataByAlbum(sortid,mContext,GET_STORY_QUERY_SF,okCallBack);
//            }else{
//                Log.i(TAG,"有声读物分类列表：有缓存-------");
//                handler.sendEmptyMessage(HANDLER_LOAD_AFTER);
//            }
        }else{
            Toast.makeText(mContext,"出错", Toast.LENGTH_LONG).show();
        }
    }

    private int item_id=0;

    private void setTitleName() {
        text_title= (TextView) findViewById(R.id.text_title);

        if(TextUtils.isEmpty(sortname)){
            text_title.setText("经典故事");
        }else{
            text_title.setText(sortname);
        }

        switch (sortname){
            case "成语故事":
                item_id=0;
                break;
            case "观察思考":
                item_id=1;
                break;
            case "国学经典":
                item_id=2;
                break;
            case "健康安全":
                item_id=3;
                break;
            case "历史故事":
                item_id=4;
                break;
            case "生活习惯":
                item_id=5;
                break;
            case "睡眠音乐":
                item_id=6;
                break;
            case "童话故事":
                item_id=7;
                break;
            case "性格培养":
                item_id=8;
                break;
            case "英语学习":
                item_id=9;
                break;
            case "英语园地":
                item_id=10;
                break;
            case "语言启蒙":
                item_id=11;
                break;
            case "知识疑问":
                item_id=12;
                break;
            case "智慧故事":
                item_id=13;
                break;
            case "中华童谣":
                item_id=14;
                break;
            case "中文儿歌":
                item_id=15;
                break;
            case "自然科普":
                item_id=16;
                break;
        }
    }

    private String Play_Name="";
    boolean isLoggedIn = true;
    String PINCODE_ID;
    private void initView() {
        listView= (ListView) findViewById(R.id.listView);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    boolean isLoggedIn = true;
//                    String PINCODE_ID = preferences.getString(Constant.EXTRA_PINCODE_ID, null);
//
//                    if (PINCODE_ID == null || ("0").equals(PINCODE_ID) || PINCODE_ID.equals("")) {
//                        isLoggedIn = false;
//                    }
//                    if (isLoggedIn) {
//                        play(audiobooksSorts.get(position).getFileid().toString(), audiobooksSorts.get(position).getUrl(), PINCODE_ID);
//                        Play_Name = audiobooksSorts.get(position).getFiletitle();
//                    } else {
//                        Toaster.showLongToast(mContext, getString(R.string.un_binding_fail));
//                    }
//
//            }
//        });
        btn_back= (LinearLayout) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(clickListener);
        img_right= (ImageView) findViewById(R.id.img_right);
        img_right.setOnClickListener(clickListener);

//        AnimationDrawable animationDrawable= (AnimationDrawable) img_right.getDrawable();
//        animationDrawable.stop();
//
//        img_right.setVisibility(View.GONE);

        isLoggedIn = true;
        PINCODE_ID = preferences.getString(Constant.EXTRA_PINCODE_ID, null);

        if (PINCODE_ID == null || ("0").equals(PINCODE_ID) || PINCODE_ID.equals("")) {
            isLoggedIn = false;
        }
    }


    //点播
    public void play(String fID,String url,String pincode){
        HttpUtils.play(fID,url,pincode,mContext,GET_STORY_QUERY_FL,okCallBack);
    }

    MyHandler handler=new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<StorySortListActivity> fragmentWeakReference;
        public MyHandler(StorySortListActivity fragment){
            fragmentWeakReference = new WeakReference<StorySortListActivity>(fragment);
        }
        @Override
        public void handleMessage(Message msg){
            StorySortListActivity mFragment = fragmentWeakReference.get();
            if(mFragment == null){
                return;
            }
            mFragment.handleMessages(msg);
        }
    };

    private void handleMessages(Message message){
        switch (message.what){
            case HANDLER_LOAD_AFTER:
//                  adapter=new StorySortAdapter(this,audiobooksSorts,R.layout.activity_story_sort_item);
                lvAdapter_s=new LvAdapter(audiobooksSorts,this);
                listView.setAdapter(lvAdapter_s);
                break;
        }
    }


    View.OnClickListener clickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int vId=v.getId();
            switch (vId){
                case R.id.btn_back:
                    finish();
                    overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                    break;
                case R.id.img_right:
//                      Intent intent=new Intent(mContext,StoryContentListActivity.class);
//                      startActivity(intent);
//                      rightToLeft();
                    break;
            }
        }
    };

//
//    class StorySortAdapter extends BaseViewHolderAdapter<AudiobooksSort> {
//        public StorySortAdapter(Context context, List data, int layoutRes) {
//            super(context, data, layoutRes);
//        }
//
//        @Override
//        protected void bindData(int pos, AudiobooksSort itemData) {
//            TextView tvName= (TextView) getViewFromHolder(R.id.tvName);
//            tvName.setText(itemData.getFiletitle());
//
//
//            ImageView img_icon= (ImageView) getViewFromHolder(R.id.img_icon);
//            img_icon.setBackgroundResource(storyPictures[item_id]);
//
//
//        }
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    OkHttpUtils.OkCallBack okCallBack=new OkHttpUtils.OkCallBack() {
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
            Log.i(TAG,"response:"+response.toString()+"----tag:"+tag);
            if(response!=null) {
                if (tag.toString().equals(GET_STORY_QUERY_SF)) {
                    ResponseBean responseBean = JsonParseUtil.json2Object(response.toString(),ResponseBean.class);
                    if(responseBean != null&&responseBean.getCode().equals(Constant.CODE_REQUEST_SUCC)) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<ArrayList<AudiobooksSort>>() {
                        }.getType();
                        try {
                            JSONObject json = new JSONObject(response.toString());
                            String sets = json.getString("data");

                            Log.d("wzg","sets=="+sets);
                            audiobooksSorts = gson.fromJson(sets, type);

                            // KARDBHelper.newInstance(mContext).insertAllAudiobooksSort(audiobooksSorts);
                            handler.sendEmptyMessage(HANDLER_LOAD_AFTER);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else{
                        showMessage(responseBean);
                    }
                }else if (tag.toString().equals(GET_STORY_QUERY_FL)) {//点播返回

                    ResponseBean responseBean = JsonParseUtil.json2Object(response.toString(),ResponseBean.class);
                    if(responseBean != null&&responseBean.getCode().equals(Constant.CODE_REQUEST_SUCC)) {
                        Toast.makeText(mContext,"点播了："+Play_Name,Toast.LENGTH_LONG).show();
                    }else{
                        showMessage(responseBean);
                    }
                }
            }
        }
    };

    private void showMessage(Object object){
        BaseResponseBean baseResponseBean = (BaseResponseBean)object;
        if(baseResponseBean==null){
            Toaster.showShortToast(StorySortListActivity.this,"请求失败");
        }else {
            Toaster.showShortToast(StorySortListActivity.this,baseResponseBean.getMessage());
        }
    }


    class LvAdapter extends BaseAdapter{
        private ArrayList<AudiobooksSort> lvItemBeanList;
        // 布局加载器
        private LayoutInflater mInflater;
        // 上下文
        private Context context;
        //布局缓存对象
        private ViewHolder holder;

        //记录当前展开项的索引
        private int expandPosition = -1;
        public LvAdapter(ArrayList<AudiobooksSort> lvItemBeanList, Context context) {
            super();
            this.lvItemBeanList = lvItemBeanList;
            this.context = context;
            mInflater = LayoutInflater.from(context);
            preferences = context.getSharedPreferences(Constant.SHARED_KARROBOT, Context.MODE_PRIVATE);

        }

        @Override

        public int getCount() {
            return null == lvItemBeanList ? 0 : lvItemBeanList.size();

        }

        @Override
        public Object getItem(int position) {
            return lvItemBeanList.get(position);

        }

        @Override

        public long getItemId(int position) {

            return position;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (null == convertView) {

                convertView = mInflater.inflate(R.layout.activity_story_sort_item, null);

                holder = new ViewHolder();

                holder.img_icon = (ImageView) convertView.findViewById(R.id.img_icon);
                holder.tv1 = (TextView) convertView.findViewById(R.id.tvName);


                holder.img_arrow1 = (ImageView) convertView.findViewById(img_arrow1);

                holder.Linear_arrow1= (LinearLayout) convertView.findViewById(R.id.Linear_arrow1);
                holder.btn_audition_item= (RelativeLayout) convertView.findViewById(btn_audition_item);
                holder.btn_OnDemand_item= (RelativeLayout) convertView.findViewById(R.id.btn_OnDemand_item);
                holder.btn_download_item= (RelativeLayout) convertView.findViewById(R.id.btn_download_item);
                holder.btn_like_item= (RelativeLayout) convertView.findViewById(R.id.btn_like_item);



                convertView.setTag(holder);

            } else {

                holder = (ViewHolder) convertView.getTag();

            }

            AudiobooksSort lvItemBean = lvItemBeanList.get(position);
            holder.tv1.setText(lvItemBean.getFiletitle());
            if (null != lvItemBean) {

                holder.img_arrow1.setOnClickListener(new OnLvItemClickListener(position));


                holder.btn_audition_item.setOnClickListener(new OnLvItemClickListener(position));
                holder.btn_OnDemand_item.setOnClickListener(new OnLvItemClickListener(position));
                holder.btn_download_item.setOnClickListener(new OnLvItemClickListener(position));
                holder.btn_like_item.setOnClickListener(new OnLvItemClickListener(position));



//如果点击的是当前项，则将其展开，否则将其隐藏

                if (expandPosition == position) {
//                    String tag= holder.btn_audition_item.getTag().toString();
//
//                    if(tag.equals("0")){
//                        MediaManage.release();
//                    }
                    holder.Linear_arrow1.setVisibility(View.VISIBLE);

                } else {

                    holder.Linear_arrow1.setVisibility(View.GONE);


                }
            }
            return convertView;
        }

        class OnLvItemClickListener implements View.OnClickListener {
            private int position;
            public OnLvItemClickListener(int position) {

                super();

                this.position = position;

            }

            @Override
            public void onClick(View v) {

                switch (v.getId()){
                    case R.id.img_arrow1:
                        //如果当前项为展开，则将其置为-1，目的是为了让其隐藏，如果当前项为隐藏，则将当前位置设置给全局变量，让其展开，这也就是借助于中间变量实现布局的展开与隐藏
                        if(expandPosition == position){
                            expandPosition = -1;
                        }else{
                            expandPosition = position;
                        }
                        MediaManage.release();
                        break;

                    case R.id.btn_audition_item:
                        if (isLoggedIn) {

                            //String tag= holder.btn_audition_item.getTag().toString();
                           // if(tag.equals("0")) {
                             //   holder.btn_audition_item.setTag(1);
                                MediaManage.playSound(lvItemBeanList.get(position).getUrl(),
                                        new MediaPlayer.OnCompletionListener() {
                                            @Override
                                            public void onCompletion(MediaPlayer mp) {
                                                // 在播放结束以后，将动画取消
                                              //  holder.btn_audition_item.setTag(0);
                                              //  holder.btn_audition_item.setBackgroundResource(R.drawable.btn_audition_icon_s);
                                            }
                                        });
                           // }else{
                            //    holder.btn_audition_item.setTag(0);
                            //    MediaManage.release();
                           // }
                        } else {
                            Toaster.showLongToast(context, context.getString(R.string.un_binding_fail));
                        }
                        break;

                    case R.id.btn_OnDemand_item:

                        if (isLoggedIn) {
                            play(lvItemBeanList.get(position).getFileid().toString(), lvItemBeanList.get(position).getUrl(), PINCODE_ID);
                            Play_Name = lvItemBeanList.get(position).getFiletitle();
                        } else {
                            Toaster.showLongToast(context, context.getString(R.string.un_binding_fail));
                        }
                        break;

                    case R.id.btn_download_item:

                        break;
                    case R.id.btn_like_item:

                        break;
                }

                notifyDataSetChanged();

            }
        }


        class ViewHolder {

            ImageView img_icon;
            TextView tv1;

            ImageView img_arrow1;

            LinearLayout Linear_arrow1;

            RelativeLayout btn_audition_item;
            RelativeLayout btn_OnDemand_item;
            RelativeLayout btn_download_item;
            RelativeLayout btn_like_item;
        }


    }

}
