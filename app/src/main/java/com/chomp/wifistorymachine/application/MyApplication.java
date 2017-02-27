package com.chomp.wifistorymachine.application;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;

public class MyApplication extends Application {

	public List<Activity> activityList = new LinkedList<Activity>(); 
	private static MyApplication instance;

    private MyApplication()
    {
    }

     public static MyApplication getInstance()
     {
         if(null == instance)
         {
             instance = new MyApplication();
         }
         return instance;             
     }

     public void addActivity(Activity activity)
     {
         activityList.add(activity);
     }

     public void exitActivity()
     {
         for(Activity activity:activityList)
         {
        	 //Log.i(TAG, "activity is ->" + activity);
             activity.finish();
         }
    }
     

     public void exitApp() {
         for (Activity activity : activityList) {
             activity.finish();  
         }
         System.exit(0);
         System.gc();
        } 
}