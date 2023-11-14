package com.app.gmv3.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.app.gmv3.Config;
import com.app.gmv3.R;
import com.google.android.material.snackbar.Snackbar;

public class PlanTrabajoActivity extends AppCompatActivity {
    private String ruta_id;
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_trabajo);
        Intent intent = getIntent();
        ruta_id     = intent.getStringExtra("id_Ruta");
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_plan_trabajo);
        }
        webView = (WebView) findViewById(R.id.webview);
        initComponents();



        Log.e("TAG_ERROR", "onCreate: " + Config.API_COMMISIOENS.concat("api/Schedule/").concat(ruta_id) );


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void initComponents() {
        webView.getSettings().setJavaScriptEnabled(true);

        webView.getSettings().setSupportZoom(true);
        webView.setLayerType(WebView.LAYER_TYPE_HARDWARE, null);
        webView.loadUrl(Config.API_COMMISIOENS.concat("/api/Schedule/").concat(ruta_id));

    }
}