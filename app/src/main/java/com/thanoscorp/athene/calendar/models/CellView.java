package com.thanoscorp.athene.calendar.models;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class CellView extends View implements View.OnTouchListener {

    private boolean IS_PRESSED=false;
    private Paint paint = new Paint();
    private int number = 0;

    public CellView(Context context) {
        super(context);
    }

    public CellView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CellView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CellView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawText(String.valueOf(number), getMeasuredWidth()/2, getMeasuredHeight()/2, paint);
        if(IS_PRESSED){
            //todo
        }else{
            //todo
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        IS_PRESSED = !IS_PRESSED;
        invalidate();
        return true;
    }

    public void setNumber(int number){
        this.number = number;
        invalidate();
    }
}
