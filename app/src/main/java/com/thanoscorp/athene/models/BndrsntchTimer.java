/*
 * Copyright 2019 Purushottam Pawar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Credits to https://github.com/iamporus/BndrsntchTimer for the hard work
// Had to edit to implement some additional functionality

package com.thanoscorp.athene.models;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.thanoscorp.athene.R;

/**
 * A horizontal progress bar shrinking with time; similar to Bandersnatch choice interface.
 */
@SuppressWarnings( { "unused", "SpellCheckingInspection" } )
public class BndrsntchTimer extends View implements LifecycleObserver
{
    private static final String TAG = "BndrsntchTimer";
    private static final String LEFT_POS_PROPERTY = "xLeftPos";
    private static final int RESET_ANIM_DURATION = 1000;

    private static int DEFAULT_HEIGHT = 16;                     //px
    private static int DEFAULT_ROUND_RECT_RADIUS = 0;           //px
    private static final int DEFAULT_STROKE_WIDTH = 2;          //px
    private static int DEFAULT_PROGRESS_COLOR = android.R.color.white;

    @SuppressLint( "ResourceAsColor" )
    private @ColorInt
    int mProgressColor = DEFAULT_PROGRESS_COLOR;
    private int mRoundRectRadius = DEFAULT_ROUND_RECT_RADIUS;
    private int mTimerHeight = DEFAULT_HEIGHT;
    private long mTimerDuration;
    private int mLeftXPosition;
    private int mFactor;

    private RectF mRectF;
    private Paint mBackgroundPaint;
    private OnTimerElapsedListener mOnTimerElapsedListener;
    private OnTimeUpListener mOnTimeUpListener;
    private onResetListener mResetListener;
    private ValueAnimator mTransformValueAnimator;
    private long mCurrentPlayTime;
    private boolean mbViewVisible;
    private boolean mbTimerElapsed;

    private float lastLeft;
    private int width;

    /**
     * Callback to be invoked when Timer is elaspsed.
     */
    public interface OnTimerElapsedListener
    {
        /**
         * Notifies the implementer when timer has elapsed.
         *
         * @param elapsedDuration long ellapsed duration in millis
         * @param totalDuration   long    total duration in millis
         */
        void onTimeElapsed( final long elapsedDuration, final long totalDuration );
    }

    public interface  OnTimeUpListener
    {
        // Addendum: Notifies the implementer when timer has finished
        void onTimeUp();
    }

    public interface onResetListener
    {
        void onResetFinished();
    }

    public BndrsntchTimer( Context context )
    {
        super( context );

        init();
    }

    public BndrsntchTimer( Context context, @Nullable AttributeSet attrs )
    {
        this( context, attrs, 0 );
        init();
    }

    @SuppressLint("ResourceAsColor")
    public BndrsntchTimer(Context context, @Nullable AttributeSet attrs, int defStyleAttr )
    {
        super( context, attrs, defStyleAttr );

        TypedArray array = context.obtainStyledAttributes( attrs, R.styleable.BndrsntchTimer, defStyleAttr, 0 );
        mProgressColor = array.getColor( R.styleable.BndrsntchTimer_progress_color, -1 );
        if( mProgressColor == -1 )
        {
            mProgressColor = ContextCompat.getColor( context, DEFAULT_PROGRESS_COLOR );
        }

        init();

        array.recycle();
    }

    private void init()
    {
        mRectF = new RectF();

        initPaint();
    }

    @SuppressLint( "ResourceType" )
    private void initPaint()
    {
        mBackgroundPaint = new Paint();
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setAntiAlias( true );
        mBackgroundPaint.setStrokeWidth( DEFAULT_STROKE_WIDTH );
        mBackgroundPaint.setColor( mProgressColor );
    }

    public long getTimerDuration()
    {
        return mTimerDuration;
    }

    private void setTimerDuration( long timerDuration )
    {
        mTimerDuration = timerDuration;
    }

    private void setTimerElapsed( boolean bTimerElapsed )
    {
        this.mbTimerElapsed = bTimerElapsed;
    }

