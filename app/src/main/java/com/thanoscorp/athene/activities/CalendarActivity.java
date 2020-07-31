package com.thanoscorp.athene.activities;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.mikesu.horizontalexpcalendar.HorizontalExpCalendar;
import com.mikesu.horizontalexpcalendar.common.Config;
import com.thanoscorp.athene.R;
import com.thanoscorp.athene.calendar.adapters.CalendarMonthAdapter;
import com.thanoscorp.athene.calendar.models.YearView;
import com.thanoscorp.athene.models.ActivityX;
import com.thanoscorp.athene.models.CCardView;
import com.thanoscorp.athene.utility.TouchSnapper;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *  Calendar Activity under construction.
 *
 *  Check README to see how it looks.
 *
 */
public class CalendarActivity extends ActivityX implements View.OnTouchListener {

    private HorizontalExpCalendar calendar;
    private CCardView cv;
    private float lastTouch, currentTouch, touchDiff, topMargin, lastTopMargin, velocity = 0;
    private float ESCAPE_VELOCITY = 5;
    private ValueAnimator snapValueAnimator;

    private ViewPager monthViewPager;
    private CalendarMonthAdapter adapter;
    private YearView yearView;
    private boolean isMonthVPTouched = false, isCalendarTouched = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar);
        calendar = findViewById(R.id.calendar);
        cv = findViewById(R.id.cv);
        yearView = findViewById(R.id.yearView);

        cv.setCardHeight(getScreenHeight());
        cv.setTopMargin(calendar.getCalendarHeight());
        cv.setOnTouchListener(this);

        initMonthViewPager();
        initCalendar();
        initYearView();

        TouchSnapper.init(calendar.collapsedHeight, calendar.expandedHeight);
    }

    private void initYearView() {
        yearView.init();
        yearView.setYear(Config.INIT_DATE.getYear());
    }

    private void moveBarrier(float topMargin, float lastTopMargin) {
        if (topMargin == lastTopMargin) return;
        calendar.setCalendarHeight((int) lastTopMargin, (int) topMargin);
        cv.setTopMargin(topMargin);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initCalendar() {
        monthViewPager.setCurrentItem(Config.INIT_DATE.getMonthOfYear() - 1);
        calendar.setHorizontalExpCalListener(new HorizontalExpCalendar.HorizontalExpCalListener() {
            @Override
            public void onCalendarScroll(DateTime dateTime) {
                Log.i("CalendarView", "onCalendarScroll: " + dateTime.toString());
                monthViewPager.setCurrentItem(dateTime.getMonthOfYear() - 1, true);
                yearView.setYear(dateTime.getYear());
            }

            @Override
            public void onDateSelected(DateTime dateTime) {
                Log.i("CalendarView", "onDateSelected: " + dateTime.toString());
            }

            @Override
            public void onChangeViewPager(Config.ViewPagerType viewPagerType) {
                Log.i("CalendarView", "onChangeViewPager: " + viewPagerType.name());
            }
        });
        calendar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!isMonthVPTouched) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                        case MotionEvent.ACTION_MOVE:
                            isCalendarTouched = true;
                            monthViewPager.dispatchTouchEvent(event);
                            break;
                        case MotionEvent.ACTION_UP:
                            monthViewPager.dispatchTouchEvent(event);
                            isCalendarTouched = false;
                            break;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initMonthViewPager() {
        monthViewPager = findViewById(R.id.monthViewPager);
        adapter = new CalendarMonthAdapter(getApplicationContext(), getAcademicMonthList());
        monthViewPager.setClipToPadding(false);
        monthViewPager.setPadding(100, 0, 200, 0);
        monthViewPager.setPageMargin(-50);
        monthViewPager.setOffscreenPageLimit(adapter.getCount());
        monthViewPager.setAdapter(adapter);
        monthViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!isCalendarTouched) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                        case MotionEvent.ACTION_MOVE:
                            isMonthVPTouched = true;
                            calendar.dispatchTouchEvent(event);
                            break;
                        case MotionEvent.ACTION_UP:
                            calendar.dispatchTouchEvent(event);
                            isMonthVPTouched = false;
                            break;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
        setViewPagerListener();
    }

    private void setViewPagerListener() {
        monthViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d("ViewPager", "Scrolling " + position + " to " + positionOffset);
                adapter.setFocusTextView(position, 1 - positionOffset);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private List<String> getAcademicMonthList() {
        List<String> list = new ArrayList<>();
        list.add("January");
        list.add("February");
        list.add("March");
        list.add("April");
        list.add("May");
        list.add("June");
        list.add("July");
        list.add("August");
        list.add("September");
        list.add("October");
        list.add("November");
        list.add("December");
        return list;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v == cv) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastTouch = event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    currentTouch = (event.getRawY());
                    touchDiff = currentTouch - lastTouch;
                    lastTopMargin = cv.getTopMargin();
                    topMargin = cv.getTopMargin() + touchDiff;

                    if (topMargin > calendar.expandedHeight)
                        topMargin = calendar.expandedHeight;

                    else if (topMargin < calendar.collapsedHeight)
                        topMargin = calendar.collapsedHeight;
                    velocity = (topMargin - lastTopMargin);
                    Log.d("velocityTouch", "" + velocity);
                    lastTouch = currentTouch;
                    moveBarrier(topMargin, lastTopMargin);
                    break;
                case MotionEvent.ACTION_UP:
                    if (Math.abs(velocity) > ESCAPE_VELOCITY) {
                        Log.d("velocityTouch", "" + Math.abs(velocity) + " lastTop " + TouchSnapper.getOtherOrdinate(lastTopMargin) + " " + lastTopMargin);
                        if (snapValueAnimator == null) {
                            snapValueAnimator = ValueAnimator.ofFloat(lastTopMargin, TouchSnapper.getOtherOrdinate(velocity));
                            snapValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    topMargin = (float) animation.getAnimatedValue();
                                    Log.d("AnimProceed", "" + animation.getAnimatedFraction());
                                    moveBarrier(topMargin, lastTopMargin);
                                    lastTopMargin = topMargin;
                                }
                            });
                            snapValueAnimator.setStartDelay(0);
                            snapValueAnimator.setInterpolator(new DecelerateInterpolator());
                        } else
                            snapValueAnimator.setFloatValues(lastTopMargin, TouchSnapper.getOtherOrdinate(velocity));
                        snapValueAnimator.setCurrentPlayTime(0);
                        snapValueAnimator.start();
                    } else {
                        Log.d("velocityTouch", "" + velocity);
                        if (snapValueAnimator == null) {
                            snapValueAnimator = ValueAnimator.ofFloat(lastTopMargin, TouchSnapper.getEndOrdinate(lastTopMargin));
                            snapValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    topMargin = (float) animation.getAnimatedValue();
                                    Log.d("AnimBack", "" + animation.getAnimatedFraction());
                                    moveBarrier(topMargin, lastTopMargin);
                                    lastTopMargin = topMargin;
                                }
                            });
                            snapValueAnimator.setStartDelay(0);
                            snapValueAnimator.setInterpolator(new DecelerateInterpolator());
                        } else
                            snapValueAnimator.setFloatValues(lastTopMargin, TouchSnapper.getEndOrdinate(lastTopMargin));
                        snapValueAnimator.setCurrentPlayTime(0);
                        snapValueAnimator.start();
                    }
                    break;
                default:
                    return false;
            }
        }
        return true;
    }
}
