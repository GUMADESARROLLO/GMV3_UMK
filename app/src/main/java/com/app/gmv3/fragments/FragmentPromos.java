package com.app.gmv3.fragments;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.app.gmv3.R;
import com.app.gmv3.activities.ActivityDetailsPromo;
import com.app.gmv3.activities.ActivityImageDetail;
import com.app.gmv3.activities.MyApplication;
import com.app.gmv3.adapters.AdapterImageSlider;
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

    private ViewPager viewPager;
    private LinearLayout layout_dots;
    private AdapterImageSlider adapterImageSlider;
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


        mAdapter = new AdapterListNews(getContext(), NewsList, R.layout.item_news_light);
        recyclerViewNews.setAdapter(mAdapter);

        layout_dots = view.findViewById(R.id.layout_dots);
        viewPager =  view.findViewById(R.id.pager);
        adapterImageSlider = new AdapterImageSlider(getActivity(), bannerList);

        mAdapter.setOnItemClickListener(new AdapterListNews.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Banner obj, int position) {
                Intent intent = new Intent(getActivity(), ActivityDetailsPromo.class);
                intent.putExtra("Promo_descripcion", obj.getBanner_description());
                intent.putExtra("promo_imagen", obj.getBanner_image());
                startActivity(intent);
            }


        });

        adapterImageSlider.setOnItemClickListener(new AdapterImageSlider.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Banner obj, int position) {


                Intent intent = new Intent(getActivity(), ActivityImageDetail.class);
                intent.putExtra("image", obj.getBanner_image());
                intent.putExtra("root", "banners");
                startActivity(intent);
            }


        });


        fetchData();
        onRefresh();


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int pos, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int pos) {
                addBottomDots(layout_dots, adapterImageSlider.getCount(), pos);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });




    }
    private void addBottomDots(LinearLayout layout_dots, int size, int current) {
        ImageView[] dots = new ImageView[size];

        layout_dots.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(getActivity());
            int width_height = 15;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(width_height, width_height));
            params.setMargins(10, 0, 10, 0);
            dots[i].setLayoutParams(params);
            dots[i].setImageResource(R.drawable.shape_circle_outline);
            dots[i].setColorFilter(ContextCompat.getColor(getContext(), R.color.grey_soft), PorterDuff.Mode.SRC_ATOP);
            layout_dots.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[current].setImageResource(R.drawable.shape_circle);
            dots[current].setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        }
    }
    private void startAutoSlider(final int count) {
        runnable = new Runnable() {
            @Override
            public void run() {
                int pos = viewPager.getCurrentItem();
                pos = pos + 1;
                if (pos >= count) pos = 0;
                viewPager.setCurrentItem(pos);
                handler.postDelayed(runnable, 7000);
            }
        };
        handler.postDelayed(runnable, 6000);
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
        JsonArrayRequest rqBanner = new JsonArrayRequest(GET_BANNER, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response == null) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.failed_fetch_data), Toast.LENGTH_LONG).show();
                    return;
                }
                 List<Banner> items = new Gson().fromJson(response.toString(), new TypeToken<List<Banner>>() {
                }.getType());

                bannerList.clear();
                bannerList.addAll(items);

                adapterImageSlider.setItems(bannerList);
                viewPager.setAdapter(adapterImageSlider);
                viewPager.setCurrentItem(0);
                addBottomDots(layout_dots, adapterImageSlider.getCount(), 0);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("INFO", "Error: " + error.getMessage());
                Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        startAutoSlider(adapterImageSlider.getCount());
        MyApplication.getInstance().addToRequestQueue(rqBanner);




        JsonArrayRequest rqNews = new JsonArrayRequest(GET_NEWS, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response == null) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.failed_fetch_data), Toast.LENGTH_LONG).show();
                    return;
                }
                List<Banner> items = new Gson().fromJson(response.toString(), new TypeToken<List<Banner>>() {
                }.getType());

                NewsList.clear();
                NewsList.addAll(items);

                if (NewsList.size() > 0 ){
                    ryt_empty_history.setVisibility(View.GONE);
                }else{
                    ryt_empty_history.setVisibility(View.VISIBLE);
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
        MyApplication.getInstance().addToRequestQueue(rqNews);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search, menu);


        super.onCreateOptionsMenu(menu, inflater);
    }



}