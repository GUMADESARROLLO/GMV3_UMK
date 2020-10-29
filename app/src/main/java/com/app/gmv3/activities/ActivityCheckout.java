package com.app.gmv3.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import com.app.gmv3.adapters.RecyclerAdapterBnfc;
import com.app.gmv3.adapters.RecyclerAdapterLstResumen;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.gmv3.Config;
import com.app.gmv3.R;
import com.app.gmv3.utilities.DBHelper;
import com.app.gmv3.utilities.SharedPref;
import com.onesignal.OneSignal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import static com.app.gmv3.utilities.Constant.GET_SHIPPING;
import static com.app.gmv3.utilities.Constant.POST_ORDER;

public class ActivityCheckout extends AppCompatActivity {

    RequestQueue requestQueue;
    Button btn_submit_order;
    TextView edt_name, edt_email, edt_phone, edt_address, edt_order_list, edt_order_total,edt_iva,edt_total_precio,edt_id_pedido,txt_count;
    String str_name, str_email, str_phone, str_address, str_order_list, str_order_total, str_comment="";
    String data_order_list = "";
    double str_tax;
    String str_currency_code;
    ProgressDialog progressDialog;
    DBHelper dbhelper;
    ArrayList<ArrayList<Object>> data;
    private static final String ALLOWED_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    View view;
    private String rand = getRandomString(9);
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String date = dateFormat.format(Calendar.getInstance().getTime());
    SharedPref sharedPref;

    RecyclerView rcListaProductos;
    RecyclerAdapterLstResumen rcLista_Resumen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        view = findViewById(android.R.id.content);

        if (Config.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }

        sharedPref = new SharedPref(this);

        rand = sharedPref.getYourName().concat("-").concat(rand);
        setTitle(rand);

        setupToolbar();
        //getSpinnerData();
        getTaxCurrency();

        dbhelper = new DBHelper(this);
        try {
            dbhelper.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }

        // Creating Volley newRequestQueue
        requestQueue = Volley.newRequestQueue(ActivityCheckout.this);
        progressDialog = new ProgressDialog(ActivityCheckout.this);

        btn_submit_order = findViewById(R.id.btn_submit_order);

        edt_name = findViewById(R.id.edt_name);
        edt_email = findViewById(R.id.edt_email);
        edt_phone = findViewById(R.id.edt_phone);
        edt_address = findViewById(R.id.edt_address);
        edt_order_list = findViewById(R.id.edt_order_list);

        edt_order_total = findViewById(R.id.edt_order_total);
        edt_iva = findViewById(R.id.edt_iva);
        edt_total_precio = findViewById(R.id.edt_total_precio);

        edt_id_pedido = findViewById(R.id.edt_id_pedido);
        txt_count = findViewById(R.id.id_count_items);

        edt_id_pedido.setText(("ID: ").concat(rand));

        rcListaProductos = findViewById(R.id.recycler_item_resumen);
        rcListaProductos.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rcListaProductos.setItemAnimator(new DefaultItemAnimator());




        edt_name.setEnabled(false);
        edt_email.setEnabled(false);
        edt_phone.setEnabled(false);
        edt_address.setEnabled(false);
        edt_order_list.setEnabled(false);


        getDataFromDatabase();
        submitOrder();

        rcLista_Resumen = new RecyclerAdapterLstResumen(this, data,str_currency_code);
        rcListaProductos.setAdapter(rcLista_Resumen);

        txt_count.setText(rcLista_Resumen.getItemCount() + " Item(s)");



