package com.app.gmv3.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.app.gmv3.utilities.ApiClient;
import com.app.gmv3.utilities.CommentApi;
import com.app.gmv3.R;
import com.app.gmv3.models.PostComments;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;

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
                        Toast.makeText(view.getContext(), "Eliminar comentario #" + (Comment.getId_comments()), Toast.LENGTH_SHORT).show();
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
