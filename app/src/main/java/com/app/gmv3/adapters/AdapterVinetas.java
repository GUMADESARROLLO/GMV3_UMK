package com.app.gmv3.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.gmv3.R;
import com.app.gmv3.models.Vineta;

import java.util.List;

public class AdapterVinetas extends RecyclerView.Adapter<AdapterVinetas.ViewHolder> {

    private List<Vineta> arrayVineta;

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txt_vineta;
        TextView txt_factura;
        TextView txt_fecha;

        TextView txt_valor;
        TextView txt_cantidad;
        TextView txt_total_valor;


        public ViewHolder(View view) {
            super(view);

            txt_vineta = view.findViewById(R.id.vineta_id);
            txt_factura  = view.findViewById(R.id.vineta_factura);
            txt_fecha = view.findViewById(R.id.vineta_fecha);

            txt_cantidad = view.findViewById(R.id.vineta_cantidad);
            txt_valor  = view.findViewById(R.id.vineta_unidad_valor);
            txt_total_valor = view.findViewById(R.id.vineta_total_valor);

        }

    }

    public AdapterVinetas(List<Vineta> arrayItemCart) {

        this.arrayVineta = arrayItemCart;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_voucher, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        Vineta Vinetas = arrayVineta.get(position);

        holder.txt_vineta.setText(Vinetas.getmVineta());
        holder.txt_factura.setText(Vinetas.getmFactura());
        holder.txt_fecha.setText(Vinetas.getmFecha());

        holder.txt_cantidad.setText(Vinetas.getmCantidad());
        holder.txt_valor.setText(" X C$ "+ Vinetas.getmValor());
        holder.txt_total_valor.setText("C$ " + Vinetas.getmTotal());


        //String txt01 = String.format(Locale.ENGLISH, "%1$,.2f", Double.parseDouble(Factura.getMONTO()));



    }

    @Override
    public int getItemCount() {
        return arrayVineta.size();
    }

}
