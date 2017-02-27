package com.chomp.wifistorymachine.ui.update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.chomp.wifistorymachine.R;

import static android.R.attr.versionCode;

public class UpdateManager
{

	private static final int DOWNLOAD = 1;

	private static final int DOWNLOAD_FINISH = 2;

	private UpdataInfo mHashMap;

	private String mSavePath;

	private int progress;

	private boolean cancelUpdate = false;

	private Context mContext;

	private ProgressBar mProgress;
	private Dialog mDownloadDialog;

	private Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{

			case DOWNLOAD:
				mProgress.setProgress(progress);
				break;
			case DOWNLOAD_FINISH:
				installApk();
				break;
			default:
				break;
			}
		};
	};

	public UpdateManager(Context context)
	{
		this.mContext = context;
	}

	/**
	 * 
	 */
	public void checkUpdate()
	{

		if (isUpdate())
		{
			showNoticeDialog();
		} else
		{
			Toast.makeText(mContext, R.string.soft_update_no, Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 
	 * 
	 * @return
	 */
	private boolean isUpdate()
	{
		new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					mHashMap = NewsService.getListNews();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();

		try {
			Thread.sleep(2000);
			if (null != mHashMap)
			{
				int serviceCode = Integer.valueOf(mHashMap.getVersion());
				int versionCode = getVersionCode(mContext);
				Log.d("wzg","serviceCode="+serviceCode);
				Log.d("wzg","versionCode="+versionCode);
				if (serviceCode > versionCode)
				{
					return true;
				}

			}
		}catch (Exception e){
           e.printStackTrace();
		}
		return false;
	}

	private int getVersionCode(Context context)
	{
		int versionCode = 0;
		try
		{	
		versionCode = context.getPackageManager().getPackageInfo("com.chomp.wifistorymachine", 0).versionCode;
		} catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}
		return versionCode;
	}

	/**
	 * 
	 */
	private void showNoticeDialog()
	{

		Builder builder = new Builder(mContext);
		builder.setTitle(R.string.soft_update_title);
		builder.setMessage(R.string.soft_update_info);
	
		builder.setPositiveButton(R.string.soft_update_updatebtn, new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				showDownloadDialog();
			}
		});
		
		builder.setNegativeButton(R.string.soft_update_later, new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
			}
		});
		Dialog noticeDialog = builder.create();
		noticeDialog.show();
	}

	
	private void showDownloadDialog()
	{
		
		Builder builder = new Builder(mContext);
		builder.setTitle(R.string.soft_updating);
		
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.softupdate_progress, null);
		mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
		builder.setView(v);
		
		builder.setNegativeButton(R.string.soft_update_cancel, new OnClickListener()
		{
			
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				cancelUpdate = true;
			}
		});
		mDownloadDialog = builder.create();
		mDownloadDialog.show();

		downloadApk();
	}

	/**
	 *
	 */
	private void downloadApk()
	{
	
		new downloadApkThread().start();
	}

	private class downloadApkThread extends Thread
	{
		@Override
		public void run()
		{
			try
			{
			
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
				{
					
					String sdpath = Environment.getExternalStorageDirectory() + "/";
					mSavePath = sdpath;
					URL url = new URL(mHashMap.getUrl());
				
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.connect();
					
					int length = conn.getContentLength();
					
					InputStream is = conn.getInputStream();

					File file = new File(mSavePath);
				
					if (!file.exists())
					{
						file.mkdir();
					}
					File apkFile = new File(mSavePath, mHashMap.getName());
					FileOutputStream fos = new FileOutputStream(apkFile);
					int count = 0;
				
					byte buf[] = new byte[1024];
					
					do
					{
						int numread = is.read(buf);
						count += numread;
						
						progress = (int) (((float) count / length) * 100);
						
						mHandler.sendEmptyMessage(DOWNLOAD);
						if (numread <= 0)
						{
						
							mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
							break;
						}
					
						fos.write(buf, 0, numread);
					} while (!cancelUpdate);
					fos.close();
					is.close();
				}
			} catch (MalformedURLException e)
			{
				e.printStackTrace();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			
			mDownloadDialog.dismiss();
		}
	};
	/**
	 * 安装APK文件
	 */
	private void installApk() {
		Log.d("zcw", "====installApk=====");
		File apkfile = new File(mSavePath,mHashMap.getName());
		if (!apkfile.exists()) {
			return;
		}
		// 通过Intent安装APK文件
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		mContext.startActivity(i);
		android.os.Process.killProcess(android.os.Process.myPid());
	}
}
