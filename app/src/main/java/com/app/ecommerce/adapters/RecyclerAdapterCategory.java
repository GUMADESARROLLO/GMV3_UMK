package com.app.ecommerce.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.ecommerce.Config;
import com.app.ecommerce.R;
import com.app.ecommerce.models.Category;
import com.app.ecommerce.models.Clients;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapterCategory extends RecyclerView.Adapter<RecyclerAdapterCategory.MyViewHolder> implements Filterable {

    private Context context;
    private List<Clients> categoryList;
    private List<Clients> categoryListFiltered;
    private ContactsAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView category_name, product_count,txt_cliente_codigo,txt_cliente_limite,txt_cliente_saldo,txt_cliente_disponible;
        public CardView cardView;


        public MyViewHolder(View view) {
            super(view);
            category_name = view.findViewById(R.id.category_name);
            product_count = view.findViewById(R.id.product_count);
            txt_cliente_codigo = view.findViewById(R.id.id_cliente_codigo);
            txt_cliente_limite = view.findViewById(R.id.id_cliente_limite);
            txt_cliente_saldo = view.findViewById(R.id.id_cliente_saldo);
            txt_cliente_disponible = view.findViewById(R.id.id_cliente_disponible);
            cardView = view.findViewById(R.id.id_element_cardview);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onContactSelected(categoryListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }

    public RecyclerAdapterCategory(Context context, List<Clients> categoryList, ContactsAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.categoryList = categoryList;
        this.categoryListFiltered = categoryList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Clients clients = categoryListFiltered.get(position);

        if (clients.getMOROSO().equals("S")){
            holder.category_name.setText(clients.getNOMBRE().concat(" [MOROSO]"));
            //holder.category_name.setTextColor(context.getResources().getColor(R.color.red_light));
            holder.cardView.setBackgroundColor(context.getResources().getColor(R.color.red_light));;
        }else{
            holder.category_name.setText(clients.getNOMBRE());
            //holder.category_name.setTextColor(context.getResources().getColor(R.color.black));
            holder.cardView.setBackgroundColor(context.getResources().getColor(R.color.white));;
        }

        holder.product_count.setText(clients.getDIRECCION());
        holder.txt_cliente_codigo.setText(clients.getCLIENTE());

        holder.txt_cliente_limite.setText(("C$ ").concat(clients.getLIMITE()));
        holder.txt_cliente_saldo.setText(("C$ ").concat(clients.getSALDO()));
        holder.txt_cliente_disponible.setText(("C$ ").concat(clients.getDIPONIBLE()));


    }

    @Override
    public int getItemCount() {
        return categoryListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    categoryListFiltered = categoryList;
                } else {
                    List<Clients> filteredList = new ArrayList<>();
                    for (Clients row : categoryList) {
                        if (row.getNOMBRE().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    categoryListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = categoryListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                categoryListFiltered = (ArrayList<Clients>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface ContactsAdapterListener {
        void onContactSelected(Clients clients);
    }
}
