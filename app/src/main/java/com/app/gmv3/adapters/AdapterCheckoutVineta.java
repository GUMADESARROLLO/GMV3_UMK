package com.app.gmv3.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.gmv3.R;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.Locale;

public class AdapterCheckoutVineta extends RecyclerView.Adapter<AdapterCheckoutVineta.ViewHolder> {

    private Context context;
    private ArrayList<ArrayList<Object>> Lista_Producto;


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txt_nombre,txt_boni,txt_precio_linea,txt_cantidad,txt_sku;
        ImageView image_producto;

        public ViewHolder(View view) {
            super(view);
            txt_nombre          = view.findViewById(R.id.edt_producto_nombre);
            txt_boni            = view.findViewById(R.id.id_bonificacion);
            txt_precio_linea    = view.findViewById(R.id.product_total);
            txt_cantidad        = view.findViewById(R.id.id_cant_item);
            txt_sku             = view.findViewById(R.id.id_sku);
            image_producto      = view.findViewById(R.id.id_image_producto);

        }

    }

    public AdapterCheckoutVineta(Context context, ArrayList<ArrayList<Object>>  arrayItemCart) {
        this.context = context;
        this.Lista_Producto = arrayItemCart;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto_resumen, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        ArrayList<Object> row = Lista_Producto.get(position);

        String Factura      = row.get(0).toString();
        String Cod_Vineta   = row.get(3).toString();
        String Cant_Vineta  = row.get(4).toString();
        String Valor_und    = row.get(5).toString();
        String Valor_total  = row.get(6).toString();

        double Sub_total_price = Double.parseDouble(Valor_total);
        String _Sub_total_price = String.format(Locale.ENGLISH, "%1$,.2f", Sub_total_price);
        String _Valor_und = String.format(Locale.ENGLISH, "%1$,.2f", Double.parseDouble(Valor_und));

        holder.txt_nombre.setText(Cod_Vineta);
        holder.txt_sku.setText("Fact. " + Factura.concat(" "));
        holder.txt_boni.setText("Unit. C$ " + _Valor_und );
        holder.txt_precio_linea.setText(Cant_Vineta);
        holder.txt_cantidad.setText(" C$ " + _Sub_total_price);



        Transformation transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(6)
                .oval(false)
                .build();

        Picasso.with(context)
                .load(R.drawable.money)
                .placeholder(R.drawable.ic_loading)
                .resize(250, 250)
                .centerCrop()
                .transform(transformation)
                .into(holder.image_producto);
    }

    @Override
    public int getItemCount() {
        return Lista_Producto.size();
    }

}
