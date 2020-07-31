package com.mikesu.horizontalexpcalendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.mikesu.horizontalexpcalendar.adapter.CalendarAdapter;
import com.mikesu.horizontalexpcalendar.common.Animations;
import com.mikesu.horizontalexpcalendar.common.Config;
import com.mikesu.horizontalexpcalendar.common.Constants;
import com.mikesu.horizontalexpcalendar.common.Marks;
import com.mikesu.horizontalexpcalendar.common.Utils;
import com.mikesu.horizontalexpcalendar.listener.SmallPageChangeListener;
import com.mikesu.horizontalexpcalendar.view.cell.BaseCellView;
import com.mikesu.horizontalexpcalendar.view.cell.LabelCellView;
import com.mikesu.horizontalexpcalendar.view.page.PageView;

import org.joda.time.DateTime;

/**
 * Created by MikeSu on 23/04/18.
 * www.michalsulek.pl
 */

public class HorizontalExpCalendar extends RelativeLayout implements PageView.PageViewListener, Animations.AnimationsListener {

    private TextView titleTextView;
    private RelativeLayout centerContainer;
    private GridLayout animateContainer;
    private GridLayout lableGrid;

    private ViewPager monthViewPager;
    private CalendarAdapter monthPagerAdapter;

    private ViewPager weekViewPager;
    private CalendarAdapter weekPagerAdapter;

    private Animations animations;
    private HorizontalExpCalListener horizontalExpCalListener;

    private RelativeLayout.LayoutParams monthLP, weekLP;

    private View.OnTouchListener listener;

    private boolean lock;

    public int expandedHeight, collapsedHeight;

    public void setTouchListeners(View.OnTouchListener listener){
        this.listener = listener;
    }

    public HorizontalExpCalendar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public HorizontalExpCalendar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public void setHorizontalExpCalListener(HorizontalExpCalListener horizontalExpCalListener) {
        this.horizontalExpCalListener = horizontalExpCalListener;
    }

    public void removeHorizontalExpCalListener() {
        this.horizontalExpCalListener = null;
    }

    @Override
    protected void onDetachedFromWindow() {
        animations.unbind();
        Marks.clear();
        super.onDetachedFromWindow();
    }

    private void init(AttributeSet attributeSet) {
        inflate(getContext(), R.layout.horizontal_exp_calendar, this);

        centerContainer = (RelativeLayout) findViewById(R.id.center_container);
        lock = false;

        setValuesFromAttr(attributeSet);
        setupCellWidth();

        Marks.init();
        Marks.markToday();
        Marks.refreshMarkSelected(Config.selectionDate);
        //renderCustomMarks();

        initAnimation();

        expandedHeight = Config.monthViewPagerHeight;
        collapsedHeight = Config.weekViewPagerHeight;
    }

    private void renderCustomMarks() {
        // custom1
        Marks.refreshCustomMark(new DateTime().minusDays(5), Marks.CustomMarks.CUSTOM1, true);
        Marks.refreshCustomMark(new DateTime().plusDays(1), Marks.CustomMarks.CUSTOM1, true);
        Marks.refreshCustomMark(new DateTime().plusDays(4), Marks.CustomMarks.CUSTOM1, true);
        // custom2
        Marks.refreshCustomMark(new DateTime().minusDays(7), Marks.CustomMarks.CUSTOM2, true);
        Marks.refreshCustomMark(new DateTime().plusDays(1), Marks.CustomMarks.CUSTOM2, true);
        Marks.refreshCustomMark(new DateTime().plusDays(10), Marks.CustomMarks.CUSTOM2, true);
    }

    private void setCellHeight() {
        Config.cellHeight = Config.monthViewPagerHeight / (Config.MONTH_ROWS + Utils.dayLabelExtraRow());
    }

