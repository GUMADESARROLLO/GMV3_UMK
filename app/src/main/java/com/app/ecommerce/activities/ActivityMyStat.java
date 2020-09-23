package com.app.ecommerce.activities;

import android.app.ProgressDialog;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.app.ecommerce.R;
import com.app.ecommerce.adapters.RecyclerAdapterProductStat;
import com.app.ecommerce.models.Venta_Articulos;
import com.app.ecommerce.models.Ventas_stat;
import com.app.ecommerce.utilities.ItemOffsetDecoration;
import com.app.ecommerce.utilities.SemiCircleView;
import com.app.ecommerce.utilities.SharedPref;
import com.app.ecommerce.utilities.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.app.ecommerce.utilities.Constant.GET_STAT;
import static com.app.ecommerce.utilities.Constant.GET_STAT_ARTI;

public class ActivityMyStat extends AppCompatActivity {

    SemiCircleView semi_circulo;
    ProgressBar progressBar;
    SharedPref sharedPref;
    TextView txt_Meta_venta;
    TextView txt_Meta_venta_real;
    TextView txt_monto_venta;
    TextView txt_monto_venta_real;
    TextView txt_monto_venta_dif;
    TextView txt_meta_faltante;
    TextView txt_progress;
    EditText btn_buscar;

    ProgressDialog progressDialog;

    private RecyclerView recyclerView;
    private List<Venta_Articulos> productList;
    private RecyclerAdapterProductStat mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_stat);

        semi_circulo = findViewById(R.id.id_semi_circulo);
        progressBar = findViewById(R.id.id_progressbar);
        txt_Meta_venta = findViewById(R.id.id_Meta_venta);
        txt_Meta_venta_real = findViewById(R.id.id_Meta_venta_real);

        txt_monto_venta = findViewById(R.id.id_monto_meta_venta);
        txt_monto_venta_real = findViewById(R.id.id_monto_meta_venta_real);
        txt_monto_venta_dif = findViewById(R.id.id_monto_meta_venta_dif);
        txt_meta_faltante = findViewById(R.id.id_meta_faltante);
        txt_progress = findViewById(R.id.txt_progress);
        btn_buscar = findViewById(R.id.bt_exp_date);

        progressDialog = new ProgressDialog(ActivityMyStat.this);

        sharedPref = new SharedPref(this);
        semi_circulo.startAnim(0);
        progressBar.setProgress(0);

        recyclerView = findViewById(R.id.recycler_view);
        productList = new ArrayList<>();
        mAdapter = new RecyclerAdapterProductStat( productList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);


        btn_buscar.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e("INFO", "Error: " + s.toString());
                mAdapter.getFilter().filter(s.toString());
            }
        });


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Utils.isNetworkAvailable(ActivityMyStat.this)) {
                    progressDialog.setTitle(getString(R.string.checkout_submit_title));
                    progressDialog.setMessage("Buscando Información...");
                    progressDialog.show();
                    fetchData();
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }

            }
        }, 1500);

    }

    private void fetchData() {


        JsonArrayRequest request = new JsonArrayRequest(GET_STAT + sharedPref.getYourName(), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response == null) {
                    Toast.makeText(getApplicationContext(), R.string.failed_fetch_data, Toast.LENGTH_LONG).show();
                    return;
                }
                List<Ventas_stat> items = new Gson().fromJson(response.toString(), new TypeToken<List<Ventas_stat>>() {
                }.getType());

                    double Meta = Double.parseDouble(items.get(0).getmMetaVenta());
                    double Real = Double.parseDouble(items.get(0).getmVentaReal());


                    double faltante_meta = Meta - Real;


                    String txt01 = String.format(Locale.ENGLISH, "%1$,.2f", Meta);
                    String txt02 = String.format(Locale.ENGLISH, "%1$,.2f", Real);

                    String txt03 = String.format(Locale.ENGLISH, "%1$,.2f", Double.parseDouble(items.get(0).getmVntCanti()));
                    String txt04 = String.format(Locale.ENGLISH, "%1$,.2f", Double.parseDouble(items.get(0).getmVntCantiReal()));
                    String txt05 = String.format(Locale.ENGLISH, "%1$,.2f", faltante_meta);

                    txt_progress.setText(items.get(0).getmVntCantiDif().concat(" %"));
                    txt_Meta_venta.setText(("C$ ").concat(txt01));
                    txt_Meta_venta_real.setText(("C$ ").concat(txt02));

                    txt_monto_venta.setText(("C$ ").concat(txt03));
                    txt_monto_venta_real.setText(("C$ ").concat(txt04));
                    txt_monto_venta_dif.setText(items.get(0).getmVntCantiDif());

                    txt_meta_faltante.setText(("C$ ").concat(txt05).concat("\n").concat(" en ").concat(items.get(0).getmVentaDif().concat(" %")));

                    semi_circulo.startAnim(((int) Double.parseDouble(items.get(0).getmVentaDif())));

                    progressBar.setProgress((int) Double.parseDouble(items.get(0).getmVntCantiDif()));


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

        JsonArrayRequest request_02 = new JsonArrayRequest(GET_STAT_ARTI + sharedPref.getYourName(), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response == null) {
                    Toast.makeText(getApplicationContext(), R.string.failed_fetch_data, Toast.LENGTH_LONG).show();
                    return;
                }
                List<Venta_Articulos> items = new Gson().fromJson(response.toString(), new TypeToken<List<Venta_Articulos>>() {
                }.getType());

                productList.clear();
                productList.addAll(items);

                // refreshing recycler view
                mAdapter.notifyDataSetChanged();
                progressDialog.dismiss();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error in getting json
                Log.e("INFO", "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

        MyApplication.getInstance().addToRequestQueue(request_02);


    }
}