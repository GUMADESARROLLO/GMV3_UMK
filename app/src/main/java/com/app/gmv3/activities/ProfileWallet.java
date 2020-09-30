package com.app.gmv3.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.app.gmv3.R;
import com.app.gmv3.adapters.RecyclerAdapterPerfilLotes;
import com.app.gmv3.models.Facturas_mora;
import com.app.gmv3.models.Moras;
import com.app.gmv3.utilities.MyDividerItemDecoration;
import com.app.gmv3.utilities.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static com.app.gmv3.utilities.Constant.GET_PROFIL_USER;



public class ProfileWallet extends AppCompatActivity{
    TextView txt_perfil_name_cliente,txt_perfil_disponible,txt_perfil_saldo,txt_perfil_limite;
    TextView txt_perfil_noVencido,txt_perfil_d30,txt_perfil_d60,txt_perfil_d90,txt_perfil_d120,txt_perfil_m120;
    TextView txt_tele,txt_condicion_pago;
    String code_cliente;

    public static ArrayList<String> factura_id = new ArrayList<String>();
    public static ArrayList<String> factura_date = new ArrayList<String>();
    public static ArrayList<String> factura_cant = new ArrayList<String>();
    public static ArrayList<String> factura_monto = new ArrayList<String>();

    RecyclerView recyclerView;
    RecyclerAdapterPerfilLotes recyclerAdaptePerfilFactura;
    List<Facturas_mora> arrayItemLotes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_wallet);
        initToolbar();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.deep_purple_500), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txt_perfil_name_cliente = findViewById(R.id.id_perfil_name_cliente);
        txt_perfil_disponible   = findViewById(R.id.id_perfil_disponible);
        txt_perfil_saldo        = findViewById(R.id.id_perfil_saldo);
        txt_perfil_limite       = findViewById(R.id.id_perfil_limite);

        txt_tele                = findViewById(R.id.id_tele);
        txt_condicion_pago      = findViewById(R.id.id_condicion_pago);

        txt_perfil_noVencido    = findViewById(R.id.id_perfil_noVencido);
        txt_perfil_d30          = findViewById(R.id.id_perfil_d30);
        txt_perfil_d60          = findViewById(R.id.id_perfil_d60);
        txt_perfil_d90          = findViewById(R.id.id_perfil_d90);
        txt_perfil_d120         = findViewById(R.id.id_perfil_d120);
        txt_perfil_m120         = findViewById(R.id.id_perfil_m120);

        recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new MyDividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL, 0));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerAdaptePerfilFactura = new RecyclerAdapterPerfilLotes(this, arrayItemLotes);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.grey_5));
        }

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(),  new ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(ProfileWallet.this, ShoppingCartSimple.class);
                        intent.putExtra("factura_id",factura_id.get(position));
                        intent.putExtra("factura_date",factura_date.get(position));
                        startActivity(intent);
                    }
                }, 400);
            }
        }));


        Utils.setSystemBarLight(this);




        Intent intent = getIntent();
        code_cliente = intent.getStringExtra("Client_Code");
        txt_perfil_name_cliente.setText(intent.getStringExtra("CLient_name"));
        txt_tele.setText(intent.getStringExtra("Telefono"));
        txt_condicion_pago.setText(intent.getStringExtra("Condicion_pago"));

        txt_perfil_disponible.setText(("C$ ").concat(intent.getStringExtra("Disponible")));
        txt_perfil_saldo.setText(("C$ ").concat(intent.getStringExtra("Saldo")));
        txt_perfil_limite.setText(("C$ ").concat(intent.getStringExtra("Limite")));

        findViewById(R.id.id_history_last_3m).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileWallet.this, Activitytresmeses.class);

                intent.putExtra("factura_id",code_cliente);
                intent.putExtra("Name_cliente",txt_perfil_name_cliente.getText());

                startActivity(intent);
            }
        });
        findViewById(R.id.id_history_nc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileWallet.this, PaymentProfile.class);

                intent.putExtra("factura_id",code_cliente);
                intent.putExtra("Name_cliente",txt_perfil_name_cliente.getText());

                startActivity(intent);
            }
        });
        fetchData();
    }
    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final ClickListener clickListener) {

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
    public void clearData() {
        factura_id.clear();
        factura_date.clear();
        factura_cant.clear();
        factura_monto.clear();
    }

    private void fetchData() {
        JsonArrayRequest request = new JsonArrayRequest(GET_PROFIL_USER + code_cliente, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response == null) {
                    Toast.makeText(getApplicationContext(), R.string.failed_fetch_data, Toast.LENGTH_LONG).show();
                    return;
                }

                List<Moras> items = new Gson().fromJson(response.toString(), new TypeToken<List<Moras>>() {
                }.getType());

                txt_perfil_noVencido.setText(("C$ ").concat(items.get(0).getNoVencidos()));
                txt_perfil_d30.setText(("C$ ").concat(items.get(0).getDias30()));
                txt_perfil_d60.setText(("C$ ").concat(items.get(0).getDias60()));
                txt_perfil_d90.setText(("C$ ").concat(items.get(0).getDias90()));
                txt_perfil_d120.setText(("C$ ").concat(items.get(0).getDias120()));
                txt_perfil_m120.setText(("C$ ").concat(items.get(0).getMas120()));


                clearData();
                List<String> data = Arrays.asList(items.get(0).getFACT_PEND().split(","));

                for (int i = 0; i < data.size(); i++) {

                    List<String> row = Arrays.asList(data.get(i).split(":"));
                    factura_id.add(row.get(0));
                    factura_cant.add(row.get(1));
                    factura_date.add(row.get(2));
                    factura_monto.add(row.get(3));

                }
                recyclerView.setAdapter(recyclerAdaptePerfilFactura);

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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle() + " clicked", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}

