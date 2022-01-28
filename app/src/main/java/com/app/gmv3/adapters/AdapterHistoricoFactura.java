package com.app.gmv3.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.app.gmv3.R;
import com.app.gmv3.models.Factura_Historico;
import com.app.gmv3.models.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdapterHistoricoFactura extends RecyclerView.Adapter<AdapterHistoricoFactura.ViewHolder> implements Filterable {

    private Context context;
    private List<Factura_Historico> productList;
    private List<Factura_Historico> productListFiltered;



    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txt_documento;
        TextView txt_fechae;
        TextView txt_monto;
        TextView txt_cliente;
        TextView txt_activo;
        TextView txt_factura_vence;

        LinearLayout lytFactura;

        public ViewHolder(View view) {
            super(view);
            txt_documento       = view.findViewById(R.id.factura_id);
            txt_fechae          = view.findViewById(R.id.factura_date);
            txt_monto           = view.findViewById(R.id.factura_monto);
            txt_cliente         = view.findViewById(R.id.factura_cliente);
            txt_activo          = view.findViewById(R.id.factura_activo);
            lytFactura          = view.findViewById(R.id.lyt_Factura);
            txt_factura_vence   = view.findViewById(R.id.factura_vence);
        }

    }

    public AdapterHistoricoFactura(Context context, List<Factura_Historico> productList) {
        this.context = context;
        this.productList = productList;
        this.productListFiltered = productList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_facturas_historico, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Factura_Historico product = productListFiltered.get(position);
        String Plazo_, Label_plazo;



        Factura_Historico Factura = productList.get(position);

        String txt01 = String.format(Locale.ENGLISH, "%1$,.2f", Double.parseDouble(Factura.getMONTO()));


        if (Factura.getPLAZO().equals("1")||Factura.getPLAZO().equals("01")){
            Plazo_ = "Factura Fue de Contado.";
        }else{
            if (Factura.getVENCE().equals("-/-/-")){
                Plazo_ = "Factura ya fue cancelada";
                holder.txt_factura_vence.setVisibility(View.GONE);
            }else{
                Label_plazo = (Integer.parseInt(Factura.getDVENCIDOS()) > 0)? "Lleva vencida " : " Vence en ";

                Plazo_ = ("Plazo ").concat(Factura.getPLAZO()).concat(" Dias.").concat(Label_plazo).concat(String.valueOf(Math.abs(Integer.parseInt(Factura.getDVENCIDOS())))).concat(" dias.");
            }

        }

        holder.txt_documento.setText(Factura.getFACTURA());
        holder.txt_fechae.setText(Factura.getFECHA());
        holder.txt_monto.setText(("C$ ").concat(txt01));
        holder.txt_cliente.setText(Plazo_);
        holder.txt_activo.setText("");
        holder.txt_factura_vence.setText(("Ven. ").concat(Factura.getVENCE()));

        if (Integer.parseInt(Factura.getACTIVA()) == 0){
            holder.lytFactura.setBackgroundResource(R.drawable.rectangle_factura_pagada);
            holder.lytFactura.setPadding(18, 26, 18, 26);
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
                    List<Factura_Historico> filteredList = new ArrayList<>();
                    for (Factura_Historico row : productList) {
                        if (row.getFACTURA().toLowerCase().contains(charString.toLowerCase())) {
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
                productListFiltered = (ArrayList<Factura_Historico>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


}
