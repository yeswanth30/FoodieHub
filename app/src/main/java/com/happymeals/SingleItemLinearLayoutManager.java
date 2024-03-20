package com.happymeals;


import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SingleItemLinearLayoutManager extends LinearLayoutManager {

    public SingleItemLinearLayoutManager(Context context) {
        super(context);
    }

    @Override
    public boolean canScrollVertically() {
        // Disables vertical scrolling
        return false;
    }

    @Override
    public boolean canScrollHorizontally() {
        // Enables horizontal scrolling
        return true;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int scrolled = super.scrollHorizontallyBy(dx, recycler, state);
        if (scrolled != 0) {
            RecyclerView recyclerView = recycler.getViewForPosition(0).getParent() instanceof RecyclerView ? (RecyclerView) recycler.getViewForPosition(0).getParent() : null;
            if (recyclerView != null && recyclerView.getAdapter() != null) {
                // Notify that the data set has changed
                // This is to ensure that only one item is visible at a time
                // This will trigger onScrollStateChanged and onScrolled events in your RecyclerView
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        }
        return scrolled;
    }
}






