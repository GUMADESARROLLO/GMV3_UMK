package com.app.gmv3.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.app.gmv3.Config;
import com.app.gmv3.R;
import com.app.gmv3.adapters.RecyclerAdapterBnfc;
import com.app.gmv3.adapters.RecyclerAdapterLotes;
import com.app.gmv3.models.Lotes;
import com.app.gmv3.utilities.DBHelper;
import com.app.gmv3.utilities.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static com.app.gmv3.utilities.Constant.GET_TAX_CURRENCY;

public class ActivityProductDetail extends AppCompatActivity {

    String  product_id;
    TextView txt_product_name, txt_product_price, txt_product_quantity,txt_id_producto;
    private String product_name, product_image, category_name, product_status, currency_code, product_description,product_bonificado,product_lotes,product_und;
    private double product_price;
    private double product_quantity;
    WebView txt_product_description;
    ImageView img_product_image;
    Button btn_cart;
    public static DBHelper dbhelper;
    final Context context = this;
    double resp_tax;
    String resp_currency_code;

    public static ArrayList<String> lote_name = new ArrayList<String>();
    public static ArrayList<String> lote_date = new ArrayList<String>();
    public static ArrayList<String> lote_cant = new ArrayList<String>();

    RecyclerView recyclerView, rcViewBnfc;
    RecyclerAdapterLotes recyclerAdapterLotes;
    RecyclerAdapterBnfc rcBonificado;
    List<Lotes> arrayItemLotes;
    List<String> sList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        if (Config.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }

        dbhelper = new DBHelper(this);

