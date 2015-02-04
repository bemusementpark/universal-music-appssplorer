package com.nothing.universalmusic;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.graphics.Canvas;
import android.graphics.Path;


/**
 * Created by andy on 10/01/15.
 */
public class WaveView extends View
{
    private int bgColor;
    private int waveColor;

    public WaveView( Context ctx , AttributeSet attrs )
    {
        super( ctx , attrs );

//        setWillNotDraw(false);
        init();

//        TypedArray a = ctx.getTheme().obtainStyledAttributes(
//                attrs,
//                R.styleable.WaveView,
//                0, 0
//        );

//        try {
//        try {
            //TODO: get these working...
//            bgColor = a.getInt(R.styleable.WaveView_bgColor,0xff333333);
//            waveColor = a.getInt(R.styleable.WaveView_waveColor,0xff666666);
//        }finally {
//            a.recycle();
//        }


    }


    private short[] data;
    public void setData( short [] data )
    {
        this.data = data;

        float h = mBounds.height();

        path = new Path();
        if(pts==null || pts.length * 2 != data.length)pts = new float[data.length * 2];

        path.moveTo( mBounds.right ,mBounds.bottom );
        path.lineTo(mBounds.left,mBounds.bottom);

        for( int i = 0 ; i< data.length ; i ++)
        {
            float x = (float)( i * mBounds.width() / (double) data.length );
            float y = (float) Math.round( h * data[i] / (double) Short.MAX_VALUE + h / 2 );

            path.lineTo(x,y);

        }



        invalidate();
    }


    private Paint mBgPaint;
    private Paint mWavePaint;

    private void init()
    {
        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mWavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mBgPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mWavePaint.setStyle(Paint.Style.FILL_AND_STROKE);

//        mBgPaint.setColor(bgColor);
        mBgPaint.setColor(0xff000000);
//        mWavePaint.setColor(waveColor);
        mWavePaint.setColor(0xffff00ff);


    }




    private Path path;
    private float[] pts;
    @Override
    protected void onDraw(Canvas canvas)
    {

        super.onDraw(canvas);

//        System.out.println("mBounds: " + mBounds.toString() );

//        canvas.drawRect(mBounds,mBgPaint);

//        canvas.drawRect(mBounds.left,mBounds.top,mBounds.width() / 2, mBounds.height() / 2,mWavePaint);

        if(pts != null || path != null)
        {
//            canvas.drawLine(mBounds.width(), mBounds.height()/2,mBounds.width(),mBounds.height(),mWavePaint);
//            canvas.drawLine(mBounds.width(),mBounds.height(),mBounds.left,mBounds.height(),mWavePaint);

//            canvas.drawLines(pts,0,pts.length,mWavePaint);

            canvas.drawPath(path, mWavePaint);
//            canvas.drawLines(pts,2,pts.length-2,mWavePaint);


        }

//        draw(canvas);

//        canvas.drawPoints(,0,,mWavePaint);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w,h,oldw,oldh);
        mBounds = new RectF(0,0,w,h);
    }
    RectF mBounds;

    public void setBgColor(int color)
    {
        bgColor = color;
        invalidate();
        requestLayout();
    }

    public int getBgColor()
    {
        return bgColor;
    }

    public void setWaveColor(int color)
    {
        waveColor = color;
        invalidate();
        requestLayout();
    }

    public int getWaveColor()
    {
        return waveColor;
    }

}