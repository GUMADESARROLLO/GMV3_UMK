package com.app.gmv3.adapters;

import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.gmv3.R;
import com.app.gmv3.models.Comentario;

import java.util.List;

public class RecyclerAdapterComentarios extends RecyclerView.Adapter<RecyclerAdapterComentarios.MyViewHolder>  {

    private List<Comentario> productList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_title, txt_Date;
        public ImageView product_image;
        public WebView txt_comentarios;

        public MyViewHolder(View view) {
            super(view);
            txt_title = view.findViewById(R.id.txt_title);
            txt_Date = view.findViewById(R.id.comment_date);
            txt_comentarios = view.findViewById(R.id.product_description);


        }
    }

    public RecyclerAdapterComentarios(List<Comentario> productList) {
        this.productList = productList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Comentario product = productList.get(position);
        holder.txt_title.setText(product.getPlayer_id());
        holder.txt_Date.setText(product.getDate_coment());
        //holder.txt_comentarios.setText(product.getOrden_comment());

        holder.txt_comentarios.setBackgroundColor(Color.parseColor("#ffffff"));
        holder.txt_comentarios.setFocusableInTouchMode(false);
        holder.txt_comentarios.setFocusable(false);
        holder.txt_comentarios.getSettings().setDefaultTextEncodingName("UTF-8");
        WebSettings webSettings =  holder.txt_comentarios.getSettings();
        webSettings.setJavaScriptEnabled(true);
        String mimeType = "text/html; charset=UTF-8";
        String encoding = "utf-8";
        String htmlText = product.getOrden_comment();

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

        holder.txt_comentarios.loadDataWithBaseURL(null, text, mimeType, encoding, null);


    }

    @Override
    public int getItemCount() {
        return productList.size();
    }




}
