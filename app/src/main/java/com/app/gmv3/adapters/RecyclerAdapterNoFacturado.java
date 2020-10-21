package com.app.gmv3.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.gmv3.R;
import com.app.gmv3.models.Factura_lineas;

import java.util.List;

public class RecyclerAdapterNoFacturado extends RecyclerView.Adapter<RecyclerAdapterNoFacturado.MyViewHolder>  {

    private List<Factura_lineas> productListFiltered;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView product_name, product_cant,product_venta,product_fecha;

        public MyViewHolder(View view) {
            super(view);
            product_name = view.findViewById(R.id.product_name);
            product_cant = view.findViewById(R.id.product_cant);
            product_venta = view.findViewById(R.id.product_venta);
            product_fecha = view.findViewById(R.id.id_factura_date);


        }
    }

    public RecyclerAdapterNoFacturado(List<Factura_lineas> productList) {
        this.productListFiltered = productList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_no_facturado, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Factura_lineas product = productListFiltered.get(position);
        holder.product_name.setText(product.getDESCRIPCION());
    }

    @Override
    public int getItemCount() {
        return productListFiltered.size();
    }




}
