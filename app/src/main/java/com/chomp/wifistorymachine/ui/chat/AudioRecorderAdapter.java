package com.chomp.wifistorymachine.ui.chat;

import java.util.List;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chomp.wifistorymachine.R;

import com.chomp.wifistorymachine.ui.chat.ConversationListFragment.Recorder;

public class AudioRecorderAdapter extends ArrayAdapter<Recorder> {

	private List<Recorder> mDatas;  //数据集
	private Context mContext;

	private int mMinItemWidth;
	private int mMaxItemWidth;

	private LayoutInflater mInflater;

	//private DateTimeDialog mDateTimeDialog;
	//private ReminderDialog mReminderDialog;

	//构造函数
	public AudioRecorderAdapter(Context context, List<Recorder> datas) {
		super(context, -1, datas);
		mContext=context;
		mDatas=datas;

		mInflater=LayoutInflater.from(context);

		//下面是根据屏幕宽度初始化一个最大宽度和一个最小宽度（控制录音条的长度）
		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		windowManager.getDefaultDisplay().getMetrics(outMetrics);
		mMaxItemWidth = (int) (outMetrics.widthPixels * 0.8f);    //控制录音条的最大宽度
		mMinItemWidth = (int) (outMetrics.widthPixels * 0.15f);    //控制录音条的最小宽度

	//	mDateTimeDialog=new DateTimeDialog(getContext());
	//	mReminderDialog=new ReminderDialog(getContext());
	}

	/* *
	 * ViewHolder模式
	 * (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.voicenotes_recorder_item, parent, false);
			// Item上控件的初始化
			// 关联组件，提高效率

			holder = new ViewHolder(convertView);

			holder.currentTime=(TextView) convertView.findViewById(R.id.currentTime_text);
			holder.seconds=(TextView) convertView.findViewById(R.id.right_recorder_time);
			holder.right_mLength=convertView.findViewById(R.id.right_recorder_length);
			holder.left_mLength=convertView.findViewById(R.id.left_recorder_length);


			convertView.setTag(holder);   //设置标签，标识convertView

			//Recorder recorder = mDatas.get(position);
			//convertView.setTag(recorder.getmCurrentTime());  //用录音的CurrentTime来标识convertView，方便以后获得
		}else{
			holder=(ViewHolder) convertView.getTag();
		}

		// Item上的“图片按钮”的监听器初始化

		//reminderBtnListener=new ReminderBtnListener(position);

		// 设置控件的显示内容
		holder.currentTime.setText(getItem(position).getmCurrentTime());
		holder.seconds.setText(Math.round(getItem(position).getTime())+"\"");   //录音的时间的显示
		ViewGroup.LayoutParams lp=holder.right_mLength.getLayoutParams();
		lp.width=(int) (mMinItemWidth + (mMaxItemWidth/60f * getItem(position).getTime()));

		ViewGroup.LayoutParams lp2=holder.left_mLength.getLayoutParams();
		lp2.width=(int) (mMinItemWidth + (mMaxItemWidth/60f * getItem(position).getTime()));

		if(mDatas.get(position).getFilePath().contains("bb")){
			// viewHolder.text_right.setText(right);
			holder.left.setVisibility(View.VISIBLE);
			holder.right.setVisibility(View.INVISIBLE);
		}

		if(mDatas.get(position).getFilePath().contains("app")){
			// viewHolder.text_left.setText(left);

			holder.right.setVisibility(View.VISIBLE);
			holder.left.setVisibility(View.INVISIBLE);
		}


		//holder.reminderBtn.setOnClickListener(reminderBtnListener);

		return convertView;
	}

	/**
	 * Item控件
	 * @author songshi
	 *
	 */
	private class ViewHolder {
		public TextView currentTime;      //显示系统当前的时间
		public TextView seconds;          //录音的时间长度
		public View right_mLength;              //“录音条”显示长度
		public View left_mLength;              //“录音条”显示长度

		public LinearLayout left;
		public View text_right;
		public LinearLayout right;
		public View rootView;

		public ViewHolder(View rootView) {
			this.rootView = rootView;
			this.left = (LinearLayout) rootView.findViewById(R.id.left);

			this.right = (LinearLayout) rootView.findViewById(R.id.right);
		}


	}


}
