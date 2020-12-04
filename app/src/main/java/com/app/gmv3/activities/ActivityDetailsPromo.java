package com.app.gmv3.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.app.gmv3.Config;
import com.app.gmv3.R;
import com.app.gmv3.utilities.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class ActivityDetailsPromo extends AppCompatActivity {

    String promo_descripcion, promo_imagen;
    WebView txt_product_description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_promo);

        Intent intent = getIntent();
        promo_descripcion = intent.getStringExtra("Promo_descripcion");
        promo_imagen = intent.getStringExtra("promo_imagen");

        initToolbar();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Utils.setSystemBarLight(this);
        txt_product_description = findViewById(R.id.product_description);

        ImageView image =  findViewById(R.id.id_img_promo);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivityImageDetail.class);
                intent.putExtra("image", promo_imagen);
                intent.putExtra("root", "news");
                startActivity(intent);
            }
        });

        try {
            Glide.with(this).load(Config.ADMIN_PANEL_URL + "/upload/news/" + promo_imagen)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(image);



        } catch (Exception e) {
        }


        WebSettings webSettings = txt_product_description.getSettings();
        Resources res = getResources();
        int fontSize = res.getInteger(R.integer.font_size);
        webSettings.setDefaultFontSize(fontSize);
        webSettings.setJavaScriptEnabled(true);

        String mimeType = "text/html; charset=UTF-8";
        String encoding = "utf-8";
        String htmlText = ((promo_descripcion.equals("")) ? "Sin Descripcion" : promo_descripcion);

        String text = "<html><head>"
                + "<style type=\"text/css\">body{color: #525252;}"
                + "</style></head>"
                + "<body>"
                + htmlText
                + "</body></html>";

        String text_rtl = "<html dir='rtl'><head>"
                + "<style type=\"text/css\">body{color: #525252;}"
                + "</style></head>"
                + "<body>"
                + htmlText
                + "</body></html>";

        if (Config.ENABLE_RTL_MODE) {
            txt_product_description.loadDataWithBaseURL(null, text_rtl, mimeType, encoding, null);
        } else {
            txt_product_description.loadDataWithBaseURL(null, text, mimeType, encoding, null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
