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