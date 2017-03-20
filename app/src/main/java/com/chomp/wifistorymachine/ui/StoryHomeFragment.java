package com.chomp.wifistorymachine.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.chomp.wifistorymachine.R;
import com.chomp.wifistorymachine.constants.Constant;
import com.chomp.wifistorymachine.dao.AudiobooksType;
import com.chomp.wifistorymachine.dao.KARDBHelper;
import com.chomp.wifistorymachine.model.BaseResponseBean;
import com.chomp.wifistorymachine.model.ResponseBean;
import com.chomp.wifistorymachine.okhttp.HttpUtils;
import com.chomp.wifistorymachine.okhttp.OkHttpUtils;
import com.chomp.wifistorymachine.ui.chat.ConversationListFragment;
import com.chomp.wifistorymachine.util.JsonParseUtil;
import com.chomp.wifistorymachine.util.Toaster;
import com.chomp.wifistorymachine.util.Utils;
import com.chomp.wifistorymachine.view.MyImgScroll;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import android.widget.ImageView.ScaleType;




public class StoryHomeFragment extends BaseFragment {

	String TAG="StoryHomeFragment";

	private final String GET_STORY_QUERY_ALBUM = "get_story_queryAlbum";


	private final String Electricity_Temperature_Signal = "get_electricity_eemperature_signal";


    private final String GET_STORY_QUERY_SF="get_story_querySF";
	private final String GET_STORY_QUERY_FL="get_story_queryFL";

	private final int HANDLER_LOAD_AFTER=101;
	private final int HANDLER_LOCAL_UPDATE=102;

	private ImageView img_left;
	private TextView text_title;
	private ImageView img_right;
	private ImageView imgV_db;
	private LinearLayout Layout_electricity_temperature_signal;
	private View include_bm_toy_state_layout;

	private ImageView electricity_iv;
	private ImageView online_state_iv;
	private TextView net_state_tv;
	private ImageView electricity_state_iv;
	private TextView electricity_state_tv;
	private ImageView net_state_iv;
	private TextView temperature_state_tx;
	private TextView volume_tv;
	private ImageView volume_value_iv;


	private ImageView volume_up_ib;
	private ImageView volume_down_ib;

	private ImageButton close_ib;
	private MyGridView list_story_type;
	private StoryTypeAdapter adapter;
	protected Activity mActivity;

	private AlertDialog dialog;

	String pbattery = null;
	String ptem = null;
	String online = null;
	String pvolume = null;

	private SharedPreferences preferences;



	private int[] storyPictures = new int[] { R.drawable.chengyugushi, R.drawable.guanchasikao, R.drawable.guoxuejindian, R.drawable.jiankanganquan,
			R.drawable.lishigushi, R.drawable.shenghuoxiguan, R.drawable.shuimianyinyue, R.drawable.tonghuagushi,
			R.drawable.xinggepeiyang, R.drawable.yingyuxuexi, R.drawable.yingyuyuandi, R.drawable.yuyanqimeng, R.drawable.zhihuigushi,R.drawable.zhishiwenda,
			R.drawable.zhonghuatongyao,R.drawable.zhongwenerge,R.drawable.zirankepu,
	};

