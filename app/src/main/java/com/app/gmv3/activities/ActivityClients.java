package com.app.gmv3.activities;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.app.gmv3.Config;
import com.app.gmv3.R;
import com.app.gmv3.adapters.RecyclerAdapterClients;
import com.app.gmv3.models.Clients;
import com.app.gmv3.utilities.ItemOffsetDecoration;
import com.app.gmv3.utilities.SharedPref;
import com.app.gmv3.utilities.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import static com.app.gmv3.utilities.Constant.GET_CLIENTS;

public class ActivityClients extends AppCompatActivity implements RecyclerAdapterClients.ContactsAdapterListener {

    private RecyclerView recyclerView;
    private List<Clients> productList;
    private RecyclerAdapterClients mAdapter;
    private SearchView searchView;
    SharedPref sharedPref;
    double str_tax,total_price;
    String str_currency_code;
    SwipeRefreshLayout swipeRefreshLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clients);
        sharedPref = new SharedPref(this);
        if (Config.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        str_tax = intent.getDoubleExtra("tax", 0);
        str_currency_code = intent.getStringExtra("currency_code");
        total_price = intent.getDoubleExtra("total_price", 0);

        // toolbar fancy stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("CLIENTES");

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        recyclerView = findViewById(R.id.recycler_view);
        productList = new ArrayList<>();
        mAdapter = new RecyclerAdapterClients(this, productList, this);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        fetchData();
        onRefresh();

    }

    private void onRefresh() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                productList.clear();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (Utils.isNetworkAvailable(ActivityClients.this)) {
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

        JsonArrayRequest request = new JsonArrayRequest(GET_CLIENTS + sharedPref.getYourName(), new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null) {
                            Toast.makeText(getApplicationContext(), R.string.failed_fetch_data, Toast.LENGTH_LONG).show();
                            return;
                        }

                        List<Clients> items = new Gson().fromJson(response.toString(), new TypeToken<List<Clients>>() {
                        }.getType());

                        // adding contacts to contacts list
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
        getMenuInflater().inflate(R.menu.search, menu);

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

    @Override
    public void onContactSelected(Clients cls) {
        Intent intent = new Intent(ActivityClients.this, ActivityCheckout.class);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        intent.putExtra("cliente_codigo", cls.getCLIENTE());
        intent.putExtra("cliente_nombre", cls.getNOMBRE());
        intent.putExtra("cliente_direcc", cls.getDIRECCION());

        intent.putExtra("tax", str_tax);
        intent.putExtra("currency_code", str_currency_code);

        try {
            if (cls.getMOROSO().equals("S")){

                builder.setTitle("Alerta!")
                        .setMessage("Cliente en Estado de Morosidad, no se le permite crear pedido.")
                        .create()
                        .show();

            }else if(total_price <= Double.parseDouble(cls.getDIPONIBLE().replaceAll(",","") )  ){
                startActivity(intent);

            }else{
                builder.setTitle("Alerta!")
                        .setMessage("Pedido Exede el limite Disponible")
                        .create()
                        .show();
            }
        } catch (Exception e) {

        }
    }

}
