package com.app.gmv3.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.gmv3.R;
import com.app.gmv3.models.Factura_lineas;

import java.util.List;
import java.util.Locale;

public class RecyclerAdapterFacturasLineas extends RecyclerView.Adapter<RecyclerAdapterFacturasLineas.MyViewHolder>  {

    private Context context;
    private List<Factura_lineas> productList;
    private List<Factura_lineas> productListFiltered;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView product_name, product_cant,product_venta;

        public MyViewHolder(View view) {
            super(view);
            product_name = view.findViewById(R.id.product_name);
            product_cant = view.findViewById(R.id.product_cant);
            product_venta = view.findViewById(R.id.product_venta);

        }
    }

    public RecyclerAdapterFacturasLineas(Context context, List<Factura_lineas> productList) {
        this.context = context;
        this.productList = productList;
        this.productListFiltered = productList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_factura_lineas, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Factura_lineas product = productListFiltered.get(position);



        String price = String.format(Locale.ENGLISH, "%1$,.2f", Double.parseDouble(product.getVENTA()));

        holder.product_name.setText(product.getDESCRIPCION());
        holder.product_venta.setText(("C$ ").concat(price));
        holder.product_cant.setText(product.getCANTIDAD());
    }

    @Override
    public int getItemCount() {
        return productListFiltered.size();
    }




}
