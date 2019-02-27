package ptml.releasing.views


import ptml.releasing.R

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class SwipeRefreshLayoutHelper {
    fun init(swipeRefreshLayout: SwipeRefreshLayout) {
        swipeRefreshLayout.setColorSchemeColors(swipeRefreshLayout.context.resources.getColor(R.color.colorPrimary),
                swipeRefreshLayout.context.resources.getColor(R.color.colorAccent), swipeRefreshLayout.context.resources.getColor(R.color.colorAccent))
    }
}
