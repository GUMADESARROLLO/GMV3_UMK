package com.app.gmv3.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.SQLException;
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
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.gmv3.Config;
import com.app.gmv3.R;
import com.app.gmv3.adapters.AdapterCartVineta;
import com.app.gmv3.models.Cart;
import com.app.gmv3.utilities.DBHelper;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ActivityCartVineta extends AppCompatActivity {

    RecyclerView recyclerView;
    View lyt_empty_cart;
    RelativeLayout lyt_order;
    DBHelper dbhelper;
    AdapterCartVineta adapterCart;
    double total_price;
    final int CLEAR_ALL_ORDER = 0;
    final int CLEAR_ONE_ORDER = 1;
    int FLAG;
    String ID;
    AppCompatButton btn_checkout;
    Button  btn_continue;
    ArrayList<ArrayList<Object>> data;
    public static ArrayList<Integer> table_id = new ArrayList<Integer>();
    public static ArrayList<String> vineta_factura = new ArrayList<String>();
    public static ArrayList<String> vineta_cod_producto = new ArrayList<String>();
    public static ArrayList<String> vineta_descripcion = new ArrayList<String>();
    public static ArrayList<String> vineta_code = new ArrayList<String>();
    public static ArrayList<Integer> vineta_cant = new ArrayList<Integer>();
    public static ArrayList<String> vineta_und_valor = new ArrayList<String>();

    public static ArrayList<Integer> sub_total_price = new ArrayList<Integer>();
    List<Cart> arrayCart;
    View view;

    Intent intent;
    String Cod_cliente,name_cliente,dir_cliente;

    TextView txt_no_item_message;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        view = findViewById(android.R.id.content);

        if (Config.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_cart);
        }

        txt_no_item_message = findViewById(R.id.no_item_message);

        intent = getIntent();
        Cod_cliente     = intent.getStringExtra("cod_cliente");
        name_cliente    = intent.getStringExtra("cliente_nombre");
        dir_cliente     = intent.getStringExtra("cliente_direcc");




        recyclerView = findViewById(R.id.recycler_view);
        lyt_empty_cart = findViewById(R.id.lyt_empty_history);
        btn_checkout = findViewById(R.id.btn_checkout);
        btn_checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbhelper.close();
               Intent intent = new Intent(ActivityCartVineta.this, ActivityCheckoutVineta.class);
                intent.putExtra("tax", 0);
                intent.putExtra("cliente_codigo", Cod_cliente );
                intent.putExtra("cliente_direcc", dir_cliente );
                intent.putExtra("cliente_nombre", name_cliente );
                intent.putExtra("currency_code", "NIO" );
                intent.putExtra("total_price", total_price);
                startActivity(intent);
            }
        });
        btn_continue = findViewById(R.id.btn_continue);
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
       // recyclerView.addItemDecoration(new MyDividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL, 86));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        lyt_order = findViewById(R.id.lyt_history);

        adapterCart = new AdapterCartVineta(this, arrayCart);
        dbhelper = new DBHelper(this);

        try {
            dbhelper.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showClearDialog(CLEAR_ONE_ORDER, table_id.get(position).toString());
                    }
                }, 400);



            }


            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        new getDataTask().execute(Cod_cliente);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cart, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;

            case R.id.clear:
                if (vineta_factura.size() > 0) {
                    showClearDialog(CLEAR_ALL_ORDER, "1111");
                } else {
                    Snackbar.make(view, R.string.msg_empty_cart, Snackbar.LENGTH_SHORT).show();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showClearDialog(int flag, String id) {
        FLAG = flag;
        ID = id;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirm);
        switch (FLAG) {
            case 0:
                builder.setMessage(getString(R.string.clear_all_order));
                break;
            case 1:
                builder.setMessage(getString(R.string.clear_one_order));
                break;
        }
        builder.setCancelable(false);
        builder.setPositiveButton(getResources().getString(R.string.dialog_option_yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (FLAG) {
                    case 0:
                        dbhelper.deleteAllDataVineta(Cod_cliente);
                        clearData();
                        new getDataTask().execute(Cod_cliente);
                        break;
                    case 1:
                        dbhelper.deleteDataVineta(ID,Cod_cliente);
                        clearData();
                        new getDataTask().execute(Cod_cliente);
                        break;
                }
            }
        });

        builder.setNegativeButton(getResources().getString(R.string.dialog_option_no), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    public void clearData() {
        vineta_factura.clear();
        vineta_cod_producto.clear();
        vineta_descripcion.clear();
        vineta_code.clear();
        vineta_cant.clear();
        vineta_und_valor.clear();
        sub_total_price.clear();
        table_id.clear();
    }

    public class getDataTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... arg0) {
            Log.e("TAG_", "doInBackground: " +  arg0[0]);
            getDataFromDatabase(arg0[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            String _price = String.format(Locale.ENGLISH, "%1$,.2f", total_price);
            TextView txt_total_price = findViewById(R.id.txt_total_price);
            txt_total_price.setText("C$ " + _price);
            if (vineta_factura.size() > 0) {
                lyt_order.setVisibility(View.VISIBLE);
                recyclerView.setAdapter(adapterCart);
            } else {
                btn_continue.setText(R.string.btn_continue_vine);
                txt_no_item_message.setText(R.string.empty_cart_vine);
                lyt_empty_cart.setVisibility(View.VISIBLE);
            }

        }
    }

    public void getDataFromDatabase(String cod_cliente) {

        total_price = 0;
        clearData();
        data = dbhelper.getAllDataVinete(cod_cliente);

        for (int i = 0; i < data.size(); i++) {
            ArrayList<Object> row = data.get(i);

            vineta_factura.add(row.get(0).toString());

            //vineta_cod_producto.add(row.get(1).toString());
            //vineta_descripcion.add(row.get(2).toString());

            vineta_code.add(row.get(3).toString());
            vineta_cant.add(Integer.parseInt(row.get(4).toString()));
            vineta_und_valor.add(row.get(5).toString());

            sub_total_price.add(Integer.parseInt(row.get(6).toString()));

            total_price += Integer.parseInt(row.get(6).toString());

            table_id.add(Integer.parseInt(row.get(7).toString()));

        }



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {

            this.clickListener = clickListener;

            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
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

        public void onLongClick(View view, int position);
    }

}