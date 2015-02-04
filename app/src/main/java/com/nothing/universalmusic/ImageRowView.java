package com.nothing.universalmusic;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * Created by andy on 4/02/15.
 */
public class ImageRowView extends LinearLayout implements View.OnClickListener {

    private static final String TAG = "com.nothing.UniversalMusic";
    private final Context context;
    private final ArrayList<ImageLoaderView> arrView;

    public int idRow = 0;

    private int nImages = 0;
    private NewSongListener newSongListener;
    private float childAlpha;
    private MainActivity activity;

    public ImageRowView(Context context) {
        this(context, null);
    }

    public ImageRowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageRowView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ImageRowView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;

        arrView = new ArrayList<ImageLoaderView>();

//        try{ init( context ); } catch( Exception e ){};
    }

    public void loadImage(Entry entry , MainActivity activity)
    {
        this.activity = activity;

        ImageLoaderView v = new ImageLoaderView(context);
        v.asyncSetImageEntry(entry , activity);
        addView(v);

        arrView.add( v );

        ++nImages;
        setWeightSum( nImages );

        v.setOnClickListener( this );
    }

    @Override
    public void onClick(View v) {
        ImageLoaderView ilv = (ImageLoaderView) v;
        activity.setChildAlpha( 0.5f );
        v.setAlpha(1f);

        newSongListener.newSong( ilv.getEntry() , idRow );
    }


    public void setNewSongListener(NewSongListener newSongListener) {
        this.newSongListener = newSongListener;
    }

    public void setChildAlpha(float childAlpha) {
        for( ImageLoaderView v : arrView )
        {
            v.setAlpha( childAlpha );
        }
    }

    public interface NewSongListener
    {
        public void newSong( Entry entry , int rowId );
    }
}