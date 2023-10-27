package com.app.gmv3.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.gmv3.Config;
import com.app.gmv3.R;

import com.app.gmv3.activities.ActivityImageDetail;
import com.app.gmv3.models.Comentarios;
import com.app.gmv3.utilities.Utils;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdapterInteligenciaMercado extends RecyclerView.Adapter<AdapterInteligenciaMercado.MyViewHolder> implements Filterable {

    private Context context;
    private List<Comentarios> productList;
    private List<Comentarios> productListFiltered;
    private ContactsAdapterListener listener;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView product_title, product_date,product_comentario,product_autor;
        public ImageView product_image;

        public MyViewHolder(View view) {
            super(view);
            product_title = (TextView) view.findViewById(R.id.id_title);
            product_date = (TextView) view.findViewById(R.id.id_date);
            product_comentario = (TextView) view.findViewById(R.id.id_comentario);
            product_autor = (TextView) view.findViewById(R.id.id_autor);
            product_autor = (TextView) view.findViewById(R.id.id_autor);
            product_image = (ImageView) view.findViewById(R.id.id_img_news);



            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onContactSelected(productListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }

    public AdapterInteligenciaMercado(Context context, List<Comentarios> productList, ContactsAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.productList = productList;
        this.productListFiltered = productList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_news_horizontal, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Comentarios product = productListFiltered.get(position);

        PrettyTime prettyTime = new PrettyTime();
        long timeAgo = Utils.timeStringtoMilis(product.getFecha());
        holder.product_title.setText(product.getTitulo());
        holder.product_date.setText(prettyTime.format(new Date(timeAgo)));
        holder.product_comentario.setText(product.getContenido());
        holder.product_autor.setText(product.getAutor());


        holder.product_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ActivityImageDetail.class);
                intent.putExtra("image", product.getImagen());
                intent.putExtra("root", "news");
                context.startActivity(intent);
            }
        });

        Transformation transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(6)
                .oval(false)
                .build();


        if (product.getImagen().equals("")){
            holder.product_image.setVisibility(View.GONE);
        }else{
            Picasso.with(context)
                    .load(product.getImagen())
                    .placeholder(R.drawable.ic_loading)
                    .resize(250, 250)
                    .centerCrop()
                    .transform(transformation)
                    .into(holder.product_image);
        }




    }

    @Override
    public int getItemCount() {
        return productListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    productListFiltered = productList;
                } else {
                    List<Comentarios> filteredList = new ArrayList<>();
                    for (Comentarios row : productList) {
                        if (row.getTitulo().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    productListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = productListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                productListFiltered = (ArrayList<Comentarios>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface ContactsAdapterListener {
        void onContactSelected(Comentarios product);
    }
}
