package com.app.ecommerce.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.ecommerce.Config;
import com.app.ecommerce.R;
import com.app.ecommerce.models.Product;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecyclerAdapterProduct extends RecyclerView.Adapter<RecyclerAdapterProduct.MyViewHolder> implements Filterable {

    private Context context;
    private List<Product> productList;
    private List<Product> productListFiltered;
    private ContactsAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView product_name, product_price,product_cant,product_code;
        public ImageView product_image;

        public MyViewHolder(View view) {
            super(view);
            product_name = view.findViewById(R.id.product_name);
            product_price = view.findViewById(R.id.product_price);
            product_cant = view.findViewById(R.id.id_Cant_item);
            product_code = view.findViewById(R.id.id_cod_articulo);
            product_image = view.findViewById(R.id.category_image);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onContactSelected(productListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }

    public RecyclerAdapterProduct(Context context, List<Product> productList, ContactsAdapterListener listener) {
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
