package com.chomp.wifistorymachine.okhttp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.ArrayMap;
import android.util.Log;

import com.chomp.wifistorymachine.constants.Constant;
import com.chomp.wifistorymachine.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

import static com.chomp.wifistorymachine.R.string.account;


public class HttpUtils
{
	private static String appid="201716446113";

	  public static void login(Context context, String tag, String account, String password, OkHttpUtils.OkCallBack callback)
	  {
	    String passwordMD5 = Utils.string2md5(password);

//	    SignatureBean signatureBean = new SignatureBean();
//	    signatureBean.setAccount(account);
//	    signatureBean.setPassword(passwordMD5);
//	    signatureBean = SignatureUtil.getSignature(context, "case_login", signatureBean);

//	    ArrayMap requestStrs = new ArrayMap();
//	    requestStrs.put("subsystemId", signatureBean.getSubsystemId());
//	    requestStrs.put("clientId", signatureBean.getClientId());
//	    requestStrs.put("timestamp", signatureBean.getTimestamp());
//	    requestStrs.put("signature", signatureBean.getSignature());
//	    requestStrs.put("account", account);
//	    requestStrs.put("password", signatureBean.getPassword());

		  JSONObject json = new JSONObject();
		  try {
			  json.put("mobile",account);
			  json.put("password",passwordMD5);
			  json.put("appid",appid);
		  } catch (JSONException e) {
			  e.printStackTrace();
		  }
	    OkHttpUtils.getInstance().postAsyn(context, tag, "http://www.bbgushiji.com:833/users/login.aspx", json, callback);
	  }

	  public static void getAccessToken(String flushToken, Context context, String tag, OkHttpUtils.OkCallBack callback)
	  {
	   /* SignatureBean signatureBean = new SignatureBean();
	    signatureBean.setFlushToken(flushToken);
	    signatureBean = SignatureUtil.getSignature(context, "case_get_accesstoken", signatureBean);

	    ArrayMap requestStrs = new ArrayMap();
	    requestStrs.put("subsystemId", signatureBean.getSubsystemId());
	    requestStrs.put("clientId", signatureBean.getClientId());
	    requestStrs.put("timestamp", signatureBean.getTimestamp());
	    requestStrs.put("signature", signatureBean.getSignature());
	    requestStrs.put("flushToken", signatureBean.getFlushToken());
	    OkHttpUtils.getInstance().postAsyn(context, tag, "http://uc.hivoice.cn:80/rest/v1/token/get_access_token", requestStrs, callback);*/
	  }

	  public static void getKar(String accessToken, Context context, String tag, OkHttpUtils.OkCallBack callback)
	  {
	   /* String getKar = "";
	    try {
	      getKar = String.format("/user/login/getka?accessTk=%s&udid=%s", new Object[] { URLEncoder.encode(accessToken, "UTF-8"), Utils.getPhoneID(context) });
	    } catch (UnsupportedEncodingException e) {
	      e.printStackTrace();
	    }
	    OkHttpUtils.getInstance().getAsyn(context, tag, "http://csc.sh.hivoice.cn/csc" + getKar, callback);*/
	  }

	  public static void registAccount(String username,String phoneNum, String veriCode, String password, Context context, String tag, OkHttpUtils.OkCallBack callback)
	  {
		   
		    
//		   ArrayMap requestStrs = new ArrayMap();
		    
		   // requestStrs.put("devInfo", JsonParseUtil.object2Json(username));
		   
		  /*  “username”:”马东东”,
		    ” mobile”:”13590296144”,
		    ” smscode”:”8888”,
		    ”password”:”F59BD65F7EDAFB087A81D4DCA06C4910”,
		    ”appid”:”001113”*/

		  String passwordMD5 = Utils.string2md5(password);

		  Log.d("zcw","===passwordMD5="+passwordMD5);
		    
//		    requestStrs.put("username", username);
//		    requestStrs.put("mobile",phoneNum);
//		    requestStrs.put("smscode",veriCode);
//		    requestStrs.put("password", password);
//		    requestStrs.put("appid", "201716446113");

		  JSONObject json = new JSONObject();
		  try {
			  json.put("username",username);
			  json.put("mobile",phoneNum);
			  json.put("smscode",veriCode);
			  json.put("password",passwordMD5);
			  json.put("appid",appid);
		  } catch (JSONException e) {
			  e.printStackTrace();
		  }
		  OkHttpUtils.getInstance().postAsyn(context, tag, "http://www.bbgushiji.com:833/users/register.aspx", json, callback);
	  }



