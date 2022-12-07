package com.gym.gymapp.utils

import android.R
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView


class AutoFitGridRecyclerView : RecyclerView {
    private var manager: GridLayoutManager? = null
    private var columnWidth = -1

    constructor(context: Context) : super(context) {
        initialization(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initialization(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        initialization(context, attrs)
    }

    private fun initialization(context: Context, attrs: AttributeSet?) {
        try {
            if (attrs != null) {
                // list the attributes we want to fetch
                val attrsArray = intArrayOf(
                    R.attr.columnWidth
                )
                val array = context.obtainStyledAttributes(attrs, attrsArray)
                //retrieve the value of the 0 index, which is columnWidth
                columnWidth = array.getDimensionPixelSize(0, -1)
                array.recycle()
            }
            manager = GridLayoutManager(context, 1)
            layoutManager = manager
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        return super.dispatchTouchEvent(ev)
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        super.onMeasure(widthSpec, heightSpec)
        try {
            if (columnWidth > 0) {
                //The spanCount will always be at least 1
                val spanCount = Math.max(1, measuredWidth / columnWidth)
                manager!!.spanCount = spanCount
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}