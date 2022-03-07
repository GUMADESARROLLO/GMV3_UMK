package com.app.gmv3.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.gmv3.R;
import com.app.gmv3.models.Facturas_mora;
import com.app.gmv3.utilities.DBHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdapterPerfilLotes extends RecyclerView.Adapter<AdapterPerfilLotes.ViewHolder> {

    private Context context;
    private List<Facturas_mora> listFacturaMora;
    private OnClickListener onClickListener = null;

    private SparseBooleanArray selected_items;
    private int current_selected_idx = -1;
    public static DBHelper dbhelper;


    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txt_factura_id;
        public TextView txt_factura_date;
        public TextView txt_factura_cantidad;
        public TextView txt_factura_monto;
        public View lyt_parent;
        public ImageView image;

        public ViewHolder(View view) {
            super(view);
            txt_factura_id          = view.findViewById(R.id.factura_id);
            txt_factura_date        = view.findViewById(R.id.factura_date);
            txt_factura_cantidad    = view.findViewById(R.id.factura_cantidad);
            txt_factura_monto       = view.findViewById(R.id.factura_monto);
            lyt_parent              = view.findViewById(R.id.lyt_parent);
            image                   = view.findViewById(R.id.image);
            dbhelper                = new DBHelper(context);
        }

    }

    public AdapterPerfilLotes(Context context, List<Facturas_mora> arrayItemCart) {
        this.context = context;
        this.listFacturaMora = arrayItemCart;
        selected_items = new SparseBooleanArray();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_perfil_factura_cliente, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Facturas_mora factura = listFacturaMora.get(position);


        String price = String.format(Locale.ENGLISH, "%1$,.2f", Double.parseDouble(factura.Cantidad));
        String Monto = String.format(Locale.ENGLISH, "%1$,.2f", Double.parseDouble(factura.Saldo));


        holder.txt_factura_id.setText(("Nº ").concat(factura.Codigo));
        holder.txt_factura_date.setText(factura.Fecha);
        holder.txt_factura_cantidad.setText(("C$ ").concat(price));
        holder.txt_factura_monto.setText(("C$ ").concat(Monto));

        holder.lyt_parent.setActivated(selected_items.get(position, false));


        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener == null) return;
                onClickListener.onItemClick(v, factura, position);
            }
        });

        holder.lyt_parent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onClickListener == null) return false;
                onClickListener.onItemLongClick(v, factura, position);
                return true;
            }
        });

        toggleCheckedIcon(holder, position);

    }


    private void toggleCheckedIcon(ViewHolder holder, int position) {
        if (selected_items.get(position, false)) {
           // holder.lyt_checked.setVisibility(View.VISIBLE);
            holder.lyt_parent.setBackgroundResource(R.drawable.rectangle_factura_pagada);
            if (current_selected_idx == position) resetCurrentIndex();
        } else {
            //holder.lyt_checked.setVisibility(View.GONE);
            holder.lyt_parent.setBackgroundResource(R.drawable.bg_multi_selection);
            if (current_selected_idx == position) resetCurrentIndex();
        }
    }

    public Facturas_mora getItem(int position) {
        return listFacturaMora.get(position);
    }


    public void toggleSelection(int pos) {
        current_selected_idx = pos;
        String Factura      = listFacturaMora.get(pos).Codigo;

        if (selected_items.get(pos, false)) {
            selected_items.delete(pos);
            dbhelper.deleteOneRecibos(Factura);
        } else {

            ArrayList<ArrayList<Object>> data = dbhelper.getAllDataRecibo(listFacturaMora.get(pos).FacturaCliente);
            int id_tabla = (data.size() + 1);

            String Monto        = listFacturaMora.get(pos).Saldo;
            String NotaCredito  = "0" ;
            String Retencion    = "0" ;
            String Descuento    = "0" ;
            String recValor     = listFacturaMora.get(pos).Saldo ;

            dbhelper.addRecibo(
                    Factura,
                    Monto,
                    NotaCredito,
                    Retencion,
                    Descuento,
                    recValor,
                    (Double.parseDouble(Monto) - Double.parseDouble(recValor) ),
                    id_tabla,
                    listFacturaMora.get(pos).FacturaCliente,
                    "Cancelacion"
            );
            selected_items.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void clearSelections() {
        selected_items.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selected_items.size();
    }


    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(selected_items.size());
        for (int i = 0; i < selected_items.size(); i++) {
            items.add(selected_items.keyAt(i));
        }
        return items;
    }

    public void removeData(int position) {
        listFacturaMora.remove(position);
        resetCurrentIndex();
    }
    public int getItemCount() {
        return listFacturaMora.size();
    }
    private void resetCurrentIndex() {
        current_selected_idx = -1;
    }
    public interface OnClickListener {
        void onItemClick(View view, Facturas_mora obj, int pos);

        void onItemLongClick(View view, Facturas_mora obj, int pos);
    }



}
