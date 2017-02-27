package com.chomp.wifistorymachine.util;

import android.text.TextUtils;

import com.google.gson.Gson;

/**
 * json解析
 * 
 */
public class JsonParseUtil {
	private static final String TAG = JsonParseUtil.class.getSimpleName();

	private static Gson gson = new Gson();

	/**
	 * 解析response数据
	 * 
	 * @param json
	 * @return
	 */
	public static <T> T json2Object(String json,Class<T> valueType) {
		if (TextUtils.isEmpty(json)) {
			return null;
		}
		if (gson == null) {
			gson = new Gson();
		}
		T bean = null;
		try {
			bean = gson.fromJson(json, valueType);
		} catch (Exception e) {
			return null;
		}
		return bean;
	}
	/**
	 * 将对象转换成jsonstring
	 *
	 * @param valueType
	 *            要传化的对象
	 * @return json字符串
	 */
	public static String object2Json(Object valueType) {
		String json = "";
		Gson gson = new Gson();
		json = gson.toJson(valueType);
		return json;
	}
}
