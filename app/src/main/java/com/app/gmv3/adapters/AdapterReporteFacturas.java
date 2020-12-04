package com.app.gmv3.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.gmv3.R;
import com.app.gmv3.models.Reporte_Factura;
import java.util.List;
import java.util.Locale;

public class AdapterReporteFacturas extends RecyclerView.Adapter<AdapterReporteFacturas.ViewHolder> {

    private List<Reporte_Factura> arrayItemCart;

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

    public AdapterReporteFacturas(List<Reporte_Factura> arrayItemCart) {

        this.arrayItemCart = arrayItemCart;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rpt_facturas, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        Reporte_Factura Factura = arrayItemCart.get(position);

        String txt01 = String.format(Locale.ENGLISH, "%1$,.2f", Double.parseDouble(Factura.getMONTO()));

        holder.txt_documento.setText(Factura.getFACTURA());
        holder.txt_fechae.setText(Factura.getFECHA());
        holder.txt_monto.setText(("C$ ").concat(txt01));
        holder.txt_cliente.setText(Factura.getCLIENTE());


    }

    @Override
    public int getItemCount() {
        return arrayItemCart.size();
    }

}
