package com.ibrickedlabs.drops.adapters;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by RajeshAatrayan on 30-08-2018.
 */
//this class extends that becz we need to perfom swip operations
public class SimpleTouchCallBack extends ItemTouchHelper.Callback {
private  SwipeListener mSwipeListener;

    public SimpleTouchCallBack(SwipeListener mSwipeListener) {
        this.mSwipeListener = mSwipeListener;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
      /*
      we need to return makwMovementFlags(int drag,int swip);since we dont want to draw draw is 0 and         for swipe we are provindig some values
       */
        //HERE END STANDS FOR ANY END
        return makeMovementFlags(0, ItemTouchHelper.END);
    }
    /*
    since we dont want to drag elements
     */

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    /**
     * onMove is called when the drag happends since we dont care about drag we are leaveing it
     *
     * @param recyclerView
     * @param viewHolder
     * @param target
     * @return
     */

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }


    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
       if(viewHolder instanceof  AdapterExam.DropHolder){
           super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
       }

    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if(viewHolder instanceof  AdapterExam.DropHolder){
            super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

    }

    /**
     * It is called when the ViewHolder is swiped by the user
     * ie..it is called only after swiping
     *
     * @param viewHolder
     * @param direction
     */

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        //we used adapterPosition because layout postion takes bittime to update so we used it
        if(viewHolder instanceof  AdapterExam.DropHolder){
            mSwipeListener.onSwipe(viewHolder.getAdapterPosition());
        }


    }
}
