package com.app.gmv3.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.app.gmv3.R;
import com.app.gmv3.activities.ActivityDetailsPromo;
import com.app.gmv3.activities.ActivityImageDetail;
import com.app.gmv3.activities.MyApplication;
import com.app.gmv3.adapters.AdapterListNews;
import com.app.gmv3.models.Banner;
import com.app.gmv3.utilities.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;


import static com.app.gmv3.utilities.Constant.GET_BANNER;
import static com.app.gmv3.utilities.Constant.GET_NEWS;


public class FragmentPromos extends Fragment {
    View view;
    private Runnable runnable = null;
    private Handler handler = new Handler();

    private RecyclerView recyclerViewNews;
    private AdapterListNews mAdapter;

    private List<Banner> bannerList;
    private List<Banner> NewsList;


    RelativeLayout ryt_empty_history;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_promos, container, false);
        initToolbar();
        initComponent();
        return view;
    }
    private void initToolbar() {
    }

    private void initComponent() {


        bannerList = new ArrayList<>();
        NewsList = new ArrayList<>();

        recyclerViewNews = view.findViewById(R.id.recyclerView);
        recyclerViewNews.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewNews.setHasFixedSize(true);
        ryt_empty_history = (RelativeLayout) view.findViewById(R.id.id_no_feed);


        mAdapter = new AdapterListNews(getContext(), NewsList, R.layout.item_news);
        recyclerViewNews.setAdapter(mAdapter);


//        mAdapter.setOnItemClickListener(new AdapterListNews.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, Banner obj, int position) {
//                Intent intent = new Intent(getActivity(), ActivityDetailsPromo.class);
//                intent.putExtra("Promo_descripcion", obj.getBanner_description());
//                intent.putExtra("promo_imagen", obj.getBanner_image());
//                startActivity(intent);
//            }
//
//
//        });
        onRefresh();





    }


    private void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Utils.isNetworkAvailable(getActivity())) {
                    fetchData();
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }

            }
        }, 1500);
    }
    private void fetchData() {
        JsonArrayRequest News = new JsonArrayRequest(GET_NEWS, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response == null) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.failed_fetch_data), Toast.LENGTH_LONG).show();
                    return;
                }
                List<Banner> items = new Gson().fromJson(response.toString(), new TypeToken<List<Banner>>() {
                }.getType());

                Log.e("TAG_ERROR", items.get(0).getDescripcion() );

                NewsList.clear();
                NewsList.addAll(items);

                if (NewsList.size() > 0 ){
                    //ryt_empty_history.setVisibility(View.GONE);
                    StringBuilder TituloBuilder = new StringBuilder();
                    for (int i = 0; i < items.size(); i++) {
                        String dtIni = (String) DateFormat.format("EEE dd MMM yyyy", Utils.timeStringtoMilis(items.get(i).getFechaInicio()));
                        String dtEnd = (String) DateFormat.format("EEE dd MMM yyyy", Utils.timeStringtoMilis(items.get(i).getFechaFinal()));

                        TituloBuilder.append(items.get(i).getTitulo());
                        TituloBuilder.append("\n");
                        TituloBuilder.append(("Del ").concat(dtIni).concat(" al ").concat(dtEnd));
                        TituloBuilder.append("\n\n");
                        TituloBuilder.append(items.get(i).getDescripcion());
                        TituloBuilder.append("\n\n");

                    }
                    String Titulo = TituloBuilder.toString();
                    ShowNotifications(Titulo);
                }else{
                    //ryt_empty_history.setVisibility(View.VISIBLE);

                }

                mAdapter.notifyDataSetChanged();



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("INFO", "Error: " + error.getMessage());
                Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        MyApplication.getInstance().addToRequestQueue(News);
    }

    private void ShowNotifications(String Body) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_promociones);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        ((TextView) dialog.findViewById(R.id.Id_Content)).setText(Body);


        ((ImageButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ((Button) dialog.findViewById(R.id.bt_accept)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "Click", Toast.LENGTH_SHORT).show();
                dialog.dismiss();

            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search, menu);


        super.onCreateOptionsMenu(menu, inflater);
    }



}