    public void setupCellWidth() {
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                HorizontalExpCalendar.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                Config.cellWidth = getMeasuredWidth() / Config.COLUMNS;
                setupViews();
            }
        });
    }

    private void setValuesFromAttr(AttributeSet attributeSet) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.HorizontalExpCalendar);
        if (typedArray != null) {
            setupMiddleContainerFromAttr(typedArray);
            setupExpandedFromAttr(typedArray);
            typedArray.recycle();
        }

        setHeightToCenterContainer(Utils.isMonthView() ? Config.monthViewPagerHeight : Config.weekViewPagerHeight);
    }

    private void setupMiddleContainerFromAttr(TypedArray typedArray) {
        if (typedArray.hasValue(R.styleable.HorizontalExpCalendar_center_container_expanded_height)) {
            Config.monthViewPagerHeight = typedArray.getDimensionPixelSize(
                    R.styleable.HorizontalExpCalendar_center_container_expanded_height, LinearLayout.LayoutParams.WRAP_CONTENT);

            setCellHeight();

            Config.weekViewPagerHeight = Config.cellHeight * (Config.USE_DAY_LABELS ? 2 : 1);
        }
    }

    private void setupExpandedFromAttr(TypedArray typedArray) {
        if (typedArray.hasValue(R.styleable.HorizontalExpCalendar_expanded)) {
            Config.currentViewPager = typedArray.getBoolean(R.styleable.HorizontalExpCalendar_expanded, true) ?
                    Config.ViewPagerType.MONTH : Config.ViewPagerType.WEEK;
        }
    }

    private void initAnimation() {
        animations = new Animations(getContext(), HorizontalExpCalendar.this, Utils.animateContainerExtraTopOffset(getResources()));
    }

    private void setupViews() {
        initCenterContainer();
        initAnimateContainer();
        initLableView();
        refreshTitleTextView();
    }

    private void initAnimateContainer() {
        animateContainer = (GridLayout) findViewById(R.id.animate_container);
        animateContainer.getLayoutParams().height = Config.cellHeight;
        int sideMargin = Utils.animateContainerExtraSideOffset(getResources());
        animateContainer.setPadding(sideMargin, 0, sideMargin, 0);
    }

    private void initCenterContainer() {
        initMonthViewPager();
        initWeekViewPager();
    }

    private void initMonthViewPager() {
        monthViewPager = (ViewPager) findViewById(R.id.month_view_pager);
        monthPagerAdapter = new CalendarAdapter(getContext(), Config.ViewPagerType.MONTH, this);
        monthViewPager.setAdapter(monthPagerAdapter);
        monthViewPager.setCurrentItem(Utils.monthPositionFromDate(Config.INIT_DATE));
        monthViewPager.addOnPageChangeListener(new SmallPageChangeListener() {
            @Override
            public void scrollStart() {
                if (Utils.isMonthView()) {
                    lock();
                }
            }

            @Override
            public void scrollEnd() {
                if (Utils.isMonthView()) {
                    Config.scrollDate = Utils.getDateByMonthPosition(monthViewPager.getCurrentItem());
                    if (Utils.isTheSameMonthToScrollDate(Config.selectionDate)) {
                        Config.scrollDate = Config.selectionDate.toDateTime();
                    }
                    refreshTitleTextView();
                    if (horizontalExpCalListener != null) {
                        horizontalExpCalListener.onCalendarScroll(Config.scrollDate.withDayOfMonth(1));
                    }
                    unlock();
                }
            }
        });
        monthViewPager.setVisibility(Utils.isMonthView() ? VISIBLE : GONE);
        monthViewPager.setOnTouchListener(listener);
    }

    private void initWeekViewPager() {
        weekViewPager = (ViewPager) findViewById(R.id.week_view_pager);
        weekPagerAdapter = new CalendarAdapter(getContext(), Config.ViewPagerType.WEEK, this);
        weekViewPager.setAdapter(weekPagerAdapter);
        setWeekViewPagerPosition(Utils.weekPositionFromDate(Config.INIT_DATE), false);
        weekViewPager.addOnPageChangeListener(new SmallPageChangeListener() {
            @Override
            public void scrollStart() {
                if (!Utils.isMonthView()) {
                    lock();
                }
            }

            @Override
            public void scrollEnd() {
                if (!Utils.isMonthView()) {
                    Config.scrollDate = Utils.getDateByWeekPosition(weekViewPager.getCurrentItem());
                    if (Utils.weekPositionFromDate(Config.scrollDate) == Utils.weekPositionFromDate(Config.selectionDate)) {
                        Config.scrollDate = Config.selectionDate;
                    }
                    refreshTitleTextView();
                    if (horizontalExpCalListener != null) {
                        horizontalExpCalListener.onCalendarScroll(Config.scrollDate.withDayOfWeek(1));
                    }
                    unlock();
                }
            }
        });
        weekViewPager.setVisibility(!Utils.isMonthView() ? VISIBLE : GONE);
    }

    public void scrollToDate(DateTime dateTime, boolean animate) {
        if (Config.currentViewPager == Config.ViewPagerType.MONTH && Utils.isTheSameMonthToScrollDate(dateTime)) {
            return;
        }
        if (Config.currentViewPager == Config.ViewPagerType.WEEK && Utils.isTheSameWeekToScrollDate(dateTime) && Utils.isTheSameMonthToScrollDate(dateTime)) {
            return;
        }

        boolean isMonthView = Utils.isMonthView();
        scrollToDate(dateTime, isMonthView, !isMonthView, animate);
    }

    private void setWeekViewPagerPosition(int position, boolean animate) {
        weekViewPager.setCurrentItem(position, animate);
    }

    private void setMonthViewPagerPosition(int position, boolean animate) {
        monthViewPager.setCurrentItem(position, animate);
    }

    private void refreshTitleTextView() {
        DateTime titleDate = Config.scrollDate;
        if (Config.currentViewPager == Config.ViewPagerType.MONTH) {
            if (Utils.isTheSameMonthToScrollDate(Config.selectionDate)) {
                titleDate = Config.selectionDate;
            }
        } else {
            if (Utils.isTheSameWeekToScrollDate(Config.selectionDate)) {
                titleDate = Config.selectionDate;
            }
        }
        //titleTextView.setText(String.format("%s - %s", titleDate.getYear(), titleDate.getMonthOfYear()));
    }

    private void lock() {
        lock = true;
    }

    private void unlock() {
        lock = false;
    }

    private boolean isLocked() {
        return lock;
    }

    public void setVisible() {
        setVisibility(View.VISIBLE);
        if (Config.cellWidth == 0) {
            setupCellWidth();
        }
        if (Config.cellHeight == 0) {
            setCellHeight();
        }
    }

    public void setGone() {
        setVisibility(View.GONE);
    }

    public void initLableView() {
        lableGrid = (GridLayout) findViewById(R.id.labelGrid);
        lableGrid.getLayoutParams().height = Config.cellHeight;
        int sideMargin = Utils.animateContainerExtraSideOffset(getResources());
        lableGrid.setPadding(sideMargin, 0, sideMargin, 0);
        for (int l = 0; l < Config.COLUMNS; l++) {
            LabelCellView label = new LabelCellView(getContext());

            GridLayout.LayoutParams labelParams = new GridLayout.LayoutParams(GridLayout.spec(0), GridLayout.spec(l));
            labelParams.height = Config.cellHeight;
            labelParams.width = Config.cellWidth;
            label.setLayoutParams(labelParams);
            label.setText(Constants.NAME_OF_DAYS[(l + 1 + Utils.firstDayOffset()) % 7]);
            label.setDayType(Utils.isWeekendByColumnNumber(l) ? BaseCellView.DayType.WEEKEND : BaseCellView.DayType.NO_WEEKEND);

            Log.d("LabelCellView", label.getText());

            lableGrid.addView(label, l);
        }
        lableGrid.setVisibility(View.GONE);
    }

    @Override
    public void scrollToDate(DateTime dateTime, boolean scrollMonthPager, boolean scrollWeekPager, boolean animate) {
        if (scrollMonthPager) {
            setMonthViewPagerPosition(Utils.monthPositionFromDate(dateTime), animate);
        }
        if (scrollWeekPager) {
            setWeekViewPagerPosition(Utils.weekPositionFromDate(dateTime), animate);
        }
    }

    @Override
    public void animateContainerAddView(View view) {
        animateContainer.addView(view);
    }

    @Override
    public void animateContainerRemoveViews() {
        animateContainer.removeAllViews();
    }

    @Override
    public void updateMarks() {
        if (Config.currentViewPager == Config.ViewPagerType.MONTH) {
            monthPagerAdapter.updateMarks();
        } else {
            weekPagerAdapter.updateMarks();
        }
    }

    @Override
    public void changeViewPager(Config.ViewPagerType viewPagerType) {
        if (horizontalExpCalListener != null) {
            horizontalExpCalListener.onChangeViewPager(viewPagerType);
        }
    }

    @Override
    public void onDayClick(DateTime dateTime) {
        scrollToDate(dateTime, true);

        Marks.refreshMarkSelected(dateTime);
        updateMarks();

        refreshTitleTextView();

        if (horizontalExpCalListener != null) {
            horizontalExpCalListener.onDateSelected(dateTime);
        }
    }

    @Override
    public void setHeightToCenterContainer(int height) {
        ((LinearLayout.LayoutParams) centerContainer.getLayoutParams()).height = height;
        centerContainer.requestLayout();
    }

    @Override
    public void setTopMarginToAnimContainer(int margin) {
        ((RelativeLayout.LayoutParams) animateContainer.getLayoutParams()).topMargin = margin;
    }

    @Override
    public void setWeekPagerVisibility(int visibility) {
        if (visibility == View.VISIBLE) Log.d("VisibilityWeek", "VISIBLE");
        if (visibility == View.INVISIBLE) Log.d("VisibilityWeek", "INVISIBLE");
        if (visibility == View.GONE) Log.d("VisibilityWeek", "GONE");
        weekViewPager.setVisibility(visibility);
    }

    @Override
    public void setMonthPagerVisibility(int visibility) {
        if (visibility == View.VISIBLE) Log.d("VisibilityMonth", "VISIBLE");
        if (visibility == View.INVISIBLE) Log.d("VisibilityMonth", "INVISIBLE");
        if (visibility == View.GONE) Log.d("VisibilityMonth", "GONE");
        monthViewPager.setVisibility(visibility);
    }

    @Override
    public void setAnimatedContainerVisibility(int visibility) {
        animateContainer.setVisibility(visibility);
    }

    @Override
    public void setMonthPagerAlpha(float alpha) {
        monthViewPager.setAlpha(alpha);
    }

    @Override
    public void setWeekPagerAlpha(float alpha) {
        weekViewPager.setAlpha(alpha);
    }

    public interface HorizontalExpCalListener {
        void onCalendarScroll(DateTime dateTime);

        void onDateSelected(DateTime dateTime);

        void onChangeViewPager(Config.ViewPagerType viewPagerType);
    }

    public int getCalendarHeight() {
        return centerContainer.getLayoutParams().height;
    }

    @Override
    public void setCalendarHeight(int fromHeight, int toHeight) {
        if (fromHeight == expandedHeight)
            animations.startCollapse();
        if (fromHeight == collapsedHeight)
            animations.startExpand();

        //setMonthViewPagerTopMargin(toHeight - collapsedHeight);
        float animator = (((float) (toHeight - collapsedHeight) / (float) (expandedHeight - collapsedHeight)));
        Log.d("CalendarHeight", "" + animator);
        animations.setPagerHeight(animator);
        animations.setPagerAlpha(animator);

        if (toHeight == collapsedHeight) {
            Config.currentViewPager = Config.ViewPagerType.WEEK;
            animations.endCollapse();
        } else if (toHeight == expandedHeight) {
            Config.currentViewPager = Config.ViewPagerType.MONTH;
            animations.endExpand();
        }
    }

    @Override
    public void setMonthViewPagerTopMargin(int margin) {
        monthLP = (RelativeLayout.LayoutParams) monthViewPager.getLayoutParams();
        monthLP.topMargin = (margin);
        monthViewPager.setLayoutParams(monthLP);
    }

    @Override
    public void setWeekViewPagerTopMargin(int margin) {
        weekLP = (RelativeLayout.LayoutParams) weekViewPager.getLayoutParams();
        weekLP.topMargin = (collapsedHeight - margin);
        weekViewPager.setLayoutParams(weekLP);
    }

    @Override
    public int getMonthPagerVisibility() {
        return monthViewPager.getVisibility();
    }

    @Override
    public void hideLableGird() {
        lableGrid.setVisibility(View.GONE);
    }

    @Override
    public void showLableGrid() {
        lableGrid.setVisibility(View.VISIBLE);
    }

    @Override
    public int getAnimContainerTopMargin() {
        return ((RelativeLayout.LayoutParams) animateContainer.getLayoutParams()).topMargin;
    }

}