        getData();
        initComponent();
        displayData();
        setupToolbar();
        makeJsonObjectRequest();

    }



    public void setupToolbar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Utils.setSystemBarColor(this);
    }

    public void getData() {
        Intent intent = getIntent();
        product_id = intent.getStringExtra("product_id");
        product_name = intent.getStringExtra("title");
        product_image = intent.getStringExtra("image");
        product_price = intent.getDoubleExtra("product_price", 0);
        product_description = intent.getStringExtra("product_description");
        product_quantity = intent.getDoubleExtra("product_quantity", 0);
        product_status = intent.getStringExtra("product_status");
        currency_code = intent.getStringExtra("currency_code");
        category_name = intent.getStringExtra("category_name");
        product_bonificado = intent.getStringExtra("product_bonificado");
        product_lotes = intent.getStringExtra("product_lotes");
        product_und = intent.getStringExtra("product_und");
    }

    public void initComponent() {
        txt_id_producto = findViewById(R.id.id_sku_product);;
        txt_product_name = findViewById(R.id.product_name);
        img_product_image = findViewById(R.id.product_image);
        txt_product_price = findViewById(R.id.product_price);
        txt_product_description = findViewById(R.id.product_description);
        txt_product_quantity = findViewById(R.id.product_quantity);
        btn_cart = findViewById(R.id.btn_add_cart);

        sList = Arrays.asList(product_bonificado.split(","));

        final FloatingActionButton fab =  findViewById(R.id.btn_cart);

        recyclerView = findViewById(R.id.recycler_view);
        rcViewBnfc = findViewById(R.id.recycler_bonificado);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerAdapterLotes = new RecyclerAdapterLotes(this, arrayItemLotes);



        rcViewBnfc.setLayoutManager(new GridLayoutManager(getApplicationContext(), 4));

        rcBonificado = new RecyclerAdapterBnfc(this, sList);

        rcViewBnfc.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(),  new ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                final String str_bonificado =  sList.get(position);
                final List<String> row_arr = new ArrayList<>();
                row_arr.add(Arrays.asList(str_bonificado.replace("+", ",").split(",")).get(0));



                if (!str_bonificado.equalsIgnoreCase("0")) {



                    final Dialog dialog = new Dialog(ActivityProductDetail.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
                    dialog.setContentView(R.layout.dialog_info);
                    dialog.setCancelable(true);

                    LinearLayout lyt = dialog.findViewById(R.id.lyt);
                    TextView txt_title = dialog.findViewById(R.id.title);
                    TextView txt_msg = dialog.findViewById(R.id.content);
                    TextView edtQuantity = dialog.findViewById(R.id.id_valor_producto_2);
                    AppCompatButton AppBtn = dialog.findViewById(R.id.bt_close);

                    txt_title.setText("¿Quiere agreagar un :? ");
                    txt_msg.setText( str_bonificado);

                    edtQuantity.setVisibility(View.VISIBLE);
                    double cnt_valor = Double.parseDouble(row_arr.get(0));
                    double vLinea = (product_price * cnt_valor);
                    String _cnt_valor = String.format(Locale.ENGLISH, "%1$,.2f", vLinea);
                    edtQuantity.setText(_cnt_valor.concat(" ").concat(currency_code));



                    AppBtn.setText("Confirmar");

                    lyt.setBackgroundColor(context.getResources().getColor(R.color.light_blue_400));;

                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(dialog.getWindow().getAttributes());
                    lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;




                    AppBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addProducto(row_arr.get(0),str_bonificado);
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                    dialog.getWindow().setAttributes(lp);


                 /*   AlertDialog.Builder builder = new AlertDialog.Builder(ActivityProductDetail.this);
                    builder.setTitle("Alerta");
                    builder.setMessage("Confirma que quiere agreagar un: " + str_bonificado);
                    builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final List<String> row_arr = new ArrayList<>();
                            row_arr.add(Arrays.asList(str_bonificado.replace("+", ",").split(",")).get(0));

                            addProducto(row_arr.get(0),str_bonificado);

                            dialog.cancel();
                        }
                    });
                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();*/

                } else {
                    Toast.makeText(ActivityProductDetail.this, "NO hay Bonificado que agregar", Toast.LENGTH_SHORT).show();

                }

            }
        }));





        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputDialog();
            }
        });




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
            View child = rcViewBnfc.findChildViewUnder(e.getX(), e.getY());
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

    public void displayData() {
        txt_id_producto.setText(("SKU: ").concat(product_id));

        txt_product_name.setText(product_name);

        Picasso.with(this)
                .load(Config.ADMIN_PANEL_URL + "/upload/product/" + product_image.replace(" ", "%20"))
                .placeholder(R.drawable.ic_loading)
                .into(img_product_image);

        img_product_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivityImageDetail.class);
                intent.putExtra("image", product_image);
                intent.putExtra("root", "product");
                startActivity(intent);
            }
        });

        String price = String.format(Locale.ENGLISH, "%1$,.2f", product_price);
        txt_product_price.setText(price + " " + currency_code);

        String product_quantity_ = String.format(Locale.ENGLISH, "%1$,.2f", product_quantity);
        txt_product_quantity.setText(product_quantity_ + " " + product_und);

        txt_product_description.setBackgroundColor(Color.parseColor("#ffffff"));
        txt_product_description.setFocusableInTouchMode(false);
        txt_product_description.setFocusable(false);
        txt_product_description.getSettings().setDefaultTextEncodingName("UTF-8");

        WebSettings webSettings = txt_product_description.getSettings();
        Resources res = getResources();
        int fontSize = res.getInteger(R.integer.font_size);
        webSettings.setDefaultFontSize(fontSize);
        webSettings.setJavaScriptEnabled(true);

        String mimeType = "text/html; charset=UTF-8";
        String encoding = "utf-8";
        String htmlText = ((product_description.equals("")) ? "Sin Descripcion" : product_description);

        String text = "<html><head>"
                + "<style type=\"text/css\">body{color: #525252;}"
                + "</style></head>"
                + "<body>"
                + htmlText
                + "</body></html>";

        String text_rtl = "<html dir='rtl'><head>"
                + "<style type=\"text/css\">body{color: #525252;}"
                + "</style></head>"
                + "<body>"
                + htmlText
                + "</body></html>";

        if (Config.ENABLE_RTL_MODE) {
            txt_product_description.loadDataWithBaseURL(null, text_rtl, mimeType, encoding, null);
        } else {
            txt_product_description.loadDataWithBaseURL(null, text, mimeType, encoding, null);
        }

        getDataFromDatabase();

    }

    public void getDataFromDatabase() {

        clearData();

        List<String> data = Arrays.asList(product_lotes.split(","));

        for (int i = 0; i < data.size(); i++) {
            List<String> row = Arrays.asList(data.get(i).split(":"));
            lote_name.add(row.get(0));
            lote_cant.add(row.get(1));
            lote_date.add(row.get(2));

        }



        recyclerView.setAdapter(recyclerAdapterLotes);
        rcViewBnfc.setAdapter(rcBonificado);

    }

    public void clearData() {
       lote_name.clear();
       lote_cant.clear();
       lote_date.clear();
    }

    public void inputDialog() {

        try {
            dbhelper.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }



        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);

        View mView = layoutInflaterAndroid.inflate(R.layout.input_dialog_calculadora, null);

        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setView(mView);

        final TextView edtQuantity  = mView.findViewById(R.id.userInputDialog);
        final TextView edtBonificado = mView.findViewById(R.id.txt_bonificado);
        final TextView edtValor  = mView.findViewById(R.id.id_valor_producto);

        int maxLength = 10;
        edtQuantity.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        edtQuantity.setInputType(InputType.TYPE_CLASS_NUMBER);
        edtBonificado.setText("0");

        final List<String> row_arr = new ArrayList<>();
        for (int i = 0; i < sList.size(); i++) row_arr.add(Arrays.asList(sList.get(i).replace("+", ",").split(",")).get(0));






        mView.findViewById(R.id.button9).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                edtQuantity.setText(edtQuantity.getText().toString().concat("9"));
                int position = row_arr.indexOf(edtQuantity.getText().toString());

                if (position == -1) {
                    edtBonificado.setText("0");
                }else{
                    edtBonificado.setText(sList.get(position));
                }
                double cnt_valor = Double.parseDouble(edtQuantity.getText().toString());
                double vLinea = (product_price * cnt_valor);
                String _cnt_valor = String.format(Locale.ENGLISH, "%1$,.2f", vLinea);
                edtValor.setText(_cnt_valor.concat(" ").concat(currency_code));
            }
        });
        mView.findViewById(R.id.button8).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtQuantity.setText(edtQuantity.getText().toString().concat("8"));
                int position = row_arr.indexOf(edtQuantity.getText().toString());

                if (position == -1) {
                    edtBonificado.setText("0");
                }else{
                    edtBonificado.setText(sList.get(position));
                }
                double cnt_valor = Double.parseDouble(edtQuantity.getText().toString());
                double vLinea = (product_price * cnt_valor);
                String _cnt_valor = String.format(Locale.ENGLISH, "%1$,.2f", vLinea);
                edtValor.setText(_cnt_valor.concat(" ").concat(currency_code));
            }
        });
        mView.findViewById(R.id.button7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtQuantity.setText(edtQuantity.getText().toString().concat("7"));
                int position = row_arr.indexOf(edtQuantity.getText().toString());

                if (position == -1) {
                    edtBonificado.setText("0");
                }else{
                    edtBonificado.setText(sList.get(position));
                }
                double cnt_valor = Double.parseDouble(edtQuantity.getText().toString());
                double vLinea = (product_price * cnt_valor);
                String _cnt_valor = String.format(Locale.ENGLISH, "%1$,.2f", vLinea);
                edtValor.setText(_cnt_valor.concat(" ").concat(currency_code));
            }
        });
        mView.findViewById(R.id.button6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtQuantity.setText(edtQuantity.getText().toString().concat("6"));
                int position = row_arr.indexOf(edtQuantity.getText().toString());

                if (position == -1) {
                    edtBonificado.setText("0");
                }else{
                    edtBonificado.setText(sList.get(position));
                }
                double cnt_valor = Double.parseDouble(edtQuantity.getText().toString());
                double vLinea = (product_price * cnt_valor);
                String _cnt_valor = String.format(Locale.ENGLISH, "%1$,.2f", vLinea);
                edtValor.setText(_cnt_valor.concat(" ").concat(currency_code));
            }
        });
        mView.findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtQuantity.setText(edtQuantity.getText().toString().concat("5"));
                int position = row_arr.indexOf(edtQuantity.getText().toString());

                if (position == -1) {
                    edtBonificado.setText("0");
                }else{
                    edtBonificado.setText(sList.get(position));
                }
                double cnt_valor = Double.parseDouble(edtQuantity.getText().toString());
                double vLinea = (product_price * cnt_valor);
                String _cnt_valor = String.format(Locale.ENGLISH, "%1$,.2f", vLinea);
                edtValor.setText(_cnt_valor.concat(" ").concat(currency_code));
            }
        });
        mView.findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtQuantity.setText(edtQuantity.getText().toString().concat("4"));
                int position = row_arr.indexOf(edtQuantity.getText().toString());

                if (position == -1) {
                    edtBonificado.setText("0");
                }else{
                    edtBonificado.setText(sList.get(position));
                }
                double cnt_valor = Double.parseDouble(edtQuantity.getText().toString());
                double vLinea = (product_price * cnt_valor);
                String _cnt_valor = String.format(Locale.ENGLISH, "%1$,.2f", vLinea);
                edtValor.setText(_cnt_valor.concat(" ").concat(currency_code));
            }
        });
        mView.findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtQuantity.setText(edtQuantity.getText().toString().concat("3"));
                int position = row_arr.indexOf(edtQuantity.getText().toString());

                if (position == -1) {
                    edtBonificado.setText("0");
                }else{
                    edtBonificado.setText(sList.get(position));
                }
                double cnt_valor = Double.parseDouble(edtQuantity.getText().toString());
                double vLinea = (product_price * cnt_valor);
                String _cnt_valor = String.format(Locale.ENGLISH, "%1$,.2f", vLinea);
                edtValor.setText(_cnt_valor.concat(" ").concat(currency_code));
            }
        });
        mView.findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtQuantity.setText(edtQuantity.getText().toString().concat("2"));
                int position = row_arr.indexOf(edtQuantity.getText().toString());

                if (position == -1) {
                    edtBonificado.setText("0");
                }else{
                    edtBonificado.setText(sList.get(position));
                }
                double cnt_valor = Double.parseDouble(edtQuantity.getText().toString());
                double vLinea = (product_price * cnt_valor);
                String _cnt_valor = String.format(Locale.ENGLISH, "%1$,.2f", vLinea);
                edtValor.setText(_cnt_valor.concat(" ").concat(currency_code));
            }
        });
        mView.findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtQuantity.setText(edtQuantity.getText().toString().concat("1"));
                int position = row_arr.indexOf(edtQuantity.getText().toString());

                if (position == -1) {
                    edtBonificado.setText("0");
                }else{
                    edtBonificado.setText(sList.get(position));
                }
                double cnt_valor = Double.parseDouble(edtQuantity.getText().toString());
                double vLinea = (product_price * cnt_valor);
                String _cnt_valor = String.format(Locale.ENGLISH, "%1$,.2f", vLinea);
                edtValor.setText(_cnt_valor.concat(" ").concat(currency_code));
            }
        });
        mView.findViewById(R.id.button0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtQuantity.setText(edtQuantity.getText().toString().concat("0"));
                int position = row_arr.indexOf(edtQuantity.getText().toString());

                if (position == -1) {
                    edtBonificado.setText("0");
                }else{
                    edtBonificado.setText(sList.get(position));
                }
                double cnt_valor = Double.parseDouble(edtQuantity.getText().toString());
                double vLinea = (product_price * cnt_valor);
                String _cnt_valor = String.format(Locale.ENGLISH, "%1$,.2f", vLinea);
                edtValor.setText(_cnt_valor.concat(" ").concat(currency_code));
            }
        });

        mView.findViewById(R.id.button_ce).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtQuantity.setText("");
                edtValor.setText("0.00 NIO");
                int position = row_arr.indexOf(edtQuantity.getText().toString());

                if (position == -1) {
                    edtBonificado.setText("0");
                }else{
                    edtBonificado.setText(sList.get(position));
                }


            }
        });
        mView.findViewById(R.id.button_equals).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });





        alert.setCancelable(false);

        alert.setPositiveButton(R.string.dialog_option_add, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String temp = edtQuantity.getText().toString();

                if (!temp.equalsIgnoreCase("")) {

                  addProducto(temp,edtBonificado.getText().toString());

                } else {
                    dialog.cancel();
                }
            }
        });

        alert.setNegativeButton(R.string.dialog_option_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = alert.create();
        alertDialog.show();

    }



    private void addProducto(String cnt,String Bonificado) {

        int quantity = 0;

        quantity = Integer.parseInt(cnt);

        if (quantity <= 0) {
            ShowDialog("Alerta",
                    context.getResources().getString(R.string.msg_stock_below_0),
                    R.color.red_light);
        } else if (quantity > product_quantity) {
            ShowDialog("Alerta",context.getResources().getString(R.string.msg_stock_not_enough),R.color.red_light);
        } else {
            ShowDialog("Exito",context.getResources().getString(R.string.msg_success_add_cart),R.color.light_green_400);

            /*
             * HAY OCACIONES EN QUE EL CLIENTE FALTURA EL MISMO ARTICULO CON LAS MISMA ESPEFICACIONES
             * ESTO CON EL FIN DE APROVECHAR EL BONIFICADO, POR ESO SE BONITE ESTA VALIDACION, DE QUE SI EXISTE YA EL ITEM
             * EN EL CARRITO
             * */

                       /* if (dbhelper.isDataExist(product_id)) {
                            dbhelper.updateData(product_id, quantity, (product_price * quantity));
                        } else {

                        }*/

            dbhelper.addData(product_id, product_name, quantity, (product_price * quantity), currency_code, product_image,Bonificado);
        }
    }

    private void makeJsonObjectRequest() {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, GET_TAX_CURRENCY, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("INFO", response.toString());
                try {
                    resp_tax = response.getDouble("tax");
                    resp_currency_code = response.getString("currency_code");

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("INFO", "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        MyApplication.getInstance().addToRequestQueue(jsonObjReq);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.cart:
               Intent intent = new Intent(getApplicationContext(), ActivityCart.class);
                intent.putExtra("tax", resp_tax);
                intent.putExtra("currency_code", resp_currency_code);
                startActivity(intent);
                break;


            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    @TargetApi(16)
    private void requestStoragePermission() {
        Dexter.withActivity(ActivityProductDetail.this)
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            (new ShareTask(ActivityProductDetail.this)).execute(Config.ADMIN_PANEL_URL + "/upload/product/" + product_image);
                        }
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Error occurred! " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityProductDetail.this);
        builder.setTitle(R.string.permission_msg);
        builder.setMessage(R.string.permission_storage);
        builder.setPositiveButton(R.string.dialog_settings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton(R.string.dialog_option_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    public class ShareTask extends AsyncTask<String, String, String> {
        private Context context;
        private ProgressDialog pDialog;
        URL myFileUrl;
        Bitmap bmImg = null;
        File file;

        public ShareTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage(getString(R.string.loading_msg));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            try {
                myFileUrl = new URL(args[0]);
                HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                bmImg = BitmapFactory.decodeStream(is);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                String path = myFileUrl.getPath();
                String idStr = path.substring(path.lastIndexOf('/') + 1);
                File filepath = Environment.getExternalStorageDirectory();
                File dir = new File(filepath.getAbsolutePath() + "/" + getResources().getString(R.string.app_name) + "/");
                dir.mkdirs();
                String fileName = idStr;
                file = new File(dir, fileName);
                FileOutputStream fos = new FileOutputStream(file);
                bmImg.compress(Bitmap.CompressFormat.PNG, 99, fos);
                fos.flush();
                fos.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String args) {
            if (Config.ENABLE_DECIMAL_ROUNDING) {
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getAbsolutePath()));
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_product_section_one) + " " + product_name + " " + getString(R.string.share_product_section_two) + " " + String.format(Locale.ENGLISH, "%1$,.0f", product_price) + " " + currency_code + getString(R.string.share_product_section_three) + "\n" + "https://play.google.com/store/apps/details?id=" + getPackageName());
                startActivity(Intent.createChooser(intent, "Share Image"));
                pDialog.dismiss();
            } else {
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getAbsolutePath()));
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_product_section_one) + " " + product_name + " " + getString(R.string.share_product_section_two) + " " + product_price + " " + currency_code + getString(R.string.share_product_section_three) + "\n" + "https://play.google.com/store/apps/details?id=" + getPackageName());
                startActivity(Intent.createChooser(intent, "Share Image"));
                pDialog.dismiss();
            }
        }
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
