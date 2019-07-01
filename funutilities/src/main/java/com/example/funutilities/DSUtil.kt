package com.example.funutilities

import androidx.recyclerview.widget.RecyclerView
import com.example.dragswipe.*

fun <T, VH : RecyclerView.ViewHolder> DragSwipeUtils.startEndDrag(
    dragSwipeAdapter: DragSwipeAdapter<T, VH>,
    recyclerView: RecyclerView,
    swipeDirs: Int = Direction.NOTHING.value,
    dragSwipeActions: DragSwipeActions<T, VH>? = null
): DragSwipeHelper {
    return setDragSwipeUp(
        dragSwipeAdapter,
        recyclerView,
        Direction.START + Direction.END,
        swipeDirs,
        dragSwipeActions
    )
}

fun <T, VH : RecyclerView.ViewHolder> DragSwipeUtils.upDownDrag(
    dragSwipeAdapter: DragSwipeAdapter<T, VH>,
    recyclerView: RecyclerView,
    swipeDirs: Int = Direction.NOTHING.value,
    dragSwipeActions: DragSwipeActions<T, VH>? = null
): DragSwipeHelper {
    return setDragSwipeUp(
        dragSwipeAdapter,
        recyclerView,
        Direction.UP + Direction.DOWN,
        swipeDirs,
        dragSwipeActions
    )
}

fun <T, VH : RecyclerView.ViewHolder> DragSwipeUtils.startEndSwipe(
    dragSwipeAdapter: DragSwipeAdapter<T, VH>,
    recyclerView: RecyclerView,
    dragDirs: Int = Direction.NOTHING.value,
    dragSwipeActions: DragSwipeActions<T, VH>? = null
): DragSwipeHelper {
    return setDragSwipeUp(
        dragSwipeAdapter,
        recyclerView,
        dragDirs,
        Direction.START + Direction.END,
        dragSwipeActions
    )
}

fun <T, VH : RecyclerView.ViewHolder> DragSwipeUtils.upDownSwipe(
    dragSwipeAdapter: DragSwipeAdapter<T, VH>,
    recyclerView: RecyclerView,
    dragDirs: Int = Direction.NOTHING.value,
    dragSwipeActions: DragSwipeActions<T, VH>? = null
): DragSwipeHelper {
    return setDragSwipeUp(
        dragSwipeAdapter,
        recyclerView,
        dragDirs,
        Direction.UP + Direction.DOWN,
        dragSwipeActions
    )
}

fun <T, VH : RecyclerView.ViewHolder> DragSwipeAdapter<T, VH>.shuffleItems() {
    list.shuffle()
}

fun <T, VH : RecyclerView.ViewHolder> DragSwipeAdapter<T, VH>.getFirstItem(): T {
    return list.first()
}

fun <T, VH : RecyclerView.ViewHolder> DragSwipeAdapter<T, VH>.getMiddleItem(): T {
    return list[itemCount / 2]
}

fun <T, VH : RecyclerView.ViewHolder> DragSwipeAdapter<T, VH>.getLastItem(): T {
    return list.last()
}

fun RecyclerView.attachDragSwipeHelper(dragSwipeHelper: DragSwipeHelper) {
    DragSwipeUtils.enableDragSwipe(dragSwipeHelper, this)
}

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
