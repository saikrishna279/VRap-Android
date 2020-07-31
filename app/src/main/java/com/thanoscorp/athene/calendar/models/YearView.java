package com.thanoscorp.athene.calendar.models;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridLayout;

import androidx.annotation.Nullable;

import com.thanoscorp.athene.R;
import com.thanoscorp.athene.models.StrokedTextView;

import java.util.ArrayList;
import java.util.List;

public class YearView extends GridLayout {

    private StrokedTextView one,two,three,four;
    private int lastYear;
    private List<StrokedTextView> tvList = new ArrayList<>();
    private int i;

    public YearView(Context context) {
        super(context);
    }

    public YearView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public YearView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public YearView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void init(){
        inflate(getContext(), R.layout.calendar_year_layout, this);

        one = findViewById(R.id.one);
        two = findViewById(R.id.two);
        three = findViewById(R.id.three);
        four = findViewById(R.id.four);

        tvList.add(0, one);
        tvList.add(1, two);
        tvList.add(2, three);
        tvList.add(3, four);

        one.setOutlineWidth(1);
        two.setOutlineWidth(1);
        three.setOutlineWidth(1);
        four.setOutlineWidth(1);

        one.setOutlineColor(getResources().getColor(R.color.calendar_cell_text));
        two.setOutlineColor(getResources().getColor(R.color.calendar_cell_text));
        three.setOutlineColor(getResources().getColor(R.color.calendar_cell_text));
        four.setOutlineColor(getResources().getColor(R.color.calendar_cell_text));

    }

    public void setYear(int year){
        if(year != lastYear) {
            final CharSequence sequence = String.valueOf(year);
            CharSequence lastSequence = String.valueOf(lastYear);

            one.setText(String.valueOf(sequence.charAt(0)));
            two.setText(String.valueOf(sequence.charAt(1)));
            three.setText(String.valueOf(sequence.charAt(2)));
            four.setText(String.valueOf(sequence.charAt(3)));
            lastYear = year;
        }
    }
}
