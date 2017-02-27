package com.chomp.wifistorymachine.okhttp;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.ArrayMap;
import android.util.Log;
import android.widget.Toast;

import com.chomp.wifistorymachine.R;
import com.chomp.wifistorymachine.model.BaseResponseBean;
import com.chomp.wifistorymachine.util.IOStreamUtils;
import com.chomp.wifistorymachine.util.JsonParseUtil;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.chomp.wifistorymachine.dao.AudiobooksSortDao.Properties.url;


public class OkHttpUtils
{
	  public static final String REQUEST_STRING_KEY_NAME = "mobile_request_attribute";
	  public static final int CONNECTION_TIME_OUT_DEFAULT = 20000;
	  public static final int READ_TIME_OUT_DEFAULT = 60000;
	  private static OkHttpUtils mInstance;
	  private OkHttpClient mOkHttpClient;
	  private Handler mDelivery;
	  private Context mContext;

	  public OkHttpUtils(OkHttpClient okHttpClient)
	  {
	    if (okHttpClient == null) {
	      OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
	      okHttpClientBuilder.hostnameVerifier(new HostnameVerifier()
	      {
	        public boolean verify(String hostname, SSLSession session) {
	          return true;
	        }
	      });
	      this.mOkHttpClient = okHttpClientBuilder.build();
	    } else {
	      this.mOkHttpClient = okHttpClient;
	    }
	    init();
	  }

	  private void init()
	  {
	    this.mDelivery = new Handler(Looper.getMainLooper());
	  }

	  public static OkHttpUtils getInstance(OkHttpClient okHttpClient)
	  {
	    if (mInstance == null) {
	      synchronized (OkHttpUtils.class) {
	        if (mInstance == null) {
	          mInstance = new OkHttpUtils(okHttpClient);
	        }
	      }
	    }
	    return mInstance;
	  }

	  public static OkHttpUtils getInstance()
	  {
	    if (mInstance == null) {
	      synchronized (OkHttpUtils.class) {
	        if (mInstance == null) {
	          mInstance = new OkHttpUtils(null);
	        }
	      }
	    }
	    return mInstance;
	  }

	  private Handler getDelivery()
	  {
	    return this.mDelivery;
	  }

	  public OkHttpClient getOkHttpClient()
	  {
	    this.mOkHttpClient.newBuilder().connectTimeout(20000L, TimeUnit.MILLISECONDS).readTimeout(60000L, TimeUnit.MILLISECONDS).writeTimeout(60000L, TimeUnit.MICROSECONDS).build();

	    return this.mOkHttpClient;
	  }

	  public void cancelTag(Object tag)
	  {
	    for (Call call : this.mOkHttpClient.dispatcher().queuedCalls()) {
	      if (tag.equals(call.request().tag())) {
	        call.cancel();
	      }
	    }
	    for (Call call : this.mOkHttpClient.dispatcher().runningCalls())
	      if (tag.equals(call.request().tag()))
	        call.cancel();
	  }

	  private void sendFailResultCallback(final Call call, final Response response, final Exception e, final OkCallBack callback, final Object tag)
	  {
	    if (callback == null) return;
	    this.mDelivery.post(new Runnable()
	    {
	      public void run()
	      {
			  if(response !=null){
				  Toast.makeText(OkHttpUtils.this.mContext, "当前网络繁忙:"+response.code(), Toast.LENGTH_SHORT).show();
			  }

	        callback.onError(call, response, e, tag);
	        callback.onAfter(tag);
	      }
	    });
	  }

	  private void sendSuccessResultCallback(final String result, final OkCallBack callback, final Object tag)
	  {
	    if (callback == null) return;
	    this.mDelivery.post(new Runnable()
	    {
	      public void run()
	      {
	        callback.onResponse(result, tag);
	        callback.onAfter(tag);

	      }
	    });
	  }

	private void sendSuccessResultCallback(final Call call, final Response response, final OkCallBack callback, final Object tag)
	{
		if (callback == null) return;
		this.mDelivery.post(new Runnable()
		{
			public void run()
			{
				callback.onResponse(call,response, tag);
				callback.onAfter(tag);

			}
		});
	}

