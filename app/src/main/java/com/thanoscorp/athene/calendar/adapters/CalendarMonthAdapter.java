package com.thanoscorp.athene.calendar.adapters;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.thanoscorp.athene.R;
import com.thanoscorp.athene.models.StrokedTextView;

import java.util.List;

public class CalendarMonthAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<String> list;
    private SparseArray<StrokedTextView> tvList = new SparseArray<>();
    private ArgbEvaluator evaluator = new ArgbEvaluator();
    private int startColor, endColor;

    public CalendarMonthAdapter(Context context, List<String> list) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.list = list;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = inflater.inflate(R.layout.calendar_month_vp_view_layout, null);

        StrokedTextView tv = view.findViewById(R.id.tv);
        tv.setText(list.get(position));
        tv.setOutlineColor(context.getResources().getColor(R.color.calendar_cell_text));
        tv.setOutlineWidth(1);
        container.addView(view);
        tvList.put(position, tv);

        return view;
    }

    public void setFocusTextView(int index, float animValue) {

        Log.d("STV", "index: " + index + " val: " + animValue);
        tvList.get(index).setTextColor((int) evaluator.evaluate(animValue,
                context.getResources().getColor(R.color.calendar_bg),
                context.getResources().getColor(R.color.calendar_cell_text)));
        if (index < getCount()) {
            Log.d("STV", "index: " + (index+1) + " val: " + (1-animValue));
            tvList.get(index + 1).setTextColor((int) evaluator.evaluate(1 - animValue,
                    context.getResources().getColor(R.color.calendar_bg),
                    context.getResources().getColor(R.color.calendar_cell_text)));
        }
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
