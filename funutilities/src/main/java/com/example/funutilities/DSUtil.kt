package com.example.funutilities

import androidx.recyclerview.widget.RecyclerView
import com.example.dragswipe.DragSwipeAdapter
import com.example.dragswipe.DragSwipeHelper
import com.example.dragswipe.DragSwipeUtils
import java.util.*
import kotlin.random.Random

fun <T, VH : RecyclerView.ViewHolder> DragSwipeAdapter<T, VH>.shuffleItems() {
    for (i in list.indices) {
        val num = Random.nextInt(0, list.size - 1)
        Collections.swap(list, i, num)
        notifyItemMoved(i, num)
        notifyItemChanged(i)
        notifyItemChanged(num)
    }
}

fun <T, VH : RecyclerView.ViewHolder> DragSwipeAdapter<T, VH>.getFirstItem(): T {
    return list.first()
}

fun <T, VH : RecyclerView.ViewHolder> DragSwipeAdapter<T, VH>.getMiddleItem(): T {
    return list.middle()
}

fun <T, VH : RecyclerView.ViewHolder> DragSwipeAdapter<T, VH>.getLastItem(): T {
    return list.last()
}

operator fun <T, VH : RecyclerView.ViewHolder> DragSwipeAdapter<T, VH>.get(num: Int): T = list[num]

operator fun <T, VH : RecyclerView.ViewHolder> DragSwipeAdapter<T, VH>.set(num: Int, element: T) {
    list[num] = element
}

operator fun <T, VH : RecyclerView.ViewHolder> DragSwipeAdapter<T, VH>.set(
    num: IntRange,
    element: List<T>
) {
    for ((i, j) in num.withIndex()) {
        list[j] = element[i]
    }
}

operator fun <T, VH : RecyclerView.ViewHolder> DragSwipeAdapter<T, VH>.plusAssign(elements: List<T>) {
    addItems(elements)
}

operator fun <T, VH : RecyclerView.ViewHolder> DragSwipeAdapter<T, VH>.plusAssign(element: T) {
    addItem(element)
}

operator fun <T, VH : RecyclerView.ViewHolder> DragSwipeAdapter<T, VH>.minusAssign(element: List<T>) {
    val intList = arrayListOf<Int>()
    for (i in list.withIndex())
        if (i == element)
            intList += i.index
    for (i in intList)
        removeItem(i)
}

operator fun <T, VH : RecyclerView.ViewHolder> DragSwipeAdapter<T, VH>.minusAssign(element: T) {
    removeItem(list.indexOf(element))
}

operator fun <T, VH : RecyclerView.ViewHolder> DragSwipeAdapter<T, VH>.contains(element: T): Boolean {
    return element in list
}

/**
 * allows iteration
 */
operator fun <T, VH : RecyclerView.ViewHolder> DragSwipeAdapter<T, VH>.iterator() = list.iterator()

/**
 * @see [DragSwipeUtils.enableDragSwipe]
 */
fun RecyclerView.attachDragSwipeHelper(dragSwipeHelper: DragSwipeHelper) {
    DragSwipeUtils.enableDragSwipe(dragSwipeHelper, this)
}

/**
 * @see [DragSwipeUtils.disableDragSwipe]
 */
fun RecyclerView.removeDragSwipeHelper(dragSwipeHelper: DragSwipeHelper) {
    DragSwipeUtils.disableDragSwipe(dragSwipeHelper)
}

class RecyclerViewDragSwipeManager(private val recyclerView: RecyclerView) {
    var dragSwipeHelper: DragSwipeHelper? = null
        set(value) {
            if (field != null) {
                disableDragSwipe()
            }
            field = value
            setDragSwipe()
        }

    /**
     * if true, it will enable DragSwipe
     * if false, it will disable DragSwipe
     */
    var dragSwipedEnabled: Boolean = true
        set(value) {
            field = value
            setDragSwipe()
        }

    private fun setDragSwipe() {
        when (dragSwipedEnabled) {
            true -> enableDragSwipe()
            false -> disableDragSwipe()
        }
    }

    private fun disableDragSwipe() {
        if (dragSwipeHelper != null) {
            DragSwipeUtils.disableDragSwipe(dragSwipeHelper!!)
        }
    }

    private fun enableDragSwipe() {
        if (dragSwipeHelper != null) {
            DragSwipeUtils.enableDragSwipe(dragSwipeHelper!!, recyclerView)
        }
    }
}
