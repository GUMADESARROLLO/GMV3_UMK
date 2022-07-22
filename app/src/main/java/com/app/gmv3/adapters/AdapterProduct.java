package com.app.gmv3.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.gmv3.Config;
import com.app.gmv3.R;
import com.app.gmv3.models.Product;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class AdapterProduct extends RecyclerView.Adapter<AdapterProduct.MyViewHolder> implements Filterable {

    private Context context;
    private List<Product> productList;
    private List<Product> productListFiltered;
    private ContactsAdapterListener listener;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView product_name, product_price,product_cant,product_code,product_label_offer;
        public ImageView product_image,product_premiun;
        private LinearLayout lvl_offer;
        public MyViewHolder(View view) {
            super(view);
            product_name = view.findViewById(R.id.product_name);
            product_price = view.findViewById(R.id.product_price);
            product_cant = view.findViewById(R.id.id_Cant_item);
            product_code = view.findViewById(R.id.id_cod_articulo);
            product_image = view.findViewById(R.id.category_image);
            product_premiun = view.findViewById(R.id.img_premiun);
            product_label_offer = view.findViewById(R.id.txt_offer);
            lvl_offer = view.findViewById(R.id.lvl_offer);


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onContactSelected(productListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }

    public AdapterProduct(Context context, List<Product> productList, ContactsAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.productList = productList;
        this.productListFiltered = productList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Product product = productListFiltered.get(position);
        holder.product_name.setText(product.getProduct_name());

         String price = String.format(Locale.ENGLISH, "%1$,.2f", product.getProduct_price());
         holder.product_price.setText(("C$ ").concat(price));

        String quantity = String.format(Locale.ENGLISH, "%1$,.2f", product.getProduct_quantity());
        holder.product_cant.setText(quantity.concat(" [" + product.getProduct_und().concat("]")));

        holder.product_code.setText(product.getProduct_id());

        if (product.getCALIFICATIVO().equals("A")) {
            holder.product_premiun.setVisibility(View.VISIBLE);
        } else {
            holder.product_premiun.setVisibility(View.GONE);
        }



        List<String> sVinneta = Arrays.asList(product.getISPROMO().split(":"));

        //holder.Ruta_asignada.setText(sVinneta.get(1));

        if (sVinneta.get(0).equals("S")) {
            holder.lvl_offer.setVisibility(View.VISIBLE);
            holder.product_label_offer.setText(sVinneta.get(1));

        } else {
            holder.lvl_offer.setVisibility(View.GONE);
        }


        Transformation transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(6)
                .oval(false)
                .build();

        Picasso.with(context)
                .load(Config.ADMIN_PANEL_URL + "/upload/product/" + product.getProduct_image())
                .placeholder(R.drawable.ic_loading)
                .resize(250, 250)
                .centerCrop()
                .transform(transformation)
                .into(holder.product_image);

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
                    List<Product> filteredList = new ArrayList<>();
                    for (Product row : productList) {
                        if (row.getProduct_name().toLowerCase().contains(charString.toLowerCase())) {
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
                productListFiltered = (ArrayList<Product>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface ContactsAdapterListener {
        void onContactSelected(Product product);
    }
}
