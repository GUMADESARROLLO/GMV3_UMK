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

import com.app.ecommerce.R;
import com.app.ecommerce.models.Venta_Articulos;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecyclerAdapterProductStat extends RecyclerView.Adapter<RecyclerAdapterProductStat.MyViewHolder> implements Filterable {

    private List<Venta_Articulos> productList;

    private List<Venta_Articulos> productListFiltered;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView product_name, product_sku,product_meta,product_real,articulo_dif;


        public MyViewHolder(View view) {
            super(view);
            product_name = view.findViewById(R.id.articulo_name);
            product_sku = view.findViewById(R.id.articulo_sku);
            product_meta = view.findViewById(R.id.articulo_meta);
            product_real = view.findViewById(R.id.articulo_real);
            articulo_dif = view.findViewById(R.id.articulo_dif);


        }
    }

    public RecyclerAdapterProductStat(List<Venta_Articulos> productList) {

        this.productList = productList;
        this.productListFiltered = productList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_articulos_stat, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Venta_Articulos product = productList.get(position);

        double Meta = Double.parseDouble(product.getmMeta_monto());
        double Real = Double.parseDouble(product.getmReal_monto());

        double faltante_meta = Meta - Real;

        String txt01 = String.format(Locale.ENGLISH, "%1$,.2f", Double.parseDouble(product.getmMeta_monto()));
        String txt02 = String.format(Locale.ENGLISH, "%1$,.2f", Double.parseDouble(product.getmReal_monto()));
        String txt03 = String.format(Locale.ENGLISH, "%1$,.2f", faltante_meta);

        holder.product_name.setText(product.getmName());
        holder.product_sku.setText(product.getmCodigo());
        holder.product_meta.setText(txt01);
        holder.product_real.setText(txt02);
        holder.articulo_dif.setText(txt03);


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
                    List<Venta_Articulos> filteredList = new ArrayList<>();
                    for (Venta_Articulos row : productList) {
                        if (row.getmName().toLowerCase().contains(charString.toLowerCase())) {
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
                productListFiltered = (ArrayList<Venta_Articulos>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }


}
