package com.app.gmv3.activities;

import static com.app.gmv3.utilities.Constant.GET_RECIBOS_COLECTOR;
import static com.app.gmv3.utilities.Constant.POST_ANULAR_RECIBO;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.gmv3.R;
import com.app.gmv3.adapters.AdapterHistoryRecibo;
import com.app.gmv3.models.Factura_Historico;
import com.app.gmv3.models.ItemHistorico;
import com.app.gmv3.utilities.Constant;
import com.app.gmv3.utilities.ItemOffsetDecoration;
import com.app.gmv3.utilities.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.onesignal.OneSignal;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ActivityHistoryRecibos extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<ItemHistorico> productList;
    private AdapterHistoryRecibo mAdapter;
    private SearchView searchView;
    SwipeRefreshLayout swipeRefreshLayout = null;
    private String ruta_id;

    EditText txt_desde;
    EditText txt_hasta;
    TextView txt_factura_total;
    TextView txt_count_recibos;

    String OrderBY = "Desc";
    View ryt_empty_history;
    private static final String[] ANIMATION_TYPE = new String[]{
            "Mas Recientes", "Mas Antiguos"
    };
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_recibo_colector);

        initToolbar();

        initComponent();
        fetchData();
        onRefresh();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Utils.setSystemBarColor(this);

        Intent intent = getIntent();
        ruta_id     = intent.getStringExtra("id_Ruta");

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_post);
        dialog.setCancelable(true);
    }


    private void initComponent() {

        swipeRefreshLayout      = findViewById(R.id.swipeRefreshLayout);
        recyclerView            = findViewById(R.id.recycler_view);
        productList             = new ArrayList<>();
        mAdapter                = new AdapterHistoryRecibo(this, productList);

        txt_desde               = findViewById(R.id.id_rng_desde);
        txt_hasta               = findViewById(R.id.id_hasta);
        txt_factura_total       = findViewById(R.id.id_recibos_total);
        txt_count_recibos       = findViewById(R.id.id_count_recibos);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        ryt_empty_history = findViewById(R.id.lyt_empty_result);

        txt_desde.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogDatePickerLight(1);
            }
        });

        txt_hasta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogDatePickerLight(2);
            }
        });

        Calendar calendar = Calendar.getInstance();
        long date_ship_millis = calendar.getTimeInMillis();
        txt_desde.setText(Utils.getFormattedDateSimple(date_ship_millis));
        txt_hasta.setText(Utils.getFormattedDateSimple(date_ship_millis));


        (findViewById(R.id.id_img_desde)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Validar();
            }
        });
        (findViewById(R.id.id_rng_hasta)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Validar();
            }
        });



    }
    public void Validar(){
        if (txt_hasta.getText().toString().equalsIgnoreCase("") || txt_hasta.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, R.string.checkout_fill_form, Toast.LENGTH_SHORT).show();
        } else {
            fetchData();
        }
    }
    private void dialogDatePickerLight(final int lbl) {
        //Calendar cur_calender = Calendar.getInstance();
        DatePickerDialog datePicker = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        long date_ship_millis = calendar.getTimeInMillis();

                        if (lbl == 1){
                            txt_desde.setText(Utils.getFormattedDateSimple(date_ship_millis));
                        }else {
                            txt_hasta.setText(Utils.getFormattedDateSimple(date_ship_millis));
                        }

                    }
                }
        );
        datePicker.setThemeDark(false);
        datePicker.setAccentColor(getResources().getColor(R.color.colorPrimary));
        datePicker.show(getFragmentManager(), "Datepickerdialog");
    }

    private void onRefresh() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                productList.clear();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (Utils.isNetworkAvailable(ActivityHistoryRecibos.this)) {
                            swipeRefreshLayout.setRefreshing(false);
                            fetchData();
                        } else {
                            swipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                        }

                    }
                }, 1500);
            }
        });
    }

    private void fetchData() {
        
        JsonArrayRequest request = new JsonArrayRequest(GET_RECIBOS_COLECTOR + ruta_id + "&OrderBy=" + OrderBY+ "&Desde=" + txt_desde.getText() + "&Hasta=" + txt_hasta.getText(), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response == null) {
                    Toast.makeText(getApplicationContext(), R.string.failed_fetch_data, Toast.LENGTH_LONG).show();
                    return;
                }

                List<ItemHistorico> items = new Gson().fromJson(response.toString(), new TypeToken<List<ItemHistorico>>() {
                }.getType());

                productList.clear();
                productList.addAll(items);

                double Order_price      = 0;
                double CountRecibo      = 0;
                double RecibosAnulados  = 0;

                if (productList.size() > 0 ){
                    ryt_empty_history.setVisibility(View.GONE);
                    CountRecibo = productList.size();
                }else{
                    ryt_empty_history.setVisibility(View.VISIBLE);
                }

                String _count_recibos = String.format(Locale.ENGLISH, "%1$,.0f", CountRecibo );
                txt_count_recibos.setText(("Total recibido:  ( ").concat(_count_recibos).concat(" )"));


                for (int i = 0; i < items.size(); i++) {
                    double Sub_total_price = Double.parseDouble(items.get(i).getmOrderTotal().replace("C$","").replace(",",""));

                    if (items.get(i).getmStatus().equals("4")){
                        RecibosAnulados++;
                    }

                    Order_price += Sub_total_price;
                }

               // String str_anulados = String.format(Locale.ENGLISH, "%1$,.0f", RecibosAnulados);
               // txt_count_anulados.setText(("Anulados ( ").concat(str_anulados).concat(" )"));

                String _Order_price = String.format(Locale.ENGLISH, "%1$,.2f", Order_price);
                txt_factura_total.setText(("C$ ").concat(_Order_price));

                mAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error in getting json
                Log.e("INFO", "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        MyApplication.getInstance().addToRequestQueue(request);
    }


    public void Show_form_add_num_recibo() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_add_recibo_anular);
        dialog.setCancelable(true);

        final EditText edt_cantidad = dialog.findViewById(R.id.edt_cantidad_vineta);
        edt_cantidad.setHint("Nº Recibo: ");

        final TextView txt_title_frm = dialog.findViewById(R.id.id_title_frm);
        txt_title_frm.setText("Digite Numero de Recibo a Anular");

        final TextView txt_fecha_Recibo = dialog.findViewById(R.id.id_text_recibo);



        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        txt_fecha_Recibo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDatePickerLight(txt_fecha_Recibo);
            }
        });

        (dialog.findViewById(R.id.bt_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        (dialog.findViewById(R.id.bt_add)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getRecibo = edt_cantidad.getText().toString();
                String getFecha  = txt_fecha_Recibo.getText().toString();

                if (getRecibo.equalsIgnoreCase("") || getFecha.equalsIgnoreCase("0000/00/00")){
                    Toast.makeText(getApplicationContext(), "Falta datos por completar", Toast.LENGTH_SHORT).show();

                }else{
                    if (Utils.isNetworkAvailable(ActivityHistoryRecibos.this)) {

                        int txtVal = Integer.parseInt(getRecibo);
                        int len01 = String.valueOf(txtVal).length();
                        getRecibo=  ("00000").substring(0,(5 - len01)).concat(String.valueOf(txtVal));

                        new TaskSendInfoRecibo(getRecibo,getFecha,ruta_id).execute(getRecibo);
                        dialog.dismiss();
                    }
                }




            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void dialogDatePickerLight(final TextView txt_number_recibo) {
        //Calendar cur_calender = Calendar.getInstance();
        DatePickerDialog datePicker = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        long date_ship_millis = calendar.getTimeInMillis();
                        txt_number_recibo.setText(Utils.getFormattedDateSimple(date_ship_millis));

                    }
                }
        );

        datePicker.setThemeDark(false);
        datePicker.setAccentColor(getResources().getColor(R.color.colorPrimary));
        datePicker.show(getFragmentManager(), "Datepickerdialog");
    }
    private class TaskSendInfoRecibo extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        String Recibo;
        String Fecha;
        String Ruta;
        String player_id;

        TaskSendInfoRecibo(String strRecibo, String strFecha, String strRuta) {
            this.Recibo     = strRecibo;
            this.Fecha      = strFecha;
            this.Ruta       = strRuta;
            this.player_id  = OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId();
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ActivityHistoryRecibos.this);
            progressDialog.setTitle(getResources().getString(R.string.title_please_wait));
            progressDialog.setMessage(getResources().getString(R.string.factura_historico_process));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                StringRequest stringRequest = new StringRequest(POST_ANULAR_RECIBO + Recibo + "&Fecha_Recibo=" + Fecha + "&Ruta=" + Ruta + "&Player_Id=" + player_id, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {

                        /*if (response == null) {
                            Toast.makeText(getApplicationContext(), R.string.failed_fetch_data, Toast.LENGTH_LONG).show();
                            return;
                        }*/

                        if (ServerResponse.equals("Nuevo")){

                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    dialogSuccessOrder();
                                }
                            }, 2000);

                        }else  if (ServerResponse.equals("Error")){
                            Toast.makeText(getApplicationContext(), "Ocurrio un Problema, intentelo mas tarde", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                        }else  if (ServerResponse.equals("Existe")){
                            Toast.makeText(getApplicationContext(), "El Numero de Recibo Ya fue Utilizado", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error in getting json
                        Log.e("INFO", "Error: " + error.getMessage());
                        Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                RequestQueue requestQueue = Volley.newRequestQueue(ActivityHistoryRecibos.this);
                requestQueue.add(stringRequest);

            } catch (Exception e) {
                e.printStackTrace();
            }



            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (null != progressDialog && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
            }, Constant.DELAY_PROGRESS_DIALOG);
            super.onPostExecute(result);
        }
    }
    public void dialogSuccessOrder() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_info);
        dialog.setCancelable(true);

        TextView txt_title = dialog.findViewById(R.id.title);
        TextView txt_msg = dialog.findViewById(R.id.content);


        txt_title.setText(" ANULADO ");
        txt_msg.setText( "Su recibo fue anulado correctamente." );

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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_historico_recibo, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       /* if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);*/

        switch (item.getItemId()) {
            case R.id.save_image:

                Show_form_add_num_recibo();


                return true;

            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {



    }




}
