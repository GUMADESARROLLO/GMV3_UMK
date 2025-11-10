package com.app.gmv3.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.gmv3.Config;
import com.app.gmv3.R;
import com.app.gmv3.adapters.AdapterComentariosIM;

import com.app.gmv3.models.PostComments;
import com.app.gmv3.utilities.ItemOffsetDecoration;
import com.app.gmv3.utilities.Utils;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.app.gmv3.utilities.Constant.GET_COMMENTS_POST_IM;
import static com.app.gmv3.utilities.Constant.POST_IM_ADD_COMMENTS;
import static com.app.gmv3.utilities.Constant.POST_IM_REMOVE_COMMENTS;

public class ActivityComentarioIM extends AppCompatActivity  {

    private RecyclerView recyclerView;
    private List<PostComments> productList;
    private AdapterComentariosIM mAdapter;
    private int id_post;
    Dialog dialog;
    String st_comment,str_name, str_path;
    String post_title, post_comment, post_date, post_img;
    TextView txt_count_comments;
    View lytEmptyHistory;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_im);

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

        post_title      = intent.getStringExtra("post_title");
        post_comment    = intent.getStringExtra("post_comment");
        post_date       = intent.getStringExtra("post_date");
        post_img        = intent.getStringExtra("post_img");

        TextView PostTitle      = findViewById(R.id.txt_title);
        TextView PostComment    = findViewById(R.id.txt_comment);
        TextView PostDate       = findViewById(R.id.txt_date);
        TextView PostCreated    = findViewById(R.id.txt_path_name);
        TextView PostPath       = findViewById(R.id.txt_path_post);
        ImageView PostImg       = findViewById(R.id.img_post);
        txt_count_comments      = findViewById(R.id.txt_count_comments);
        lytEmptyHistory    = findViewById(R.id.lyt_empty_history);

        PostTitle.setText(post_title);
        PostComment.setText(post_comment);
        PostDate.setText(post_date);
        PostCreated.setText(str_name);
        PostPath.setText(str_path);

        if (post_img.equals("ND")){
            PostImg.setVisibility(View.GONE);
        }else{
            Picasso.with(this)
                    .load(post_img)
                    .placeholder(R.drawable.ic_loading)
                    .into(PostImg);
        }



        // toolbar fancy stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recycler_view);
        productList = new ArrayList<>();
        mAdapter = new AdapterComentariosIM(productList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
//        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.item_offset);
//        recyclerView.addItemDecoration(itemDecoration);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        getComment();
        onRefresh();
        txt_count_comments.setText("COMENTARIO ( 0 ) ");
        progressDialog = new ProgressDialog(ActivityComentarioIM.this);




    }

    private void onRefresh() {
        productList.clear();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Utils.isNetworkAvailable(ActivityComentarioIM.this)) {
                    getComment();
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }

            }
        }, 1500);
    }

    public void AddComments(final String Comment) {

        progressDialog.setTitle(getString(R.string.post_submit_title));
        progressDialog.setMessage(getString(R.string.post_submit_msg));
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, POST_IM_ADD_COMMENTS, new Response.Listener<String>() {
            @Override
            public void onResponse(final String ServerResponse) {

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        getComment();
                        onRefresh();
                        dialog.hide();
                        //dialogSuccess();
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
                params.put("IdPost", String.valueOf(id_post));
                params.put("Comment", Comment);
                params.put("CeatedBy", str_name);
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(ActivityComentarioIM.this);
        requestQueue.add(stringRequest);
    }

    public void RemoveComments() {

        progressDialog.setTitle(getString(R.string.alert_title));
        progressDialog.setMessage(getString(R.string.post_submit_msg));
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, POST_IM_REMOVE_COMMENTS, new Response.Listener<String>() {
            @Override
            public void onResponse(final String ServerResponse) {

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        dialogSuccess();
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
                params.put("IdPost", String.valueOf(id_post));
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(ActivityComentarioIM.this);
        requestQueue.add(stringRequest);
    }



    private void getComment() {
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

                if (mAdapter.getItemCount() == 0) {
                    recyclerView.setVisibility(View.INVISIBLE);
                    lytEmptyHistory.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    lytEmptyHistory.setVisibility(View.GONE);
                }

                // refreshing recycler view
                mAdapter.notifyDataSetChanged();
                txt_count_comments.setText(("COMENTARIO ( " ).concat( String.valueOf(mAdapter.getItemCount())).concat(" )") ) ;
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
    public void dialogSuccess() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.alert_title);
        builder.setMessage(R.string.alert_remove_post);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.checkout_option_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
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
                    builder.setTitle(R.string.alert_title);
                    builder.setMessage(R.string.alert_add_comments);
                    builder.setCancelable(false);
                    builder.setPositiveButton(getResources().getString(R.string.dialog_option_yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AddComments(st_comment);
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
        getMenuInflater().inflate(R.menu.menu_article_share_save, menu);

        // Aplica color gris a todos los íconos
        Utils.changeMenuIconColor(menu, getResources().getColor(R.color.grey_60));

        // Luego selecciona solo el ítem "delete" y cámbiale el color individualmente
        MenuItem deleteItem = menu.findItem(R.id.action_form_delete);
        if (deleteItem != null && deleteItem.getIcon() != null) {
            deleteItem.getIcon().mutate()
                    .setColorFilter(getResources().getColor(R.color.red_light), PorterDuff.Mode.SRC_IN);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_form_add_comment:
                AddCommentForm();
                break;
            case R.id.action_form_delete:

                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityComentarioIM.this);
                builder.setTitle(R.string.alert_title);
                builder.setMessage("¿Deseas eliminar este post?");
                builder.setCancelable(false);
                builder.setPositiveButton(getResources().getString(R.string.dialog_option_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RemoveComments();
                        mAdapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton(getResources().getString(R.string.dialog_option_no), null);
                builder.setCancelable(false);
                builder.show();

                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }



}
