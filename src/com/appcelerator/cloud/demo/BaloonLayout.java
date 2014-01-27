package com.appcelerator.cloud.demo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class BaloonLayout extends LinearLayout {

	public BaloonLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOrientation(VERTICAL);
		setLayoutParams(new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,android.view.ViewGroup.LayoutParams.FILL_PARENT));
	}
	
    public BaloonLayout(Context context) {
		super(context);
		setOrientation(VERTICAL);
		setLayoutParams(new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,android.view.ViewGroup.LayoutParams.FILL_PARENT));	
		}

	@Override
    protected void dispatchDraw(Canvas canvas) {        
        Paint panelPaint  = new Paint();
        panelPaint.setARGB(0, 0, 0, 0);
                
        RectF panelRect = new RectF();
        panelRect.set(0,0, getMeasuredWidth(), getMeasuredHeight());
        canvas.drawRoundRect(panelRect, 5, 5, panelPaint);
        
        RectF baloonRect = new RectF();
        baloonRect.set(0,0, getMeasuredWidth(), 2*(getMeasuredHeight()/3));
        panelPaint.setARGB(230, 255, 255, 255);        
        canvas.drawRoundRect(baloonRect, 10, 10, panelPaint);
        
        Path baloonTip = new Path();
        baloonTip.moveTo(5*(getMeasuredWidth()/8), 2*(getMeasuredHeight()/3));
        baloonTip.lineTo(getMeasuredWidth()/2, getMeasuredHeight());
        baloonTip.lineTo(3*(getMeasuredWidth()/4), 2*(getMeasuredHeight()/3));
        
        canvas.drawPath(baloonTip, panelPaint);
                
        super.dispatchDraw(canvas);
    }
}