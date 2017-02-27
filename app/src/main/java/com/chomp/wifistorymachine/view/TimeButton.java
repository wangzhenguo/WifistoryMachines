package com.chomp.wifistorymachine.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class TimeButton extends Button implements OnClickListener {
	private long lenght = 60 * 1000;
	private String textafter = "s";
	private String textbefore = "获取";
	private final String TIME = "time";
	private final String CTIME = "ctime";
	private OnClickListener mOnclickListener;
	private Timer timer;
	private TimerTask timerTask;
	private long time;
	private Map<String, Long> map = new HashMap<String, Long>();

	public TimeButton(Context context) {
		super(context);
		setOnClickListener(this);

	}

	public TimeButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnClickListener(this);
	}

	@SuppressLint("HandlerLeak")
	Handler han = new Handler() {
		public void handleMessage(android.os.Message msg) {
			TimeButton.this.setText(time / 1000 + textafter);
			time = time - 1000;
			if (time < 0) {
				TimeButton.this.setEnabled(true);
				TimeButton.this.setClickable(true);
				TimeButton.this.setText(textbefore);
				clearTimer();
			}
		};
	};

	private void initTimer() {
		time = lenght;
		timer = new Timer();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				han.sendEmptyMessage(0x01);
			}
		};
	}

	private void clearTimer() {
		if (timerTask != null) {
			timerTask.cancel();
			timerTask = null;
		}
		if (timer != null)
			timer.cancel();
		timer = null;
		han.removeMessages(0x01);
	}

	@Override
	public void setOnClickListener(OnClickListener l) {
		if (l instanceof TimeButton) {
			super.setOnClickListener(l);
		} else
			this.mOnclickListener = l;
	}

	@Override
	public void onClick(View v) {
		if (mOnclickListener != null)
			mOnclickListener.onClick(v);
	}

	public void onDestroy() {
		if (map == null)
			map = new HashMap<String, Long>();
		map.put(TIME, time);
		map.put(CTIME, System.currentTimeMillis());
		clearTimer();
	}

	public void onCreate(Bundle bundle) {
		if (map == null)
			return;
		if (map.size() <= 0)
			return;
		long time = System.currentTimeMillis() - map.get(CTIME) - map.get(TIME);
		map.clear();
		if (time > 0)
			return;
		else {
			initTimer();
			this.time = Math.abs(time);
			timer.schedule(timerTask, 0, 1000);
			this.setText(time + textafter);
			this.setEnabled(false);
		}
	}

	/** * 设置计时时显示的文本 */
	public TimeButton setTextAfter(String text1) {
		this.textafter = text1;
		return this;
	}

	/** * 设置点击之前的文 */
	public TimeButton setTextBefore(String text0) {
		this.textbefore = text0;
		this.setText(textbefore);
		return this;
	}

	/**
	 * 设置到计时长
	 * 
	 * @param lenght
	 *            时间 默认毫秒
	 * @return
	 */
	public TimeButton setLenght(long lenght) {
		this.lenght = lenght;
		return this;
	}

	public void startCountdown() {
		initTimer();
		this.setText(time / 1000 + textafter);
		this.setEnabled(false);
		this.setClickable(false);
		timer.schedule(timerTask, 0, 1000);
	}
}