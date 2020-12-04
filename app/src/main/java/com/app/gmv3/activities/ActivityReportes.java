package com.app.gmv3.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.app.gmv3.R;
import com.app.gmv3.adapters.AdapterReporteFacturas;
import com.app.gmv3.models.Reporte_Factura;
import com.app.gmv3.utilities.ItemOffsetDecoration;
import com.app.gmv3.utilities.SharedPref;
import com.app.gmv3.utilities.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.app.gmv3.utilities.Constant.POST_RPT_RUTA;

public class ActivityReportes extends AppCompatActivity {

    private Toolbar toolbar;
    TextView txt_desde;
    TextView txt_hasta;
    ProgressDialog progressDialog;
    SharedPref sharedPref;
    private RecyclerView recyclerView;
    private List<Reporte_Factura> productList;
    private AdapterReporteFacturas mAdapter;
    View lyt_empty_history;
    TextView txt_factura_total;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rpt_venta);
        initToolbar();

        txt_desde = findViewById(R.id.id_desde);
        txt_hasta = findViewById(R.id.id_hasta);
        txt_factura_total = findViewById(R.id.id_factura_total);
        lyt_empty_history = findViewById(R.id.lyt_empty_result);
        sharedPref = new SharedPref(this);
        progressDialog = new ProgressDialog(ActivityReportes.this);
        recyclerView = findViewById(R.id.recycler_view);

        productList = new ArrayList<>();
        mAdapter = new AdapterReporteFacturas( productList);
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



        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(),  new ActivityPerfilCliente.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Intent intent = new Intent(ActivityReportes.this, ActivityViewFactura.class);

                        intent.putExtra("factura_id",productList.get(position).getFACTURA());
                        intent.putExtra("factura_date",productList.get(position).getFECHA());

                        startActivity(intent);



                    }
                }, 400);
            }
        }));
        if (Utils.isNetworkAvailable(this)) {
            requestAction();
        } else {
            Toast.makeText(this, getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }


    }

    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ActivityPerfilCliente.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final ActivityPerfilCliente.ClickListener clickListener) {

            this.clickListener = clickListener;

            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
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

                        if (productList.size() > 0) {
                            lyt_empty_history.setVisibility(View.GONE);
                        } else {
                            lyt_empty_history.setVisibility(View.VISIBLE);
                        }
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
        getMenuInflater().inflate(R.menu.main, menu);
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
