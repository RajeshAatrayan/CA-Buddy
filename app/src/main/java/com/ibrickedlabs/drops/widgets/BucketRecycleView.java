package com.ibrickedlabs.drops.widgets;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by RajeshAatrayan on 28-08-2018.
 */

public class BucketRecycleView extends RecyclerView {

    private List<View> mNonEmptyViews = Collections.emptyList();//makes zero items
    private List<View> mEmptyViews = Collections.emptyList();

    /**
     * Since we want to override all the methods in RecyclerView and make our own custom RecyclerView iee..BucketRecyclerView we just nmaking this
     * @param context
     */

    /**
     * Since we want to check whether the adapter contains views and for all rest of the operations we are using this AdapterDataObserver
     * and btw notifyDataSetChanged will indirectly call dese methods
     */
    private AdapterDataObserver adapterDataObserver = new AdapterDataObserver() {
        /**
         * we removed super bcz the class methods are not expecting anything from here
         */
        @Override
        public void onChanged() {
            toggleViews();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            toggleViews();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            toggleViews();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {

        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            toggleViews();
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            toggleViews();
        }
    };

    private void toggleViews() {
        if (getAdapter() != null && !mNonEmptyViews.isEmpty() && !mEmptyViews.isEmpty()) {


            if (getAdapter().getItemCount() == 0) {

                //hides the recyclerview
                setVisibility(View.GONE);


                //hides toolbar
                for (View view : mNonEmptyViews) {
                    view.setVisibility(View.GONE);
                }
//shows the EmptyView

                for (View view : mEmptyViews) {
                    view.setVisibility(View.VISIBLE);
                }
            } else {


                //shows recyclerview
                setVisibility(View.VISIBLE);

                //hides toolbar
                for (View view : mNonEmptyViews) {
                    view.setVisibility(View.VISIBLE);
                }
//shows the EmptyView

                for (View view : mEmptyViews) {
                    view.setVisibility(View.GONE);
                }

            }
        }
    }

    public BucketRecycleView(Context context) {
        super(context);
    }

    public BucketRecycleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BucketRecycleView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * we are overriding this becaouse we set AdapterDataObserver on this adapter
     *
     * @param adapter--> we pass in for the recycler view
     */
    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        if (adapter != null) {//so we have set the adapter in the mainACtivity
            adapter.registerAdapterDataObserver(adapterDataObserver);//we need to register the dataobserver
        }
        adapterDataObserver.onChanged();
    }

    public void showIfEmpty(View... mViews) {

        mEmptyViews = Arrays.asList(mViews);
    }

    public void hideIfEmpty(View... views) {
        mNonEmptyViews = Arrays.asList(views);

    }
}
