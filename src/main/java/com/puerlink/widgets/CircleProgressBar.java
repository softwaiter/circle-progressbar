package com.puerlink.widgets;

import com.puerlink.circleprogressbar.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

public class CircleProgressBar extends View {

	/** 
     * 画笔对象的引用 
     */  
    private Paint mPaint;  
    
    private PaintFlagsDrawFilter mPaintFlags;
    
    /** 
     * 圆环的颜色 
     */  
    private int mCircleColor;  
    
    /** 
     * 圆环进度的颜色 
     */  
    private int mProgressColor;
    
	/** 
	 * 中间进度百分比的字符串的颜色 
	 */  
	private int mTextColor;  
	    
	/** 
	 * 中间进度百分比的字符串的字体 
	 */  
	private float mTextSize;  
  
    /** 
     * 圆环的宽度 
     */  
    private float mCircleWidth;  
    
	/** 
	 * 最大进度 
	 */  
	private int mMax;
	    
	/** 
	 * 当前进度 
	 */  
	private int mProgress = 0;  
	
	/** 
	 * 是否显示中间的进度  
	 */  
	private boolean mShowText;  
	    
	/** 
	 * 进度的风格，实心或者空心 
	 */  
	private int mStyle;  
	
    public static final int STROKE = 0;  
    public static final int FILL = 1;  
	
	public CircleProgressBar(Context context) {  
        this(context, null);  
    }  
  
    public CircleProgressBar(Context context, AttributeSet attrs) {  
        this(context, attrs, 0);
    }
    
    public CircleProgressBar(Context context, AttributeSet attrs, int defStyle) {  
        super(context, attrs, defStyle);  
          
        mPaint = new Paint();  
        mPaintFlags = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
          
        TypedArray ta = context.obtainStyledAttributes(attrs,  
                R.styleable.CircleProgressBar);
        try
        {
	        //获取自定义属性和默认值  
        	mCircleColor = ta.getColor(R.styleable.CircleProgressBar_circleColor, Color.RED);
        	mProgressColor = ta.getColor(R.styleable.CircleProgressBar_progressColor, Color.GREEN);
        	mTextColor = ta.getColor(R.styleable.CircleProgressBar_textColor, Color.GREEN);
        	mTextSize = ta.getDimension(R.styleable.CircleProgressBar_textSize, 15);
        	mCircleWidth = ta.getDimension(R.styleable.CircleProgressBar_circleWidth, 5);
        	mMax = ta.getInteger(R.styleable.CircleProgressBar_max, 100);
        	mShowText = ta.getBoolean(R.styleable.CircleProgressBar_showText, true);
	        mStyle = ta.getInt(R.styleable.CircleProgressBar_style, 0);
        }
        catch (Exception exp)
        {
        	;
        }
        finally
        {
        	ta.recycle();
        }
    }

    @Override  
    protected void onDraw(Canvas canvas) {  
        super.onDraw(canvas);  
          
        canvas.setDrawFilter(mPaintFlags);
        
        /** 
         * 画最外层的大圆环 
         */  
        int centre = getWidth()/2; //获取圆心的x坐标    
        int radius = (int) (centre - mCircleWidth / 2); //圆环的半径    
        mPaint.setColor(mCircleColor); //设置圆环的颜色  
        mPaint.setStyle(Paint.Style.STROKE); //设置空心   
        mPaint.setStrokeWidth(mCircleWidth); //设置圆环的宽度    
        mPaint.setAntiAlias(true);  //消除锯齿 
        canvas.drawCircle(centre, centre, radius, mPaint); //画出圆环    
          
        /** 
         * 画进度百分比 
         */  
        mPaint.setStrokeWidth(0);   
        mPaint.setColor(mTextColor);  
        mPaint.setTextSize(mTextSize);  
        mPaint.setTypeface(Typeface.DEFAULT_BOLD); //设置字体   
        int percent = (int)(((float)mProgress / (float)mMax) * 100);  //中间的进度百分比，先转换成float在进行除法运算，不然都为0    
        float textWidth = mPaint.measureText(percent + "%");   //测量字体宽度，我们需要根据字体的宽度设置在圆环中间  
          
        if(mShowText && percent != 0 && mStyle == STROKE){  
            canvas.drawText(percent + "%", centre - textWidth / 2, centre + mTextSize / 2, mPaint); //画出进度百分比    
        }  
          
          
        /** 
         * 画圆弧 ，画圆环的进度 
         */  
        //设置进度是实心还是空心  
        mPaint.setStrokeWidth(mCircleWidth); //设置圆环的宽度    
        mPaint.setColor(mProgressColor);  //设置进度的颜色  
        RectF oval = new RectF(centre - radius, centre - radius, centre  
                + radius, centre + radius);  //用于定义的圆弧的形状和大小的界限    
          
        switch (mStyle) {  
	        case STROKE:{  
	        	mPaint.setStyle(Paint.Style.STROKE);  
	            canvas.drawArc(oval, 0, 360 * mProgress / mMax, false, mPaint);  //根据进度画圆弧    
	            break;  
	        }  
	        case FILL:{  
	        	mPaint.setStyle(Paint.Style.FILL_AND_STROKE);  
	            if(mProgress !=0)  
	                canvas.drawArc(oval, 0, 360 * mProgress / mMax, true, mPaint);  //根据进度画圆弧  
	            break;  
	        }  
        }  
          
    }  
    public synchronized int getMax() {  
        return mMax;  
    }  
  
    /** 
     * 设置进度的最大值 
     * @param max 
     */  
    public synchronized void setMax(int max) {  
        if(max < 0){  
            throw new IllegalArgumentException("max not less than 0");  
        }  
        this.mMax = max;  
    }  
  
    /** 
     * 获取进度.需要同步 
     * @return 
     */  
    public synchronized int getProgress() {  
        return mProgress;  
    }  
  
    /** 
     * 设置进度，此为线程安全控件，由于考虑多线的问题，需要同步  
     * 刷新界面调用postInvalidate()能在非UI线程刷新 
     * @param progress 
     */  
    public synchronized void setProgress(int progress) {  
        if(progress < 0){  
            throw new IllegalArgumentException("progress not less than 0");
        }  
        if(progress > mMax){  
            progress = mMax;  
        }  
        if(progress <= mMax){
            this.mProgress = progress;
            postInvalidate();
        }
    }  
      
    public int getCricleColor() {  
        return mCircleColor;  
    }  
  
    public void setCricleColor(int cricleColor) {  
        this.mCircleColor = cricleColor;  
    }  
  
    public int getProgressColor() {  
        return mProgressColor;  
    }  
  
    public void setProgressColor(int progressColor) {  
        this.mProgressColor = progressColor;  
    }  
  
    public int getTextColor() {  
        return mTextColor;
    }  
  
    public void setTextColor(int textColor) {  
        this.mTextColor = textColor;  
    }  
  
    public float getTextSize() {  
        return mTextSize;
    }  
  
    public void setTextSize(float textSize) {  
        this.mTextSize = textSize;  
    }  
  
    public float getCircleWidth() {  
        return mCircleWidth;  
    }  
  
    public void setCircleWidth(float circleWidth) {  
        this.mCircleWidth = circleWidth;  
    }  
    
}
