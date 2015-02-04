package com.nothing.universalmusic;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by andy on 4/02/15.
 */
public class ImageLoaderView extends ImageView
{
    private static final String TAG = "com.nothing.UniversalMusic";
    private Entry entry;
    private Bitmap bitmap;
    private Activity activity;

    public ImageLoaderView(Context context) {
        this(context, null);
    }

    public ImageLoaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageLoaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ImageLoaderView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setBackgroundColor(0xff000000 + (int) (0xffffff * Math.random()));
        setMinimumHeight(50);
        setMinimumWidth(50);

        setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f));

        init(context);
    }

    private void init(Context context)
    {
        setAdjustViewBounds(true);
    }

    public void asyncSetImageUrl( final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                setImageUrl( url );
            }
        }).start();
    }

    public void setImageUrl(final String url ) {

        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bitmap = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();

            System.out.println(" XXXX : " + bitmap.getWidth() + " " + bitmap.getHeight() );

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setImageBitmap(bitmap);
                }
            });

        } catch (IOException e) {
            Log.e(TAG, "Error getting bitmap", e);
        }
    }

    public Entry getEntry()
    {
        return entry;
    }

    public void asyncSetImageEntry(Entry entry, Activity activity)
    {
        this.activity = activity;
        this.entry = entry;

        asyncSetImageUrl( entry.getImageUrl() );
    }
}