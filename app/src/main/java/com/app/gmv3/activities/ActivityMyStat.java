package com.app.gmv3.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.os.Handler;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.app.gmv3.R;
import com.app.gmv3.adapters.RecyclerAdapterProductStat;
import com.app.gmv3.models.Venta_Articulos;
import com.app.gmv3.models.Ventas_stat;
import com.app.gmv3.utilities.ItemOffsetDecoration;
import com.app.gmv3.utilities.SemiCircleView;
import com.app.gmv3.utilities.SharedPref;
import com.app.gmv3.utilities.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.app.gmv3.utilities.Constant.GET_STAT;
import static com.app.gmv3.utilities.Constant.GET_STAT_ARTI;

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

    private SearchView searchView;

    ProgressDialog progressDialog;

    SwipeRefreshLayout swipeRefreshLayout = null;
    private RecyclerView recyclerView;
    private List<Venta_Articulos> productList;
    private List<Venta_Articulos> productListFiltered;
    private RecyclerAdapterProductStat mAdapter;
    RelativeLayout relativeLayout;
    String mRuta,mMes,mAnno;

    TextView txt_lbl_moth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_stat);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedPref = new SharedPref(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("MIS ESTADISTICAS");
        Calendar rightNow = Calendar.getInstance();

        mRuta = sharedPref.getYourName();
        mMes = String.valueOf(rightNow.get(Calendar.MONTH) + 1);
        mAnno = String.valueOf(rightNow.get(Calendar.YEAR));


        semi_circulo = findViewById(R.id.id_semi_circulo);
        progressBar = findViewById(R.id.id_progressbar);
        txt_Meta_venta = findViewById(R.id.id_Meta_venta);
        txt_Meta_venta_real = findViewById(R.id.id_Meta_venta_real);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        txt_monto_venta = findViewById(R.id.id_monto_meta_venta);
        txt_monto_venta_real = findViewById(R.id.id_monto_meta_venta_real);
        txt_monto_venta_dif = findViewById(R.id.id_monto_meta_venta_dif);
        txt_meta_faltante = findViewById(R.id.id_meta_faltante);
        txt_progress = findViewById(R.id.txt_progress);
        relativeLayout = findViewById(R.id.rlayresulta);
        txt_lbl_moth = findViewById(R.id.lbl_moth);

        txt_lbl_moth.setText(Utils.theMonth(rightNow.get(Calendar.MONTH) + 1).concat(" ").concat(mAnno));

        progressDialog = new ProgressDialog(ActivityMyStat.this);


        semi_circulo.startAnim(0);
        progressBar.setProgress(0);

        recyclerView = findViewById(R.id.recycler_view);
        productList = new ArrayList<>();
        mAdapter = new RecyclerAdapterProductStat(this, productList);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.addItemDecoration(new ItemOffsetDecoration(this, R.dimen.item_offset));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);






        if (Utils.isNetworkAvailable(ActivityMyStat.this)) {
            fetchData(mRuta,mMes,mAnno);
            onRefresh();
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }

        /*new Handler().postDelayed(new Runnable() {
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
        }, 1500);*/

    }



    private void onRefresh() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                productList.clear();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (Utils.isNetworkAvailable(ActivityMyStat.this)) {
                            swipeRefreshLayout.setRefreshing(false);
                            progressDialog.setTitle(getString(R.string.checkout_submit_title));
                            progressDialog.setMessage("Buscando Información...");
                            progressDialog.show();
                            fetchData(mRuta,mMes,mAnno);
                        } else {
                            swipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                        }

                    }
                }, 1500);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_stat, menu);

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
            case R.id.item_confi:
                showCustomDialog();
                break;


            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }



    private void showCustomDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_review);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final Spinner spn_meses = dialog.findViewById(R.id.spn_meses);
        final Spinner spn_anno = dialog.findViewById(R.id.spn_anno);

        ArrayAdapter<String> array_meses = new ArrayAdapter<>(
                this,
                R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.Meses));
        array_meses.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spn_meses.setAdapter(array_meses);
        spn_meses.setSelection(0);

        ArrayAdapter<String> array_anno = new ArrayAdapter<>(
                this,
                R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.anno));
        array_anno.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spn_anno.setAdapter(array_anno);
        spn_anno.setSelection(0);


        ((Button) dialog.findViewById(R.id.bt_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        ((Button) dialog.findViewById(R.id.bt_submit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strMes      = String.valueOf( spn_meses.getSelectedItemPosition() + 1);
                String StrAnnio    = spn_anno.getSelectedItem().toString();

                txt_lbl_moth.setText(spn_meses.getSelectedItem().toString().concat(" ").concat(StrAnnio));

                fetchData(mRuta,strMes,StrAnnio);

                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void fetchData(String Ruta,String Mes,String anno) {

        JsonArrayRequest request_02 = new JsonArrayRequest(GET_STAT_ARTI + Ruta + "&sMes=" + Mes + "&sAnno=" + anno, new Response.Listener<JSONArray>() {
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
                relativeLayout.setVisibility(View.GONE);


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


        JsonArrayRequest request = new JsonArrayRequest(GET_STAT + Ruta + "&sMes=" + Mes + "&sAnno=" + anno, new Response.Listener<JSONArray>() {
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

                    int porcentaje = ((((int) Double.parseDouble(items.get(0).getmVentaDif()))) * (180)) /100 ;

                    semi_circulo.startAnim(porcentaje);

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




    }
}