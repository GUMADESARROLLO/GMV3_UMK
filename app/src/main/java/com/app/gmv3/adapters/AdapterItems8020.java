package com.app.gmv3.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.gmv3.R;
import com.app.gmv3.models.Producto_lista8020;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdapterItems8020 extends RecyclerView.Adapter<AdapterItems8020.MyViewHolder> implements Filterable {

    private List<Producto_lista8020> productListFiltered;
    private List<Producto_lista8020> productList;
    private ContactsAdapterListener listener;

    private Context context;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView item_descripcion, item_articulo,item_meta,item_venta,item_porcent,item_lista,item_cumplio;
        public CardView cardView;

        public MyViewHolder(View view) {
            super(view);
            item_descripcion    = view.findViewById(R.id.item_descripcion);
            item_articulo       = view.findViewById(R.id.item_articulo);
            item_meta           = view.findViewById(R.id.item_meta);
            item_venta          = view.findViewById(R.id.item_venta);
            item_porcent        = view.findViewById(R.id.item_porcent);
            item_lista          = view.findViewById(R.id.item_lista);
            cardView            = view.findViewById(R.id.item_cardview);
            item_cumplio        = view.findViewById(R.id.item_cumplio);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onContactSelected(productListFiltered.get(getAdapterPosition()));
                }
            });


        }
    }

    public AdapterItems8020(Context context, List<Producto_lista8020> productList, ContactsAdapterListener listener) {
        this.productList = productList;
        this.productListFiltered = productList;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.items_8020, parent, false);

        return new MyViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Producto_lista8020 product = productListFiltered.get(position);

        holder.item_descripcion.setText(product.getDESCRIPCION());
        holder.item_articulo.setText(product.getARTICULOS());

        String meta = String.format(Locale.ENGLISH, "%1$,.0f",  Double.parseDouble(product.getMETAUND()));
        holder.item_meta.setText(meta);

        String venta = String.format(Locale.ENGLISH, "%1$,.0f",  Double.parseDouble(product.getVENTAUND()));
        holder.item_venta.setText(venta);

        String porcent = String.format(Locale.ENGLISH, "%1$,.2f",  Double.parseDouble(product.getPORCECUMP()));
        holder.item_porcent.setText(porcent.concat(" %"));

        holder.item_lista.setText(("SKU: ").concat(product.getLISTA().concat(" %")));

        holder.cardView.setBackgroundColor(context.getResources().getColor(((product.getVENTAUND().equals("0.0")) ? R.color.white : R.color.light_green_300)));


        if (product.getISCUMP().equals("SI")) {
            holder.item_cumplio.setVisibility(View.VISIBLE);
        } else {
            holder.item_cumplio.setVisibility(View.GONE);
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
                    List<Producto_lista8020> filteredList = new ArrayList<>();
                    for (Producto_lista8020 row : productList) {
                        if (row.getDESCRIPCION().toLowerCase().contains(charString.toLowerCase())) {
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
                productListFiltered = (ArrayList<Producto_lista8020>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
    public interface ContactsAdapterListener {
        void onContactSelected(Producto_lista8020 product);
    }



}
