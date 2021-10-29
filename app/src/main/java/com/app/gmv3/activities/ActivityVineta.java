package com.app.gmv3.activities;

import static com.app.gmv3.utilities.Constant.GET_VINETA;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.app.gmv3.R;
import com.app.gmv3.adapters.AdapterLotesSearch;
import com.app.gmv3.adapters.AdapterSuggestionSearch;
import com.app.gmv3.adapters.AdapterVinetas;
import com.app.gmv3.models.Vineta;
import com.app.gmv3.utilities.DBHelper;
import com.app.gmv3.utilities.Utils;
import com.app.gmv3.utilities.ViewAnimation;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;


public class ActivityVineta extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText et_search;
    private ImageButton bt_procesar;

    private ProgressBar progress_bar;
    private LinearLayout lyt_no_result;

    private RecyclerView recyclerView;
    private List<Vineta> ListaVineta;
    private AdapterVinetas mAdapter;

    Intent intent;
    String Cod_cliente, name_cliente, dir_cliente;

    public static DBHelper dbhelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vinneta);

        initToolbar();
        initComponent();
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Utils.setSystemBarColor_Search(this, R.color.grey_5);
        Utils.setSystemBarLight(this);
    }

    private void initComponent() {
        intent = getIntent();

        Cod_cliente     = intent.getStringExtra("cod_cliente");
        name_cliente    = intent.getStringExtra("cliente_nombre");
        dir_cliente     = intent.getStringExtra("cliente_direcc");

        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        lyt_no_result = (LinearLayout) findViewById(R.id.lyt_no_result);

        dbhelper = new DBHelper(this);

        try {
            dbhelper.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }

        et_search = (EditText) findViewById(R.id.et_search);
        et_search.setEnabled(false);

        bt_procesar = (ImageButton) findViewById(R.id.bt_clear);

        recyclerView = findViewById(R.id.id_rv_resultado);

        ListaVineta = new ArrayList<>();
        mAdapter = new AdapterVinetas(ListaVineta);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(),  new ActivityPerfilCliente.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        FormAddVineta(ListaVineta.get(position));
                    }
                }, 400);
            }
        }));

        bt_procesar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityVineta.this, ActivityCartVineta.class);
                intent.putExtra("cod_cliente",Cod_cliente);
                intent.putExtra("cliente_nombre",name_cliente);
                intent.putExtra("cliente_direcc",dir_cliente);
                startActivity(intent);
            }
        });


        hideKeyboard();
        searchAction();
    }


    public void FormAddVineta(final Vineta vineta) {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_add_vineta);
        dialog.setCancelable(true);

        final EditText edt_cantidad = dialog.findViewById(R.id.edt_cantidad_vineta);

        edt_cantidad.setText(vineta.getmCantidad());

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

                if ( Integer.parseInt(edt_cantidad.getText().toString()) > Integer.parseInt(vineta.getmCantidad())){
                    Toast.makeText(getApplicationContext(), "Excede la cantidad disponible.", Toast.LENGTH_SHORT).show();
                }else{
                    addVineta(vineta , edt_cantidad.getText().toString());
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
    private void addVineta(Vineta vnt , String cnt) {
        int quantity = 0;

        quantity = Integer.parseInt(cnt);

        if (quantity <= 0) {
            ShowDialog("Alerta","No puede ser Cero",R.color.red_light);
        } else if (quantity > Integer.parseInt(vnt.getmCantidad())) {
            ShowDialog("Alerta","Excede la vantidad de la viñeta",R.color.red_light);
        } else {
            ShowDialog("Exito","Viñeta Agregada",R.color.light_green_400);

            ArrayList<ArrayList<Object>> data = dbhelper.getAllDataVinete(Cod_cliente);

            int id_tabla = (data.size() + 1) ;

            dbhelper.addVineta(
                    vnt.getmFactura(),
                    quantity,
                    vnt.getmVineta(),
                    vnt.getmValor(),
                    (Double.parseDouble(vnt.getmValor()) * quantity),
                    id_tabla,
                    vnt.getmLinea(),
                    Cod_cliente
            );
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
        lyt.setBackgroundColor(getApplicationContext().getResources().getColor(color));;

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

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void searchAction() {
        progress_bar.setVisibility(View.VISIBLE);
        lyt_no_result.setVisibility(View.GONE);

        fetchLotes();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                // AL MOMENTO DE SALID DE LA APLICACION, SI HAY INFORMACION EN LA TABLA, MANDAR UNA ALERTA DE QUE SE BORRARA SI NO LA ENVIA
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }
    private void fetchLotes() {


        JsonArrayRequest stringRequest = new JsonArrayRequest( GET_VINETA + Cod_cliente , new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(final JSONArray ServerResponse) {

                final List<Vineta> Found_Lotes = new Gson().fromJson(ServerResponse.toString(), new TypeToken<List<Vineta>>() {
                }.getType());


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ListaVineta.clear();
                        ListaVineta.addAll(Found_Lotes);
                        if (ListaVineta.size() > 0) {
                            progress_bar.setVisibility(View.GONE);
                            lyt_no_result.setVisibility(View.INVISIBLE);
                        } else {
                            progress_bar.setVisibility(View.GONE);
                            lyt_no_result.setVisibility(View.VISIBLE);
                        }

                        // refreshing recycler view
                        mAdapter.notifyDataSetChanged();
                    }
                }, 2000);


            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getApplicationContext(), volleyError.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {

        };

        RequestQueue requestQueue = Volley.newRequestQueue(ActivityVineta.this);
        requestQueue.add(stringRequest);

    }
    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ActivityPerfilCliente.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final ActivityPerfilCliente.ClickListener clickListener) {

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
}
