package com.app.gmv3.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.gmv3.utilities.ApiClient;
import com.app.gmv3.utilities.CommentApi;
import com.app.gmv3.R;
import com.app.gmv3.models.PostComments;
import com.app.gmv3.utilities.Utils;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class AdapterComentariosIM extends RecyclerView.Adapter<AdapterComentariosIM.MyViewHolder>  {

    private List<PostComments> productList;
    private String userId;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_title, txt_Date, txt_comentarios;
        public ImageView product_image, iconMore;
        public LinearLayout lyt_parent;
        public CardView lyt_thread;

        public MyViewHolder(View view) {
            super(view);
            txt_title = view.findViewById(R.id.txt_title);
            txt_Date = view.findViewById(R.id.comment_date);
            txt_comentarios = view.findViewById(R.id.txt_comment);
            iconMore = itemView.findViewById(R.id.icon_delete);
            lyt_parent = view.findViewById(R.id.lyt_parent);
            lyt_thread = view.findViewById(R.id.lyt_thread);


        }
    }

    public AdapterComentariosIM(Context context,List<PostComments> productList, String userId) {
        this.productList = productList;
        this.userId = userId;
        this.context = context;
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
        PrettyTime prettyTime = new PrettyTime();

        long timeAgo = Utils.timeStringtoMilis(Comment.getCreated_at());



        holder.txt_title.setText(Comment.getCreated_by());
        holder.txt_Date.setText(prettyTime.format(new Date(timeAgo)));
        holder.txt_comentarios.setText(Comment.getComments());

        if(userId.equals(Comment.getCreated_by()) ){
            holder.lyt_parent.setPadding(100, 10, 15, 10);
            holder.lyt_parent.setGravity(Gravity.RIGHT);
            holder.lyt_thread.setCardBackgroundColor(context.getResources().getColor(R.color.teal_50));
        }else{
            holder.lyt_parent.setPadding(15, 10, 100, 10);
            holder.lyt_parent.setGravity(Gravity.LEFT);
            holder.lyt_thread.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        holder.iconMore.setVisibility(userId.equals(Comment.getCreated_by()) ? View.VISIBLE : View.INVISIBLE);

        holder.iconMore.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
            popupMenu.inflate(R.menu.menu_comment_options);

            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.action_delete:
                        //Toast.makeText(view.getContext(), "Eliminar comentario #" + (Comment.getId_comments()), Toast.LENGTH_SHORT).show();
                        deleteCommentApi(view,Comment.getId_comments(),position);
                        return true;
                    default:
                        return false;
                }
            });

            popupMenu.show();
        });
    }

    private void deleteCommentApi(View view, String commentId, int position) {
        CommentApi api = ApiClient.getClient().create(CommentApi.class);
        Map<String, String> body = new HashMap<>();
        body.put("comment_id", commentId);

        api.deleteComment(body).enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    productList.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(view.getContext(), "Comentario eliminado", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(view.getContext(), "Error al eliminar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(view.getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }




}
