package com.app.ecommerce.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.ecommerce.Config;
import com.app.ecommerce.R;
import com.app.ecommerce.adapters.RecyclerAdapterClients;
import com.app.ecommerce.adapters.RecyclerAdapterLast3M;
import com.app.ecommerce.adapters.RecyclerAdapterPerfilLotes;
import com.app.ecommerce.adapters.RecyclerAdapterRptFacturas;
import com.app.ecommerce.models.Reporte_Factura;
import com.app.ecommerce.utilities.ItemOffsetDecoration;
import com.app.ecommerce.utilities.MyDividerItemDecoration;
import com.app.ecommerce.utilities.SharedPref;
import com.app.ecommerce.utilities.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.onesignal.OneSignal;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.app.ecommerce.utilities.Constant.POST_RPT_RUTA;

public class ActivityReportes extends AppCompatActivity {

    private Toolbar toolbar;
    TextView txt_desde;
    TextView txt_hasta;
    ProgressDialog progressDialog;
    SharedPref sharedPref;
    private RecyclerView recyclerView;
    private List<Reporte_Factura> productList;
    private RecyclerAdapterRptFacturas mAdapter;

    TextView txt_factura_total;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rpt_venta);
        initToolbar();

        txt_desde = findViewById(R.id.id_desde);
        txt_hasta = findViewById(R.id.id_hasta);
        txt_factura_total = findViewById(R.id.id_factura_total);

        sharedPref = new SharedPref(this);
        progressDialog = new ProgressDialog(ActivityReportes.this);
        recyclerView = findViewById(R.id.recycler_view);

        productList = new ArrayList<>();
        mAdapter = new RecyclerAdapterRptFacturas( productList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);


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

        findViewById(R.id.id_img_desde).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Validar();
            }
        });
        findViewById(R.id.id_img_hasta).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Validar();
            }
        });

        Calendar calendar = Calendar.getInstance();
        long date_ship_millis = calendar.getTimeInMillis();
        txt_desde.setText(Utils.getFormattedDateSimple(date_ship_millis));
        txt_hasta.setText(Utils.getFormattedDateSimple(date_ship_millis));
        requestAction();



    }

    public void Validar(){
        if (txt_hasta.getText().toString().equalsIgnoreCase("") || txt_hasta.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, R.string.checkout_fill_form, Toast.LENGTH_SHORT).show();
        } else {
            requestAction();
        }
    }



    public void requestAction() {

        progressDialog.setTitle(getString(R.string.checkout_submit_title));
        progressDialog.setMessage("Buscando Registros, espere un momento");
        progressDialog.show();





        JsonArrayRequest stringRequest = new JsonArrayRequest( POST_RPT_RUTA + sharedPref.getYourName() + "&desde=" + txt_desde.getText().toString() + "&hasta=" + txt_hasta.getText().toString(), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(final JSONArray ServerResponse) {

                final List<Reporte_Factura> items = new Gson().fromJson(ServerResponse.toString(), new TypeToken<List<Reporte_Factura>>() {
                }.getType());



               final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        productList.clear();
                        productList.addAll(items);
                        mAdapter.notifyDataSetChanged();



                    progressDialog.dismiss();
                    }
                }, 2000);

                double Order_price = 0;

                for (int i = 0; i < items.size(); i++) {
                    double Sub_total_price = Double.parseDouble(items.get(i).getMONTO());
                    Order_price += Sub_total_price;
                }
                String _Order_price = String.format(Locale.ENGLISH, "%1$,.2f", Order_price);
                txt_factura_total.setText(("C$ ").concat(_Order_price));

                }
            },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), volleyError.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {

        };

        RequestQueue requestQueue = Volley.newRequestQueue(ActivityReportes.this);
        requestQueue.add(stringRequest);
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
        //set dark light
        datePicker.setThemeDark(false);
        datePicker.setAccentColor(getResources().getColor(R.color.colorPrimary));
        datePicker.show(getFragmentManager(), "Datepickerdialog");
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);
        Utils.setBarColor(this, R.color.available);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        Utils.changeMenuIconColor(menu, getResources().getColor(android.R.color.white));
        Utils.changeOverflowMenuIconColor(toolbar, getResources().getColor(android.R.color.white));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
