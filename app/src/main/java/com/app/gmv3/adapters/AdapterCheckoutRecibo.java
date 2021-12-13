package com.app.gmv3.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.gmv3.R;
import com.app.gmv3.activities.ActivityCartReciboColector;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.Locale;

public class AdapterCheckoutRecibo extends RecyclerView.Adapter<AdapterCheckoutRecibo.ViewHolder> {

    private Context context;
    private ArrayList<ArrayList<Object>> Lista_Producto;


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txt_Factura;
        TextView txt_Valor_Factura;
        TextView txt_NotaCredito;
        TextView txt_Retencion;
        TextView txt_Descuento;
        TextView txt_ValorRecibido;
        TextView txt_Saldo;


        public ViewHolder(View view) {
            super(view);
            txt_Factura         = view.findViewById(R.id.id_Factura);
            txt_Valor_Factura   = view.findViewById(R.id.id_Valor_factura);
            txt_NotaCredito     = view.findViewById(R.id.id_nc);
            txt_Retencion       = view.findViewById(R.id.id_retencion);
            txt_Descuento       = view.findViewById(R.id.id_descuento);
            txt_ValorRecibido   = view.findViewById(R.id.id_valor_recibido);
            txt_Saldo           = view.findViewById(R.id.id_saldo);

        }

    }

    public AdapterCheckoutRecibo(Context context, ArrayList<ArrayList<Object>>  arrayItemCart) {
        this.context = context;
        this.Lista_Producto = arrayItemCart;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_recibo, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        String StrFormat = "%1$,.2f";

        holder.txt_Factura.setText(("Fact. ").concat(ActivityCartReciboColector.vineta_factura.get(position)));

        double dbl_und_total = Double.parseDouble(ActivityCartReciboColector.fact_valor.get(position));
        String _dbl_und_total = String.format(Locale.ENGLISH, StrFormat, dbl_und_total);
        holder.txt_Valor_Factura.setText(("C$ ").concat(_dbl_und_total));

        double nota_credito = ActivityCartReciboColector.NotaCredito.get(position);
        String _nota_credito = String.format(Locale.ENGLISH, StrFormat, nota_credito);
        holder.txt_NotaCredito.setText(("C$ ").concat(_nota_credito));

        double retencion = ActivityCartReciboColector.Retencion.get(position);
        String _retencion = String.format(Locale.ENGLISH, StrFormat, retencion);
        holder.txt_Retencion.setText(("C$ ").concat(_retencion));

        double valorrecibido =  ActivityCartReciboColector.ValorRecibido.get(position);
        String _valorrecibido = String.format(Locale.ENGLISH, StrFormat, valorrecibido);
        holder.txt_ValorRecibido.setText(("C$ ").concat(_valorrecibido));

        double descuento = ActivityCartReciboColector.Descuento.get(position);
        String _descuento = String.format(Locale.ENGLISH, StrFormat, descuento);
        holder.txt_Descuento.setText(("C$ ").concat(_descuento));

        double saldo = ActivityCartReciboColector.Saldo.get(position);
        String _saldo = String.format(Locale.ENGLISH, StrFormat, saldo);
        holder.txt_Saldo.setText(("C$ ").concat(_saldo));
    }

    @Override
    public int getItemCount() {
        return Lista_Producto.size();
    }

}