	  public static void getVeriCode(String phoneNum, Context context, String tag, OkHttpUtils.OkCallBack callback)
	  {
	   /* SignatureBean signatureBean = new SignatureBean();
	    signatureBean.setUserCell(phoneNum);
	    signatureBean = SignatureUtil.getSignature(context, "case_get_vericode", signatureBean);
	    ArrayMap requestStrs = new ArrayMap();
	    requestStrs.put("subsystemId", signatureBean.getSubsystemId());
	    requestStrs.put("clientId", signatureBean.getClientId());
	    requestStrs.put("timestamp", signatureBean.getTimestamp());
	    requestStrs.put("signature", signatureBean.getSignature());
	    requestStrs.put("userCell", signatureBean.getUserCell());
	    OkHttpUtils.getInstance().postAsyn(context, tag, "http://uc.hivoice.cn:80/rest/v1/phone/send_phone_code", requestStrs, callback);*/
	  }

	  public static void checkAccount(String account, Context context, String tag, OkHttpUtils.OkCallBack callback)
	  {
//	   SignatureBean signatureBean = new SignatureBean();
//	    signatureBean.setAccount(account);
//	    signatureBean = SignatureUtil.getSignature(context, "case_check_account", signatureBean);
//
//	    ArrayMap requestStrs = new ArrayMap();
//	    requestStrs.put("subsystemId", signatureBean.getSubsystemId());
//	    requestStrs.put("clientId", signatureBean.getClientId());
//	    requestStrs.put("timestamp", signatureBean.getTimestamp());
//	    requestStrs.put("signature", signatureBean.getSignature());
//	    requestStrs.put("account", account);

		  JSONObject json = new JSONObject();
		  try {
			  json.put("mobile",account);
			  json.put("appid",appid);
		  } catch (JSONException e) {
			  e.printStackTrace();
		  }

		  OkHttpUtils.getInstance().postAsyn(context, tag, "http://www.bbgushiji.com:833/users/getsmscode.aspx", json, callback);
	  }

	  public static void resetPassword(String phoneNum, String veriCode, String password, Context context, String tag, OkHttpUtils.OkCallBack callback)
	  {
//	    String passwordMD5 = Utils.string2md5(password);
//	    SignatureBean signatureBean = new SignatureBean();
//	    signatureBean.setUserCell(phoneNum);
//	    signatureBean.setPhoneCode(veriCode);
//	    signatureBean.setPassword(passwordMD5);
//	    signatureBean = SignatureUtil.getSignature(context, "case_reset_passwd", signatureBean);
//	    ArrayMap requestStrs = new ArrayMap();
//	    requestStrs.put("subsystemId", signatureBean.getSubsystemId());
//	    requestStrs.put("clientId", signatureBean.getClientId());
//	    requestStrs.put("timestamp", signatureBean.getTimestamp());
//	    requestStrs.put("signature", signatureBean.getSignature());
//	    requestStrs.put("userCell", signatureBean.getUserCell());
//	    requestStrs.put("phoneCode", signatureBean.getPhoneCode());
//	    requestStrs.put("password", signatureBean.getPassword());
//	    OkHttpUtils.getInstance().postAsyn(context, tag, "http://uc.hivoice.cn:80/rest/v1/phone/reset_pwd", requestStrs, callback);
	  }

	  public static void bindDevice(String pcode, Context context, String tag, OkHttpUtils.OkCallBack callback)
	  {
//	    String bindDevice = "";

//
//	    bindDevice = String.format("/user/bind3?karAccount=%s&idenCode=%s", new Object[] { preferences.getString("passport", ""), code });
//	    OkHttpUtils.getInstance().getAsyn(context, tag, "http://csc.sh.hivoice.cn/csc" + bindDevice, callback);

		//  SharedPreferences preferences = context.getSharedPreferences("karrobot_shared", 0);
		//  String userid=preferences.getString("passport", "");

		  SharedPreferences  preferences = context.getSharedPreferences(Constant.SHARED_KARROBOT, Context.MODE_PRIVATE);
		  String  userid = preferences.getString(Constant.EXTRA_USER_ID, null);

		  JSONObject json = new JSONObject();
		  try {
			  json.put("appid",appid);
			  json.put("userid",userid);
			  json.put("pcode",pcode);

		  } catch (JSONException e) {
			  e.printStackTrace();
		  }
		  OkHttpUtils.getInstance().postAsyn(context, tag, "http://www.bbgushiji.com:833/users/binding.aspx", json, callback);
	  }


