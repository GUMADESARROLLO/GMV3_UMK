package com.app.gmv3.adapters;

import android.content.Context;
import android.content.res.TypedArray;

import com.app.gmv3.models.Image;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.app.gmv3.R;
import com.app.gmv3.models.News;

public class DataGenerator {
    private static Random r = new Random();
    public static int randInt(int max) {
        int min = 0;
        return r.nextInt((max - min) + 1) + min;
    }
    /**
     * Generate dummy data image
     *
     * @param ctx android context
     * @return list of object
     */
    public static List<Image> getImageDate(Context ctx) {
        List<Image> items = new ArrayList<>();
        TypedArray drw_arr = ctx.getResources().obtainTypedArray(R.array.sample_images);
        String name_arr[] = ctx.getResources().getStringArray(R.array.sample_images_name);
        String date_arr[] = ctx.getResources().getStringArray(R.array.general_date);
        for (int i = 0; i < drw_arr.length(); i++) {
            Image obj = new Image();
            obj.image = drw_arr.getResourceId(i, -1);
            obj.name = name_arr[i];
            obj.brief = date_arr[randInt(date_arr.length - 1)];
            obj.counter = r.nextBoolean() ? randInt(500) : null;
            obj.imageDrw = ctx.getResources().getDrawable(obj.image);
            items.add(obj);
        }
        Collections.shuffle(items);
        return items;
    }
    /**
     * Generate dummy data News
     *
     * @param ctx   android context
     * @param count total result data
     * @return list of object
     */
    public static List<News> getNewsData(Context ctx, int count) {

        List<News> items = new ArrayList<>();

        List<Integer> images = getAllImages(ctx);
        List<String> titles = getStringsMedium(ctx);
        List<String> full_date = getFullDate(ctx);
        String cat[] = ctx.getResources().getStringArray(R.array.news_category);

        for (int i = 0; i < count; i++) {
            News obj = new News();
            obj.image = images.get(getRandomIndex(images.size()));
            obj.title = titles.get(getRandomIndex(titles.size()));
            obj.subtitle = cat[getRandomIndex(cat.length)];
            obj.date = full_date.get(getRandomIndex(full_date.size()));
            items.add(obj);
        }
        return items;
    }
    public static List<String> getFullDate(Context ctx) {
        List<String> items = new ArrayList<>();
        String name_arr[] = ctx.getResources().getStringArray(R.array.full_date);
        for (String s : name_arr) items.add(s);
        Collections.shuffle(items);
        return items;
    }
    public static List<String> getStringsMedium(Context ctx) {
        List<String> items = new ArrayList<>();
        String name_arr[] = ctx.getResources().getStringArray(R.array.strings_medium);
        for (String s : name_arr) items.add(s);
        Collections.shuffle(items);
        return items;
    }
    public static List<Integer> getAllImages(Context ctx) {
        List<Integer> items = new ArrayList<>();
        TypedArray drw_arr = ctx.getResources().obtainTypedArray(R.array.all_images);
        for (int i = 0; i < drw_arr.length(); i++) {
            items.add(drw_arr.getResourceId(i, -1));
        }
        Collections.shuffle(items);
        return items;
    }
    private static int getRandomIndex(int max) {
        return r.nextInt(max - 1);
    }
}
