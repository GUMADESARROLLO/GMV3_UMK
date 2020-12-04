package com.app.gmv3.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.gmv3.Config;
import com.app.gmv3.R;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.Locale;

public class AdapterCheckout extends RecyclerView.Adapter<AdapterCheckout.ViewHolder> {

    private Context context;
    private ArrayList<ArrayList<Object>> Lista_Producto;
    private String srt_moneda;

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

    public AdapterCheckout(Context context, ArrayList<ArrayList<Object>>  arrayItemCart, String Moneda) {
        this.context = context;
        this.Lista_Producto = arrayItemCart;
        this.srt_moneda = Moneda;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto_resumen, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        ArrayList<Object> row = Lista_Producto.get(position);

        String prod_cod     = row.get(0).toString();
        String Menu_name    = row.get(1).toString();
        String Quantity     = row.get(2).toString();
        String Bonificado   = row.get(3).toString();
        String Image        = row.get(6).toString();

        double Sub_total_price = Double.parseDouble(row.get(4).toString());
        double cantidad        = Double.parseDouble(Quantity);

        double _precio = Sub_total_price / cantidad;



        String _Sub_total_price = String.format(Locale.ENGLISH, "%1$,.2f", Sub_total_price);
        String _precio_und = String.format(Locale.ENGLISH, "%1$,.2f", _precio);


        holder.txt_nombre.setText(Menu_name);
        holder.txt_sku.setText(prod_cod.concat(" "));
        holder.txt_boni.setText(Bonificado );
        holder.txt_precio_linea.setText(_Sub_total_price + " " + srt_moneda);
        holder.txt_cantidad.setText(_precio_und.concat(" X ").concat(Quantity));



        Transformation transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(6)
                .oval(false)
                .build();

        Picasso.with(context)
                .load(Config.ADMIN_PANEL_URL + "/upload/product/" + Image)
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
