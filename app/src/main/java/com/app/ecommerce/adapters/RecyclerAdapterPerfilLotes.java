package com.app.ecommerce.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.ecommerce.R;
import com.app.ecommerce.activities.ActivityProductDetail;
import com.app.ecommerce.activities.ProfileWallet;
import com.app.ecommerce.models.Clients;
import com.app.ecommerce.models.Facturas_mora;

import java.util.List;
import java.util.Locale;

public class RecyclerAdapterPerfilLotes extends RecyclerView.Adapter<RecyclerAdapterPerfilLotes.ViewHolder> {

    private Context context;
    private List<Facturas_mora> arrayItemCart;

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txt_factura_id;
        TextView txt_factura_date;
        TextView txt_factura_cantidad;
        TextView txt_factura_monto;

        public ViewHolder(View view) {
            super(view);
            txt_factura_id = view.findViewById(R.id.factura_id);
            txt_factura_date = view.findViewById(R.id.factura_date);
            txt_factura_cantidad = view.findViewById(R.id.factura_cantidad);
            txt_factura_monto = view.findViewById(R.id.factura_monto);

        }

    }

    public RecyclerAdapterPerfilLotes(Context context, List<Facturas_mora> arrayItemCart) {
        this.context = context;
        this.arrayItemCart = arrayItemCart;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_perfil_factura_cliente, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        String price = String.format(Locale.ENGLISH, "%1$,.2f", Double.parseDouble(ProfileWallet.factura_cant.get(position)));
        String Monto = String.format(Locale.ENGLISH, "%1$,.2f", Double.parseDouble(ProfileWallet.factura_monto.get(position)));

        holder.txt_factura_id.setText(("Nº ").concat(ProfileWallet.factura_id.get(position)));
        holder.txt_factura_date.setText(ProfileWallet.factura_date.get(position));
        holder.txt_factura_cantidad.setText(("C$ ").concat(price));
        holder.txt_factura_monto.setText(("C$ ").concat(Monto));
    }

    @Override
    public int getItemCount() {
        return ProfileWallet.factura_id.size();
    }



}