        ((ImageView) findViewById(R.id.id_img_comment)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomDialog();
            }
        });

    }




    public void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Resumen");
        }
    }






    public void submitOrder() {
        btn_submit_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getValueFromEditText();
            }
        });
    }

    public void getValueFromEditText() {

        str_name = edt_name.getText().toString();
        str_email = edt_email.getText().toString();
        str_phone = edt_phone.getText().toString();
        str_address = edt_address.getText().toString();
        str_order_list = data_order_list;
        str_order_total = edt_order_total.getText().toString();

        if (str_name.equalsIgnoreCase("") ||
                str_email.equalsIgnoreCase("") ||
                str_phone.equalsIgnoreCase("") ||
                str_address.equalsIgnoreCase("") ||
                str_order_list.equalsIgnoreCase("")) {
            Snackbar.make(view, R.string.checkout_fill_form, Snackbar.LENGTH_SHORT).show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.checkout_dialog_title);
            builder.setMessage(R.string.checkout_dialog_msg);
            builder.setCancelable(false);
            builder.setPositiveButton(getResources().getString(R.string.dialog_option_yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    requestAction();
                    //new sendData().execute();

                    /*Intent intent = new Intent(ActivityCheckout.this, CardcreditActivity.class);
                    intent.putExtra("tax", str_tax);
                    intent.putExtra("currency_code", str_currency_code);
                    startActivity(intent);*/
                }
            });
            builder.setNegativeButton(getResources().getString(R.string.dialog_option_no), null);
            builder.setCancelable(false);
            builder.show();
        }
    }

    public void requestAction() {

        progressDialog.setTitle(getString(R.string.checkout_submit_title));
        progressDialog.setMessage(getString(R.string.checkout_submit_msg));
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, POST_ORDER, new Response.Listener<String>() {
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
                params.put("code", rand);
                params.put("name", str_name);
                params.put("email", str_email);
                params.put("phone", str_phone);
                params.put("address", str_address);
                params.put("shipping", "str_shipping");
                params.put("order_list", str_order_list);
                params.put("order_total", str_order_total);
                params.put("comment", str_comment);
                params.put("player_id", OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId());

                params.put("date", date);
                params.put("server_url", Config.ADMIN_PANEL_URL);

                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(ActivityCheckout.this);
        requestQueue.add(stringRequest);
    }

    public void getTaxCurrency() {
        Intent intent = getIntent();

       /* params.put("name", str_name);
        params.put("email", str_email);
        params.put("phone", str_phone);
        params.put("address", str_address);*/

        str_name = intent.getStringExtra("cliente_nombre");
        str_email = intent.getStringExtra("cliente_codigo");
        str_address = intent.getStringExtra("cliente_direcc");

        str_tax = intent.getDoubleExtra("tax", 0);
        str_currency_code = intent.getStringExtra("currency_code");
    }

    public void getDataFromDatabase() {

        data = dbhelper.getAllData();



        double Order_price = 0;
        double Total_price = 0;
        double tax = 0;

        for (int i = 0; i < data.size(); i++) {
            ArrayList<Object> row = data.get(i);

            String prod_cod     = row.get(0).toString();
            String Menu_name    = row.get(1).toString();
            String Quantity     = row.get(2).toString();
            String Bonificado   = row.get(3).toString();

            double Sub_total_price = Double.parseDouble(row.get(4).toString());

            String _Sub_total_price = String.format(Locale.ENGLISH, "%1$,.2f", Sub_total_price);

            Order_price += Sub_total_price;

            if (Config.ENABLE_DECIMAL_ROUNDING) {
                data_order_list += (Quantity + " " + "[" + prod_cod + "] - " + Menu_name + " " + Bonificado + " " + _Sub_total_price + " " + str_currency_code + ",\n\n");
            } else {
                data_order_list += (Quantity + " " + "[" + prod_cod + "] - " + Menu_name + " " + Bonificado + " " + _Sub_total_price + " " + str_currency_code + ",\n\n");
            }
        }

        if (data_order_list.equalsIgnoreCase("")) {
            data_order_list += getString(R.string.no_order_menu);
        }

        tax = Order_price * (str_tax / 100);
        Total_price = Order_price + tax;

        String price_tax = String.format(Locale.ENGLISH, "%1$,.2f", str_tax);
        String _Order_price = String.format(Locale.ENGLISH, "%1$,.2f", Order_price);
        String _tax = String.format(Locale.ENGLISH, "%1$,.2f", tax);
        String _Total_price = String.format(Locale.ENGLISH, "%1$,.2f", Total_price);


        if (Config.ENABLE_DECIMAL_ROUNDING) {
            data_order_list += "\n" + getResources().getString(R.string.txt_order) + " " + _Order_price + " " + str_currency_code +
                    "\n" + getResources().getString(R.string.txt_tax) + " " + price_tax + " % : " + _tax + " " + str_currency_code +
                    "\n" + getResources().getString(R.string.txt_total) + " " + _Total_price + " " + str_currency_code;


            edt_order_total.setText(_Total_price + " " + str_currency_code);

        } else {
            data_order_list += "\n" + getResources().getString(R.string.txt_order) + " " + _Order_price + " " + str_currency_code +
                    "\n" + getResources().getString(R.string.txt_tax) + " " + str_tax + " % : " + tax + " " + str_currency_code +
                    "\n" + getResources().getString(R.string.txt_total) + " " + _Total_price + " " + str_currency_code;

            edt_order_total.setText(_Order_price + " " + str_currency_code);
            edt_iva.setText(str_tax + " " + str_currency_code);
            edt_total_precio.setText(_Total_price + " " + str_currency_code);
        }

        edt_order_list.setText(str_comment);

    }

    public void dialogSuccessOrder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.checkout_success_title);
        builder.setMessage(R.string.checkout_success_msg);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.checkout_option_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                String strClient = ("[").concat(str_email).concat("] - ").concat(str_phone);

                dbhelper.addDataHistory(rand, str_order_list, str_order_total, date,strClient);
                dbhelper.deleteAllData();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private static String getRandomString(final int sizeOfRandomString) {
        final Random random = new Random();
        final StringBuilder stringBuilder = new StringBuilder(sizeOfRandomString);
        for (int i = 0; i < sizeOfRandomString; ++i)
            stringBuilder.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return stringBuilder.toString();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_resumen_pedido, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;
            case R.id.id_comment_white:
                showCustomDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void showCustomDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_comment);
        dialog.setCancelable(true);

        final TextView et_post =  dialog.findViewById(R.id.et_post);

        et_post.setText("");
        et_post.setText(str_comment);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        final String Fecha = (String) DateFormat.format("EEE dd MMM yyyy hh:mm aaa'", Calendar.getInstance().getTime());

        final AppCompatButton bt_submit = dialog.findViewById(R.id.bt_submit);



        ((TextView) dialog.findViewById(R.id.edt_Nombre)).setText(sharedPref.getYourAddress());
        ((TextView) dialog.findViewById(R.id.edt_ruta)).setText(sharedPref.getYourName());
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
                str_comment = et_post.getText().toString();
                edt_order_list.setText(str_comment);
                dialog.dismiss();

            }
        });



        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
    @Override
    public void onResume() {
        edt_name.setText(sharedPref.getYourName());
        edt_email.setText(str_email.concat(" - "));
        edt_phone.setText(str_name);
        edt_address.setText(str_address);



        super.onResume();
    }

}
