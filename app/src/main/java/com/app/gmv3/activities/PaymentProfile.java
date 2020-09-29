package com.app.gmv3.activities;

import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.app.gmv3.R;
import com.app.gmv3.adapters.RecyclerAdapterNC;
import com.app.gmv3.models.NotasDeCreadito;
import com.app.gmv3.utilities.ItemOffsetDecoration;
import com.app.gmv3.utilities.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import static com.app.gmv3.utilities.Constant.GET_NC;

public class PaymentProfile extends AppCompatActivity {
    String cod_factura,name_text;
    private RecyclerAdapterNC mAdapter;
    private RecyclerView recyclerView;
    private List<NotasDeCreadito> productList;

    TextView txt_nc_name_cliente;
    TextView txt_nc_cod_cliente;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_profile);
        initToolbar();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Utils.setSystemBarColor(this);

        recyclerView = findViewById(R.id.recycler_view);
        productList = new ArrayList<>();

        txt_nc_name_cliente = findViewById(R.id.id_nc_name_cliente);
        txt_nc_cod_cliente = findViewById(R.id.id_nc_cod_cliente);


        mAdapter = new RecyclerAdapterNC(this, productList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        Intent intent = getIntent();
        cod_factura = intent.getStringExtra("factura_id");
        name_text = intent.getStringExtra("Name_cliente");

        fetchData();

        txt_nc_name_cliente.setText(name_text);
        txt_nc_cod_cliente.setText(cod_factura);


    }

    private void fetchData() {
        JsonArrayRequest request = new JsonArrayRequest(GET_NC + cod_factura, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response == null) {
                    Toast.makeText(getApplicationContext(), R.string.failed_fetch_data, Toast.LENGTH_LONG).show();
                    return;
                }

                List<NotasDeCreadito> items = new Gson().fromJson(response.toString(), new TypeToken<List<NotasDeCreadito>>() {
                }.getType());

                productList.clear();
                productList.addAll(items);

                // refreshing recycler view
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_setting, menu);
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
