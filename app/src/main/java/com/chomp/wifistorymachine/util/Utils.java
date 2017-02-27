package com.chomp.wifistorymachine.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.chomp.wifistorymachine.constants.Constant;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;


public class Utils {
	public static final String INSTALLATION = "INSTALLATION";
	// 密钥  
	private static final String key = "api_yF$(1)2#m";  
	// private Timer timer = new Timer();
	private static SimpleDateFormat mTimeFormat = new SimpleDateFormat("mm:ss");

	public static String formatTime(long time) {
		if (time < 0) {
			time = 0;
		}
		return mTimeFormat.format(new Date(time));
	}



	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		if(null==context){
			return (int)dpValue*2;
		}
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}




	public static String getTimeByLong(long l){
		SimpleDateFormat sdf= new SimpleDateFormat("HH:mm");
		Date dt = new Date(l);
		String sDateTime = sdf.format(dt);
		return sDateTime;
	}

	public static String getDateByLong(long l){

		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
		Date dt = new Date(l);
		String sDateTime = sdf.format(dt);
		return sDateTime;
	}
	
	public static String getUdid(Context context){
		TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String deviceId = manager.getDeviceId();
		if (TextUtils.isEmpty(deviceId)) {
			deviceId = "1234567890";
		}
		return deviceId;
	}
	
	public static void hideInput(Context context) {
		InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromInputMethod(((Activity) context).getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/**
	 * 获取当前应用的版本号
	 * */
	public static String getVersionName(Context context) {
		// 获取packagemanager的实例
		PackageManager packageManager = context.getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		try {
			PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			String version = packInfo.versionName;
			return version;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 获取用户id
	 * 
	 * @param context
	 * @return
	 */
	public synchronized static String getPhoneID(Context context) {
		String sID = null;
		SharedPreferences preferences = context.getSharedPreferences(Constant.SHARED_KARROBOT, Context.MODE_PRIVATE);
		sID = preferences.getString(Constant.CLIENTID,null);
		if (sID == null) {
			File installation = new File(context.getFilesDir(), INSTALLATION);
			try {
				if (!installation.exists()) {
					writeInstallationFile(installation);
				}
				sID = readInstallationFile(context,installation);
			} catch (Exception e) {
			}
		}
		return sID;
	}

	public static boolean getWifiState(Context context) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		if (wifiManager != null) {
			if (!wifiManager.isWifiEnabled()) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	public static boolean isNetworkConnected(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(Constant.SHARED_KARROBOT, Context.MODE_PRIVATE);
		return preferences.getBoolean(Constant.NETWORK_CONNECTED, false);
	}
	/**
	 * 写入安装文件
	 * 
	 * @param installation
	 * @throws IOException
	 */
	private static void writeInstallationFile(File installation) throws IOException {
		FileOutputStream out = new FileOutputStream(installation);
		String id = UUID.randomUUID().toString();
		out.write(id.getBytes());
		out.close();
	}

	/**
	 * 读取安装文件
	 * 
	 * @param installation
	 * @return
	 * @throws IOException
	 */
	private static String readInstallationFile(Context context,File installation) throws IOException {
		RandomAccessFile f = new RandomAccessFile(installation, "r");
		byte[] bytes = new byte[(int) f.length()];
		f.readFully(bytes);
		f.close();
		SharedPreferences preferences = context.getSharedPreferences(Constant.SHARED_KARROBOT, Context.MODE_PRIVATE);
		preferences.edit().putString(Constant.CLIENTID,new String(bytes)).commit();
		return new String(bytes);
	}

	public static String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; b != null && n < b.length; n++) {
			stmp = (Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
		}
		return hs.toUpperCase();
	}

	public static byte[] hex2byte(byte[] b) {
		if ((b.length % 2) != 0)
			throw new IllegalArgumentException();
		byte[] b2 = new byte[b.length / 2];
		for (int n = 0; n < b.length; n += 2) {
			String item = new String(b, n, 2);
			b2[n / 2] = (byte) Integer.parseInt(item, 16);
		}
		return b2;
	}

	public static byte[] encrypt(byte[] src, byte[] key) throws Exception {
		SecureRandom sr = new SecureRandom();
		DESKeySpec dks = new DESKeySpec(key);
		SecretKeyFactory keyFactory = SecretKeyFactory
				.getInstance("DES");
		SecretKey securekey = keyFactory.generateSecret(dks);
		Cipher cipher = Cipher.getInstance("DES");
		cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
		return cipher.doFinal(src);
	}

	public static byte[] decrypt(byte[] src, byte[] key) throws Exception {
		SecureRandom sr = new SecureRandom();
		DESKeySpec dks = new DESKeySpec(key);
		SecretKeyFactory keyFactory = SecretKeyFactory
				.getInstance("DES");
		SecretKey securekey = keyFactory.generateSecret(dks);
		Cipher cipher = Cipher.getInstance("DES");
		cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
		return cipher.doFinal(src);
	}

	//des解密
	public static String decrypt(String data) {
		if (data != null)
			try {
				return new String(decrypt(hex2byte(data.getBytes()),
						key.getBytes()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		return null;
	}

	//des加密
	public static String encrypt(String data) {
		if (data != null)
			try {
				return byte2hex(encrypt(data.getBytes(), key.getBytes()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		return null;
	}

	// 图片按比例大小压缩方法并获取压缩后图片的路径
	public static String getsaveimage(Context context,String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		float hh = 800f;// 这里设置高度为800f
		float ww = 800f;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		bitmap = compressImage(bitmap);

		String name = new DateFormat().format("yyyyMMddhhmmss",
				Calendar.getInstance(Locale.CHINA))
				+ ".png";
		String saveDir = Environment.getExternalStorageDirectory() + "/manager";
		File dir = new File(saveDir);
		if (!dir.exists()) {
			dir.mkdir();
		}
		// 保存入sdCard
		File file2 = new File(saveDir, name);
		try {
			FileOutputStream out = new FileOutputStream(file2);
			if (bitmap.compress(Bitmap.CompressFormat.JPEG,40, out)) {
				out.flush();
				out.close();
				bitmap.recycle();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return file2.getPath();// 压缩好比例大小后再进行质量压缩
	}

	// 质量压缩方法
	public static Bitmap compressImage(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}
	
	public static String string2md5(String passwd) {
		byte[] hash = null;
		try {
			hash = MessageDigest.getInstance("MD5").digest(passwd.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		StringBuilder builder = new StringBuilder(hash.length * 2);
		for (byte b : hash) {
			if ((b & 0xFF) < 0x10)
				builder.append("0");
			builder.append(Integer.toHexString(b & 0xFF));
		}
		return builder.toString();
	}
	
	public static boolean checkPhoneNum(String mobiles) {
		Pattern pattern = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(17[^4,\\D])|(14[^4,\\D])|(18[0-9]))\\d{8}$");
		Matcher matcher = pattern.matcher(mobiles);
		return matcher.matches();
	}



	/**
	 * 获取版本号
	 * @return 当前应用的版本号Code
	 */
	public static int getVersion(Context context) {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			int version = info.versionCode;
			Log.i("TEMPLOG","版本号:"+version);
			return version;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}



	/**
	   * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
	   *
	   * @param v
	   * @param event
	   * @return
	   */
	   public static boolean isShouldHideInput(View v, MotionEvent event) {
			if (v != null && (v instanceof EditText)) {
				 int[] l = { 0, 0 };
				 v.getLocationInWindow(l);
				 int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
						 + v.getWidth();
				 if (event.getX() > left && event.getX() < right
						 && event.getY() > top && event.getY() < bottom) {
						// 点击EditText的事件，忽略它。
					 return false;
				 } else {
					 return true;
				 }
			}
			// 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
			return false;
	   }

}