	  private static Request postRequest(Object tag, String url, ArrayMap<String, String> requestStrs)
	  {
	    FormBody.Builder mBuilder = new FormBody.Builder();
	    if (requestStrs != null) {
	      for (int i = 0; i < requestStrs.size(); i++) {
	        mBuilder.add((String)requestStrs.keyAt(i), (String)requestStrs.valueAt(i));
	      }
	    }
	    return new Request.Builder().tag(tag).url(url).post(mBuilder.build()).build();
	  }

//	  private static Request postRequestWithPic(Object tag, String url, ArrayMap<String, String> requestStrs, ArrayMap<String, String> files)
//	  {
//	    MultipartBody.Builder builder = new MultipartBody.Builder();
//	    builder.setType(MultipartBody.FORM);
//
//	    if (requestStrs != null) {
//	      for (int i = 0; i < requestStrs.size(); i++) {
//	        builder.addFormDataPart((String)requestStrs.keyAt(i), (String)requestStrs.valueAt(i));
//	      }
//	    }
//
//	    if (files != null) {
//	      for (int i = 0; i < files.size(); i++) {
//	        builder.addFormDataPart((String)files.keyAt(i), (String)files.keyAt(i), RequestBody.create(MultipartBody.FORM, new File((String)files.valueAt(i))));
//	      }
//	    }
//	    return new Request.Builder().tag(tag).url(url).post(builder.build()).build();
//	  }

	  private static Request getRequest(Object tag, String url)
	  {
	    return new Request.Builder().tag(tag).url(url).get().build();
	  }

	  private void foaCall(Call call, OkCallBack okCallBack, final Object tag)
	  {
	    if (okCallBack == null) {
	      return;
	    }
	    final OkCallBack finalCallBack = okCallBack;
	    okCallBack.onBefore(call.request(), tag);
	    call.enqueue(new Callback()
	    {
	      public void onFailure(Call call, IOException e) {
			  Log.d("wzg","IOException="+e.toString());
	        OkHttpUtils.this.sendFailResultCallback(call, null, e, finalCallBack, tag);
	      }

	      public void onResponse(Call call, Response response) throws IOException {
	        if ((response.code() >= 400) && (response.code() <= 599)) {
	          try {
				  Log.d("wzg","onResponse="+response.code());
				//  Toast.makeText(OkHttpUtils.this.mContext, "onResponse="+response.code(), Toast.LENGTH_SHORT).show();
	            OkHttpUtils.this.sendFailResultCallback(call, response, new RuntimeException(response.body().string()), finalCallBack, tag);
	          } catch (IOException e) {
	            e.printStackTrace();
	          }
	          return;
	        }
	        try {
				String result = OkHttpUtils.this.parseNetworkResponse(response);
				OkHttpUtils.this.sendSuccessResultCallback(result,finalCallBack, tag);
	        } catch (Exception e) {
				Log.d("wzg","=Exception=="+e.toString());

	          OkHttpUtils.this.sendFailResultCallback(call, response, e, finalCallBack, tag);


	        }
	      }


	    });
	  }

	  private String parseNetworkResponse(Response response)
	  {
	    String result = null;
	    if (response != null) {
	      result = IOStreamUtils.formatResponse(response);
	    }
	    BaseResponseBean responseBean = (BaseResponseBean) JsonParseUtil.json2Object(result, BaseResponseBean.class);
	    if ((responseBean != null) && (responseBean.getCode() != null) && ((responseBean.getCode().equals("0103")) || (responseBean.getCode().equals("0105")))) {
	      try {
	        Class login = Class.forName("LoginActivity");
	        Intent intent = new Intent(this.mContext, login);
	        this.mContext.startActivity(intent);
	      } catch (ClassNotFoundException e) {
	        e.printStackTrace();
	        Toast.makeText(this.mContext, "请重新登录", Toast.LENGTH_SHORT).show();
	      }
	    }
	    return result;
	  }

