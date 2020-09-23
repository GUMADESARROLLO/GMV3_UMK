package com.app.ecommerce.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.ecommerce.R;
import com.app.ecommerce.activities.ActivityHistory;
import com.app.ecommerce.activities.ActivityProductDetail;
import com.app.ecommerce.models.History;
import com.app.ecommerce.models.Lotes;
import com.app.ecommerce.utilities.Utils;

import java.util.List;

public class RecyclerAdapterLotes extends RecyclerView.Adapter<RecyclerAdapterLotes.ViewHolder> {

    private Context context;
    private List<Lotes> arrayItemCart;

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txt_id_lote;
        TextView txt_date_lote;
        TextView txt_cant_lote;

        public ViewHolder(View view) {
            super(view);
            txt_id_lote = view.findViewById(R.id.id_name_lote);
            txt_date_lote = view.findViewById(R.id.id_date_lote);
            txt_cant_lote = view.findViewById(R.id.id_cant_lote);
        }

    }

    public RecyclerAdapterLotes(Context context, List<Lotes> arrayItemCart) {
        this.context = context;
        this.arrayItemCart = arrayItemCart;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lotes, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.txt_id_lote.setText(ActivityProductDetail.lote_name.get(position));
        holder.txt_date_lote.setText(ActivityProductDetail.lote_date.get(position));
        holder.txt_cant_lote.setText(ActivityProductDetail.lote_cant.get(position));

        Log.e("Error", "onBindViewHolder: " + ActivityProductDetail.lote_name.get(position) );

    }

    @Override
    public int getItemCount() {
        return ActivityProductDetail.lote_name.size();
    }

}
