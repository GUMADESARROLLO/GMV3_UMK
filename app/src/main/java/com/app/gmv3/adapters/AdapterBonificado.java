package com.app.gmv3.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.recyclerview.widget.RecyclerView;

import com.app.gmv3.R;

import java.util.List;

public class AdapterBonificado extends RecyclerView.Adapter<AdapterBonificado.ViewHolder> {

    private Context context;
    private List<String> ListBonificado;
    public class ViewHolder extends RecyclerView.ViewHolder {

        Button bt_generico;

        public ViewHolder(View view) {
            super(view);
            bt_generico = view.findViewById(R.id.btn_generic);

        }

    }

    public AdapterBonificado(Context context, List<String> arrayItemCart) {
        this.context = context;
        this.ListBonificado = arrayItemCart;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bonificado, parent, false);



        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.bt_generico.setText(ListBonificado.get(position));

    }

    @Override
    public int getItemCount() {
        return ListBonificado.size();
    }



}
