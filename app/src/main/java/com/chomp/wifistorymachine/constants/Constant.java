package com.chomp.wifistorymachine.constants;

public class Constant {
	// 退出app广播
	public static final String FINISH_APP = "finish_karrobot_app";
	public static final String STORY_KEY = "story";

	// shared
	public static final String SHARED_KARROBOT = "karrobot_shared";
	public static final String SHARED_USER_GENDER = "gender";
	public static final String SHARED_USER_BIRTHDAY = "birthday";
	public static final String SHARED_USER_NICKNAME = "nickName";
	public static final String CLIENTID = "client_id";
	public static final String NETWORK_CONNECTED = "network_connected";
	public static final String SHARED_UDID="udId";
	public static final String SHARED_USER_TOKEN = "tk";//accesstoken
	public static final String SHARED_USER_PASSPORT = "passport";//account id
	public static final String KEY_FLUSH_TOKEN_TIME_STAMP = "flushToken_timestamp";
	public static final String KEY_FLUSH_TOKEN = "flushToken";
	public static final String SHARED_PHONE_NUM = "phone_num";
	public static final String SHARED_USERINFO = "userinfo";

	// intent
	public static final String SSID_AND_PASSWORD = "ssid_and_password";
	public static final String SSID_LIST = "ssid_list";
	public static final String CURRENT_SSID = "current_ssid";

	//code
	public static final String CODE_REQUEST_SUCC = "success";//成功
	public static final String CODE_ACCOUNT_UNEXIT = "success";//用户不存在
	public static final String CODE_ACCOUNT_EXIT = "10002";//用户存在

	public static final String CODE_RESET_PASSWORD_CODE_ERROR="0305";
	public static final int HAS_BEEN_BOUND=3002;
	
	
	//chat
	public static final int CHATTYPE_SINGLE = 1;
	public static final int CHATTYPE_GROUP = 2;
	public static final int CHATTYPE_CHATROOM = 3;

	public static final String EXTRA_CHAT_TYPE = "chatType";
	public static final String EXTRA_USER_ID = "userId";

	public static final String EXTRA_PINCODE_ID = "pincode";

	public static final String MESSAGE_ATTR_IS_BIG_EXPRESSION = "em_is_big_expression";

}
