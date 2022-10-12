package com.app.gmv3.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.gmv3.R;
import com.app.gmv3.activities.ActivityCartReciboColector;
import com.app.gmv3.models.Cart;

import java.util.List;
import java.util.Locale;

public class AdapterCartRecibo extends RecyclerView.Adapter<AdapterCartRecibo.ViewHolder> {

    private Context context;
    private List<Cart> arrayCart;
    boolean adjuntar = false;
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txt_Factura;
        TextView txt_Valor_Factura;
        TextView txt_NotaCredito;
        TextView txt_Retencion;
        TextView txt_Descuento;
        TextView txt_ValorRecibido;
        TextView txt_Saldo;
        RelativeLayout rlyRetencion,rlyDescuento,rlyNotaCredito;
        int withRly;



        public ViewHolder(View view) {
            super(view);
            txt_Factura         = view.findViewById(R.id.id_Factura);
            txt_Valor_Factura   = view.findViewById(R.id.id_Valor_factura);
            txt_NotaCredito     = view.findViewById(R.id.id_nc);
            txt_Retencion       = view.findViewById(R.id.id_retencion);
            txt_Descuento       = view.findViewById(R.id.id_descuento);
            txt_ValorRecibido   = view.findViewById(R.id.id_valor_recibido);
            txt_Saldo           = view.findViewById(R.id.id_saldo);
            rlyRetencion        = view.findViewById(R.id.rly_retencion);
            rlyDescuento        = view.findViewById(R.id.rly_descuento);
            rlyNotaCredito      = view.findViewById(R.id.rly_nota_credito);
            withRly             = 180;
        }

    }

    public AdapterCartRecibo(Context context,List<Cart> arrayCart, boolean Adjuntar) {
        this.context        = context;
        this.arrayCart      = arrayCart;
        this.adjuntar       = Adjuntar;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_recibo, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        String StrFormat = "%1$,.2f";

        holder.txt_Factura.setText(("Fact. ").concat(ActivityCartReciboColector.vineta_factura.get(position)).concat(" [ ").concat(ActivityCartReciboColector.rec_tipo.get(position)).concat(" ] "));

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

        if(saldo<=0.05){
            saldo=0.00;
        }

        String _saldo = String.format(Locale.ENGLISH, StrFormat, saldo);
        holder.txt_Saldo.setText(("C$ ").concat(_saldo));


        if (adjuntar){
            ViewGroup.LayoutParams prm_Retencion    = holder.rlyRetencion.getLayoutParams();
            ViewGroup.LayoutParams prm_Descuento    = holder.rlyDescuento.getLayoutParams();
            ViewGroup.LayoutParams prm_NotaCredito  = holder.rlyNotaCredito.getLayoutParams();

            prm_Retencion.width     = holder.withRly;
            prm_Descuento.width     = holder.withRly;
            prm_NotaCredito.width   = holder.withRly;
        }
    }

    @Override
    public int getItemCount() {
        return ActivityCartReciboColector.vineta_factura.size();
    }

}
