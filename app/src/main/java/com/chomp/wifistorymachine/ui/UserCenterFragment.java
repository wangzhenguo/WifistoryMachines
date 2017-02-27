package com.chomp.wifistorymachine.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chomp.wifistorymachine.R;
import com.chomp.wifistorymachine.constants.Constant;
import com.chomp.wifistorymachine.ui.update.UpdateManager;




public class UserCenterFragment extends BaseFragment{

	String TAG="UserCenterFragment";

	private LinearLayout btn_back;
	private LinearLayout toy_info_ll;
	private LinearLayout my_storevoice_ll;

	private TextView TextVersion;
	private TextView text_title;


	protected Activity mActivity;

    private ImageView quit_btn;

	private SharedPreferences preferences;

	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.bm_setting_main_ui, container, false);
		initLayout(view);
		return view;
	}


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}


	public void onDestroy() {
		super.onDestroy();

	}


	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mActivity = activity;
	}

	private Handler  handler = new Handler(){
		public void handleMessage(android.os.Message msg){

		};
	};

	private void initLayout(View view) {
		btn_back = (LinearLayout)view.findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		text_title = (TextView)view.findViewById(R.id.text_title);
		text_title.setText(getString(R.string.setting_more));

		toy_info_ll = (LinearLayout)view.findViewById(R.id.toy_info_ll);
		toy_info_ll.setOnClickListener(this);

		my_storevoice_ll = (LinearLayout)view.findViewById(R.id.my_storevoice_ll);
		my_storevoice_ll.setOnClickListener(this);



		TextVersion = (TextView)view.findViewById(R.id.TextVersion);
		TextVersion.setText("V"+getVersionCode()+".0");

		quit_btn = (ImageView)view.findViewById(R.id.quit_btn);
		quit_btn.setOnClickListener(this);

		preferences = getActivity().getSharedPreferences(Constant.SHARED_KARROBOT, Context.MODE_PRIVATE);


	}

	private int getVersionCode()
	{
		int versionCode = 0;
		try
		{
			versionCode = getActivity().getPackageManager().getPackageInfo("com.chomp.wifistorymachine", 0).versionCode;
		} catch (PackageManager.NameNotFoundException e)
		{
			e.printStackTrace();
		}
		return versionCode;
	}

	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.toy_info_ll:
				Intent in=new Intent();
				in.setClass(getActivity(),MyToysActivity.class);
				startActivity(in);
				break;
			case R.id.my_storevoice_ll:
				 Toast.makeText(getActivity(), R.string.soft_update, Toast.LENGTH_LONG).show();



				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						UpdateManager manager = new UpdateManager(getActivity());
						manager.checkUpdate();
					}
				},500);
			    break ;
			case R.id.quit_btn:
				SharedPreferences.Editor editor=preferences.edit();
				editor.putString(Constant.EXTRA_USER_ID,"0");
				boolean b= editor.commit();
				Intent in2=new Intent();
				in2.setClass(getActivity(),LoginActivity.class);
				startActivity(in2);
				break;

		default:
		break;
		}
	}





}