    private List<AudiobooksType> newStorys=new ArrayList<AudiobooksType>();
	private List<AudiobooksType> localStorys=new ArrayList<AudiobooksType>();


	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_story, container, false);
		loadData();
		preferences = getActivity().getSharedPreferences(Constant.SHARED_KARROBOT, Context.MODE_PRIVATE);

		initLayout(view);

		return view;
	}


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		registerReceiver();
	}


	public void onDestroy() {
		unRegisterReceiver();
		handler.removeMessages(HANDLER_LOAD_AFTER);
		OkHttpUtils.getInstance().cancelTag(GET_STORY_QUERY_ALBUM);


		OkHttpUtils.getInstance().cancelTag(Electricity_Temperature_Signal);
		super.onDestroy();

	}

	MyHandler handler=new MyHandler(this);

	private static class MyHandler extends Handler {
		private final WeakReference<StoryHomeFragment> fragmentWeakReference;
		public MyHandler(StoryHomeFragment fragment){
			fragmentWeakReference = new WeakReference<StoryHomeFragment>(fragment);
		}
		@Override
		public void handleMessage(Message msg){
			StoryHomeFragment mFragment = fragmentWeakReference.get();
			if(mFragment == null){
				return;
			}
			mFragment.handleMessages(msg);
		}
	};


	private void handleMessages(Message message){
		switch (message.what){
			case HANDLER_LOAD_AFTER:
				 initData();
				 handler.sendEmptyMessage(HANDLER_LOCAL_UPDATE);
				break;
			case HANDLER_LOCAL_UPDATE:
				 localStorys= KARDBHelper.newInstance(mActivity).queryAllAudiobooksType();
				 Log.i(TAG,"KARDBHelper.newInstance:localStorys.size():"+localStorys.size());
				 if(localStorys!=null&&localStorys.size()>0){
                     for (AudiobooksType audiobooksType:localStorys){
						 if(newStorys.contains(audiobooksType)){
							// Log.i(TAG,"KARDBHelper.newInstance:localStorys.contains存在"+audiobooksType.getSortname()+"----v:"+audiobooksType.getSortid());
						 }else{
							// Log.i(TAG,"KARDBHelper.newInstance:localStorys.contains不存在"+audiobooksType.getSortname()+"----v:"+audiobooksType.getSortid());
//							 KARDBHelper.newInstance(mActivity).deleteAudiobooksSort(audiobooksType.getSortname());
							 KARDBHelper.newInstance(mActivity).deleteAudiobooksList(audiobooksType.getSortname());
						 }
					 };
				 }
				 KARDBHelper.newInstance(mActivity).deleteAllAudiobooksType();
				 KARDBHelper.newInstance(mActivity).insertAllAudiobooksType(newStorys);
				break;
		}
	}


	private void registerReceiver() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		mActivity.registerReceiver(broadcastReceiver, intentFilter);
	}

	private void unRegisterReceiver() {
		mActivity.unregisterReceiver(broadcastReceiver);
	}

	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction()) || ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())
					|| WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
				Utils.getWifiState(mActivity);
			}
		}
	};


	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mActivity = activity;

		if(listViews != null){
			listViews.clear();
			listViews = null;
		}
		listViews = new ArrayList<View>();
		int[] imageResId = new int[] { R.drawable.banner1, R.drawable.banner2,R.drawable.banner1,R.drawable.banner2};
		for (int i = 0; i < imageResId.length; i++) {
			ImageView imageView = new ImageView(activity);
			imageView.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {// 设置图片点击事件
//					Toast.makeText(getActivity(),
//							"点击的:" + myPager.getCurIndex(), Toast.LENGTH_SHORT)
//							.show();
				}
			});
			imageView.setImageResource(imageResId[i]);
			imageView.setScaleType(ScaleType.CENTER_CROP);
			listViews.add(imageView);
		}

	}

	private void initData() {
		adapter = new StoryTypeAdapter();
		adapter.setStoryTypes(newStorys);

		list_story_type.setAdapter(adapter);
		adapter.notifyDataSetChanged();

	}


	private MyImgScroll myPager; // 图片容器
	private LinearLayout ovalLayout; // 圆点容器
	private List<View> listViews; // 图片组

	private void initLayout(View view) {

		//开始滚动
		myPager = (MyImgScroll)view.findViewById(R.id.myvp);
		myPager.start(getActivity(), listViews, 4000, ovalLayout,
				R.layout.ad_bottom_item, R.id.ad_item_v,
				R.drawable.dot_focused, R.drawable.dot_normal);


		img_left = (ImageView) view.findViewById(R.id.img_left);
		img_left.setOnClickListener(this);

		text_title = (TextView) view.findViewById(R.id.text_title);
		text_title.setText(getString(R.string.home_story));

		img_right = (ImageView) view.findViewById(R.id.img_right);
		img_right.setOnClickListener(this);

		imgV_db= (ImageView) view.findViewById(R.id.imgV_db);
		imgV_db.setOnClickListener(this);



		include_bm_toy_state_layout= (View) view.findViewById(R.id.include_bm_toy_state_layout);

		close_ib= (ImageButton) view.findViewById(R.id.close_ib);
		close_ib.setOnClickListener(this);

		electricity_iv= (ImageView) view.findViewById(R.id.electricity_iv);
		online_state_iv= (ImageView) view.findViewById(R.id.online_state_iv);

		net_state_iv= (ImageView) view.findViewById(R.id.net_state_iv);
		electricity_state_tv= (TextView) view.findViewById(R.id.electricity_state_tv);

		electricity_state_iv= (ImageView) view.findViewById(R.id.electricity_state_iv);
		net_state_iv= (ImageView) view.findViewById(R.id.net_state_iv);
		volume_value_iv= (ImageView) view.findViewById(R.id.volume_value_iv);

		volume_up_ib= (ImageView) view.findViewById(R.id.volume_up_ib);
		volume_up_ib.setOnClickListener(this);
		volume_down_ib= (ImageView) view.findViewById(R.id.volume_down_ib);
		volume_down_ib.setOnClickListener(this);

		net_state_tv= (TextView) view.findViewById(R.id.net_state_tv);

		temperature_state_tx= (TextView) view.findViewById(R.id.temperature_state_tx);
		volume_tv= (TextView) view.findViewById(R.id.volume_tv);
		Layout_electricity_temperature_signal= (LinearLayout) view.findViewById(R.id.Layout_electricity_temperature_signal);
		Layout_electricity_temperature_signal.setOnClickListener(this);



//		AnimationDrawable animationDrawable= (AnimationDrawable) img_right.getDrawable();
//		animationDrawable.stop();
//		img_right.setVisibility(View.GONE);

		list_story_type = (MyGridView) view.findViewById(R.id.list_story_type);

		String PINCODE_ID = preferences.getString(Constant.EXTRA_PINCODE_ID, null);
        boolean isLoggedIn=true;
		if (PINCODE_ID == null || ("0").equals(PINCODE_ID) || PINCODE_ID.equals("")) {
			isLoggedIn = false;
		}

		if(isLoggedIn) {
			Layout_electricity_temperature_signal.setVisibility(View.VISIBLE);
			load_Electricity_Temperature_Signal();
		}else{
			Layout_electricity_temperature_signal.setVisibility(View.GONE);
		}

	}


	private void setViewSta_min(String electricity,String signal){

		int electricity_v=Integer.parseInt(electricity);
		int signal_v=Integer.parseInt(signal);

		electricity_state_iv.setImageDrawable(null);
		if (electricity_v>=0 && electricity_v<=20) {
			electricity_iv.setBackgroundResource(R.drawable.bm_toy_electricity_big_0);
		}else if(electricity_v>20 && electricity_v<=40){
			electricity_state_iv.setBackgroundResource(R.drawable.bm_toy_electricity_big_1);
		}else if(electricity_v>40 && electricity_v<=60){
			electricity_iv.setBackgroundResource(R.drawable.bm_toy_electricity_big_2);
		}else if(electricity_v>60 && electricity_v<=80){
			electricity_iv.setBackgroundResource(R.drawable.bm_toy_electricity_big_2);
		}else if(electricity_v>80 && electricity_v<=99){
			electricity_iv.setBackgroundResource(R.drawable.bm_toy_electricity_big_4);
		}else if(electricity_v>99 && electricity_v<=100){
			electricity_iv.setBackgroundResource(R.drawable.bm_toy_electricity_big_5);
		}else if(electricity_v>=200){
			electricity_iv.setBackgroundResource(R.drawable.bm_toy_electricity_charging);//充电
		}

		electricity_state_tv.setText("约"+electricity_v+"%");
		online_state_iv.setImageDrawable(null);
		if(signal_v==0){

			online_state_iv.setBackgroundResource(R.drawable.bm_toy_offline);
		}else{

			online_state_iv.setBackgroundResource(R.drawable.bm_toy_online_v3);
		}
	}

	private void setViewSta_max(String electricity,String signal,String temperature_v,String volume){
		int electricity_v=Integer.parseInt(electricity);
		int signal_v=Integer.parseInt(signal);
		int volume_v=Integer.parseInt(volume);
		electricity_state_iv.setImageDrawable(null);
		if (electricity_v>=0 && electricity_v<=20) {
			electricity_state_iv.setBackgroundResource(R.drawable.bm_toy_electricity_big_0);
		}else if(electricity_v>20 && electricity_v<=40){
			electricity_state_iv.setBackgroundResource(R.drawable.bm_toy_electricity_big_1);
		}else if(electricity_v>40 && electricity_v<=60){
			electricity_state_iv.setBackgroundResource(R.drawable.bm_toy_electricity_big_2);
		}else if(electricity_v>60 && electricity_v<=80){
			electricity_state_iv.setBackgroundResource(R.drawable.bm_toy_electricity_big_3);
		}else if(electricity_v>80 && electricity_v<=99){
			electricity_state_iv.setBackgroundResource(R.drawable.bm_toy_electricity_big_4);
		}else if(electricity_v>99 && electricity_v<=100){
			electricity_state_iv.setBackgroundResource(R.drawable.bm_toy_electricity_big_5);
		}else if(electricity_v>=200){
			electricity_state_iv.setBackgroundResource(R.drawable.bm_toy_electricity_charging);
		}
		temperature_state_tx.setText(temperature_v+"℃");
		String txt_volume_v = String.format(getString(R.string.new_main_ui_toy_volume_value), volume);
		volume_tv.setText(txt_volume_v+"%");

		if(volume_v>=0 && volume_v<=20){
			volume_value_iv.setImageLevel(1);
		}else if(volume_v>20 && volume_v<=40){
			volume_value_iv.setImageLevel(2);
		}else if(volume_v>40 && volume_v<=60){
			volume_value_iv.setImageLevel(3);
		}else if(volume_v>60 && volume_v<=80){
			volume_value_iv.setImageLevel(4);
		}else if(volume_v>80 && volume_v<=100){
			volume_value_iv.setImageLevel(5);
		}
		net_state_iv.setImageDrawable(null);
		if(signal_v==0){
			net_state_tv.setText(getString(R.string.new_main_ui_toy_state_offline));
			net_state_iv.setBackgroundResource(R.drawable.bm_toy_offline_big);
		}else{
			net_state_tv.setText(getString(R.string.new_main_ui_toy_state_online));
			net_state_iv.setBackgroundResource(R.drawable.bm_toy_online_big);
		}
	}


	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.imgV_db:

				Intent intent1=new Intent(mActivity,StorySortListActivity.class);

				//用Bundle携带数据
				Bundle bundle=new Bundle();
				//传递name参数为tinyphp
				bundle.putString("sortid", "点播");

				bundle.putString("sortname","点播");

				intent1.putExtras(bundle);

				startActivity(intent1);
				mActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

				break;
		case R.id.img_left:
			if (!Utils.getWifiState(mActivity)) {
				showWIFIUnableDialog();
				return;
			}
			Intent intent = new Intent(mActivity, SearchFragment.class);
			mActivity.startActivity(intent);
			mActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
		     break;
		case R.id.img_right:
