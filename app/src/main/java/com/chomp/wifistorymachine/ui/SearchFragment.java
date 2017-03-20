package com.chomp.wifistorymachine.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chomp.wifistorymachine.R;
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

import static com.chomp.wifistorymachine.R.id.btn_audition_item;
import static com.chomp.wifistorymachine.R.id.img_arrow1;


public class SearchFragment extends BaseActivity {

    private final String GET_SEARCH_QUERY_SF="get_search_querySF";
    private final int HANDLER_LOAD_AFTER=101;
    private final String GET_STORY_QUERY_FL="get_story_queryFL";
    private LinearLayout search_relatve_all;
    private Button btnSearch;
    private ImageView btn_clear_search_text;
    private EditText et_search;
    private Context mContext;
    private Button button1, button2, button3, button4, button5, button6, button7, button8, button9;

    private ArrayList<AudiobooksSort> audiobooksSorts=null;
    private LvAdapter lvAdapter_s;
    private String Play_Name="";
    boolean isLoggedIn = true;
    String PINCODE_ID;
    private ListView listView;
    private SharedPreferences preferences;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_fragment);
        MyApplication.getInstance().addActivity(this);
        setINt();
        setTitle(getString(R.string.search_with));
        mContext=SearchFragment.this;
        preferences = getSharedPreferences(Constant.SHARED_KARROBOT, Context.MODE_PRIVATE);
        setBack();
        initLayout();

    }



    private void initLayout() {
        search_relatve_all = (LinearLayout) findViewById(R.id.search_relatve_all);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);
        button5 = (Button) findViewById(R.id.button5);
        button6 = (Button) findViewById(R.id.button6);
        button7 = (Button) findViewById(R.id.button7);
        button8 = (Button) findViewById(R.id.button8);
        button9 = (Button) findViewById(R.id.button9);
        btn_clear_search_text = (ImageView) findViewById(R.id.btn_clear_search_text);
        et_search = (EditText) findViewById(R.id.et_search);
        listView= (ListView) findViewById(R.id.listView);
        img_right= (ImageView) findViewById(R.id.img_right);


        btnSearch.setOnClickListener(this);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
        button6.setOnClickListener(this);
        button7.setOnClickListener(this);
        button8.setOnClickListener(this);
        button9.setOnClickListener(this);


        img_right.setOnClickListener(clickListener);
        isLoggedIn = true;
        PINCODE_ID = preferences.getString(Constant.EXTRA_PINCODE_ID, null);

        if (PINCODE_ID == null || ("0").equals(PINCODE_ID) || PINCODE_ID.equals("")) {
            isLoggedIn = false;
        }

        btn_clear_search_text.setOnClickListener(this);
        et_search.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    setViewVISIBLE();
                    btn_clear_search_text.setVisibility(View.GONE);
                    MediaManage.release();
                } else {
                    btn_clear_search_text.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    //加载试听列表数据
    public void loadData() {
        String searchName=et_search.getText().toString();
        if(!TextUtils.isEmpty(searchName)){
            HttpUtils.getSearchData(searchName,mContext,GET_SEARCH_QUERY_SF,okCallBack);
        }else{
            Toaster.showShortToast(SearchFragment.this,getString(R.string.title_search_fragment_null));
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_clear_search_text:
                et_search.setText("");
                break;
            case R.id.btn_back:
                finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                break;
            case R.id.btnSearch://搜索
                loadData();
                break;
            case R.id.button1:

                et_search.setText(getString(R.string.search_btn_test1));
                loadData();
                break;
            case R.id.button2:
                et_search.setText(getString(R.string.search_btn_test2));
                loadData();
                break;
            case R.id.button3:
                et_search.setText(getString(R.string.search_btn_test3));
                loadData();
                break;
            case R.id.button4:
                et_search.setText(getString(R.string.search_btn_test4));
                loadData();
                break;
            case R.id.button5:
                et_search.setText(getString(R.string.search_btn_test5));
                loadData();
                break;
            case R.id.button6:
                et_search.setText(getString(R.string.search_btn_test6));
                loadData();
                break;
            case R.id.button7:
                et_search.setText(getString(R.string.search_btn_test7));
                loadData();
                break;
            case R.id.button8:
                et_search.setText(getString(R.string.search_btn_test8));
                loadData();
                break;
            case R.id.button9:

                et_search.setText(getString(R.string.search_btn_test9));

                loadData();
                break;

            default:
                break;
        }
    }

    private void setViewGONE(){
        search_relatve_all.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);

    }

    private void setViewVISIBLE(){
        search_relatve_all.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
    }

    private LinearLayout btn_back;
    private TextView text_title;
    private ImageView img_right;




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



    //点播
    public void play(String fID,String url,String pincode){
        HttpUtils.play(fID,url,pincode,mContext,GET_STORY_QUERY_FL,okCallBack);
    }

    MyHandler handler=new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<SearchFragment> fragmentWeakReference;
        public MyHandler(SearchFragment fragment){
            fragmentWeakReference = new WeakReference<SearchFragment>(fragment);
        }
        @Override
        public void handleMessage(Message msg){
            SearchFragment mFragment = fragmentWeakReference.get();
            if(mFragment == null){
                return;
            }
            mFragment.handleMessages(msg);
        }
    };

    private void handleMessages(Message message){
        switch (message.what){
            case HANDLER_LOAD_AFTER:
                setViewGONE();
                lvAdapter_s=new LvAdapter(audiobooksSorts,this);
                listView.setAdapter(lvAdapter_s);
                break;
        }
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
           // Log.i(TAG,"response:"+response.toString()+"----tag:"+tag);
            if(response!=null) {
                if (tag.toString().equals(GET_SEARCH_QUERY_SF)) {
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
            Toaster.showShortToast(SearchFragment.this,"请求失败");
        }else {
            Toaster.showShortToast(SearchFragment.this,baseResponseBean.getMessage());
        }
    }


    class LvAdapter extends BaseAdapter {
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
                holder.img_arrow1 = (ImageView) convertView.findViewById(R.id.img_arrow1);
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
                    case img_arrow1:
                        //如果当前项为展开，则将其置为-1，目的是为了让其隐藏，如果当前项为隐藏，则将当前位置设置给全局变量，让其展开，这也就是借助于中间变量实现布局的展开与隐藏
                        if(expandPosition == position){
                            expandPosition = -1;
                        }else{
                            expandPosition = position;
                        }
                        MediaManage.release();
                        break;

                    case btn_audition_item:
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

    public void onDestroy() {
        super.onDestroy();
        MediaManage.release();
        handler.removeMessages(HANDLER_LOAD_AFTER);
        OkHttpUtils.getInstance().cancelTag(GET_SEARCH_QUERY_SF);
    }

}
