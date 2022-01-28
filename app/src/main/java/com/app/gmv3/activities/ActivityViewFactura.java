package com.app.gmv3.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.app.gmv3.R;
import com.app.gmv3.adapters.AdapterFacturasLineas;
import com.app.gmv3.models.Factura_lineas;
import com.app.gmv3.models.Vineta;
import com.app.gmv3.utilities.DBHelper;
import com.app.gmv3.utilities.ItemOffsetDecoration;
import com.app.gmv3.utilities.Utils;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.app.gmv3.utilities.Constant.GET_DETALLE_FACTURA;

public class ActivityViewFactura extends AppCompatActivity {
    String cod_factura,date_factura,Cod_cliente, isRecibo;

    private AdapterFacturasLineas mAdapter;
    private RecyclerView recyclerView;
    private List<Factura_lineas> productList;
    TextView txt_factura_total,txt_observacion_factura;
    final Context context = this;
    public static DBHelper dbhelper;
    String _Order_price;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart_simple);
        dbhelper = new DBHelper(this);

        try {
            dbhelper.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }
        initToolbar();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.grey_60), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        cod_factura     = intent.getStringExtra("factura_id");
        date_factura    = intent.getStringExtra("factura_date");
        Cod_cliente     = intent.getStringExtra("cod_cliente");
        isRecibo        = intent.getStringExtra("isRecibo");

        getSupportActionBar().setTitle(("Nº ").concat(cod_factura));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.grey_5));
        }
        Utils.setSystemBarLight(this);

        txt_factura_total = findViewById(R.id.id_factura_total);
        txt_observacion_factura = findViewById(R.id.id_observacion);

        recyclerView = findViewById(R.id.recycler_view);
        productList = new ArrayList<>();
        mAdapter = new AdapterFacturasLineas(this, productList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);


        fetchData();
    }


    private void fetchData() {
        JsonArrayRequest request = new JsonArrayRequest(GET_DETALLE_FACTURA + cod_factura, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response == null) {
                    Toast.makeText(getApplicationContext(), R.string.failed_fetch_data, Toast.LENGTH_LONG).show();
                    return;
                }
                List<Factura_lineas> items = new Gson().fromJson(response.toString(), new TypeToken<List<Factura_lineas>>() {
                }.getType());

                double Order_price = 0;

                for (int i = 0; i < items.size(); i++) {

                    double Sub_total_price = Double.parseDouble(items.get(i).getVENTA());
                    Order_price += Sub_total_price;



                }

                _Order_price = String.format(Locale.ENGLISH, "%1$,.2f", Order_price);
                txt_observacion_factura.setText(items.get(0).getOBSERVACIONES());
                txt_factura_total.setText(("C$ ").concat(_Order_price));


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



        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_factura, menu);
        Utils.changeMenuIconColor(menu, getResources().getColor(R.color.grey_60));

        MenuItem add_recibo = menu.findItem(R.id.item_abono);

        if (isRecibo.equals("S")) {
            add_recibo.setVisible(true);
        }else{
            add_recibo.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.item_commit_factura:
                ShowDialog("Observaciones de la Factura",txt_observacion_factura.getText().toString(),R.color.colorPrimary);
                break;

            case R.id.item_abono:
                FormAdd();
                break;


            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
    public void FormAdd() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_add_recibo);
        dialog.setCancelable(true);

        final EditText edt_nc           = dialog.findViewById(R.id.edt_cantidad_nc);
        final EditText edt_retencion    = dialog.findViewById(R.id.edt_cantidad_retencion);
        final EditText edt_descuento    = dialog.findViewById(R.id.edt_cantidad_descuento);
        final EditText edt_rec_valor    = dialog.findViewById(R.id.edt_cantidad_recibido);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        (dialog.findViewById(R.id.bt_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        (dialog.findViewById(R.id.bt_add)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_nc.getText().toString().equalsIgnoreCase("") &&
                        edt_retencion.getText().toString().equalsIgnoreCase("") &&
                        edt_descuento.getText().toString().equalsIgnoreCase("") &&
                        edt_rec_valor.getText().toString().equalsIgnoreCase("")) {

                    Toast.makeText(getApplicationContext(), "Falta Información", Toast.LENGTH_SHORT).show();

                } else {
                    saveRecibo(
                            edt_nc.getText().toString(),
                            edt_retencion.getText().toString(),
                            edt_descuento.getText().toString(),
                            edt_rec_valor.getText().toString()
                    );
                    dialog.dismiss();
                    finish();
                }


            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
    private void saveRecibo(String NotaCredito,String Retencion,String Descuento,String recValor){
        ArrayList<ArrayList<Object>> data = dbhelper.getAllDataRecibo(Cod_cliente);

        int id_tabla = (data.size() + 1);

        _Order_price = _Order_price.replace(",","");

        NotaCredito = (NotaCredito.equals(""))  ? "0" : NotaCredito;
        Retencion   = (Retencion.equals(""))    ? "0" : Retencion;
        Descuento   = (Descuento.equals(""))    ? "0" : Descuento;
        recValor    = (recValor.equals(""))    ? "0" : recValor;

        dbhelper.addRecibo(
                cod_factura,
                _Order_price,
                NotaCredito,
                Retencion,
                Descuento,
                recValor,
                (Double.parseDouble(_Order_price) - Double.parseDouble(recValor) ),
                id_tabla,
                Cod_cliente
        );
    }
    public void ShowDialog(String strTitle, String strMsg, int color) {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_info);
        dialog.setCancelable(true);

        LinearLayout lyt = dialog.findViewById(R.id.lyt);
        TextView txt_title = dialog.findViewById(R.id.title);
        TextView txt_msg = dialog.findViewById(R.id.content);

        txt_title.setText(strTitle);
        txt_msg.setText(strMsg);
        lyt.setBackgroundColor(context.getResources().getColor(color));;

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        (dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
}
