package com.app.gmv3.adapters;

import static com.app.gmv3.utilities.Constant.DEL_ORDER_VINETA;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
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

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.gmv3.R;
import com.app.gmv3.activities.ActivityCheckoutVineta;
import com.app.gmv3.activities.ActivityHistoryVineta;
import com.app.gmv3.models.ItemHistorico;
import com.app.gmv3.utilities.Utils;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterHistoryVineta extends RecyclerView.Adapter<AdapterHistoryVineta.MyViewHolder> implements Filterable {

    private Context context;
    private List<ItemHistorico> productList;
    private List<ItemHistorico> productListFiltered;



    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView product_title, product_date,product_order_list,product_autor, txt_total_order, txt_status,txt_comentario,txt_beneficiarios,txt_coment_anulacion,txt_serie;
        ImageView img_delete_liq;


        public MyViewHolder(View view) {
            super(view);

            product_title       = view.findViewById(R.id.id_title);
            product_date        = view.findViewById(R.id.id_date);
            product_order_list  = view.findViewById(R.id.id_order_list);
            product_autor       = view.findViewById(R.id.id_autor);
            txt_total_order     = view.findViewById(R.id.id_total_order);
            txt_status          = view.findViewById(R.id.id_status);
            txt_comentario      = view.findViewById(R.id.id_comentario);
            txt_beneficiarios   = view.findViewById(R.id.id_beneficiarios);
            img_delete_liq      = view.findViewById(R.id.id_delete_liq);
            txt_coment_anulacion= view.findViewById(R.id.id_coment_anulacion);
            txt_serie           = view.findViewById(R.id.id_serie);

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
        }
    }

    public void ActionDeleteRequest(final String mID) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(context.getString(R.string.checkout_submit_title));
        progressDialog.setMessage("Procesando... ");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, DEL_ORDER_VINETA, new Response.Listener<String>() {
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
    public AdapterHistoryVineta(Context context, List<ItemHistorico> productList) {
        this.context = context;
        this.productList = productList;
        this.productListFiltered = productList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_vineta, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final ItemHistorico items = productListFiltered.get(position);

        String Lineas = "";

        String[] arrStatus = {"Pendiente","Procesado","Anulado","Eliminado"};

        List<String> sList = Arrays.asList(items.getmOrderList().split("],"));

        for(int i=0;i<sList.size() ; i++){

            List<String> sRows = Arrays.asList(sList.get(i).split(";"));

            Lineas += sRows.get(0).replace("[","").concat(" | ").concat(sRows.get(1)).concat(" | ").concat(sRows.get(2)).concat(" X C$ ").concat(sRows.get(3)).concat(" | C$ ").concat(sRows.get(4)).concat("\n");

        }


        PrettyTime prettyTime = new PrettyTime();
        long timeAgo = Utils.timeStringtoMilis(items.getmFecha());

        holder.img_delete_liq.setVisibility((items.getmStatus().equals("0") ? View.VISIBLE : View.GONE));

        holder.txt_status.setTextColor(context.getResources().getColor(((items.getmStatus().equals("1")) ? R.color.txt_price_color : R.color.grey_40)));
        //holder.txt_status.setTextColor(context.getResources().getColor(((items.getmStatus().equals("2")) ? R.color.red : R.color.grey_40)));


        holder.product_title.setText(items.getmName_Cliente());
        holder.txt_serie.setText((" Serie ").concat(items.getmRuta()));
        holder.product_date.setText(prettyTime.format(new Date(timeAgo)));
        holder.product_order_list.setText(Lineas);
        holder.product_autor.setText(items.getmRecibo());
        holder.txt_total_order.setText(items.getmOrderTotal());
        holder.txt_status.setText(arrStatus[Integer.parseInt(items.getmStatus())]);
        holder.txt_comentario.setText(items.getmComentario());
        holder.txt_beneficiarios.setText(items.getmBenificiario());
        holder.txt_coment_anulacion.setText(items.getmComment_anul());

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