//			Intent intent1=new Intent(mActivity,StoryContentListActivity.class);
//			startActivity(intent1);
//			mActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
		     break;
		case R.id.Layout_electricity_temperature_signal:
			include_bm_toy_state_layout.setVisibility(View.VISIBLE);
			if(pbattery!=null && online!=null &&ptem!=null && pvolume!=null){
				setViewSta_max(pbattery,online,ptem,pvolume);
			}

			break;
		case R.id.close_ib:
			include_bm_toy_state_layout.setVisibility(View.GONE);
			break;
		case R.id.volume_up_ib:
			//音量加
			 break;
		case R.id.volume_down_ib:
            //音量减
			break;
			default:
		break;
		}
	}

	private void showWIFIUnableDialog() {
		dialog = new AlertDialog.Builder(mActivity).create();
		dialog.show();
		Window window = dialog.getWindow();
		window.setGravity(Gravity.TOP);
		window.setContentView(R.layout.dialog_wifi_unable);
		Button btn_set = (Button) window.findViewById(R.id.btn_set);
		btn_set.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent wifiIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
				startActivity(wifiIntent);
				mActivity.overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
				dialog.dismiss();
			}
		});
		TextView text_back = (TextView) window.findViewById(R.id.text_back);
		text_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}
	//获取试听列表-首页
	private long exitTime = 0;
	public void loadData() {

			HttpUtils.loadData(mActivity, GET_STORY_QUERY_ALBUM, okCallBack);

	}

	//获取电量、温度、故事机的状态
	public void load_Electricity_Temperature_Signal() {
		HttpUtils.loadData_Electricity_Temperature_Signal(mActivity,Electricity_Temperature_Signal,okCallBack);
	}



	public class StoryTypeAdapter extends BaseAdapter {
		private List<AudiobooksType> storyTypes;

		public List<AudiobooksType> getStoryTypes() {
			return storyTypes;
		}

		public void setStoryTypes(List<AudiobooksType> storyBeans) {
			this.storyTypes = storyBeans;
		}

		@Override
		public int getCount() {
			return storyTypes.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (null == convertView) {
				viewHolder = new ViewHolder();
				LayoutInflater inflater = LayoutInflater.from(mActivity);
				convertView = inflater.inflate(R.layout.item_story_type, null);
				viewHolder.storyPicture = (ImageView) convertView.findViewById(R.id.layout_story);
				viewHolder.storyName = (TextView) convertView.findViewById(R.id.text_story_type_name);
				//viewHolder.view_diver = convertView.findViewById(R.id.view_diver);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			final AudiobooksType storyTypeDB = storyTypes.get(position);
			if (position == storyTypes.size() - 1) {
				//viewHolder.view_diver.setVisibility(View.VISIBLE);
			} else {
				//viewHolder.view_diver.setVisibility(View.GONE);
			}
			if (storyTypeDB != null) {

				switch (storyTypeDB.getSortname()){
					case "成语故事":
						viewHolder.storyPicture.setBackgroundResource(storyPictures[0]);
						break;
					case "观察思考":
						viewHolder.storyPicture.setBackgroundResource(storyPictures[1]);
						break;
					case "国学经典":
						viewHolder.storyPicture.setBackgroundResource(storyPictures[2]);
						break;
					case "健康安全":
						viewHolder.storyPicture.setBackgroundResource(storyPictures[3]);
						break;
					case "历史故事":
						viewHolder.storyPicture.setBackgroundResource(storyPictures[4]);
						break;
					case "生活习惯":
						viewHolder.storyPicture.setBackgroundResource(storyPictures[5]);
						break;
					case "睡眠音乐":
						viewHolder.storyPicture.setBackgroundResource(storyPictures[6]);
						break;
					case "童话故事":
						viewHolder.storyPicture.setBackgroundResource(storyPictures[7]);
						break;
					case "性格培养":
						viewHolder.storyPicture.setBackgroundResource(storyPictures[8]);
						break;
					case "英语学习":
						viewHolder.storyPicture.setBackgroundResource(storyPictures[9]);
						break;
					case "英语园地":
						viewHolder.storyPicture.setBackgroundResource(storyPictures[10]);
						break;
					case "语言启蒙":
						viewHolder.storyPicture.setBackgroundResource(storyPictures[11]);
						break;
					case "知识疑问":
						viewHolder.storyPicture.setBackgroundResource(storyPictures[12]);
						break;
					case "智慧故事":
						viewHolder.storyPicture.setBackgroundResource(storyPictures[13]);
						break;
					case "中华童谣":
						viewHolder.storyPicture.setBackgroundResource(storyPictures[14]);
						break;
					case "中文儿歌":
						viewHolder.storyPicture.setBackgroundResource(storyPictures[15]);
						break;
					case "自然科普":
						viewHolder.storyPicture.setBackgroundResource(storyPictures[16]);
						break;
				}

				viewHolder.storyName.setText(storyTypeDB.getSortname());
			}
			list_story_type.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Intent intent1=new Intent(mActivity,StorySortListActivity.class);

					//用Bundle携带数据
					Bundle bundle=new Bundle();
					//传递name参数为tinyphp
					bundle.putString("sortid", storyTypes.get(position).getSortid()+"");

					bundle.putString("sortname", storyTypes.get(position).getSortname());

					intent1.putExtras(bundle);

					startActivity(intent1);
			        mActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
				}
			});
			return convertView;
		}
	}


	private static class ViewHolder {
		ImageView storyPicture;
		TextView storyName;
		//View view_diver;
	}


	OkHttpUtils.OkCallBack okCallBack=new OkHttpUtils.OkCallBack() {
		public void onBefore(Request request, Object tag) {

		}



		public void onAfter(Object tag) {

		}

		public void onError(Call call, Response response, Exception e, Object tag) {

		}

		@Override
		public void onResponse(Call call, Response response,Object tag) {

		}

		public void onResponse(Object response, Object tag) {
			//Log.i(TAG,"response:"+response.toString()+"----tag:"+tag);

			if(response!=null) {
				if (tag.toString().equals(GET_STORY_QUERY_ALBUM)) {

					ResponseBean responseBean = JsonParseUtil.json2Object(response.toString(),ResponseBean.class);
					if(responseBean != null&&responseBean.getCode().equals(Constant.CODE_REQUEST_SUCC)){
						Gson gson = new Gson();
						Type type = new TypeToken<ArrayList<AudiobooksType>>() {
						}.getType();
						try {
							JSONObject json = new JSONObject(response.toString());
							String sets = json.getString("data");
							newStorys = gson.fromJson(sets, type);
							handler.sendEmptyMessage(HANDLER_LOAD_AFTER);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}else{
						showMessage(responseBean);
					}

				}else if(tag.toString().equals(Electricity_Temperature_Signal)) {

					ResponseBean responseBean = JsonParseUtil.json2Object(response.toString(),ResponseBean.class);
					if(responseBean != null&&responseBean.getCode().equals(Constant.CODE_REQUEST_SUCC)){
						try {
							JSONObject json = new JSONObject(response.toString());
							JSONObject info=json.getJSONObject("data");
							pbattery = info.getString("pbattery");
							ptem = info.getString("ptem");
							online = info.getString("online");
							pvolume = info.getString("pvolume");
							setViewSta_min(pbattery,online);
							Log.d("wzg","pbattery="+pbattery +",ptem="+ptem+",online"+online+",pvolume="+pvolume);

						//	handler.sendEmptyMessage(HANDLER_LOAD_AFTER);
						} catch (JSONException e) {
							e.printStackTrace();
						}
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
			Toaster.showShortToast(getActivity(),"请求失败");
		}else {
			Toaster.showShortToast(getActivity(),baseResponseBean.getMessage());
		}
	}


}
