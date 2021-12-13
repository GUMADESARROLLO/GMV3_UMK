package com.app.gmv3.activities;

import static com.app.gmv3.utilities.Constant.POST_ORDER_RECIBO;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.gmv3.Config;
import com.app.gmv3.R;
import com.app.gmv3.adapters.AdapterCheckoutRecibo;
import com.app.gmv3.utilities.DBHelper;
import com.app.gmv3.utilities.SharedPref;
import com.app.gmv3.utilities.Utils;
import com.google.android.material.snackbar.Snackbar;
import com.onesignal.OneSignal;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ActivityCheckoutRecibos extends AppCompatActivity {

    RequestQueue requestQueue;
    Button btn_submit_order;
    TextView edt_name, edt_email, edt_phone, edt_order_list, edt_order_total,edt_iva,txt_count,txt_number_recibo,txt_recibo;
    String str_ruta, str_cod_cliente, str_name_cliente, str_address, str_order_list, str_order_total, str_comment="",str_cod_recibo,str_fecha_recibo;
    String data_order_list = "";
    double str_tax;
    ProgressDialog progressDialog;
    DBHelper dbhelper;
    ArrayList<ArrayList<Object>> data;
    View view;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String date = dateFormat.format(Calendar.getInstance().getTime());
    SharedPref sharedPref;

    RecyclerView rcListaProductos;
    AdapterCheckoutRecibo rcLista_Resumen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_recibo);
        view = findViewById(android.R.id.content);

        if (Config.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }

        sharedPref = new SharedPref(this);



        setupToolbar();
        //getSpinnerData();
        getTaxCurrency();

        dbhelper = new DBHelper(this);
        try {
            dbhelper.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }
        txt_recibo = findViewById(R.id.id_text_recibo);
        // Creating Volley newRequestQueue
        requestQueue = Volley.newRequestQueue(ActivityCheckoutRecibos.this);
        progressDialog = new ProgressDialog(ActivityCheckoutRecibos.this);

        btn_submit_order = findViewById(R.id.btn_submit_order);

        edt_name = findViewById(R.id.edt_name);
        edt_email = findViewById(R.id.edt_email);
        edt_phone = findViewById(R.id.edt_phone);
        edt_order_list = findViewById(R.id.edt_order_list);

        txt_number_recibo = findViewById(R.id.id_text_number_recibo);

        edt_order_total = findViewById(R.id.edt_order_total);
        edt_iva = findViewById(R.id.edt_iva);

        txt_count = findViewById(R.id.id_count_items);

        rcListaProductos = findViewById(R.id.recycler_item_resumen);
        rcListaProductos.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rcListaProductos.setItemAnimator(new DefaultItemAnimator());




        edt_name.setEnabled(false);
        edt_email.setEnabled(false);
        edt_phone.setEnabled(false);
        edt_order_list.setEnabled(false);


        getDataFromDatabase();
        submitOrder();

        rcLista_Resumen = new AdapterCheckoutRecibo(this, data);
        rcListaProductos.setAdapter(rcLista_Resumen);

        txt_count.setText(rcLista_Resumen.getItemCount() + " Item(s)");



        (findViewById(R.id.id_img_comment)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomDialog("Observacion");
            }
        });
        txt_recibo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FormAddRecibo();
            }
        });

        txt_number_recibo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDatePickerLight();
            }
        });

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
                        long date_ship_millis = calendar.getTimeInMillis();
                        txt_number_recibo.setText(Utils.getFormattedDateSimple(date_ship_millis));

                    }
                }
        );
        //set dark light
        datePicker.setThemeDark(false);
        datePicker.setAccentColor(getResources().getColor(R.color.colorPrimary));
        datePicker.show(getFragmentManager(), "Datepickerdialog");
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

        str_ruta = edt_name.getText().toString();
        str_cod_cliente = edt_email.getText().toString();
        str_name_cliente = edt_phone.getText().toString();
        str_order_list = data_order_list;
        str_order_total = edt_order_total.getText().toString();
        str_cod_recibo = txt_recibo.getText().toString();
        str_fecha_recibo = txt_number_recibo.getText().toString();

        if (str_ruta.equalsIgnoreCase("") ||
                str_cod_cliente.equalsIgnoreCase("") ||
                str_cod_recibo.equalsIgnoreCase("00000") ||
                str_fecha_recibo.equalsIgnoreCase("00/00/0000") ||
                str_name_cliente.equalsIgnoreCase("") ||
                str_address.equalsIgnoreCase("") ||
                str_comment.equalsIgnoreCase("") ||
                str_order_list.equalsIgnoreCase("")) {
            Snackbar.make(view, R.string.checkout_fill_form, Snackbar.LENGTH_SHORT).show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Info.");
            builder.setMessage(R.string.checkout_dialog_msg);
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

    public void requestAction() {

        progressDialog.setTitle(getString(R.string.checkout_submit_title));
        progressDialog.setMessage("Enviando Recibo... ");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, POST_ORDER_RECIBO, new Response.Listener<String>() {
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
                params.put("ruta", str_ruta);
                params.put("cod_cliente", str_cod_cliente);
                params.put("fecha_recibo", str_fecha_recibo);
                params.put("recibo", str_cod_recibo);
                params.put("name_cliente", str_name_cliente);
                params.put("order_list", str_order_list);
                params.put("order_total", str_order_total);
                params.put("comment", str_comment);
                params.put("player_id", OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId());
                params.put("date", date);

                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(ActivityCheckoutRecibos.this);
        requestQueue.add(stringRequest);
    }

    public void getTaxCurrency() {
        Intent intent = getIntent();

        str_ruta = intent.getStringExtra("cliente_nombre");
        str_cod_cliente = intent.getStringExtra("cliente_codigo");
        str_address = intent.getStringExtra("cliente_direcc");

        str_tax = intent.getDoubleExtra("tax", 0);
    }

    public void getDataFromDatabase() {

        data = dbhelper.getAllDataRecibo(str_cod_cliente);



        double Order_price = 0;
        double Total_price = 0;
        double tax = 0;

        for (int i = 0; i < data.size(); i++) {
            ArrayList<Object> row = data.get(i);


            String Factura          = row.get(0).toString();
            String Valor_Factura    = row.get(1).toString();
            String NOta_Credito     = row.get(2).toString();
            String Retencion        = row.get(3).toString();
            String Descuento        = row.get(4).toString();
            String Valor_Recibido   = row.get(5).toString();
            String Saldo            = row.get(6).toString();
            String Cliente          = row.get(8).toString();

            double Sub_total_price = Double.parseDouble(Valor_Recibido);


            Order_price += Sub_total_price;

            data_order_list += "[" + (Factura + ";" + Valor_Factura + ";" + NOta_Credito + ";" + Retencion + ";" + Descuento + ";" + Valor_Recibido + ";" + Saldo + ";" + Cliente  + "],");
        }

        if (data_order_list.equalsIgnoreCase("")) {
            data_order_list += getString(R.string.no_order_menu);
        }

        tax = Order_price * (str_tax / 100);
        Total_price = Order_price + tax;

        String _Order_price = String.format(Locale.ENGLISH, "%1$,.2f", Order_price);
        String _Total_price = String.format(Locale.ENGLISH, "%1$,.2f", Total_price);

        edt_order_total.setText("C$ " + _Order_price);

        edt_order_list.setText(str_comment);

    }

    public void FormAddRecibo() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_add_vineta);
        dialog.setCancelable(true);

        final EditText edt_cantidad = dialog.findViewById(R.id.edt_cantidad_vineta);
        edt_cantidad.setHint("Nº Recibo: ");

        final TextView txt_title_frm = dialog.findViewById(R.id.id_title_frm);
        txt_title_frm.setText("Digite el Nº de recibo.");

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

                str_cod_recibo = edt_cantidad.getText().toString();

                if (str_cod_recibo.equals("")){
                    Toast.makeText(getApplicationContext(), "por favor, digite un valor" , Toast.LENGTH_SHORT).show();

                }else {

                    int txtVal = Integer.parseInt(str_cod_recibo);

                    int len01 = String.valueOf(txtVal).length();

                    str_cod_recibo=  ("0000").substring(0,(4 - len01)).concat(String.valueOf(txtVal));

                    txt_recibo.setText(str_cod_recibo);
                    dialog.dismiss();
                }


            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    public void dialogSuccessOrder() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_info);
        dialog.setCancelable(true);

        TextView txt_title = dialog.findViewById(R.id.title);
        TextView txt_msg = dialog.findViewById(R.id.content);


        txt_title.setText(" Enviado ");
        txt_msg.setText( "Su recibo será revisado pronto, se enviados a su telefono una notificación cuando esté listo." );

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        (dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dbhelper.addDataHistory(rand, str_order_list, str_order_total, date,strClient);

                Intent i = getIntent();
                dbhelper.deleteAllDataRecibos(i.getStringExtra("cliente_codigo"));

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
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
                showCustomDialog("");
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void showCustomDialog(final String form) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_comment);
        dialog.setCancelable(true);

        str_comment ="";

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


                if (form.equals("Observacion")){
                    str_comment = et_post.getText().toString();
                    edt_order_list.setText(str_comment);
                }else{
                }


                dialog.dismiss();

            }
        });



        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
    @Override
    public void onResume() {
        edt_name.setText(sharedPref.getYourName());
        edt_email.setText(str_cod_cliente.concat(" - "));
        edt_phone.setText(str_ruta);

        super.onResume();
    }

}
