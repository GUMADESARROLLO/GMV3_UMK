package com.app.gmv3.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.app.gmv3.Config;
import com.app.gmv3.R;
import com.app.gmv3.adapters.AdapterComentariosIM;

import com.app.gmv3.models.PostComments;
import com.app.gmv3.utilities.ItemOffsetDecoration;
import com.app.gmv3.utilities.Utils;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.app.gmv3.utilities.Constant.GET_COMMENTS_POST_IM;

public class ActivityComentarioIM extends AppCompatActivity  {

    private RecyclerView recyclerView;
    private List<PostComments> productList;
    private AdapterComentariosIM mAdapter;
    private int id_post;

    Dialog dialog;
    String st_comment,str_name, str_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentarios_im);

        if (Config.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_comment);
        dialog.setCancelable(true);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        id_post = intent.getIntExtra("id_post",0);
        str_name = intent.getStringExtra("id_Ruta");
        str_path = intent.getStringExtra("Nombre_ruta");


        // toolbar fancy stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recycler_view);
        productList = new ArrayList<>();
        mAdapter = new AdapterComentariosIM(productList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        (findViewById(R.id.bt_add_comment)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddCommentForm();
            }
        });

        fetchData();
        onRefresh();
        setTitle("COMENTARIO ( 0 ) ") ;

    }

    private void onRefresh() {
        productList.clear();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Utils.isNetworkAvailable(ActivityComentarioIM.this)) {
                    fetchData();
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }

            }
        }, 1500);
    }

    private void fetchData() {
        JsonArrayRequest request = new JsonArrayRequest(GET_COMMENTS_POST_IM + id_post, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response == null) {
                    Toast.makeText(getApplicationContext(), R.string.failed_fetch_data, Toast.LENGTH_LONG).show();
                    return;
                }

                List<PostComments> items = new Gson().fromJson(response.toString(), new TypeToken<List<PostComments>>() {
                }.getType());

                // adding contacts to contacts list
                productList.clear();
                productList.addAll(items);

                // refreshing recycler view
                mAdapter.notifyDataSetChanged();
                setTitle(("COMENTARIO ( " ).concat( String.valueOf(mAdapter.getItemCount())).concat(" )") ) ;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error in getting json
                Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        MyApplication.getInstance().addToRequestQueue(request);
    }
    private void AddCommentForm() {

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        final TextView et_post =  dialog.findViewById(R.id.et_post);
        final TextView lb_date =  dialog.findViewById(R.id.lbl_date);
        final TextView et_Name =  dialog.findViewById(R.id.edt_Nombre);
        final TextView et_Path =  dialog.findViewById(R.id.edt_ruta);
        final AppCompatButton bt_submit = dialog.findViewById(R.id.bt_submit);

        final String Fecha = (String) DateFormat.format("EEE dd MMM yyyy hh:mm aaa'", Calendar.getInstance().getTime());

        lb_date.setText(Fecha);
        et_Name.setText(str_name);
        et_Path.setText(str_path);








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

                st_comment = et_post.getText().toString();

                if (st_comment.equalsIgnoreCase("")) {
                    Snackbar.make(view, R.string.checkout_fill_form, Snackbar.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityComentarioIM.this);
                    builder.setTitle(R.string.post_dialog_title);
                    builder.setMessage(R.string.post_dialog_msg);
                    builder.setCancelable(false);
                    builder.setPositiveButton(getResources().getString(R.string.dialog_option_yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //requestAction();
                        }
                    });
                    builder.setNegativeButton(getResources().getString(R.string.dialog_option_no), null);
                    builder.setCancelable(false);
                    builder.show();
                }
            }
        });



        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }



}
