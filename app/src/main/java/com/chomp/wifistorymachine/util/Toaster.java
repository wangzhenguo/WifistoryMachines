package com.chomp.wifistorymachine.util;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chomp.wifistorymachine.R;

//toast工具类
public class Toaster {

	public static void showLongToast(Context context, String string) {
//		Toast.makeText(context, string, Toast.LENGTH_LONG).show();
		try {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			View layout = inflater.inflate(R.layout.toast_tips, null);
			LinearLayout layout_loading = (LinearLayout) layout.findViewById(R.id.layout_loading);
			layout_loading.setVisibility(View.GONE);
			ImageView img_loading = (ImageView) layout.findViewById(R.id.img_loading);
			img_loading.setVisibility(View.GONE);
			TextView text_loading = (TextView) layout.findViewById(R.id.text_loading);
			text_loading.setVisibility(View.GONE);
			TextView text_result = (TextView) layout.findViewById(R.id.text_result);
			text_result.setVisibility(View.VISIBLE);
			text_result.setText(string);
			Toast toast = new Toast(context.getApplicationContext());
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.setDuration(Toast.LENGTH_LONG);
			toast.setView(layout);
			toast.show();
		}catch (Exception e){

		}
	}

	public static void showLongToast(Context context, int res) {
		Toast.makeText(context, context.getResources().getString(res), Toast.LENGTH_LONG).show();
	}

	public static void showShortToast(Context context, String string) {
//		Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
		try {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			View layout = inflater.inflate(R.layout.toast_tips, null);
			LinearLayout layout_loading = (LinearLayout) layout.findViewById(R.id.layout_loading);
			layout_loading.setVisibility(View.GONE);
			ImageView img_loading = (ImageView) layout.findViewById(R.id.img_loading);
			img_loading.setVisibility(View.GONE);
			TextView text_loading = (TextView) layout.findViewById(R.id.text_loading);
			text_loading.setVisibility(View.GONE);
			TextView text_result = (TextView) layout.findViewById(R.id.text_result);
			text_result.setVisibility(View.VISIBLE);
			text_result.setText(string);
			Toast toast = new Toast(context.getApplicationContext());
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.setDuration(Toast.LENGTH_SHORT);
			toast.setView(layout);
			toast.show();
		}catch (Exception e){
			e.printStackTrace();
		}


	}

	public static void showShortToast(Context context, int res) {
		Toast.makeText(context, context.getResources().getString(res), Toast.LENGTH_SHORT).show();
	}

}
