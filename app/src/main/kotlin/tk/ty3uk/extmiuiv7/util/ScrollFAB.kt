package tk.ty3uk.extmiuiv7.util

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.View

class ScrollFAB(context: Context, attrs: AttributeSet) : FloatingActionButton.Behavior() {

    override fun onNestedScroll(coordinatorLayout: CoordinatorLayout?, child: FloatingActionButton?, target: View?, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed)

        if (child!!.isEnabled)
            //child -> Floating Action Button
            if (child.visibility == View.VISIBLE && dyConsumed > 0) {
                child.hide()
            } else if (child.visibility == View.GONE && dyConsumed < 0) {
                child.show()
            }
    }

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout?, child: FloatingActionButton?, directTargetChild: View?, target: View?, nestedScrollAxes: Int): Boolean {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL
    }


}