    private void startAnimation( final long currentPlayTime )
    {
        PropertyValuesHolder propertyLeftPositionHolder = PropertyValuesHolder.ofInt( LEFT_POS_PROPERTY,
                mLeftXPosition - getPaddingRight(),
                width / 2 - getPaddingRight() );

        mTransformValueAnimator = new ValueAnimator();
        mTransformValueAnimator.setValues( propertyLeftPositionHolder );
        mTransformValueAnimator.setDuration( mTimerDuration );
        mTransformValueAnimator.setCurrentPlayTime( currentPlayTime );
        mTransformValueAnimator.addUpdateListener( new ValueAnimator.AnimatorUpdateListener()
        {
            long temp;
            @Override
            public void onAnimationUpdate( ValueAnimator valueAnimator )
            {
                if( mbViewVisible )
                {
                    temp = valueAnimator.getCurrentPlayTime();
                    mFactor = ( int ) valueAnimator.getAnimatedValue( LEFT_POS_PROPERTY );


                    //re-draw view as per the new calculated factor
                    invalidate();

                    if( temp >= mTimerDuration )
                    {
                        //timer has elapsed.
                        setTimerElapsed( true );
                        mOnTimeUpListener.onTimeUp();
                    }

                    if( mOnTimerElapsedListener != null )
                    {
                        mOnTimerElapsedListener.onTimeElapsed( temp, mTimerDuration );
                    }
                }
            }


        } );

        mTransformValueAnimator.start();
        setTimerElapsed( false );
    }

