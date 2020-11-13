package com.app.gmv3.adapters;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.gmv3.Config;
import com.app.gmv3.R;
import com.app.gmv3.models.Factura_lineas;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

public class RecyclerAdapterNoFacturado extends RecyclerView.Adapter<RecyclerAdapterNoFacturado.MyViewHolder>  {

    private List<Factura_lineas> productListFiltered;

    private Context context;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView product_name, product_cant,product_venta,product_sku;
        public ImageView product_image;
        public MyViewHolder(View view) {
            super(view);
            product_name = view.findViewById(R.id.product_name);
            product_cant = view.findViewById(R.id.product_cant);
            product_venta = view.findViewById(R.id.product_venta);
            product_sku = view.findViewById(R.id.product_sku);
            product_image = view.findViewById(R.id.product_image);


        }
    }

    public RecyclerAdapterNoFacturado(Context context, List<Factura_lineas> productList) {
        this.productListFiltered = productList;
        this.context = context;
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
        holder.product_cant.setText(product.getCANTIDAD().concat(" [").concat(product.getOBSERVACIONES()).concat("]"));
        holder.product_venta.setText(("C$ ").concat(product.getVENTA()));
        holder.product_sku.setText(product.getARTICULO());


        Transformation transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(6)
                .oval(false)
                .build();

        Picasso.with(context)
                .load(Config.ADMIN_PANEL_URL + "/upload/product/" + product.getIMAGEN())
                .placeholder(R.drawable.ic_loading)
                .resize(250, 250)
                .centerCrop()
                .transform(transformation)
                .into(holder.product_image);
    }

    @Override
    public int getItemCount() {
        return productListFiltered.size();
    }




}
