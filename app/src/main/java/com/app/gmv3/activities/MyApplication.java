package com.app.gmv3.activities;

import android.app.Application;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.app.gmv3.utilities.SharedPref;
import com.onesignal.OneSignal;

public class MyApplication extends Application {

    public static final String TAG = MyApplication.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private static MyApplication mInstance;
    public SharedPreferences preferences;
    public String prefName = "news";


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        OneSignal.startInit(this)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
    public void saveIsLogin(boolean flag) {
        preferences = this.getSharedPreferences(prefName, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("IsLoggedIn", flag);
        editor.commit();
    }
    public boolean getIsLogin() {
        preferences = this.getSharedPreferences(prefName, 0);
        if (preferences != null) {
            boolean flag = preferences.getBoolean("IsLoggedIn", false);
            return flag;
        }
        return false;
    }
    public void saveLogin(String user_id, String user_name,String strFullName,String strTelefono, String strUserEmail) {
      /*  preferences = this.getSharedPreferences(prefName, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user_id", user_id);
        editor.putString("user_name", user_name);
        editor.commit();*/

        SharedPref  sharedPref = new SharedPref(this);
        sharedPref.setYourName(user_name);
        sharedPref.setYourEmail(strUserEmail);
        sharedPref.setYourPhone(strTelefono);
        sharedPref.setYourAddress(strFullName);




    }
    public void saveType(String type) {
        preferences = this.getSharedPreferences(prefName, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("type", type);
        editor.commit();
    }

}