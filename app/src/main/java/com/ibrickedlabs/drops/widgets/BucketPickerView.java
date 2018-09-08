package com.ibrickedlabs.drops.widgets;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ibrickedlabs.drops.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by RajeshAatrayan on 02-09-2018.
 */

public class BucketPickerView extends LinearLayout implements View.OnTouchListener {
    public static final String LOG_TAG=BucketPickerView.class.getSimpleName();
    private int mActiveId;
    public static final int LEFT = 0;
    public static final int TOP = 1;
    public static final int RIGHT = 2;
    public static final int BOTTOM = 3;
    private static final int MESSAGE_WHAT = 123;
    public static final int DELAY = 250;
    private TextView mTextDate;
    private TextView mTextMonth;
    private TextView mTextYear;
    private Calendar mCalendar;
    private SimpleDateFormat simpleDateFormat;
    private boolean mIncrement;
    private boolean mDecrement;
    private Context cont;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (mIncrement) {
                increment(mActiveId);
            }
            if (mDecrement) {
                decrement(mActiveId);
            }
            if (mIncrement || mDecrement) {
                mHandler.sendEmptyMessageDelayed(MESSAGE_WHAT, DELAY);
            }

            return true;
        }
    });

    public BucketPickerView(Context context) {

        super(context);
        cont=context;
        init(context);
    }

    public BucketPickerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BucketPickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        //inflating the layout we created x
        //v --->reprsents ur linear layout
        View v = LayoutInflater.from(context).inflate(R.layout.date_picker_view, this);
        mCalendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("MMM");
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTextDate = (TextView) this.findViewById(R.id.tv_date);
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/ralway.ttf");
        mTextMonth = (TextView) this.findViewById(R.id.tv_month);
        mTextYear = (TextView) this.findViewById(R.id.tv_year);
        mTextYear.setTypeface(typeface);
        mTextMonth.setTypeface(typeface);
        mTextDate.setTypeface(typeface);
        int date = mCalendar.get(Calendar.DATE);
        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);
        mTextDate.setOnTouchListener(this);
        mTextMonth.setOnTouchListener(this);
        mTextYear.setOnTouchListener(this);

        update(date, month, year, 0, 0, 0);

    }

    private void update(int date, int month, int year, int hour, int minute, int second) {
        mCalendar.set(mCalendar.DATE, date);
        mCalendar.set(mCalendar.YEAR, year);
        mCalendar.set(mCalendar.MONTH, month);
        mCalendar.set(mCalendar.HOUR, hour);
        mCalendar.set(mCalendar.MINUTE, minute);
        mCalendar.set(mCalendar.SECOND, second);
        mTextYear.setText("" + year);
        mTextMonth.setText(simpleDateFormat.format(mCalendar.getTime()) + "");
        mTextDate.setText("" + date);
    }

    public long getTime() {
        return mCalendar.getTimeInMillis();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.tv_date:
                processEventFor(mTextDate, event);
                break;

            case R.id.tv_month:
                processEventFor(mTextMonth, event);
                break;
            case R.id.tv_year:
                processEventFor(mTextYear, event);
                break;


        }
        return true;
    }

    private void processEventFor(TextView textView, MotionEvent event) {
        Drawable[] drawables = textView.getCompoundDrawables();
        if (hasDrawableTop(drawables) && hasDrawableBottom(drawables)) {
            Rect topBounds = drawables[TOP].getBounds();
            Rect bottomBounds = drawables[BOTTOM].getBounds();
            float x = event.getX();
            float y = event.getY();
            mActiveId = textView.getId();
            if (topDrawableHit(textView, topBounds.height(), x, y)) {
                if (isActionDown(event)) {
                    increment(textView.getId());
                    mIncrement = true;
                    mHandler.removeMessages(MESSAGE_WHAT);
                    mHandler.sendEmptyMessageDelayed(MESSAGE_WHAT, DELAY);
                    toggleDrawable(textView, true);
                }
                if (isActionUpOrCancel(event)) {

                    mIncrement = false;
                    toggleDrawable(textView, false);
                }


            } else if (bottomDrawableHit(textView, bottomBounds.height(), x, y)) {
                if (isActionDown(event)) {
                    decrement(textView.getId());
                    mDecrement = true;
                    mHandler.removeMessages(MESSAGE_WHAT);
                    mHandler.sendEmptyMessageDelayed(MESSAGE_WHAT, DELAY);
                    toggleDrawable(textView, true);

                }
                if (isActionUpOrCancel(event)) {
                    toggleDrawable(textView, false);
                    mDecrement = false;
                }

            } else {
                mIncrement = false;
                mDecrement = false;
                toggleDrawable(textView, false);

            }
        }


    }

    private void toggleDrawable(TextView textView, boolean pressedState) {
        if (pressedState) {
            if (mIncrement) {
                textView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.up_pressed, 0, R.drawable.down_normal);
            }


            if (mDecrement) {
                textView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.up_normal, 0, R.drawable.down_pressed);
            }


        } else {
            textView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.up_normal, 0, R.drawable.down_normal);
        }
    }

    private void increment(int id) {


        switch (id) {
            case R.id.tv_date:
                mCalendar.add(Calendar.DATE, 1);
                break;

            case R.id.tv_month:
                mCalendar.add(Calendar.MONTH, 1);

                break;
            case R.id.tv_year:
                mCalendar.add(Calendar.YEAR, 1);
                break;


        }
        set(mCalendar);

    }

    private void set(Calendar calendar) {
        int date = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        mTextDate.setText(date + "");
        mTextMonth.setText(simpleDateFormat.format(mCalendar.getTime()) + "");
        mTextYear.setText(year + "");
    }

    private void decrement(int id) {
        switch (id) {
            case R.id.tv_date:
                mCalendar.add(Calendar.DATE, -1);
                break;

            case R.id.tv_month:
                mCalendar.add(Calendar.MONTH, -1);

                break;
            case R.id.tv_year:
                mCalendar.add(Calendar.YEAR, -1);
                break;
        }
        set(mCalendar);
    }

    private boolean isActionDown(MotionEvent event) {
        return event.getAction() == MotionEvent.ACTION_DOWN;
    }

    private boolean isActionUpOrCancel(MotionEvent event) {
        return event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL;
    }

    private boolean bottomDrawableHit(TextView textView, int drawableHeight, float x, float y) {
        int xmin = textView.getPaddingLeft();
        int xmax = textView.getWidth() - textView.getPaddingRight();

        int ymax = textView.getHeight() - textView.getPaddingBottom();
        int ymin = ymax - drawableHeight;
        return x > xmin && x < xmax && y > ymin && y < ymax;
    }

    private boolean topDrawableHit(TextView textView, int drawableHeight, float x, float y) {
        int xmin = textView.getPaddingLeft();
        int xmax = textView.getWidth() - textView.getPaddingRight();
        int ymin = textView.getPaddingTop();
        int ymax = textView.getPaddingTop() + drawableHeight;
        return x > xmin && x < xmax && y > ymin && y < ymax;
    }


    private boolean hasDrawableTop(Drawable[] drawables) {
        return drawables[TOP] != null;
    }

    private boolean hasDrawableBottom(Drawable[] drawables) {
        return drawables[BOTTOM] != null;
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Log.i(LOG_TAG,"On saved Instance state");
        Bundle bundle=new Bundle();
        bundle.putParcelable("super",super.onSaveInstanceState());
        bundle.putInt("date",mCalendar.get(Calendar.DATE));
        bundle.putInt("month",mCalendar.get(Calendar.MONTH));
        bundle.putInt("year",mCalendar.get(Calendar.YEAR));
        return  bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Log.i(LOG_TAG,"on restore instance state");
        if(state instanceof  Parcelable){
            Bundle bundle=(Bundle)state;
            int date=bundle.getInt("date");
            int month=bundle.getInt("month");
            int year=bundle.getInt("year");
            state=bundle.getParcelable("super");
            update(date,month,year,0,0,0);
        }
        super.onRestoreInstanceState(state);
    }
}
