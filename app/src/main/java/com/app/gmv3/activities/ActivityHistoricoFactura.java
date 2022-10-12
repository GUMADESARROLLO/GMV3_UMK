package com.app.gmv3.activities;

import static com.app.gmv3.utilities.Constant.POST_HISTORICO_FACTURA;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.app.gmv3.R;
import com.app.gmv3.adapters.AdapterHistoricoFactura;

import com.app.gmv3.models.Factura_Historico;
import com.app.gmv3.utilities.Constant;
import com.app.gmv3.utilities.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ActivityHistoricoFactura extends AppCompatActivity {
    String cod_cliente;

    private AdapterHistoricoFactura mAdapter;
    private RecyclerView recyclerView;
    private List<Factura_Historico> productList;
    TextView txt_factura_total;

    View lyt_empty_history;

    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico_factura);
        initToolbar();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.grey_60), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        cod_cliente = intent.getStringExtra("cod_cliente");
        lyt_empty_history = findViewById(R.id.lyt_empty_result);
        getSupportActionBar().setTitle("Historico de Facturas.");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.grey_5));
        }
        Utils.setSystemBarLight(this);

        txt_factura_total = findViewById(R.id.id_factura_total);




        recyclerView = findViewById(R.id.recycler_view);
        productList = new ArrayList<>();
        mAdapter = new AdapterHistoricoFactura(this, productList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(),  new ActivityPerfilCliente.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Intent intent = new Intent(ActivityHistoricoFactura.this, ActivityViewFactura.class);
                        intent.putExtra("factura_id",productList.get(position).getFACTURA());
                        intent.putExtra("factura_date",productList.get(position).getFECHA());
                        intent.putExtra("cod_cliente",cod_cliente);
                        intent.putExtra("fac_saldo",productList.get(position).getSALDO());
                        intent.putExtra("isRecibo","S");
                        intent.putExtra("isContado","S");
                        startActivity(intent);

                        Log.e("TAG_", "initToolbar:" + cod_cliente );
                    }
                }, 400);
            }
        }));

        if (Utils.isNetworkAvailable(ActivityHistoricoFactura.this)) {

            new MyTaskLoginNormal().execute(cod_cliente);
        }


    }



    private class MyTaskLoginNormal extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ActivityHistoricoFactura.this);
            progressDialog.setTitle(getResources().getString(R.string.title_please_wait));
            progressDialog.setMessage(getResources().getString(R.string.factura_historico_process));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                String strCliente = params[0];


                JsonArrayRequest request = new JsonArrayRequest(POST_HISTORICO_FACTURA + strCliente, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null) {
                            Toast.makeText(getApplicationContext(), R.string.failed_fetch_data, Toast.LENGTH_LONG).show();
                            return;
                        }

                        List<Factura_Historico> items = new Gson().fromJson(response.toString(), new TypeToken<List<Factura_Historico>>() {
                        }.getType());
                        productList.clear();
                        productList.addAll(items);

                        if (productList.size() > 0) {
                            lyt_empty_history.setVisibility(View.GONE);
                            double Order_price = 0;

                            for (int i = 0; i < items.size(); i++) {
                                double Sub_total_price = Double.parseDouble(items.get(i).getMONTO());
                                Order_price += Sub_total_price;


                            }
                            String _Order_price = String.format(Locale.ENGLISH, "%1$,.2f", Order_price);
                            txt_factura_total.setText(("C$ ").concat(_Order_price));
                        } else {
                            lyt_empty_history.setVisibility(View.VISIBLE);
                        }

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

            } catch (Exception e) {
                e.printStackTrace();
            }



            return Utils.getJSONString(params[0]);
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        Utils.changeMenuIconColor(menu, getResources().getColor(R.color.grey_60));

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search_historico_factura).getActionView();
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
        switch (item.getItemId()) {

            case R.id.action_no_facturado:

                Intent intent = new Intent(ActivityHistoricoFactura.this, ActivityNoFacturado.class);


                intent.putExtra("cod_cliente",cod_cliente);

                startActivity(intent);
                return true;

            case R.id.action_facturado:

                Intent ins = new Intent(ActivityHistoricoFactura.this, ActivityUltimos3m.class);

                ins.putExtra("factura_id",cod_cliente);
                ins.putExtra("Name_cliente","");

                startActivity(ins);

                return true;

            case android.R.id.home:
                finish();

                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
