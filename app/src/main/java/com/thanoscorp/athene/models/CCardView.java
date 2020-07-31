package com.thanoscorp.athene.models;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

/**
 *
 *  A Custom CardView that can resize by setting Top Margins and Card Height
 *
 */

public class CCardView extends CardView {

    FrameLayout.LayoutParams params;

    public int TOP_MARGIN_OFFSET = 50;

    public CCardView(@NonNull Context context) {
        super(context);
    }

    public CCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setTopMargin(float margin){
        if(params == null) params =  (FrameLayout.LayoutParams) getLayoutParams();
        Log.d("CCardView", "Setting top margin " + margin);
        params.topMargin = (int) margin + TOP_MARGIN_OFFSET;
        setLayoutParams(params);
    }

    public float getTopMargin(){
        return ((FrameLayout.LayoutParams)getLayoutParams()).topMargin - TOP_MARGIN_OFFSET;
    }

    public void setCardHeight(int height){
        if(params == null) params =  (FrameLayout.LayoutParams) getLayoutParams();
        params.height = height;
        setLayoutParams(params);
    }
}
