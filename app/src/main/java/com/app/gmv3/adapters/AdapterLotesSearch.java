package com.app.gmv3.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.gmv3.R;
import com.app.gmv3.models.Lotes_Search;

import java.util.List;

public class AdapterLotesSearch extends RecyclerView.Adapter<AdapterLotesSearch.ViewHolder> {

    private List<Lotes_Search> arrayItemCart;

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txt_documento;
        TextView txt_fechae;
        TextView txt_monto;
        TextView txt_cliente;

        public ViewHolder(View view) {
            super(view);
            txt_documento = view.findViewById(R.id.factura_id);
            txt_fechae = view.findViewById(R.id.factura_date);
            txt_monto = view.findViewById(R.id.factura_monto);
            txt_cliente = view.findViewById(R.id.factura_cliente);
        }

    }

    public AdapterLotesSearch(List<Lotes_Search> arrayItemCart) {

        this.arrayItemCart = arrayItemCart;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rpt_facturas, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        Lotes_Search Lotes = arrayItemCart.get(position);

        holder.txt_documento.setText(Lotes.getmArticulo());
        holder.txt_fechae.setText(Lotes.getmDia());
        holder.txt_monto.setText(("Fact. ").concat(Lotes.getmFactura()));
        holder.txt_cliente.setText(Lotes.getmDescripcion());

        //String txt01 = String.format(Locale.ENGLISH, "%1$,.2f", Double.parseDouble(Factura.getMONTO()));



    }

    @Override
    public int getItemCount() {
        return arrayItemCart.size();
    }

}
