package com.app.gmv3.activities;

import static com.app.gmv3.utilities.Constant.GET_RECENT_PRODUCT;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.app.gmv3.R;
import com.app.gmv3.adapters.AdapterProduct;
import com.app.gmv3.models.Product;
import com.app.gmv3.utilities.ItemOffsetDecoration;
import com.app.gmv3.utilities.SharedPref;
import com.app.gmv3.utilities.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActivityArticulos extends AppCompatActivity implements AdapterProduct.ContactsAdapterListener {
    SharedPref sharedPref;
    List<Product> Articulos;
    AdapterProduct mAdapter;
    RecyclerView rcvArticulos;
    View lyt_empty;
    SwipeRefreshLayout RefreshArticulos = null;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articulos);
        RefreshArticulos = findViewById(R.id.RefreshArticulos);
        RefreshArticulos.setRefreshing(true);

        Toolbar toolbar = findViewById(R.id.tb_articulos);
        setSupportActionBar(toolbar);

        sharedPref = new SharedPref(this);
        Articulos = new ArrayList<>();
        mAdapter = new AdapterProduct(getApplicationContext(), Articulos, this);
        lyt_empty = findViewById(R.id.tb_articulos);

        rcvArticulos = findViewById(R.id.rcv_Articulos);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
        rcvArticulos.setLayoutManager(mLayoutManager);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getApplicationContext(), R.dimen.item_offset);
        rcvArticulos.addItemDecoration(itemDecoration);
        rcvArticulos.setItemAnimator(new DefaultItemAnimator());
        rcvArticulos.setAdapter(mAdapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("ARTICULOS");

        //onRefresh();
        getArticulos();
    }

    private void onRefresh() {
        RefreshArticulos.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Articulos.clear();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (Utils.isNetworkAvailable((Activity) getApplicationContext())) {
                            RefreshArticulos.setRefreshing(false);
                            getArticulos();
                        } else {
                            RefreshArticulos.setRefreshing(false);
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                        }

                    }
                }, 1500);
            }
        });
    }

    private void getArticulos() {
        final String[] RutaAsignada = new String[1];
        JsonArrayRequest request = new JsonArrayRequest(GET_RECENT_PRODUCT + sharedPref.getYourName(), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response == null) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.failed_fetch_data), Toast.LENGTH_LONG).show();
                    return;
                }

                List<Product> items = new Gson().fromJson(response.toString(), new TypeToken<List<Product>>() {
                }.getType());

                // adding contacts to contacts list



                Articulos.clear();
                Articulos.addAll(items);

                if (Articulos.size() > 0) {
                    List<String> sVinneta = Arrays.asList(items.get(0).getISPROMO().split(":"));
                    RutaAsignada[0] = sVinneta.get(2);


                    sharedPref.setPathAssigned(RutaAsignada[0]);
                    // ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("ARTICULOS ( "+ sharedPref.getPathAssigned() +" )");


                } else {
                }

                mAdapter.notifyDataSetChanged();

                RefreshArticulos.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("INFO", "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                RefreshArticulos.setRefreshing(false);
            }
        });

        MyApplication.getInstance().addToRequestQueue(request);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }
    public void onContactSelected(Product product){
        Intent intent = new Intent(getApplicationContext(), ActivityProductDetail.class);

        intent.putExtra("product_id", product.getProduct_id());
        intent.putExtra("title", product.getProduct_name());
        intent.putExtra("image", product.getProduct_image());
        intent.putExtra("product_price", product.getProduct_price());
        intent.putExtra("product_description", product.getProduct_description());
        intent.putExtra("product_quantity", product.getProduct_quantity());
        intent.putExtra("product_status", product.getProduct_status());
        intent.putExtra("currency_code", product.getCurrency_code());
        intent.putExtra("category_name", product.getCategory_name());
        intent.putExtra("product_bonificado", product.getProduct_bonificado());
        intent.putExtra("product_lotes", product.getProduct_lotes());
        intent.putExtra("product_und", product.getProduct_und());
        intent.putExtra("Facturable",true);




        intent.putExtra("tax", 0);
        intent.putExtra("currency_code", "NIO");
        intent.putExtra("total_price", 900000000);

        startActivity(intent);
    }
}