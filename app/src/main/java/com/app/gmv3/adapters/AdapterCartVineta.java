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
import com.app.gmv3.activities.ActivityCartVineta;
import com.app.gmv3.models.Cart;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;
import java.util.Locale;

public class AdapterCartVineta extends RecyclerView.Adapter<AdapterCartVineta.ViewHolder> {

    private Context context;
    private List<Cart> arrayCart;

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView product_name;
        TextView product_quantity;
        TextView product_price;
        TextView producto_sku;
        TextView producto_boni;
        ImageView product_image;

        public ViewHolder(View view) {
            super(view);
            product_name = view.findViewById(R.id.product_name);
            product_quantity = view.findViewById(R.id.product_quantity);
            product_price = view.findViewById(R.id.product_price);
            product_image = view.findViewById(R.id.product_image);
            producto_sku = view.findViewById(R.id.id_sku);
            producto_boni = view.findViewById(R.id.id_bonificacion);
        }

    }

    public AdapterCartVineta(Context context, List<Cart> arrayCart) {
        this.context = context;
        this.arrayCart = arrayCart;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.product_name.setText(ActivityCartVineta.vineta_code.get(position));

        holder.producto_sku.setText("Fact. " + ActivityCartVineta.vineta_factura.get(position).concat(" "));

        holder.producto_boni.setText(" Unit. C$ " + ActivityCartVineta.vineta_und_valor.get(position) );

        double _single_item = ActivityCartVineta.sub_total_price.get(position) ;
        String single_item_price = String.format(Locale.ENGLISH, "%1$,.0f", _single_item);
        holder.product_quantity.setText("C$ " + single_item_price);

        double subtotal = ActivityCartVineta.vineta_cant.get(position) ;
        String _subtotal = String.format(Locale.ENGLISH, "%1$,.0f", subtotal);

        holder.product_price.setText(_subtotal);

          Transformation transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(8)
                .oval(false)
                .build();

        Picasso.with(context)
                .load(R.drawable.stonks)
                .placeholder(R.drawable.ic_loading)
                .resize(250, 250)
                .centerCrop()
                .transform(transformation)
                .into(holder.product_image);

    }

    @Override
    public int getItemCount() {
        return ActivityCartVineta.vineta_factura.size();
    }

}
