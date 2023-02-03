package com.app.gmv3.adapters;

import static com.app.gmv3.utilities.Constant.DEL_RECIBO_COLECTOR;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.gmv3.R;
import com.app.gmv3.activities.ActivityCartReciboColector;
import com.app.gmv3.activities.ActivityRecibosAdjuntos;
import com.app.gmv3.models.Cart;
import com.app.gmv3.models.ItemHistorico;
import com.app.gmv3.utilities.Utils;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterHistoryRecibo extends RecyclerView.Adapter<AdapterHistoryRecibo.MyViewHolder> implements Filterable {

    private Context context;
    private List<ItemHistorico> productList;
    private List<ItemHistorico> productListFiltered;
    RecyclerView recyclerView;
    AdapterCartRecibo adapterCart;
    List<Cart> arrayCart;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView product_title, product_date,product_autor, txt_total_order, txt_status,txt_comentario,txt_coment_anulacion,txt_serie;
        ImageView img_delete_liq,img_adjuntar;
        public CardView cardView;


        public MyViewHolder(View view) {
            super(view);

            product_title           = view.findViewById(R.id.id_title);
            recyclerView            = view.findViewById(R.id.recycler_view);
            adapterCart             = new AdapterCartRecibo(context,arrayCart,true);
            product_date            = view.findViewById(R.id.id_date);
            product_autor           = view.findViewById(R.id.id_autor);
            txt_total_order         = view.findViewById(R.id.id_total_order);
            txt_status              = view.findViewById(R.id.id_status);
            txt_comentario          = view.findViewById(R.id.id_comentario);
            img_delete_liq          = view.findViewById(R.id.id_delete_liq);
            img_adjuntar            = view.findViewById(R.id.id_adjuntar);
            txt_coment_anulacion    = view.findViewById(R.id.id_coment_anulacion);
            txt_serie               = view.findViewById(R.id.id_serie);
            cardView                = view.findViewById(R.id.id_element_cardview);

            img_delete_liq.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Info.");
                    builder.setMessage("¿Esta Seguro que quiere eliminar esta Recibo?");
                    builder.setCancelable(false);
                    builder.setPositiveButton(context.getResources().getString(R.string.dialog_option_yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActionDeleteRequest(productListFiltered.get(getAdapterPosition()).getmId() );
                        }
                    });
                    builder.setNegativeButton(context.getResources().getString(R.string.dialog_option_no), null);
                    builder.setCancelable(false);
                    builder.show();
                }
            });

            img_adjuntar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ActivityRecibosAdjuntos.class);
                    intent.putExtra("id_recibo", productListFiltered.get(getAdapterPosition()).getmId());
                    //intent.putExtra("Nombre_ruta", sharedPref.getYourAddress());
                    context.startActivity(intent);
                }
            });


        }
    }

    public void ActionDeleteRequest(final String mID) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(context.getString(R.string.checkout_submit_title));
        progressDialog.setMessage("Procesando... ");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, DEL_RECIBO_COLECTOR, new Response.Listener<String>() {
            @Override
            public void onResponse(final String ServerResponse) {

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        dialogSuccessOrder();
                    }
                }, 2000);

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        progressDialog.dismiss();
                        Toast.makeText(context, volleyError.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("ID", mID);

                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
    public void clearData() {
        ActivityCartReciboColector.vineta_factura.clear();
        ActivityCartReciboColector.fact_valor.clear();
        ActivityCartReciboColector.NotaCredito.clear();
        ActivityCartReciboColector.Retencion.clear();
        ActivityCartReciboColector.Descuento.clear();
        ActivityCartReciboColector.ValorRecibido.clear();
        ActivityCartReciboColector.Saldo.clear();
        ActivityCartReciboColector.rec_tipo.clear();

    }
    public void dialogSuccessOrder() {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_info);
        dialog.setCancelable(true);

        TextView txt_title = dialog.findViewById(R.id.title);
        TextView txt_msg = dialog.findViewById(R.id.content);

        txt_title.setText(" Eliminado ");
        txt_msg.setText( "El Recibo se anulo de manera correcta " );

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        (dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
    public AdapterHistoryRecibo(Context context, List<ItemHistorico> productList) {
        this.context = context;
        this.productList = productList;
        this.productListFiltered = productList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_recibo, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final ItemHistorico items = productListFiltered.get(position);


        clearData();

        String[] arrStatus = {"Pendiente","Procesado","Anulado","Eliminado"};

        List<String> sList = Arrays.asList(items.getmOrderList().split("],"));

        for(int i=0;i<sList.size() ; i++){

            List<String> sRows = Arrays.asList(sList.get(i).split(";"));

            String strTipo = (sRows.size()==9) ? sRows.get(8) : "N/D";
            ActivityCartReciboColector.vineta_factura.add(sRows.get(0).replace("[","").toString());
            ActivityCartReciboColector.fact_valor.add(sRows.get(1).toString());
            ActivityCartReciboColector.NotaCredito.add(Double.parseDouble(sRows.get(2).toString()));
            ActivityCartReciboColector.Retencion.add(Double.parseDouble(sRows.get(3).toString()));
            ActivityCartReciboColector.Descuento.add(Double.parseDouble(sRows.get(4).toString()));
            ActivityCartReciboColector.ValorRecibido.add(Double.parseDouble(sRows.get(5).toString()));
            ActivityCartReciboColector.Saldo.add(Double.parseDouble(sRows.get(6).toString()));
            ActivityCartReciboColector.rec_tipo.add(strTipo);

        }
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(adapterCart);

        PrettyTime prettyTime = new PrettyTime();
        long timeAgo = Utils.timeStringtoMilis(items.getmFecha());

        holder.img_delete_liq.setVisibility((items.getmStatus().equals("0") ? View.VISIBLE : View.GONE));

        holder.txt_status.setTextColor(context.getResources().getColor(((items.getmStatus().equals("1")) ? R.color.txt_price_color : R.color.grey_40)));

        holder.product_title.setText(items.getmName_Cliente());
        holder.txt_serie.setText((" Serie ").concat(items.getmRuta()));
        holder.product_date.setText(prettyTime.format(new Date(timeAgo)));
        holder.product_autor.setText(items.getmRecibo());
        holder.txt_total_order.setText(items.getmOrderTotal());
        holder.txt_status.setText(("Adjun."));
        holder.txt_comentario.setText(items.getmComentario());
        holder.txt_coment_anulacion.setText(items.getmComment_anul());

        if(items.getmStatus().equals("4")){
            recyclerView.setVisibility(View.GONE);
            holder.txt_status.setVisibility(View.GONE);
            holder.txt_coment_anulacion.setVisibility(View.GONE);
            holder.cardView.setBackgroundColor(context.getResources().getColor(R.color.red_SOFT_light));
            holder.product_title.setText("ANULADO");
            holder.img_adjuntar.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return productListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    productListFiltered = productList;
                } else {
                    List<ItemHistorico> filteredList = new ArrayList<>();
                    for (ItemHistorico row : productList) {
                        if (row.getmName_Cliente().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    productListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = productListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                productListFiltered = (ArrayList<ItemHistorico>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
