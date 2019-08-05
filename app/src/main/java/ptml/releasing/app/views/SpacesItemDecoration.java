package ptml.releasing.app.views;

import android.graphics.Rect;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int space;
    private int spanCount;
    private int lastItemInFirstLane = -1;
    public SpacesItemDecoration(int space) {
        this(space, 1);
    }

    /**
     * @param space amount of space to be left
     * @param spanCount spans count of one lane
     */
    public SpacesItemDecoration(int space, int spanCount) {
        this.space = space;
        this.spanCount = spanCount;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        final int position = params.getViewPosition();
        final int spanSize;
        final int index;
        if (params instanceof GridLayoutManager.LayoutParams) {
            GridLayoutManager.LayoutParams gridParams = (GridLayoutManager.LayoutParams) params;
            spanSize = gridParams.getSpanSize();
            index = gridParams.getSpanIndex();
        }else{
            spanSize = 1;
            index = position % spanCount;
        }
        // invalid value
        if (spanSize < 1 || index < 0) return;

        if (spanSize == spanCount) { // full span
            outRect.left = space;
            outRect.right = space;
        } else {
            if (index == 0) {  // left one
                outRect.left = space;
            }
            // spanCount >= 1
            if (index == spanCount - 1) { // right one
                outRect.right = space;
            }
            if (outRect.left == 0) {
                outRect.left = space / 2;
            }
            if (outRect.right == 0) {
                outRect.right = space / 2;
            }
        }
        // set top to all in first lane
        if (position < spanCount && spanSize <= spanCount) {
            if (lastItemInFirstLane < 0) { // lay out at first time
                lastItemInFirstLane = position + spanSize == spanCount ? position : lastItemInFirstLane;
                outRect.top = space;
            } else if (position <= lastItemInFirstLane) { // scroll to first lane again
                outRect.top = space;
            }
        }
        outRect.bottom = space;

    }
}