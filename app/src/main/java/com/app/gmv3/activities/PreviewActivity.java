package com.app.gmv3.activities;

import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.app.gmv3.BuildConfig;
import com.app.gmv3.adapters.AdapterPdfDocument;
import com.app.gmv3.models.PEDIDO_LINEAS;
import com.app.gmv3.utilities.DBHelper;
import com.app.gmv3.R;
import static com.app.gmv3.utilities.Constant.GET_DETALLE_PEDIDO;
import static com.app.gmv3.utilities.Constant.GET_RECENT_PRODUCT;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class PreviewActivity extends AppCompatActivity {
    DBHelper dbhelper;
    String num_pedido;

    TextView txt_num_pedido;
    TextView txt_code_pedido;
    TextView txt_name;
    TextView txt_code_client;
    TextView txt_dir;
    TextView txt_total;
    TextView txt_date;

    String APP_KEY = BuildConfig.APP_KEY;


    NestedScrollView nestedScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.checkout_order_list);
        }
        nestedScrollView = findViewById(R.id.id_nested_scroll_view);

        txt_num_pedido = findViewById(R.id.txt_num_pedido);
        txt_code_pedido = findViewById(R.id.txt_code_pedido);
        txt_name = findViewById(R.id.txt_name);
        txt_code_client = findViewById(R.id.txt_code_client);
        txt_dir = findViewById(R.id.txt_dir);
        txt_total = findViewById(R.id.txt_total);
        txt_date = findViewById(R.id.txt_fecha);

        (findViewById(R.id.btn_print)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                printNestedScrollView(nestedScrollView);

            }
        });

        initDB();
    }

    public void initDB(){
        Intent intent = getIntent();

        num_pedido = intent.getStringExtra("num_pedido");

        dbhelper = new DBHelper(this);

        try {
            dbhelper.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }
        api_get_pedido();
    }
    private void api_get_pedido() {

        String URL  = GET_DETALLE_PEDIDO.concat(num_pedido).concat("&APP_KEY=").concat(APP_KEY);

        JsonArrayRequest request = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response == null) {
                    Toast.makeText(getApplicationContext(), R.string.failed_fetch_data, Toast.LENGTH_LONG).show();
                    return;
                }
                List<PEDIDO_LINEAS> pedido = new Gson().fromJson(response.toString(), new TypeToken<List<PEDIDO_LINEAS>>() {}.getType());

                UIPedido(pedido);
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

    private void UIPedido(List<PEDIDO_LINEAS> DatosPedido){

        txt_num_pedido.setText(("PEDIDO N.O: ").concat(DatosPedido.get(0).getPEDIDO_NUM()));
        txt_code_pedido.setText(DatosPedido.get(0).getPEDIDO_ID());
        txt_name.setText(DatosPedido.get(0).getPEDIDO_NOMBRE());
        txt_code_client.setText(DatosPedido.get(0).getPEDIDO_CLIENTE());
        txt_dir.setText(DatosPedido.get(0).getPEDIDO_DIR());
        txt_total.setText(DatosPedido.get(0).getPEDIDO_TOTAL().replace("NIO",""));
        txt_date.setText(DatosPedido.get(0).getPEDIDO_FECHA());

        String[] data_order_list    = DatosPedido.get(0).getPEDIDO_ORDEN().split("],");
        int cLineas                 = data_order_list.length -1;
        String str_detalles_linea = "";

        for (int i = 0; i < cLineas; i++) {

            String[] Lineas_detalles    = data_order_list[i].split(";");

            String Quantity     = Lineas_detalles[0].replace("[","");
            //String prod_cod    = Lineas_detalles[1];
            String Menu_name     = Lineas_detalles[2];
            //String Bonificado   = Lineas_detalles[3];
            String _Sub_total_price   = Lineas_detalles[4].replace("NIO","");

            //str_detalles_linea += (Quantity + " [ " + prod_cod + " ] " + Menu_name + " " + Bonificado + " " + _Sub_total_price  + "\n\n");
            str_detalles_linea += (Menu_name + " | " + Quantity + " | " + _Sub_total_price + "\n\n");

        }

        ((TextView) findViewById(R.id.product_name)).setText(str_detalles_linea);

        String SubTotal = data_order_list[cLineas].replace(";","").replace("[","").replace("]","");
        SubTotal = SubTotal.replace("Orden : ","SubTotal : ").replace("NIO","");

        //String SubTotal = ("SubTotal: ").concat(DatosPedido.get(0).getPEDIDO_TOTAL()).concat("\n\n");

        ((TextView) findViewById(R.id.id_resumen)).setText(SubTotal);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_print, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;

            case R.id.btn_mn_print:

                printNestedScrollView(nestedScrollView);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    public void printNestedScrollView(NestedScrollView nestedScrollView) {
        // Primero, medir y disposición del contenido del NestedScrollView
        nestedScrollView.measure(
                View.MeasureSpec.makeMeasureSpec(nestedScrollView.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );
        nestedScrollView.layout(0, 0, nestedScrollView.getMeasuredWidth(), nestedScrollView.getMeasuredHeight());


        // Obtener bitmap del NestedScrollView
        Bitmap bitmap = getBitmapFromView(nestedScrollView);

        // Guardar bitmap como PDF
        File pdfFile = new File(getExternalFilesDir(null), "document.pdf");
        try {
            saveBitmapAsPDF(bitmap, pdfFile);
            printPDF(pdfFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private Bitmap getBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
    private void saveBitmapAsPDF(Bitmap bitmap, File file) throws IOException {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        canvas.drawBitmap(bitmap, 0, 0, null);
        document.finishPage(page);

        try (FileOutputStream out = new FileOutputStream(file)) {
            document.writeTo(out);
        }

        document.close();
    }
    private void printPDF(File file) {
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        try {
            PrintDocumentAdapter pda = new AdapterPdfDocument(this, file.getAbsolutePath());
            printManager.print("Document", pda, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }






}