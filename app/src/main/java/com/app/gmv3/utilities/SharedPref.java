package com.app.gmv3.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.app.gmv3.R;

public class SharedPref {

    private Context ctx;
    private SharedPreferences default_prefence;

    public SharedPref(Context context) {
        this.ctx = context;
        default_prefence = PreferenceManager.getDefaultSharedPreferences(context);
    }

    private String str(int string_id) {
        return ctx.getString(string_id);
    }

    public void setYourName(String name) {
        default_prefence.edit().putString(str(R.string.pref_title_name), name).apply();
    }

    public String getYourName() {
        return default_prefence.getString(str(R.string.pref_title_name), str(R.string.default_your_name));
    }
    public String getRuta() {
        return default_prefence.getString(str(R.string.pref_title_ruta), str(R.string.default_your_ruta));
    }

    public void setYourEmail(String name) {
        default_prefence.edit().putString(str(R.string.pref_title_email), name).apply();
    }

    public String getYourEmail() {
        return default_prefence.getString(str(R.string.pref_title_email), str(R.string.default_your_email));
    }

    public void setYourPhone(String name) {
        default_prefence.edit().putString(str(R.string.pref_title_phone), name).apply();
    }

    public String getYourPhone() {
        return default_prefence.getString(str(R.string.pref_title_phone), str(R.string.default_your_phone));
    }

    public void setYourAddress(String name) {
        default_prefence.edit().putString(str(R.string.pref_title_address), name).apply();
    }

    public String getYourAddress() {
        return default_prefence.getString(str(R.string.pref_title_address), str(R.string.default_your_address));
    }

    public void setType(String name) {
        default_prefence.edit().putString(str(R.string.pref_Type), name).apply();
    }

    public String getType() {
        return default_prefence.getString(str(R.string.pref_Type), str(R.string.default_your_address));
    }

    public String getPathAssigned() {
        return default_prefence.getString(str(R.string.pref_Path_Assigned), str(R.string.default_path_assigned));
    }
    public void setPathAssigned(String path) {
        default_prefence.edit().putString(str(R.string.pref_Path_Assigned), path).apply();
    }

}
