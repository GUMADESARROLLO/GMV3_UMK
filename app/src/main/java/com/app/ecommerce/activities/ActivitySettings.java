package com.app.ecommerce.activities;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.ecommerce.Config;
import com.app.ecommerce.R;
import com.app.ecommerce.utilities.SharedPref;
import com.app.ecommerce.utilities.Utils;



/**
 * ATTENTION : To see where list of setting comes is open res/xml/setting_preferences.xml
 */

public class ActivitySettings extends PreferenceActivity {

    private AppCompatDelegate mDelegate;
    private SharedPref sharedPref;
    private View parent_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting_preferences);
        parent_view = findViewById(android.R.id.content);

        if (Config.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }

        sharedPref = new SharedPref(this);

        final EditTextPreference prefName = (EditTextPreference) findPreference(getString(R.string.pref_title_name));
        final EditTextPreference prefEmail = (EditTextPreference) findPreference(getString(R.string.pref_title_email));
        final EditTextPreference prefPhone = (EditTextPreference) findPreference(getString(R.string.pref_title_phone));
        final EditTextPreference prefAddress = (EditTextPreference) findPreference(getString(R.string.pref_title_address));



        prefName.setSummary(sharedPref.getYourName());
        prefName.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                String s = (String) o;
                if(!s.trim().isEmpty()){
                    prefName.setSummary(s);
                    return true;
                }else{
                    Snackbar snackbar = Snackbar.make(parent_view, R.string.pref_msg_invalid_name, Snackbar.LENGTH_LONG);
                    TextView textView = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.YELLOW);
                    snackbar.show();
                    return false;
                }
            }
        });

        prefEmail.setSummary(sharedPref.getYourEmail());
        prefEmail.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                String s = (String) o;
                if (Utils.isValidEmail(s)) {
                    prefEmail.setSummary(s);
                    return true;
                } else {
                    Snackbar snackbar = Snackbar.make(parent_view, R.string.pref_msg_invalid_email, Snackbar.LENGTH_LONG);
                    TextView textView = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.YELLOW);
                    snackbar.show();
                    return false;
                }
            }
        });

        prefPhone.setSummary(sharedPref.getYourPhone());
        prefPhone.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                String s = (String) o;
                if(!s.trim().isEmpty()){
                    prefPhone.setSummary(s);
                    return true;
                }else{
                    Snackbar snackbar = Snackbar.make(parent_view, R.string.pref_msg_invalid_phone, Snackbar.LENGTH_LONG);
                    TextView textView = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.YELLOW);
                    snackbar.show();
                    return false;
                }
            }
        });

        prefAddress.setSummary(sharedPref.getYourAddress());
        prefAddress.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                String s = (String) o;
                if(!s.trim().isEmpty()){
                    prefAddress.setSummary(s);
                    return true;
                }else{
                    Snackbar snackbar = Snackbar.make(parent_view, R.string.pref_msg_invalid_address, Snackbar.LENGTH_LONG);
                    TextView textView = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.YELLOW);
                    snackbar.show();
                    return false;
                }
            }
        });

        initToolbar();
    }





    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getDelegate().onPostCreate(savedInstanceState);
    }

    private void initToolbar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_profile);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }



    /*
     * Support for Activity : DO NOT CODE BELOW ----------------------------------------------------
     */

    public ActionBar getSupportActionBar() {
        return getDelegate().getSupportActionBar();
    }

    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        getDelegate().setSupportActionBar(toolbar);
    }

    @Override
    public MenuInflater getMenuInflater() {
        return getDelegate().getMenuInflater();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        getDelegate().setContentView(layoutResID);
    }

    @Override
    public void setContentView(View view) {
        getDelegate().setContentView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().setContentView(view, params);
    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().addContentView(view, params);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getDelegate().onPostResume();
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        getDelegate().setTitle(title);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getDelegate().onConfigurationChanged(newConfig);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getDelegate().onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getDelegate().onDestroy();
    }

    public void invalidateOptionsMenu() {
        getDelegate().invalidateOptionsMenu();
    }

    private AppCompatDelegate getDelegate() {
        if (mDelegate == null) {
            mDelegate = AppCompatDelegate.create(this, null);
        }
        return mDelegate;
    }

}
