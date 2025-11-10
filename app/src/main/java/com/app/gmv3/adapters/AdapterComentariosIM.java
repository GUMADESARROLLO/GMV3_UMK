package com.app.gmv3.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.app.gmv3.R;
import com.app.gmv3.models.PostComments;

import java.util.List;

public class AdapterComentariosIM extends RecyclerView.Adapter<AdapterComentariosIM.MyViewHolder>  {

    private List<PostComments> productList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_title, txt_Date, txt_comentarios;
        public ImageView product_image, iconMore;


        public MyViewHolder(View view) {
            super(view);
            txt_title = view.findViewById(R.id.txt_title);
            txt_Date = view.findViewById(R.id.comment_date);
            txt_comentarios = view.findViewById(R.id.txt_comment);

            iconMore = itemView.findViewById(R.id.icon_delete);

        }
    }

    public AdapterComentariosIM(List<PostComments> productList) {
        this.productList = productList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment_post, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final PostComments Comment = productList.get(position);
        holder.txt_title.setText(Comment.getCreated_by());
        holder.txt_Date.setText(Comment.getCreated_at());
        holder.txt_comentarios.setText(Comment.getComments());

        holder.iconMore.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
            popupMenu.inflate(R.menu.menu_comment_options);

            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {


                    case R.id.action_delete:
                        // Acción para eliminar
                        Toast.makeText(view.getContext(), "Eliminar comentario", Toast.LENGTH_SHORT).show();
                        return true;



                    default:
                        return false;
                }
            });

            popupMenu.show();
        });




    }

    @Override
    public int getItemCount() {
        return productList.size();
    }




}
