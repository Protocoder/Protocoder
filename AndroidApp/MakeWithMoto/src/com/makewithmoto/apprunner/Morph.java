package com.makewithmoto.apprunner;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.view.View;

/**
 * View that has "pluggable" handlers for callbacks. Reduces the need for subclassing View.
 * @author Mikael Kindborg
 * Email: mikael.kindborg@gmail.com
 * Blog: divineprogrammer@blogspot.com
 * Twitter: @divineprog
 * Copyright (c) Mikael Kindborg 2010
 * Source code license: MIT
 */
public class Morph extends View
{
    OnDrawListener onDrawListener;
    OnMeasureListener onMeasureListener;
    OnSizeChangedListener onSizeChangedListener;
    
    public Morph(Context context)
    {
        super(context);
    }
    
    public Morph setOnDrawListener(OnDrawListener listener) 
    { 
        this.onDrawListener = listener; 
        return this; 
    }
    
    public Morph setOnMeasureListener(OnMeasureListener listener) 
    { 
        this.onMeasureListener = listener; 
        return this; 
    }
    
    public Morph setOnSizeChangedListener(OnSizeChangedListener listener) 
    { 
        this.onSizeChangedListener = listener; 
        return this; 
    }
    
    @SuppressLint("WrongCall")
	@Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        if (null != onDrawListener) { onDrawListener.onDraw(canvas); }
    }
    
    @SuppressLint("WrongCall")
	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        // View.MeasureSpec.AT_MOST      The child can be as large as it wants up to the specified size.
        // View.MeasureSpec.EXACTLY      The parent has determined an exact size for the child.
        // View.MeasureSpec.UNSPECIFIED  The parent has not imposed any constraint on the child.
        if (null != onMeasureListener) 
        {
            Point p = onMeasureListener.onMeasure(widthMeasureSpec, heightMeasureSpec);
            setMeasuredDimension(p.x, p.y); 
        }
        else 
        {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec); 
        }
    }
     
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        if (null != onSizeChangedListener) { onSizeChangedListener.onSizeChanged(w, h, oldw, oldh); }
    }
    
    public interface OnDrawListener 
    {
        void onDraw(Canvas canvas);
    }
    
    public interface OnMeasureListener 
    {
        Point onMeasure(int widthMeasureSpec, int heightMeasureSpec);
    }
    
    public interface OnSizeChangedListener 
    {
        void onSizeChanged(int w, int h, int oldw, int oldh);
    }
}
