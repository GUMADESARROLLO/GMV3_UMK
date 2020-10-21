package com.app.gmv3.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.gmv3.adapters.RecyclerAdapterInteligenciaMercado;
import com.app.gmv3.models.Comentarios;
import com.app.gmv3.utilities.ItemOffsetDecoration;
import com.app.gmv3.utilities.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.app.gmv3.utilities.Constant.GET_COMENTARIOS_IM;
import static com.app.gmv3.utilities.Constant.POST_REPORT;

import com.app.gmv3.R;

public class ActivityInteligenciaMercado extends AppCompatActivity implements RecyclerAdapterInteligenciaMercado.ContactsAdapterListener{

    private RecyclerView recyclerView;
    private List<Comentarios> productList;
    private RecyclerAdapterInteligenciaMercado mAdapter;
    private SearchView searchView;
    SwipeRefreshLayout swipeRefreshLayout = null;
    private String category_id,category_name;
    String st_title,st_comment;
    ProgressDialog progressDialog;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String date = dateFormat.format(Calendar.getInstance().getTime());
    TextView txt_lbl_order_by;
    String OrderBY = "Desc";
    RelativeLayout ryt_empty_history;

    private static final String[] ANIMATION_TYPE = new String[]{
            "Mas Recientes", "Mas Antiguos"
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inteligencia_mercado);

        initToolbar();

        initComponent();
        fetchData();
        onRefresh();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Inteligencia de Mercado");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Utils.setSystemBarColor(this);

        Intent intent = getIntent();
        category_id = intent.getStringExtra("id_Ruta");
        category_name = intent.getStringExtra("Nombre_ruta");
    }


    private void initComponent() {
        progressDialog = new ProgressDialog(ActivityInteligenciaMercado.this);
        ((FloatingActionButton) findViewById(R.id.fab_add)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomDialog();
            }
        });
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        productList = new ArrayList<>();
        mAdapter = new RecyclerAdapterInteligenciaMercado(this, productList, this);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        ryt_empty_history = (RelativeLayout) findViewById(R.id.id_no_feed);

        txt_lbl_order_by = (TextView) findViewById(R.id.txt_lbl_order_by);

        ((ImageButton) findViewById(R.id.bt_toggle_text)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSingleChoiceDialog();
            }
        });
        txt_lbl_order_by.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSingleChoiceDialog();
            }
        });
    }

    private void showSingleChoiceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ordenar por: ");
        builder.setCancelable(false);
        builder.setSingleChoiceItems(ANIMATION_TYPE, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String selected = ANIMATION_TYPE[i];
                if (selected.equalsIgnoreCase("Mas Recientes")) {
                    txt_lbl_order_by.setText( ANIMATION_TYPE[i]);
                    OrderBY = "Desc";
                } else if (selected.equalsIgnoreCase("Mas Antiguos")) {
                    txt_lbl_order_by.setText( ANIMATION_TYPE[i]);
                    OrderBY = "Asc";
                }
                fetchData();
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }
    private void onRefresh() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                productList.clear();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (Utils.isNetworkAvailable(ActivityInteligenciaMercado.this)) {
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
        JsonArrayRequest request = new JsonArrayRequest(GET_COMENTARIOS_IM + category_id + "&OrderBy=" + OrderBY, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response == null) {
                    Toast.makeText(getApplicationContext(), R.string.failed_fetch_data, Toast.LENGTH_LONG).show();
                    return;
                }

                List<Comentarios> items = new Gson().fromJson(response.toString(), new TypeToken<List<Comentarios>>() {
                }.getType());

                // adding contacts to contacts list
                productList.clear();
                productList.addAll(items);

                if (productList.size() > 0 ){
                    ryt_empty_history.setVisibility(View.GONE);
                }else{
                    ryt_empty_history.setVisibility(View.VISIBLE);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }


    private void showCustomDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_post);
        dialog.setCancelable(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        final String Fecha = (String) DateFormat.format("EEE dd MMM yyyy hh:mm aaa'", Calendar.getInstance().getTime());

        final AppCompatButton bt_submit = (AppCompatButton) dialog.findViewById(R.id.bt_submit);

        final TextView edt_title = (TextView) dialog.findViewById(R.id.edt_title);
        final TextView et_post = (TextView) dialog.findViewById(R.id.et_post);

        ((TextView) dialog.findViewById(R.id.edt_Nombre)).setText(category_name);
        ((TextView) dialog.findViewById(R.id.edt_ruta)).setText(category_id);
        ((TextView) dialog.findViewById(R.id.lbl_date)).setText(Fecha);


        ((EditText) dialog.findViewById(R.id.et_post)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                bt_submit.setEnabled(!s.toString().trim().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                st_title = edt_title.getText().toString();
                st_comment = et_post.getText().toString();

                if (st_title.equalsIgnoreCase("") ||st_comment.equalsIgnoreCase("")) {
                    Snackbar.make(view, R.string.checkout_fill_form, Snackbar.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityInteligenciaMercado.this);
                    builder.setTitle(R.string.post_dialog_title);
                    builder.setMessage(R.string.post_dialog_msg);
                    builder.setCancelable(false);
                    builder.setPositiveButton(getResources().getString(R.string.dialog_option_yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestAction();
                        }
                    });
                    builder.setNegativeButton(getResources().getString(R.string.dialog_option_no), null);
                    builder.setCancelable(false);
                    builder.show();
                }
            }
        });



        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
    public void dialogSuccessOrder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.post_success_title);
        builder.setMessage(R.string.post_success_msg);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.checkout_option_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                finish();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
    public void requestAction() {

        progressDialog.setTitle(getString(R.string.post_submit_title));
        progressDialog.setMessage(getString(R.string.post_submit_msg));
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, POST_REPORT, new Response.Listener<String>() {
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
                        Toast.makeText(getApplicationContext(), volleyError.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("sndFecha", date);
                params.put("sndTitulo", st_title);
                params.put("sndCodigo", category_id);
                params.put("sndNombre", category_name);
                params.put("snd_comentario", st_comment);
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(ActivityInteligenciaMercado.this);
        requestQueue.add(stringRequest);
    }
    @Override
    public void onContactSelected(Comentarios comentarios) {
       /* Intent intent = new Intent(getApplicationContext(), ActivityProductDetail.class);
        intent.putExtra("product_id", product.getProduct_id());
        intent.putExtra("title", product.getProduct_name());
        intent.putExtra("image", product.getProduct_image());
        intent.putExtra("product_price", product.getProduct_price());
        intent.putExtra("product_description", product.getProduct_description());
        intent.putExtra("product_quantity", product.getProduct_quantity());
        intent.putExtra("product_status", product.getProduct_status());
        intent.putExtra("currency_code", product.getCurrency_code());
        intent.putExtra("category_name", product.getCategory_name());
        startActivity(intent);*/
    }


}
