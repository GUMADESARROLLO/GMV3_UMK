    package com.app.gmv3.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
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
import com.app.gmv3.adapters.AdapterPerfilRuta;
import com.app.gmv3.models.FacturasMoras;
import com.app.gmv3.models.Moras;
import com.app.gmv3.models.Stat_Ventas;
import com.app.gmv3.models.Stat_recuperacion;
import com.app.gmv3.utilities.MyDividerItemDecoration;
import com.app.gmv3.utilities.SemiCircleView;
import com.app.gmv3.utilities.SharedPref;
import com.app.gmv3.utilities.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import static com.app.gmv3.utilities.Constant.GET_STAT;
import static com.app.gmv3.utilities.Constant.GET_STAT_ARTI;
import static com.app.gmv3.utilities.Constant.GET_STAT_RECUP;

public class ActivityEstadisticas extends AppCompatActivity {

    SemiCircleView semi_circulo;
    ProgressBar progressBar,progressBar_recup;
    SharedPref sharedPref;
    TextView txt_Meta_venta;
    TextView txt_Meta_venta_real;
    TextView txt_monto_venta;
    TextView txt_monto_venta_real;
    TextView txt_monto_venta_dif;
    TextView txt_meta_faltante;
    TextView txt_progress;
    View lyt_empty_history;
    private SearchView searchView;

    ProgressDialog progressDialog;

    private RecyclerView recyclerView;

    AdapterPerfilRuta rcFacturasMora;
    RelativeLayout relativeLayout;
    String mRuta,mMes,mAnno;
    List<FacturasMoras> Lista_Factura_mora = new ArrayList<>();
    TextView txt_lbl_moth;

    TextView txt_meta_recup,txt_recup_credito,txt_recup_contado,txt_recup_total,txt_lbl_diferencia_recup;



    private static final String[] ANIMATION_TYPE = new String[]{
            "Mas Recientes", "Mas Antiguos", "Vencidas"
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas);
        lyt_empty_history = findViewById(R.id.lyt_empty_result);
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
        progressBar_recup = findViewById(R.id.id_progres_recup);
        txt_Meta_venta = findViewById(R.id.id_Meta_venta);
        txt_Meta_venta_real = findViewById(R.id.id_Meta_venta_real);
        txt_monto_venta = findViewById(R.id.id_monto_meta_venta);
        txt_monto_venta_real = findViewById(R.id.id_monto_meta_venta_real);
        txt_monto_venta_dif = findViewById(R.id.id_monto_meta_venta_dif);
        txt_meta_faltante = findViewById(R.id.id_meta_faltante);
        txt_progress = findViewById(R.id.txt_progress);
        txt_lbl_moth = findViewById(R.id.lbl_moth);

        txt_meta_recup = findViewById(R.id.id_meta_recup);
        txt_recup_credito = findViewById(R.id.id_recup_credito);
        txt_recup_contado = findViewById(R.id.id_recup_contado);
        txt_recup_total = findViewById(R.id.id_recup_total);
        txt_lbl_diferencia_recup = findViewById(R.id.id_lbl_diferencia_recup);


        txt_lbl_moth.setText(Utils.theMonth(rightNow.get(Calendar.MONTH) ).concat(" ").concat(mAnno));

        progressDialog = new ProgressDialog(ActivityEstadisticas.this);


        semi_circulo.startAnim(0);
        progressBar.setProgress(0);

        recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        rcFacturasMora = new AdapterPerfilRuta(this, Lista_Factura_mora);


        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(),  new ActivityPerfilCliente.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(ActivityEstadisticas.this, ActivityViewFactura.class);
                        final FacturasMoras Factura = Lista_Factura_mora.get(position);
                        intent.putExtra("factura_id",Factura.getFactura_id());
                        intent.putExtra("factura_date",Factura.getFactura_date());
                        intent.putExtra("isRecibo","N");
                        startActivity(intent);
                    }
                }, 400);
            }
        }));

        findViewById(R.id.id_filtro_factua_vencida).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFilterFacturas();
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Utils.isNetworkAvailable(ActivityEstadisticas.this)) {
                    progressDialog.setTitle(getString(R.string.checkout_submit_title));
                    progressDialog.setMessage("Buscando Información...");
                    progressDialog.show();
                    fetchData(mRuta,mMes,mAnno);
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }

            }
        }, 500);

    }

    private void DialogFilterFacturas() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ordenar por: ");
        builder.setCancelable(false);
        builder.setSingleChoiceItems(ANIMATION_TYPE, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String selected = ANIMATION_TYPE[i];
                if (selected.equalsIgnoreCase("Mas Recientes")) {
                    Collections.sort(Lista_Factura_mora, new Comparator<FacturasMoras>() {
                        @Override
                        public int compare(FacturasMoras Factura01, FacturasMoras Factura02) {
                            double Date01 = Utils.timeStringtoMilis_Factura(Factura01.getFactura_date());
                            double Date02 = Utils.timeStringtoMilis_Factura(Factura02.getFactura_date());
                            return Double.compare(Date02, Date01);
                        }
                    });
                } else if (selected.equalsIgnoreCase("Mas Antiguos")) {
                    Collections.sort(Lista_Factura_mora, new Comparator<FacturasMoras>() {
                        @Override
                        public int compare(FacturasMoras Factura01, FacturasMoras Factura02) {
                            double Date01 = Utils.timeStringtoMilis_Factura(Factura01.getFactura_date());
                            double Date02 = Utils.timeStringtoMilis_Factura(Factura02.getFactura_date());
                            return Double.compare(Date01, Date02);
                        }
                    });
                } else if (selected.equalsIgnoreCase("Vencidas")) {

                    Collections.sort(Lista_Factura_mora, new Comparator<FacturasMoras>() {
                        @Override
                        public int compare(FacturasMoras Factura01, FacturasMoras Factura02) {
                            double D1 = Double.parseDouble(Factura01.getFactura_devencidos());
                            double D2 = Double.parseDouble(Factura02.getFactura_devencidos());
                            return Double.compare(D2, D1);
                        }
                    });
                }
                rcFacturasMora.notifyDataSetChanged();
                dialogInterface.dismiss();
            }
        });
        builder.show();
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

    public interface ClickListener {
        public void onClick(View view, int position);
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
                rcFacturasMora.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                rcFacturasMora.getFilter().filter(query);
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

        JsonArrayRequest request_02 = new JsonArrayRequest(GET_STAT_ARTI + Ruta , new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response == null) {
                    Toast.makeText(getApplicationContext(), R.string.failed_fetch_data, Toast.LENGTH_LONG).show();
                    return;
                }
                List<Moras> items = new Gson().fromJson(response.toString(), new TypeToken<List<Moras>>() {
                }.getType());

                if (items.size() > 0) {


                    lyt_empty_history.setVisibility(View.GONE);

                    ((TextView) findViewById(R.id.id_perfil_noVencido)).setText(("C$ ").concat(items.get(0).getNoVencidos()));
                    ((TextView) findViewById(R.id.id_saldo_vencido)).setText(("C$ ").concat(items.get(0).getVencido()));
                    ((TextView) findViewById(R.id.id_perfil_d30)).setText(("C$ ").concat(items.get(0).getDias30()));
                    ((TextView) findViewById(R.id.id_perfil_d60)).setText(("C$ ").concat(items.get(0).getDias60()));
                    ((TextView) findViewById(R.id.id_perfil_d90)).setText(("C$ ").concat(items.get(0).getDias90()));
                    ((TextView) findViewById(R.id.id_perfil_d120)).setText(("C$ ").concat(items.get(0).getDias120()));
                    ((TextView) findViewById(R.id.id_perfil_m120)).setText(("C$ ").concat(items.get(0).getMas120()));



                    List<String> data = Arrays.asList(items.get(0).getFACT_PEND().split(","));

                    ((TextView) findViewById(R.id.id_coun_facturas_vencidas)).setText(("FACTURAS (").concat( data.size() + ")"));

                    for (int i = 0; i < data.size(); i++) {


                        List<String> row = Arrays.asList(data.get(i).split(":"));

                        FacturasMoras tmp = new FacturasMoras();
                        tmp.setFactura_devencidos(row.get(0));
                        tmp.setFactura_cliente(row.get(1));
                        tmp.setFactura_nombre(row.get(2));
                        tmp.setFactura_id(row.get(3));
                        tmp.setFactura_cant(row.get(4));
                        tmp.setFactura_date(row.get(5));
                        tmp.setFactura_monto(row.get(6));

                        Log.e("TAG_error", "onResponse: "+ Lista_Factura_mora );
                        //Lista_Factura_mora.add(tmp);



                    }
                    recyclerView.setAdapter(rcFacturasMora);
                } else {
                    lyt_empty_history.setVisibility(View.VISIBLE);
                }

                progressDialog.dismiss();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error in getting json
                Log.e("INFO_eRR", "Error: " + error.getMessage());
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
                List<Stat_Ventas> items = new Gson().fromJson(response.toString(), new TypeToken<List<Stat_Ventas>>() {
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

        JsonArrayRequest request_03 = new JsonArrayRequest(GET_STAT_RECUP + Ruta + "&sMes=" + Mes + "&sAnno=" + anno, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response == null) {
                    Toast.makeText(getApplicationContext(), R.string.failed_fetch_data, Toast.LENGTH_LONG).show();
                    return;
                }
                List<Stat_recuperacion> data = new Gson().fromJson(response.toString(), new TypeToken<List<Stat_recuperacion>>() {
                }.getType());

                double meta_recup = Double.parseDouble(data.get(0).getMeta_Recuperacion());
                double recup_credito = Double.parseDouble(data.get(0).getRecup_Credito());
                double recup_contado = Double.parseDouble(data.get(0).getRecup_Contado());


                double faltante_meta = Double.parseDouble(data.get(0).getRecup_Total());


                String txt01 = String.format(Locale.ENGLISH, "%1$,.2f", meta_recup);
                String txt02 = String.format(Locale.ENGLISH, "%1$,.2f", recup_credito);
                String txt03 = String.format(Locale.ENGLISH, "%1$,.2f", recup_contado);
                String txt04 = String.format(Locale.ENGLISH, "%1$,.2f", faltante_meta);

                txt_meta_recup.setText(("C$ ").concat(txt01));
                txt_recup_credito.setText(("C$ ").concat(txt02));
                txt_recup_contado.setText(("C$ ").concat(txt03));
                txt_recup_total.setText(("C$ ").concat(txt04));

                txt_lbl_diferencia_recup.setText(data.get(0).getRecup_cumple().concat(" %"));


                progressBar_recup.setProgress((int) Double.parseDouble(data.get(0).getRecup_cumple()));





            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error in getting json
                Log.e("INFO", "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        MyApplication.getInstance().addToRequestQueue(request_03);




    }
}