package com.app.gmv3.activities;

import static com.app.gmv3.utilities.Constant.GET_COMISION;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.app.gmv3.R;
import com.app.gmv3.utilities.Constant;
import com.app.gmv3.utilities.SharedPref;
import com.app.gmv3.utilities.Utils;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.android.material.tabs.TabLayout;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class ComisionActivity extends AppCompatActivity {
    PieChart chart;
    String RUTA;
    SharedPref sharedPref;

    TextView total_comision;
    TextView comision_bono;
    TextView txtSKUs;
    TextView txtValor;
    TextView txtFactor;
    TextView txtComisio;
    TextView Comision;
    TextView ItemFact;

    TextView cliente_promedio;
    TextView cliente_meta;
    TextView clientes_faturados;
    TextView cliente_bono;
    TextView cliente_prom;
    TextView txt_salario_base;

    String SKU_Lista_80,SKU_Lista_20;
    String TAB_lista80_valor,TAB_lista20_valor;
    String TAB_lista80_fact,TAB_lista20_fact;
    String TAB_lista80_comi,TAB_lista20_comi;

    int nMonth;
    int nYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comision);
        chart = findViewById(R.id.chart1);
        iniComponent();

        sharedPref = new SharedPref(this);
        RUTA = sharedPref.getYourName();

        if (Utils.isNetworkAvailable(this)) {

            new MyTaskLoginNormal().execute(RUTA);
        }
    }
    private void iniComponent() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        sharedPref = new SharedPref(this);

        getSupportActionBar().setTitle(sharedPref.getYourName().concat(" - ").concat(sharedPref.getYourAddress()));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        cliente_promedio    = findViewById(R.id.id_cliente_promedio);
        cliente_meta        = findViewById(R.id.id_cliente_meta);
        clientes_faturados  = findViewById(R.id.id_clientes_faturados);
        cliente_bono        = findViewById(R.id.id_cliente_bono);
        cliente_prom        = findViewById(R.id.id_cliente_prom);

        comision_bono       = findViewById(R.id.id_comision_bono);
        total_comision      = findViewById(R.id.id_total_commision);
        txtSKUs             = findViewById(R.id.id_skus);
        txtValor            = findViewById(R.id.id_valor);
        txtFactor           = findViewById(R.id.id_factor);
        txtComisio          = findViewById(R.id.id_comision);
        Comision            = findViewById(R.id.Comisiones);
        ItemFact            = findViewById(R.id.ItemFact);
        txt_salario_base    = findViewById(R.id.id_txt_salario_base);

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        nYear = calendar.get(Calendar.YEAR);
        nMonth = calendar.get(Calendar.MONTH) + 1;



        TabLayout tab_layout = findViewById(R.id.tab_layout);
        tab_layout.getTabAt(0).select();

        tab_layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    txtSKUs.setText(SKU_Lista_80);
                    txtValor.setText(TAB_lista80_valor);
                    txtFactor.setText(TAB_lista80_fact);
                    txtComisio.setText(TAB_lista80_comi);
                } else if (tab.getPosition() == 1) {
                    txtSKUs.setText(SKU_Lista_20);
                    txtValor.setText(TAB_lista20_valor);
                    txtFactor.setText(TAB_lista20_fact);
                    txtComisio.setText(TAB_lista20_comi);
                }
            }



            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
    private void showPieChart(String valor){

        int promedio = Math.round(Float.parseFloat(valor));

        Log.e("TAG_error", "showPieChart: "+promedio );

        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        String label = "type";

        //initializing data
        Map<String, Integer> typeAmountMap = new HashMap<>();
        ArrayList<Integer> colors = new ArrayList<>();
        if ( promedio>= 100 ){
            typeAmountMap.put(valor.concat("%"),promedio);
            colors.add(Color.parseColor("#309967"));
        }else{
            int vlDiff = 100 - promedio;
            typeAmountMap.put(valor.concat("%"),promedio);
            typeAmountMap.put(String.valueOf(vlDiff).concat("%"),vlDiff);
            colors.add(Color.parseColor("#304567"));
            colors.add(Color.parseColor("#309967"));
        }


        //input data and fit data into pie chart entry
        for(String type: typeAmountMap.keySet()){
            pieEntries.add(new PieEntry(typeAmountMap.get(type).floatValue(), type));
        }

        //collecting the entries with label name
        PieDataSet pieDataSet = new PieDataSet(pieEntries,"");
        //setting text size of the value
        pieDataSet.setValueTextSize(12f);
        //providing color list for coloring different entries
        pieDataSet.setColors(colors);
        //grouping the data set from entry to chart
        PieData pieData = new PieData(pieDataSet);
        //showing the value of the entries, default true if not set
        pieData.setDrawValues(false);

        pieData.setValueFormatter(new PercentFormatter());
        chart.getLegend().setEnabled(false);
        chart.getDescription().setEnabled(false);

        chart.setData(pieData);
        chart.invalidate();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.item_calendar:
                dialogDatePickerLight();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void dialogDatePickerLight() {
        //Calendar cur_calender = Calendar.getInstance();
        DatePickerDialog datePicker = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        nMonth= monthOfYear + 1 ;
                        nYear = year;

                        new MyTaskLoginNormal().execute(RUTA);
                    }
                }
        );
        //set dark light
        datePicker.setThemeDark(false);
        datePicker.setAccentColor(getResources().getColor(R.color.colorPrimary));
        datePicker.show(getFragmentManager(), "Datepickerdialog");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_comision, menu);
        return true;
    }


    private class MyTaskLoginNormal extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ComisionActivity.this);
            progressDialog.setTitle(getResources().getString(R.string.title_please_wait));
            progressDialog.setMessage(getResources().getString(R.string.factura_historico_process));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String strRuta = params[0];


            JsonArrayRequest request = new JsonArrayRequest(GET_COMISION + strRuta.concat("/").concat(String.valueOf(nMonth)).concat("/").concat(String.valueOf(nYear)) , new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    if (response == null) {
                        Toast.makeText(getApplicationContext(), R.string.failed_fetch_data, Toast.LENGTH_LONG).show();
                        return;
                    }


                    try {
                        if (response != null) {


                            JSONObject Listas   = new JSONObject(response.getJSONObject(0).getString("Comision_de_venta"));

                            String str01 = Listas.getString("Lista80");
                            str01 = str01.substring(1, str01.length() - 1);
                            String[] LISTA80      = str01.split(",");

                            String str02 = Listas.getString("Lista20");
                            str02 = str02.substring(1, str02.length() - 1);
                            String[] LISTA20      = str02.split(",");

                            String str03 = Listas.getString("Total");
                            str03 = str03.substring(1, str03.length() - 1);
                            String[] TTLISTA      = str03.split(",");


                            String Totales_finales       = response.getJSONObject(0).getString("Totales_finales") ;
                            Totales_finales              = Totales_finales.substring(1, Totales_finales.length() - 1).replace('"',' ');
                            String[] TotalesFinales      = Totales_finales.split(",");

                            String Total_Compensacion    = response.getJSONObject(0).getString("Total_Compensacion") ;
                            String Total_Promedio        = response.getJSONObject(0).getString("VntPromedio") ;
                            String SalarioBasico         = response.getJSONObject(0).getString("Salariobasico") ;

                            total_comision.setText(("C$ ").concat(String.format(Locale.ENGLISH, "%1$,.2f", Double.parseDouble(Total_Compensacion))));
                            comision_bono.setText(("C$ ").concat(String.format(Locale.ENGLISH, "%1$,.2f", Double.parseDouble(TTLISTA[3].substring(1, TTLISTA[3].length() - 1)))));

                            SKU_Lista_80        = LISTA80[0];
                            SKU_Lista_20        = LISTA20[0];

                            TAB_lista80_valor   = ("C$ ").concat(String.format(Locale.ENGLISH, "%1$,.2f", Double.parseDouble(LISTA80[1].substring(1, LISTA80[1].length() - 1))));
                            TAB_lista20_valor   = ("C$ ").concat(String.format(Locale.ENGLISH, "%1$,.2f", Double.parseDouble(LISTA20[1].substring(1, LISTA20[1].length() - 1))));

                            TAB_lista80_fact    = LISTA80[2].concat(" %");
                            TAB_lista20_fact    = LISTA20[2].concat(" %");

                            TAB_lista80_comi    = ("C$ ").concat(String.format(Locale.ENGLISH, "%1$,.2f", Double.parseDouble(LISTA80[3].substring(1, LISTA80[3].length() - 1))));
                            TAB_lista20_comi    = ("C$ ").concat(String.format(Locale.ENGLISH, "%1$,.2f", Double.parseDouble(LISTA20[3].substring(1, LISTA20[3].length() - 1))));


                            Comision.setText(("C$ ").concat(String.format(Locale.ENGLISH, "%1$,.2f", Double.parseDouble(TTLISTA[3].substring(1, TTLISTA[3].length() - 1)))));
                            ItemFact.setText(TTLISTA[0]);

                            cliente_promedio.setText(TotalesFinales[4].concat(" Prom."));
                            cliente_meta.setText(TotalesFinales[5].concat(" Meta"));
                            clientes_faturados.setText(TotalesFinales[6].concat(" Fact."));

                            cliente_bono.setText(("C$ ").concat(String.format(Locale.ENGLISH, "%1$,.2f", Double.parseDouble(TotalesFinales[2].substring(1, TotalesFinales[2].length() - 1)))));
                            cliente_prom.setText(TotalesFinales[3].concat(" %"));

                            txtSKUs.setText(SKU_Lista_80);
                            txtValor.setText(TAB_lista80_valor);
                            txtFactor.setText(TAB_lista80_fact);
                            txtComisio.setText(TAB_lista80_comi);

                            showPieChart(Total_Promedio);
                            txt_salario_base.setText(("Salario Garantizado: C$ ").concat(SalarioBasico));




                        }
                    } catch (JSONException e) {
                        Log.e("TAG_error", "ErrorWAY: " + e.getMessage() );
                        e.printStackTrace();
                    }


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
}