package com.app.gmv3.activities;


import static com.app.gmv3.utilities.Constant.GET_PLAN_CRECIMIENTO;
import static com.app.gmv3.utilities.Constant.POST_RPT_RUTA;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.app.gmv3.R;
import com.app.gmv3.utilities.Constant;
import com.app.gmv3.utilities.SharedPref;
import com.app.gmv3.utilities.Utils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.android.material.resources.TextAppearance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class ActivityPlanCrecimiento extends AppCompatActivity {
    BarChart barChart;

    TextView mtEvalu;
    TextView OptCreci;
    TextView CompMini;
    TextView PorceCreci;
    String RUTA;
    SharedPref sharedPref;
    String code_cliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_crecimiento);

        sharedPref = new SharedPref(this);
        RUTA = sharedPref.getYourName();

        initToolbar();
        iniComponent();
    }


    public void create_graph(final List<String> xLabels, List<BarEntry> entries) {





        try {
            barChart.setDrawBarShadow(false);
            barChart.setDrawValueAboveBar(false);
            barChart.getDescription().setEnabled(false);
            barChart.setPinchZoom(false);

            barChart.setDrawGridBackground(false);


            YAxis yAxis = barChart.getAxisLeft();
            yAxis.setValueFormatter(new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    //return String.valueOf((int) value);
                    return "";
                }
            });

            yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);


            yAxis.setGranularity(1f);
            yAxis.setGranularityEnabled(false);

            barChart.getAxisRight().setEnabled(false);
            XAxis xAxis = barChart.getXAxis();
            xAxis.setCenterAxisLabels(false);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);

            xAxis.setGranularity(1f);
            xAxis.setTextColor(Color.BLACK);
            xAxis.setTextSize(11);
            xAxis.setValueFormatter(new IndexAxisValueFormatter(xLabels));

            YAxis leftAxis = barChart.getAxisLeft();
            leftAxis.setTextColor(Color.WHITE);
            leftAxis.setTextSize(12);
            leftAxis.setAxisLineColor(Color.WHITE);
            leftAxis.setDrawGridLines(false);
            leftAxis.setGranularity(2);
            leftAxis.setLabelCount(4, true);
            leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);



            BarDataSet set1 = new BarDataSet(entries, "");
            set1.setColor(Color.rgb(100, 181, 246));


            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            barChart.setData(data);

            barChart.setFitBars(true);

            Legend l = barChart.getLegend();
            l.setFormSize(12f); // set the size of the legend forms/shapes
            l.setForm(Legend.LegendForm.SQUARE); // set what type of form/shape should be used
            l.setEnabled(false);
            l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_INSIDE);
            l.setTextSize(10f);
            l.setTextColor(Color.BLACK);
            l.setXEntrySpace(5f); // set the space between the legend entries on the x-axis
            l.setYEntrySpace(5f); // set the space between the legend entries on the y-axis

            barChart.invalidate();

            barChart.animateY(2000);

        } catch (Exception ignored) {
        }
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void iniComponent() {

        barChart    = findViewById(R.id.barChart_view);

        Intent intent = getIntent();

        mtEvalu     = findViewById(R.id.id_evaluado);
        OptCreci    = findViewById(R.id.id_opt_crecimiento);
        CompMini    = findViewById(R.id.id_compra_minima);
        PorceCreci  = findViewById(R.id.id_porcent_minima);

        code_cliente = intent.getStringExtra("cod_cliente");

        if (Utils.isNetworkAvailable(this)) {

            new MyTaskLoginNormal().execute(code_cliente);
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_perfil_cliente, menu);
        Utils.changeMenuIconColor(menu, Color.WHITE);
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
    private class MyTaskLoginNormal extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ActivityPlanCrecimiento.this);
            progressDialog.setTitle(getResources().getString(R.string.title_please_wait));
            progressDialog.setMessage(getResources().getString(R.string.factura_historico_process));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                String strCliente = params[0];





                JsonArrayRequest request = new JsonArrayRequest(GET_PLAN_CRECIMIENTO + strCliente + "&RUTA=" + RUTA, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null) {
                            Toast.makeText(getApplicationContext(), R.string.failed_fetch_data, Toast.LENGTH_LONG).show();
                            return;
                        }


                        try {
                            if (response != null) {
                                List<BarEntry> entries = new ArrayList<>();
                                List<String> labels = new ArrayList<>();

                                JSONObject iCLiente = new JSONObject(response.getJSONObject(0).getString("InfoCliente"));
                                String EVALUADO     = iCLiente.getString("EVALUADO");
                                String CRECIMIENTO  = iCLiente.getString("CRECIMIENTO");
                                String COMPRA_MIN   = iCLiente.getString("COMPRA_MIN");
                                String PROM_CUMP    = iCLiente.getString("PROM_CUMP");

                                mtEvalu.setText(("C$ ").concat(String.format(Locale.ENGLISH, "%1$,.0f", Double.parseDouble(EVALUADO))));
                                OptCreci.setText(("C$ ").concat(String.format(Locale.ENGLISH, "%1$,.0f", Double.parseDouble(CRECIMIENTO))));
                                CompMini.setText(("C$ ").concat(String.format(Locale.ENGLISH, "%1$,.0f", Double.parseDouble(COMPRA_MIN))));
                                PorceCreci.setText(String.format(Locale.ENGLISH, "%1$,.0f", Double.parseDouble(PROM_CUMP)).concat(" %"));

                                JSONArray SalesMonth = response.getJSONObject(0).getJSONArray("SalesMonths");

                                for (int j=0; j<SalesMonth.length(); j++)
                                {
                                    JSONObject sm = SalesMonth.getJSONObject(j);
                                    Float ttMonth = Float.valueOf(sm.getString("ttMonth"));
                                    String name_month = sm.getString("name_month");
    ;
                                    labels.add(name_month);
                                    entries.add(new BarEntry(j, ttMonth));


                                }

                                create_graph(labels,entries);

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

}

