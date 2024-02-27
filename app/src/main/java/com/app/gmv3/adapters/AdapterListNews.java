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
import com.squareup.picasso.Picasso;

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
        public TextView title;
        public TextView description;
        public TextView date;
        public ImageView image;

        public OriginalViewHolder(View v) {
            super(v);
            image = v.findViewById(R.id.image_news);
            title = v.findViewById(R.id.title);
            description = v.findViewById(R.id.description);
            date = v.findViewById(R.id.date);
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

            view.title.setText(n.getTitulo());
            view.description.setText(n.getDescripcion());

            String dtIni = (String) DateFormat.format("EEE dd MMM yyyy", Utils.timeStringtoMilis(n.getFechaInicio()));
            String dtEnd = (String) DateFormat.format("EEE dd MMM yyyy", Utils.timeStringtoMilis(n.getFechaFinal()));
            view.date.setText(("Del ").concat(dtIni).concat(" al ").concat(dtEnd));

            Glide.with(ctx).load(n.getImagen())
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(view.image);



//            view.subtitle.setText("");
//
//
//
//            view.txt_product_description.setBackgroundColor(Color.parseColor("#ffffff"));
//            view.txt_product_description.setFocusableInTouchMode(false);
//            view.txt_product_description.setFocusable(false);
//            view.txt_product_description.getSettings().setDefaultTextEncodingName("UTF-8");
//
//            WebSettings webSettings = view.txt_product_description.getSettings();
//            webSettings.setDefaultFontSize(16);
//            webSettings.setJavaScriptEnabled(true);
//
//            String mimeType = "text/html; charset=UTF-8";
//            String encoding = "utf-8";
//            String htmlText = ((n.getBanner_description().equals("")) ? "Sin Descripcion" : n.getBanner_description());
//            htmlText = getSafeSubstring(htmlText,50).concat(" ... Leer mas.");
//            String text = "<html><head>"
//                    + "<style type=\"text/css\">body{color: #525252;}"
//                    + "</style></head>"
//                    + "<body>"
//                    + htmlText
//                    + "</body></html>";
//
//
//            view.txt_product_description.loadDataWithBaseURL(null, text, mimeType, encoding, null);
//
//
//

//
//            view.lyt_parent.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (mOnItemClickListener == null) return;
//                    mOnItemClickListener.onItemClick(view, items.get(position), position);
//                }
//            });
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