	public static void unbindDevice(String pcode, Context context, String tag, OkHttpUtils.OkCallBack callback)
	{

		SharedPreferences  preferences = context.getSharedPreferences(Constant.SHARED_KARROBOT, Context.MODE_PRIVATE);
		String  userid = preferences.getString(Constant.EXTRA_USER_ID, null);

		JSONObject json = new JSONObject();
		try {
			json.put("appid",appid);
			json.put("userid",userid);
			json.put("pcode",pcode);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		OkHttpUtils.getInstance().postAsyn(context, tag, "http://www.bbgushiji.com:833/users/unbinding.aspx", json, callback);
	}


	public static void geUbindDevice(String udid, Context context, String tag, OkHttpUtils.OkCallBack callback)
	  {
//	    String getUbindDevice = "";
//	    SharedPreferences preferences = context.getSharedPreferences("karrobot_shared", 0);
//
//	    getUbindDevice = String.format("/user/unbind?karAccount=%s&udid=%s", new Object[] { preferences.getString("passport", ""), udid });
//	    OkHttpUtils.getInstance().getAsyn(context, tag, "http://csc.sh.hivoice.cn/csc" + getUbindDevice, callback);
	  }

	  public static void getMyBindDevice(Context context, String tag, OkHttpUtils.OkCallBack callback)
	  {
//	    String getDeviceInfo = "";
//	    SharedPreferences preferences = context.getSharedPreferences("karrobot_shared", 0);
//
//	    getDeviceInfo = String.format("/user/qbind?karAccount=%s", new Object[] { preferences.getString("passport", "") });
//	    OkHttpUtils.getInstance().getAsyn(context, tag, "http://csc.sh.hivoice.cn/csc" + getDeviceInfo, callback);
	  }

	  public static void getDeviceInfo(Context context, String tag, String udid, OkHttpUtils.OkCallBack callback)
	  {
//	    String getDeviceInfo = "";
//	    SharedPreferences preferences = context.getSharedPreferences("karrobot_shared", 0);
//
//	    String accessToken = preferences.getString("tk", "");
//	    getDeviceInfo = String.format("/user/login/getdi?udid=%s&tk=%s", new Object[] { udid, accessToken });
//	    OkHttpUtils.getInstance().getAsyn(context, tag, "http://csc.sh.hivoice.cn/csc" + getDeviceInfo, callback);
	  }

	  public static void loadData(String album, String subfolder, Context context, String tag, OkHttpUtils.OkCallBack callback)
	  {
//	    String params = String.format("/tb/queryFL?album=%s&subfolder=%s", new Object[] { album, subfolder });
//	    OkHttpUtils.getInstance().getAsyn(context, tag, "http://csc.sh.hivoice.cn/csc" + params, callback);
	  }

	  public static void play(String fID,String url, String pcode,Context context, String tag, OkHttpUtils.OkCallBack callback)
	  {
//	    SharedPreferences preferences = context.getSharedPreferences("karrobot_shared", 0);
//
//	    String params = String.format("/tb/cp?karAccount=%s&udid=%s&fid=%s", new Object[] { preferences.getString("passport", ""), preferences.getString("udId", ""), Long.valueOf(fID) });
//	    OkHttpUtils.getInstance().getAsyn(context, tag, "http://csc.sh.hivoice.cn/csc" + params, callback);

		  SharedPreferences  preferences = context.getSharedPreferences(Constant.SHARED_KARROBOT, Context.MODE_PRIVATE);
		  String  userid = preferences.getString(Constant.EXTRA_USER_ID, null);




			  JSONObject json = new JSONObject();
			  try {
				  json.put("fileid", fID);
				  json.put("url", url);
				  json.put("pcode", pcode);
				  json.put("userid", userid);
				  json.put("appid", appid);
			  } catch (JSONException e) {
				  e.printStackTrace();
			  }
			  OkHttpUtils.getInstance().postAsyn(context, tag, "http://www.bbgushiji.com:833/ondemand/play.aspx", json, callback);
	  }

	  public static void loadData(Context context, String tag, OkHttpUtils.OkCallBack callback)
	  {

		  JSONObject json = new JSONObject();
		  try {
			  json.put("appid",appid);
		  } catch (JSONException e) {
			  e.printStackTrace();
		  }
		  OkHttpUtils.getInstance().postAsyn(context, tag, "http://www.bbgushiji.com:833/res/sortlist.aspx", json, callback);
	  }

	public static void loadData_Electricity_Temperature_Signal(Context context, String tag, OkHttpUtils.OkCallBack callback)
	{

		SharedPreferences  preferences = context.getSharedPreferences(Constant.SHARED_KARROBOT, Context.MODE_PRIVATE);
		String  userid = preferences.getString(Constant.EXTRA_USER_ID, null);
		String  pcode = preferences.getString(Constant.EXTRA_PINCODE_ID, null);

		JSONObject json = new JSONObject();
		try {
			json.put("appid",appid);
			json.put("userid",userid);
			json.put("pcode",pcode);

			Log.d("wzg","appid="+appid +",userid="+userid + ",pcode="+pcode);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		OkHttpUtils.getInstance().postAsyn(context, tag, "http://www.bbgushiji.com:833/status/prostatus.aspx", json, callback);
	}

	  public static void getDataByAlbum(String sortid, Context context, String tag, OkHttpUtils.OkCallBack callback)
	  {
	   // String params = String.format("/tb/querySF?album=%s", new Object[] { album });

		  JSONObject json = new JSONObject();
		  try {
			  json.put("sortid",sortid);
		  } catch (JSONException e) {
			  e.printStackTrace();
		  }

	    OkHttpUtils.getInstance().postAsyn(context, tag, "http://www.bbgushiji.com:833/res/filelist.aspx", json, callback);
	  }

	public static void getSearchData(String searchName, Context context, String tag, OkHttpUtils.OkCallBack callback)
	{
		// String params = String.format("/tb/querySF?album=%s", new Object[] { album });
		JSONObject json = new JSONObject();
		try {
			json.put("appid",appid);
			json.put("keyword",searchName);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		OkHttpUtils.getInstance().postAsyn(context, tag, "http://www.bbgushiji.com:833/res/search.aspx", json, callback);
	}


	public static void getDataByOnDemand(Context context, String tag, OkHttpUtils.OkCallBack callback)
	{
		JSONObject json = new JSONObject();
		try {
			json.put("appid",appid);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		OkHttpUtils.getInstance().postAsyn(context, tag, "http://www.bbgushiji.com:833/ondemand/ranking.aspx", json, callback);
	}




	public static void saveVoiceMessage(File GS_mp3File,String savemp3Path,Context context, String tag, OkHttpUtils.OkCallBack callback)
	{

		OkHttpUtils.getInstance().getAsyn(context, tag,GS_mp3File,savemp3Path, callback);
	}




	public static void sendVoiceMessage(String mp3Path,Context context, String tag, OkHttpUtils.OkCallBack callback)
	{

		SharedPreferences  preferences = context.getSharedPreferences(Constant.SHARED_KARROBOT, Context.MODE_PRIVATE);
		String  userid = preferences.getString(Constant.EXTRA_USER_ID, null);
		String  pcode = preferences.getString(Constant.EXTRA_PINCODE_ID, null);

		JSONObject json = new JSONObject();
		try {
			json.put("userid",userid);
			json.put("pcode",pcode);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		File file = new File(mp3Path);


		OkHttpUtils.getInstance().postAsynMsg(context, tag, "http://www.bbgushiji.com:833/chat/uploadforapp.aspx",file, json, callback);
	}

	  public static void getUserInfo(Context context, String tag, OkHttpUtils.OkCallBack callback)
	  {
//	    String getUserInfo = "";
//	    try {
//	      SharedPreferences preferences = context.getSharedPreferences("karrobot_shared", 0);
//
//	      getUserInfo = String.format("/user/login/getui?karAccount=%s&tk=%s", new Object[] { preferences.getString("passport", ""), URLEncoder.encode(preferences.getString("tk", ""), "UTF-8") });
//	    } catch (UnsupportedEncodingException e) {
//	      e.printStackTrace();
//	    }
//	    OkHttpUtils.getInstance().getAsyn(context, tag, "http://csc.sh.hivoice.cn/csc" + getUserInfo, callback);
	  }

	  public static void uploadFile(String path, Context context, String tag, OkHttpUtils.OkCallBack callback)
	  {
//	    SharedPreferences preferences = context.getSharedPreferences("karrobot_shared", 0);
//
//	    ArrayMap requestStrs = new ArrayMap();
//	    requestStrs.put("karAccount", preferences.getString("passport", ""));
//	    requestStrs.put("tk", preferences.getString("tk", ""));
//
//	    ArrayMap files = new ArrayMap();
//	    files.put("file", path);
//	    OkHttpUtils.getInstance().postAsynWithPic(context, tag, "http://csc.sh.hivoice.cn/csc/user/login/avatar", requestStrs, files, callback);
	  }

	  public static void updateUsreInfo(ArrayMap<String, String> requestStrs, Context context, String tag, OkHttpUtils.OkCallBack callback)
	  {
//	    SharedPreferences preferences = context.getSharedPreferences("karrobot_shared", 0);
//
//	    requestStrs.put("karAccount", preferences.getString("passport", ""));
//	    requestStrs.put("token", preferences.getString("tk", ""));
//	    requestStrs.put("udid", Utils.getPhoneID(context));
//	    ArrayMap request = new ArrayMap();
//	    request.put("userInfo", JsonParseUtil.object2Json(requestStrs));
//	    OkHttpUtils.getInstance().postAsynWithPic(context, tag, "http://csc.sh.hivoice.cn/csc/user/login/updateui", request, null, callback);
	  }

	  public static void sendMsg(Context context, String tag, String groupId, String type, String msg, String url, int duration, OkHttpUtils.OkCallBack callback)
	  {
//	    SharedPreferences preferences = context.getSharedPreferences("karrobot_shared", 0);
//
//	    String accessToken = preferences.getString("tk", "");
//	    UserInfo userInfo = UserInfoUtils.getUserInfo(context);
//	    String fromUser = "KAR User";
//	    if ((userInfo != null) && (userInfo.getNickName() != null)) {
//	      fromUser = userInfo.getNickName();
//	    }
//	    List params = new ArrayList();
//	    params.add(accessToken);
//	    params.add(fromUser);
//	    params.add(groupId);
//	    params.add(type);
//	    if (type.equals("txt")) {
//	      params.add(msg);
//	      params.add(String.valueOf(duration));
//	    } else {
//	      params.add(url);
//	      params.add(String.valueOf(duration));
//	    }
//	    ArrayMap requestStrs = SignatureUtil.getSignature(context, params);
//	    requestStrs.put("accessToken", accessToken);
//	    requestStrs.put("fromUser", fromUser);
//	    requestStrs.put("groupId", groupId);
//	    requestStrs.put("type", type);
//	    if (type.equals("txt")) {
//	      requestStrs.put("msg", msg);
//	      requestStrs.put("duration", String.valueOf(duration));
//	    } else {
//	      requestStrs.put("url", url);
//	      requestStrs.put("duration", String.valueOf(duration));
//	    }
//	    OkHttpUtils.getInstance().postAsyn(context, tag, "http://chat.chip.hivoice.cn:80/rest/v1/group/send_msg", requestStrs, callback);
	  }

	  public static void createGroup(Context context, String tag, String groupId, String groupName, OkHttpUtils.OkCallBack callback)
	  {
//	    SharedPreferences preferences = context.getSharedPreferences("karrobot_shared", 0);
//
//	    String accessToken = preferences.getString("tk", "");
//	    List params = new ArrayList();
//	    params.add(accessToken);
//	    params.add(groupId);
//	    params.add(groupName);
//	    ArrayMap requestStrs = SignatureUtil.getSignature(context, params);
//	    requestStrs.put("accessToken", accessToken);
//	    requestStrs.put("groupId", groupId);
//	    requestStrs.put("groupName", groupName);
//	    OkHttpUtils.getInstance().postAsyn(context, tag, "http://chat.chip.hivoice.cn:80/rest/v1/group/create_group", requestStrs, callback);
	  }

	  public static void deleteGroup(Context context, String tag, String groupId, OkHttpUtils.OkCallBack callback)
	  {
//	    SharedPreferences preferences = context.getSharedPreferences("karrobot_shared", 0);
//
//	    String accessToken = preferences.getString("tk", "");
//	    List params = new ArrayList();
//	    params.add(accessToken);
//	    params.add(groupId);
//	    ArrayMap requestStrs = SignatureUtil.getSignature(context, params);
//	    requestStrs.put("accessToken", accessToken);
//	    requestStrs.put("groupId", groupId);
//	    OkHttpUtils.getInstance().postAsyn(context, tag, "http://chat.chip.hivoice.cn:80/rest/v1/group/del_group", requestStrs, callback);
	  }

	  public static void addDevice(Context context, String tag, String groupId, String deviceUdids, OkHttpUtils.OkCallBack callback)
	  {
//	    SharedPreferences preferences = context.getSharedPreferences("karrobot_shared", 0);
//
//	    String accessToken = preferences.getString("tk", "");
//	    List params = new ArrayList();
//	    params.add(accessToken);
//	    params.add(groupId);
//	    params.add(deviceUdids);
//	    ArrayMap requestStrs = SignatureUtil.getSignature(context, params);
//	    requestStrs.put("accessToken", accessToken);
//	    requestStrs.put("groupId", groupId);
//	    requestStrs.put("deviceUdids", deviceUdids);
//	    OkHttpUtils.getInstance().postAsyn(context, tag, "http://chat.chip.hivoice.cn:80/rest/v1/group/add_device", requestStrs, callback);
	  }

	  public static void delDevice(Context context, String tag, String groupId, String deviceUdids, OkHttpUtils.OkCallBack callback)
	  {
//	    SharedPreferences preferences = context.getSharedPreferences("karrobot_shared", 0);
//
//	    String accessToken = preferences.getString("tk", "");
//	    List params = new ArrayList();
//	    params.add(accessToken);
//	    params.add(groupId);
//	    params.add(deviceUdids);
//	    ArrayMap requestStrs = SignatureUtil.getSignature(context, params);
//	    requestStrs.put("accessToken", accessToken);
//	    requestStrs.put("groupId", groupId);
//	    requestStrs.put("deviceUdids", deviceUdids);
//	    OkHttpUtils.getInstance().postAsyn(context, tag, "http://chat.chip.hivoice.cn:80/rest/v1/group/del_device", requestStrs, callback);
	  }

	  public static void queryDevice(Context context, String tag, String groupIds, OkHttpUtils.OkCallBack callback)
	  {
//	    SharedPreferences preferences = context.getSharedPreferences("karrobot_shared", 0);
//
//	    String accessToken = preferences.getString("tk", "");
//	    List params = new ArrayList();
//	    params.add(accessToken);
//	    params.add(groupIds);
//	    ArrayMap requestStrs = SignatureUtil.getSignature(context, params);
//	    requestStrs.put("accessToken", accessToken);
//	    requestStrs.put("groupIds", groupIds);
//	    OkHttpUtils.getInstance().postAsyn(context, tag, "http://chat.chip.hivoice.cn:80/rest/v1/group/query_device", requestStrs, callback);
	  }

	  public static void searchUser(Context context, String tag, String key, OkHttpUtils.OkCallBack callback)
	  {
//	    String searchUser = String.format("/user/search?key=%s&udid=%s", new Object[] { key, Utils.getPhoneID(context) });
//	    OkHttpUtils.getInstance().getAsyn(context, tag, "http://csc.sh.hivoice.cn/csc" + searchUser, callback);
	  }

	  public static void searchUserInfo(Context context, String tag, String karAccount, OkHttpUtils.OkCallBack callback)
	  {
//	    String getUserInfo = "";
//	    try {
//	      SharedPreferences preferences = context.getSharedPreferences("karrobot_shared", 0);
//
//	      getUserInfo = String.format("/user/login/getui?karAccount=%s&tk=%s", new Object[] { karAccount, URLEncoder.encode(preferences.getString("tk", ""), "UTF-8") });
//	    } catch (UnsupportedEncodingException e) {
//	      e.printStackTrace();
//	    }
//	    OkHttpUtils.getInstance().getAsyn(context, tag, "http://csc.sh.hivoice.cn/csc" + getUserInfo, callback);
	  }

	  public static void updateDeviceInfo(ArrayMap<String, String> requestStrs, Context context, String tag, OkHttpUtils.OkCallBack callback)
	  {
//	    ArrayMap request = new ArrayMap();
//	    request.put("devInfo", JsonParseUtil.object2Json(requestStrs));
//	    OkHttpUtils.getInstance().postAsynWithPic(context, tag, "http://csc.sh.hivoice.cn/csc/user/login/updatedi", request, null, callback);
	  }
	}