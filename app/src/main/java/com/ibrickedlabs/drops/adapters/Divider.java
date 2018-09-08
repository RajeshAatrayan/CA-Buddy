package com.ibrickedlabs.drops.adapters;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ibrickedlabs.drops.R;

/**
 * Created by RajeshAatrayan on 30-08-2018.
 */

public class Divider extends RecyclerView.ItemDecoration {
    private Drawable mDivider;
    private  int mOrientation;

    public Divider(Context context,int orientation) {
        //we are making the divider xml file as drawable here
        mDivider= ContextCompat.getDrawable(context, R.drawable.divider);
        if(orientation!= LinearLayoutManager.VERTICAL){
            throw new IllegalArgumentException("This item is to be used with RecyclerView with Vertical LinearLayout Manger");

        }
        mOrientation=orientation;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if(mOrientation==LinearLayoutManager.VERTICAL){
            drawHorizontalDivider(c,parent,state);
        }

    }

    private void drawHorizontalDivider(Canvas c, RecyclerView parent, RecyclerView.State state) {

        int left,right,top,bottom;
        left=parent.getPaddingLeft();
        //HERE THE PARENT IS RECYCLERVIEW
        right=parent.getWidth()-parent.getPaddingRight();
        int count=parent.getChildCount();
        for(int i=0;i<count;i++){
            if(AdapterExam.FOOTER!=parent.getAdapter().getItemViewType(i))
            {
                View currentView=parent.getChildAt(i);
                RecyclerView.LayoutParams params=(RecyclerView.LayoutParams)currentView.getLayoutParams();
                top=currentView.getBottom()-params.topMargin;
                bottom=top+mDivider.getIntrinsicHeight();
                mDivider.setBounds(left,top,right,bottom);
                mDivider.draw(c);
            }
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
        if(mOrientation==LinearLayoutManager.VERTICAL){
            outRect.set(0,0,0,mDivider.getIntrinsicHeight());
        }

    }
}
