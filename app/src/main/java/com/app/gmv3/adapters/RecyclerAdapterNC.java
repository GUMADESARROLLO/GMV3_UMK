package com.app.gmv3.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.gmv3.R;
import com.app.gmv3.models.NotasDeCreadito;

import java.util.List;
import java.util.Locale;

public class RecyclerAdapterNC extends RecyclerView.Adapter<RecyclerAdapterNC.MyViewHolder>  {

    private Context context;
    private List<NotasDeCreadito> productList;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_nc, txt_nc_fecha,txt_nc_saldo,txt_nc_aplicacion,txt_nc_ruta;

        public MyViewHolder(View view) {
            super(view);
            txt_nc = view.findViewById(R.id.id_nc);
            txt_nc_fecha = view.findViewById(R.id.id_nc_fecha);
            txt_nc_saldo = view.findViewById(R.id.id_nc_saldo);
            txt_nc_aplicacion = view.findViewById(R.id.id_nc_aplicacion);
            txt_nc_ruta = view.findViewById(R.id.id_nc_ruta);


        }
    }

    public RecyclerAdapterNC(Context context, List<NotasDeCreadito> productList) {
        this.context = context;
        this.productList = productList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_nc, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final NotasDeCreadito product = productList.get(position);



        String price = String.format(Locale.ENGLISH, "%1$,.2f", Double.parseDouble(product.getSALDO_LOCAL()));

        holder.txt_nc.setText(product.getDOCUMENTO());
        holder.txt_nc_saldo.setText(("C$ ").concat(price));
        holder.txt_nc_fecha.setText(product.getFECHA());
        holder.txt_nc_aplicacion.setText(product.getAPLICACION());
        holder.txt_nc_ruta.setText(product.getVENDEDOR());
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }




}