    private void startResetAnimation(boolean isInstant)
    {
        // stop timer if it is currently running
        if( mTransformValueAnimator != null && mTransformValueAnimator.isRunning() )
        {
            mTransformValueAnimator.cancel();
        }

        if(isInstant){
            mFactor = getPaddingLeft();
            setVisibility(INVISIBLE);
            forceLayout();
            mResetListener.onResetFinished();
        }else {
            PropertyValuesHolder propertyLeftPositionHolder = PropertyValuesHolder.ofInt(LEFT_POS_PROPERTY,
                    (int) lastLeft,
                    getPaddingLeft());

            mTransformValueAnimator = new ValueAnimator();
            mTransformValueAnimator.setValues(propertyLeftPositionHolder);
            mTransformValueAnimator.setDuration(RESET_ANIM_DURATION);
            mTransformValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    if (mbViewVisible) {
                        mFactor = (int) valueAnimator.getAnimatedValue(LEFT_POS_PROPERTY);

                        //re-draw view as per the new calculated factor
                        invalidate();
                    }
                }
            });
            mTransformValueAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mResetListener.onResetFinished();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

            mTransformValueAnimator.start();
        }
    }

    public void setOnResetFinishedListener(onResetListener listener){
        mResetListener = listener;
    }


    /**
     * Start the timer for passed in duration. The timer bar will shrink in provided duration and will invoke
     *
     * @param duration long duration this timer will run for.
     */
    public void start( final long duration )
    {
        forceLayout();
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if( mCurrentPlayTime == 0 )
                {
                    setTimerDuration( duration );
                    startAnimation( 0 );
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                else
                {
                    Log.e( TAG, "start: ", new IllegalStateException( "Timer is already running." ) );
                }
            }
        });

    }

    /**
     * Reset the timer for repeated usage.
     */
    public void reset(boolean isInstant)
    {
        setTimerElapsed( true );
        setTimerDuration( 0 );
        mCurrentPlayTime = 0;
        startResetAnimation(isInstant);
    }

    /**
     * Start the timer for passed in duration. The timer bar will shrink in provided duration and will invoke
     *
     * @param duration long duration this timer will run for.
     * @param listener @{@link OnTimerElapsedListener} listener to get callback once the @{@link BndrsntchTimer} elaspses.
     */
    public void start( final long duration, final OnTimerElapsedListener listener )
    {
        setTimerDuration( duration );
        setOnTimerElapsedListener( listener );
        start( duration );
    }

    public void start( final long duration, final OnTimeUpListener listener )
    {
        setTimerDuration( duration );
        setOnTimeUpListener( listener );
        start( duration );
    }

    /**
     * Register a callback to be invoked when {@link BndrsntchTimer} is elapsed.
     *
     * @param onTimerElapsedListener {@link OnTimerElapsedListener}
     */
    public void setOnTimerElapsedListener( final OnTimerElapsedListener onTimerElapsedListener )
    {
        mOnTimerElapsedListener = onTimerElapsedListener;
    }

    /**
     * Addendum: Register a callback to be invoked when {@link BndrsntchTimer} is up.
     *
     * @param onTimeUpListener {@link OnTimeUpListener}
     */
    public void setOnTimeUpListener( final OnTimeUpListener onTimeUpListener )
    {
        mOnTimeUpListener = onTimeUpListener;
    }

    /**
     * Returns true of the timer is still running, else false
     *
     * @return boolean true of the timer is still running, else false
     */
    public boolean isRunning()
    {
        return !mbTimerElapsed;
    }

    /**
     * Sets the color for the progress indicator.
     *
     * @param progressColor the color of the progress indicator of {@link BndrsntchTimer}
     */
    @SuppressLint( "ResourceType" )
    public void setProgressColorInt( @ColorInt final int progressColor )
    {
        mProgressColor = ContextCompat.getColor( getContext(), progressColor );
        initPaint();
    }

    @Override
    protected void onDraw( Canvas canvas )
    {
        mLeftXPosition = getPaddingLeft() + mFactor;

        mRectF.left = mLeftXPosition;
        mRectF.right = getWidth() - getPaddingRight() - mFactor;

        mRectF.top = getPaddingTop();
        mRectF.bottom = mRectF.top + mTimerHeight - getPaddingBottom();

        lastLeft = mLeftXPosition;




        canvas.drawRoundRect( mRectF, mRoundRectRadius, mRoundRectRadius, mBackgroundPaint );
    }

    @Override
    protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec )
    {
        Log.v( TAG, MeasureSpec.toString( widthMeasureSpec ) );
        Log.v( TAG, MeasureSpec.toString( heightMeasureSpec ) );

        width = MeasureSpec.getSize(widthMeasureSpec);

        int desiredWidth = getMeasuredWidth();
        int desiredHeight = mTimerHeight;

        desiredWidth = desiredWidth + getPaddingLeft() + getPaddingRight();
        desiredHeight = desiredHeight + getPaddingTop() + getPaddingBottom();

        setMeasuredDimension( measureDimension( desiredWidth, widthMeasureSpec ), measureDimension( desiredHeight, heightMeasureSpec ) );
    }



    @Override
    protected void onSizeChanged( int w, int h, int oldw, int oldh )
    {
        super.onSizeChanged( w, h, oldw, oldh );

        //if animation was running, timer wasn't elapsed.
        if( mCurrentPlayTime != 0 )
        {
            post( new Runnable()
            {
                @Override
                public void run()
                {
                    //start the animation from elasped time.
                    startAnimation( mCurrentPlayTime );
                }
            } );
        }
    }

    private int measureDimension( int desiredSize, int measureSpec )
    {
        int result = desiredSize;

        int specMode = MeasureSpec.getMode( measureSpec );
        int specSize = MeasureSpec.getSize( measureSpec );

        switch( specMode )
        {
            case MeasureSpec.EXACTLY:
            {
                result = specSize;
                break;
            }
            case MeasureSpec.AT_MOST:
            {
                result = Math.min( result, specSize );
                break;
            }
            case MeasureSpec.UNSPECIFIED:
            {
                result = desiredSize;
                break;
            }
        }

        if( result < desiredSize )
        {
            Log.e( TAG, "The view is too small." );
        }

        return result;
    }


    public LifecycleObserver getLifecycleObserver()
    {
        return this;
    }

    @OnLifecycleEvent( Lifecycle.Event.ON_START )
    private void onViewStarted()
    {
        mbViewVisible = true;
        //if animation was running, timer wasn't elapsed.
        if( mCurrentPlayTime != 0 )
        {
            startAnimation( mCurrentPlayTime );
        }
    }

    @OnLifecycleEvent( Lifecycle.Event.ON_STOP )
    private void onViewStopped()
    {
        mbViewVisible = false;
        if( mTransformValueAnimator != null && mTransformValueAnimator.isRunning() )
        {
            mCurrentPlayTime = mTransformValueAnimator.getCurrentPlayTime();
            mTransformValueAnimator.cancel();
        }
    }

    @Override
    public Parcelable onSaveInstanceState()
    {
        Parcelable superState = super.onSaveInstanceState();

        SavedState savedState = new SavedState( superState );
        savedState.setSavedPlayTime( mCurrentPlayTime );
        savedState.setSavedDuration( mTimerDuration );
        savedState.setSavedFactor( mFactor );
        savedState.setSavedPosition( mLeftXPosition );
        savedState.setTimerElapsed( mbTimerElapsed );
        return savedState;
    }

    @Override
    public void onRestoreInstanceState( Parcelable state )
    {
        SavedState savedState = ( SavedState ) state;
        super.onRestoreInstanceState( savedState.getSuperState() );

        mCurrentPlayTime = savedState.getSavedPlayTime();
        mTimerDuration = savedState.getSavedDuration();
        mFactor = savedState.getSavedFactor();
        mLeftXPosition = savedState.getSavedPosition();
        mbTimerElapsed = savedState.isTimerElapsed();

        if( mCurrentPlayTime != 0 )
        {
            startAnimation( mCurrentPlayTime );
        }
    }

    /**
     * Class to save view's internal state across lifecycle owner's state changes.
     */
    private static class SavedState extends BaseSavedState
    {
        private long mSavedPlayTime;
        private long mSavedDuration;
        private int mSavedFactor;
        private int mSavedPosition;
        private boolean mbTimerElapsed;

        private static final int PLAY_TIME_INDEX = 0, FACTOR_INDEX = 0;
        private static final int DURATION_INDEX = 1, POSITION_INDEX = 1;

        private SavedState( Parcel source )
        {
            super( source );

            long[] longValues = { 0, 0 };
            source.readLongArray( longValues );

            mSavedPlayTime = longValues[ PLAY_TIME_INDEX ];
            mSavedDuration = longValues[ DURATION_INDEX ];

            int[] intValues = { 0, 0 };
            source.readIntArray( intValues );
            mSavedFactor = intValues[ FACTOR_INDEX ];
            mSavedPosition = intValues[ POSITION_INDEX ];
        }

        private SavedState( Parcelable superState )
        {
            super( superState );
        }

        private void setSavedPlayTime( long savedPlayTime )
        {
            mSavedPlayTime = savedPlayTime;
        }

        private long getSavedPlayTime()
        {
            return mSavedPlayTime;
        }

        private long getSavedDuration()
        {
            return mSavedDuration;
        }

        private void setSavedDuration( long savedDuration )
        {
            mSavedDuration = savedDuration;
        }

        private int getSavedFactor()
        {
            return mSavedFactor;
        }

        private void setSavedFactor( int savedFactor )
        {
            mSavedFactor = savedFactor;
        }

        private int getSavedPosition()
        {
            return mSavedPosition;
        }

        private void setSavedPosition( int savedPosition )
        {
            mSavedPosition = savedPosition;
        }

        private void setTimerElapsed( boolean bTimerExhausted )
        {
            mbTimerElapsed = bTimerExhausted;
        }

        private boolean isTimerElapsed()
        {
            return mbTimerElapsed;
        }

        @Override
        public void writeToParcel( Parcel out, int flags )
        {
            super.writeToParcel( out, flags );

            long[] longValues = { mSavedPlayTime, mSavedDuration };
            out.writeLongArray( longValues );

            int[] intValues = { mSavedFactor, mSavedPosition };
            out.writeIntArray( intValues );
        }

        public static final Parcelable.Creator< SavedState > CREATOR = new Creator< SavedState >()
        {
            @Override
            public SavedState createFromParcel( Parcel parcel )
            {
                return new SavedState( parcel );
            }

            @Override
            public SavedState[] newArray( int size )
            {
                return new SavedState[ size ];
            }
        };
    }

}
