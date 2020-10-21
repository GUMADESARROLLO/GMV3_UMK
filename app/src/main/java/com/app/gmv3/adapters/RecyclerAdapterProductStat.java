package com.app.gmv3.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.gmv3.R;
import com.app.gmv3.models.Venta_Articulos;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecyclerAdapterProductStat extends RecyclerView.Adapter<RecyclerAdapterProductStat.MyViewHolder> implements Filterable {

    private Context context;
    private List<Venta_Articulos> productList;
    private List<Venta_Articulos> productListFiltered;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView product_name, product_sku,product_meta,product_real,articulo_dif,txt_progress;
        private ProgressBar progressBar;

        public MyViewHolder(View view) {
            super(view);
            product_name = view.findViewById(R.id.articulo_name);
            product_sku = view.findViewById(R.id.articulo_sku);
            product_meta = view.findViewById(R.id.articulo_meta);
            product_real = view.findViewById(R.id.articulo_real);
            articulo_dif = view.findViewById(R.id.articulo_dif);
            progressBar = view.findViewById(R.id.id_progressbar_item_metas);
            txt_progress = view.findViewById(R.id.txt_progress);


        }
    }

    public RecyclerAdapterProductStat(Context context, List<Venta_Articulos> productList) {
        this.context = context;

        this.productList = productList;
        this.productListFiltered = productList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_articulos_stat, parent, false);

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
        holder.product_meta.setText(("C$ ").concat(txt01));
        holder.product_real.setText(("C$ ").concat(txt02));
        holder.articulo_dif.setText(("C$ ").concat(txt03));

        holder.progressBar.setProgress((int) Double.parseDouble(product.getMcump_monto().replace(",","")));
        holder.txt_progress.setText(product.getMcump_monto().concat(" %"));




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



}


