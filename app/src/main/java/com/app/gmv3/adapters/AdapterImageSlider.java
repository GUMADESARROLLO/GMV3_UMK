package com.app.gmv3.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.app.gmv3.R;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.app.gmv3.models.Banner;
import com.app.gmv3.utilities.Utils;
import com.balysv.materialripple.MaterialRippleLayout;

import java.util.List;

public class AdapterImageSlider extends PagerAdapter {

    private Activity act;
    private List<Banner> items;

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Banner obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    // constructor
    public AdapterImageSlider(Activity activity, List<Banner> items) {
        this.act = activity;
        this.items = items;
    }

    @Override
    public int getCount() {
        return this.items.size();
    }

    public Banner getItem(int pos) {
        return items.get(pos);
    }

    public void setItems(List<Banner> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final Banner o = items.get(position);
        LayoutInflater inflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.item_slider_image, container, false);

        ImageView image =  v.findViewById(R.id.image);
        MaterialRippleLayout lyt_parent = v.findViewById(R.id.lyt_parent);

        //Utils.displayImageOriginal(act, image, o.getBanner_image());


        lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener == null) return;
                mOnItemClickListener.onItemClick(view, items.get(position), position);
            }
        });

        ((ViewPager) container).addView(v);


        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);

    }

}