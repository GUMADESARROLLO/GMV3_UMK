package com.app.gmv3.utilities;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.squareup.picasso.Transformation;

public class ImageTransformation {

    public static Transformation getTransformation(final ImageView imageView) {
        return new Transformation() {
            @Override
            public Bitmap transform(Bitmap source) {
                int targetWidth = imageView.getWidth();

                double imageViewRatio = (double) source.getHeight() / (double) source.getWidth();
                int targetHeight = (int) (targetWidth * imageViewRatio);

                Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);

                if (result != source) {
                    source.recycle();   
                }
                return result;
            }

            @Override
            public String key() {
                return "transformation" + " desiredWidth";
            }
        };
    }
    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;

        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }
}