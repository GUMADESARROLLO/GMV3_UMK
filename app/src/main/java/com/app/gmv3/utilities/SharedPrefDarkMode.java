package com.app.gmv3.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.app.gmv3.R;

public class SharedPrefDarkMode {

    private Context ctx;
    private SharedPreferences default_prefence;

    public SharedPrefDarkMode(Context context) {
        this.ctx = context;
        default_prefence = PreferenceManager.getDefaultSharedPreferences(context);
    }

    private String str(int string_id) {
        return ctx.getString(string_id);
    }

    public void setModeDark(Boolean valor) {
        default_prefence.edit().putBoolean(str(R.string.pref_DarkMode),valor).apply();
    }

    public boolean getModeDark() {
        return default_prefence.getBoolean(str(R.string.pref_DarkMode), true);
    }


}
