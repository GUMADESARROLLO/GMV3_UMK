package com.app.gmv3.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.gmv3.Config;
import com.app.gmv3.R;
import com.app.gmv3.models.Banner;
import com.app.gmv3.utilities.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AdapterListNews extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Banner> items = new ArrayList<>();

    private Context ctx;

    @LayoutRes
    private int layout_id;

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Banner obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterListNews(Context context, List<Banner> items, @LayoutRes int layout_id) {
        this.items = items;
        ctx = context;
        this.layout_id = layout_id;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;

        public TextView subtitle;
        public TextView date;
        public View lyt_parent;
        public WebView txt_product_description;

        public OriginalViewHolder(View v) {
            super(v);
            image = v.findViewById(R.id.image);
            subtitle = v.findViewById(R.id.subtitle);
            date = v.findViewById(R.id.date);
            lyt_parent = v.findViewById(R.id.lyt_parent);
            txt_product_description = v.findViewById(R.id.product_description);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(layout_id, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {

            OriginalViewHolder view = (OriginalViewHolder) holder;

            Banner n = items.get(position);
            view.subtitle.setText("");
            final String Fecha = (String) DateFormat.format("EEE dd MMM yyyy", Utils.timeStringtoMilis(n.getCreated_at()));
            view.date.setText(Fecha);


            view.txt_product_description.setBackgroundColor(Color.parseColor("#ffffff"));
            view.txt_product_description.setFocusableInTouchMode(false);
            view.txt_product_description.setFocusable(false);
            view.txt_product_description.getSettings().setDefaultTextEncodingName("UTF-8");

            WebSettings webSettings = view.txt_product_description.getSettings();
            webSettings.setDefaultFontSize(16);
            webSettings.setJavaScriptEnabled(true);

            String mimeType = "text/html; charset=UTF-8";
            String encoding = "utf-8";
            String htmlText = ((n.getBanner_description().equals("")) ? "Sin Descripcion" : n.getBanner_description());
            htmlText = getSafeSubstring(htmlText,50).concat(" ... Leer mas.");
            String text = "<html><head>"
                    + "<style type=\"text/css\">body{color: #525252;}"
                    + "</style></head>"
                    + "<body>"
                    + htmlText
                    + "</body></html>";


            view.txt_product_description.loadDataWithBaseURL(null, text, mimeType, encoding, null);



            Glide.with(ctx).load(Config.ADMIN_PANEL_URL + "/upload/news/" + n.getBanner_image())
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(view.image);

            view.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener == null) return;
                    mOnItemClickListener.onItemClick(view, items.get(position), position);
                }
            });
        }
    }

    public String getSafeSubstring(String s, int maxLength){
        if(!TextUtils.isEmpty(s)){
            if(s.length() >= maxLength){
                return s.substring(0, maxLength);
            }
        }
        return s;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


}