	  public void postAsyn(Context context, Object tag, String url, JSONObject mJSONObject, OkCallBack callback)
	  {
	      this.mContext = context;
		  String jsonString = mJSONObject.toString();
		  Request request = new Request.Builder().tag(tag).url(url).header("key", "EED3642E-CB0B-4508-AF7C-F863687A7E3E")
				  .addHeader("content-length", ""+mJSONObject.length())
				  .addHeader("content-type", "application/json").post(RequestBody.create(MediaType.parse("json"), jsonString)).build();

		  Call call = getInstance().getOkHttpClient().newCall(request);//postRequest(tag, url, requestStrs)
	      getInstance().foaCall(call, callback, tag);
	  }

	public void postAsynMsg(Context context, Object tag, String url,File file, JSONObject mJSONObject, OkCallBack callback)
	{
		this.mContext = context;
		String jsonString = mJSONObject.toString();
		RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
		RequestBody requestBody = new MultipartBody.Builder()
				.setType(MultipartBody.FORM)
				.addFormDataPart("files", "file.amr", fileBody)
				.build();
		Request request = new Request.Builder()
				.url(url)
				.addHeader("Authorization", ""+jsonString)
				.post(requestBody)
				.build();
		Call call = getInstance().getOkHttpClient().newBuilder().writeTimeout(50, TimeUnit.SECONDS).build().newCall(request);
		getInstance().foaCall(call, callback, tag);
	}




	  public String postSyn(Context context, Object tag, String url, ArrayMap<String, String> requestStrs)
	    throws Exception
	  {
	    this.mContext = context;
	    return getInstance().getOkHttpClient().newCall(postRequest(tag, url, requestStrs)).execute().body().string();
	  }

	  public void getAsyn(Context context, Object tag,final File GS_mp3File, String url,final OkCallBack callback){
	    this.mContext = context;
	    Log.i("OkHttpUtils", "tag:" + tag + "-----------url:" + url);
	   // Call call = getInstance().getOkHttpClient().newCall(getRequest(tag, url));
	   // getInstance().foaCall(call, callback, tag);

		  Request request = new Request.Builder().url(url).build();
		  getInstance().getOkHttpClient().newCall(request).enqueue(new Callback() {
			  @Override
			  public void onFailure(Call call, IOException e) {

			  }

			  @Override
			  public void onResponse(Call call, Response response) {
				  InputStream inputStream = response.body().byteStream();
				  FileOutputStream fileOutputStream = null;


				  try {
					  fileOutputStream = new FileOutputStream(GS_mp3File);
					  byte[] buffer = new byte[2048];
					  int len = 0;
					  while ((len = inputStream.read(buffer)) != -1) {
						  fileOutputStream.write(buffer, 0, len);
					  }
					  fileOutputStream.flush();
				  } catch (IOException e) {
					  Log.i("wangshu", "IOException");
					  e.printStackTrace();
				  }
				  Log.d("wangshu", "1文件下载成功");
				  OkHttpUtils.this.sendSuccessResultCallback(call, response,callback, "chatwith");

			  }
		  });


	  }

	private void downAsynFile() {

	}

	  /*public void postAsynWithPic(Context context, Object tag, String url, ArrayMap<String, String> requestStrs, ArrayMap<String, String> files, OkCallBack callback)
	  {
	    this.mContext = context;
	    Call call = getInstance().getOkHttpClient().newCall(postRequestWithPic(tag, url, requestStrs, files));
	    getInstance().foaCall(call, callback, tag);
	  }*/

	  public static abstract interface OkCallBack
	  {
	    public abstract void onBefore(Request paramRequest, Object paramObject);

	    public abstract void onAfter(Object paramObject);

	    public abstract void onError(Call paramCall, Response paramResponse, Exception paramException, Object paramObject);

	    public abstract void onResponse(Object paramObject1, Object paramObject2);

		  public abstract void onResponse(Call call, Response response,Object tag);
	  }
	}