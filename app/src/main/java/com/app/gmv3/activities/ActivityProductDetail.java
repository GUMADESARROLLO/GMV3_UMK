package com.app.gmv3.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
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
import android.os.StrictMode;
import android.provider.Settings;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.app.gmv3.Config;
import com.app.gmv3.R;
import com.app.gmv3.adapters.RecyclerAdapterLotes;
import com.app.gmv3.models.Lotes;
import com.app.gmv3.utilities.DBHelper;
import com.app.gmv3.utilities.MyDividerItemDecoration;
import com.app.gmv3.utilities.Utils;
import com.app.gmv3.utilities.ViewAnimation;
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
    TextView txt_product_name, txt_product_price, txt_product_quantity,txt_bonificado;
    private String product_name, product_image, category_name, product_status, currency_code, product_description,product_bonificado,product_lotes;
    private double product_price;
    private double product_quantity;
    WebView txt_product_description;
    ImageView img_product_image;
    Button btn_cart;
    public static DBHelper dbhelper;
    final Context context = this;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;
    double resp_tax;
    String resp_currency_code;

    public static ArrayList<String> lote_name = new ArrayList<String>();
    public static ArrayList<String> lote_date = new ArrayList<String>();
    public static ArrayList<String> lote_cant = new ArrayList<String>();
    RecyclerView recyclerView;
    RecyclerAdapterLotes recyclerAdapterLotes;
    List<Lotes> arrayItemLotes;


    private ImageButton bt_toggle_reviews, bt_toggle_warranty, bt_toggle_description;
    private View lyt_expand_reviews, lyt_expand_warranty, lyt_expand_description;
    private NestedScrollView nested_scroll_view;





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

        /*final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }*/

        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("");
        appBarLayout = findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(category_name);
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle("");
                    isShow = false;
                }
            }
        });
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
    }

    public void initComponent() {
        txt_product_name = findViewById(R.id.product_name);
        img_product_image = findViewById(R.id.product_image);
        txt_product_price = findViewById(R.id.product_price);
        txt_product_description = findViewById(R.id.product_description);
        txt_product_quantity = findViewById(R.id.product_quantity);
        txt_bonificado = findViewById(R.id.item_bonificado);
        btn_cart = findViewById(R.id.btn_add_cart);

        recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerAdapterLotes = new RecyclerAdapterLotes(this, arrayItemLotes);


        nested_scroll_view = findViewById(R.id.nested_scroll_view);

        bt_toggle_reviews = findViewById(R.id.bt_toggle_reviews);
        lyt_expand_reviews =  findViewById(R.id.lyt_expand_reviews);
        bt_toggle_reviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSection(view, lyt_expand_reviews);
            }
        });

        bt_toggle_warranty = findViewById(R.id.bt_toggle_warranty);
        lyt_expand_warranty = findViewById(R.id.lyt_expand_warranty);
        bt_toggle_warranty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSection(view, lyt_expand_warranty);
            }
        });

        bt_toggle_description = findViewById(R.id.bt_toggle_description);
        lyt_expand_description = findViewById(R.id.lyt_expand_description);
        bt_toggle_description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSection(view, lyt_expand_description);
            }
        });

        toggleArrow(bt_toggle_description);
        lyt_expand_description.setVisibility(View.VISIBLE);

        ( findViewById(R.id.btn_cart)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputDialog();
            }
        });


    }
    private void toggleSection(View bt, final View lyt) {
        boolean show = toggleArrow(bt);
        if (show) {
            ViewAnimation.expand(lyt, new ViewAnimation.AnimListener() {
                @Override
                public void onFinish() {
                    Utils.nestedScrollTo(nested_scroll_view, lyt);
                }
            });
        } else {
            ViewAnimation.collapse(lyt);
        }
    }

    public boolean toggleArrow(View view) {
        if (view.getRotation() == 0) {
            view.animate().setDuration(200).rotation(180);
            return true;
        } else {
            view.animate().setDuration(200).rotation(0);
            return false;
        }
    }
    public void displayData() {
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
                startActivity(intent);
            }
        });

        String price = String.format(Locale.ENGLISH, "%1$,.2f", product_price);
        txt_product_price.setText(price + " " + currency_code);

        String product_quantity_ = String.format(Locale.ENGLISH, "%1$,.2f", product_quantity);
        txt_product_quantity.setText(product_quantity_ + " " + getString(R.string.txt_items));

       /* if (product_status.equals("Available")) {
            btn_cart.setText(R.string.btn_add_to_cart);
            btn_cart.setBackgroundResource(R.color.available);
            btn_cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    inputDialog();
                }
            });
        } else {
            btn_cart.setEnabled(false);
            btn_cart.setText(R.string.btn_out_of_stock);
            btn_cart.setBackgroundResource(R.color.sold);
        }*/

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
        txt_bonificado.setText(product_bonificado);


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

        View mView = layoutInflaterAndroid.inflate(R.layout.input_dialog, null);

        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setView(mView);

        final EditText edtQuantity = (EditText) mView.findViewById(R.id.userInputDialog);

        List<String> sList = Arrays.asList(product_bonificado.split(","));

        final Spinner spinner = mView.findViewById(R.id.spinner_bonificado);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(ActivityProductDetail.this, R.layout.spinner_item, sList);
        myAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(myAdapter);

        alert.setCancelable(false);
        int maxLength = 3;
        edtQuantity.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        edtQuantity.setInputType(InputType.TYPE_CLASS_NUMBER);

        alert.setPositiveButton(R.string.dialog_option_add, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String temp = edtQuantity.getText().toString();
                int quantity = 0;

                if (!temp.equalsIgnoreCase("")) {

                    quantity = Integer.parseInt(temp);

                    if (quantity <= 0) {
                        Toast.makeText(getApplicationContext(), R.string.msg_stock_below_0, Toast.LENGTH_SHORT).show();
                    } else if (quantity > product_quantity) {
                        Toast.makeText(getApplicationContext(), R.string.msg_stock_not_enough, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.msg_success_add_cart , Toast.LENGTH_SHORT).show();

                        /*
                        * HAY OCACIONES EN QUE EL CLIENTE FALTURA EL MISMO ARTICULO CON LAS MISMA ESPEFICACIONES
                        * ESTO CON EL FIN DE APROVECHAR EL BONIFICADO, POR ESO SE BONITE ESTA VALIDACION, DE QUE SI EXISTE YA EL ITEM
                        * EN EL CARRITO
                        * */

                       /* if (dbhelper.isDataExist(product_id)) {
                            dbhelper.updateData(product_id, quantity, (product_price * quantity));
                        } else {

                        }*/

                        dbhelper.addData(product_id, product_name, quantity, (product_price * quantity), currency_code, product_image,spinner.getSelectedItem().toString());
                    }

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
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.cart:
              /*  Intent intent = new Intent(getApplicationContext(), ActivityCart.class);
                intent.putExtra("tax", resp_tax);
                intent.putExtra("currency_code", resp_currency_code);
                startActivity(intent);*/
                break;

            case R.id.share:
             //   requestStoragePermission();
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
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_product_section_one) + " " + product_name + " " + getString(R.string.share_product_section_two) + " " + String.format(Locale.GERMAN, "%1$,.0f", product_price) + " " + currency_code + getString(R.string.share_product_section_three) + "\n" + "https://play.google.com/store/apps/details?id=" + getPackageName());
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

}
