package com.example.myapplication

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

private class DragManageAdapter<T, VH : RecyclerView.ViewHolder>(
    adapter: DragAdapter<T, VH>,
    dragDirs: Int,
    swipeDirs: Int
) :
    ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {
    private var listAdapter = adapter

    var actions: DragActions<T, VH> = object : DragActions<T, VH> {}

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        actions.onMove(recyclerView, viewHolder, target, listAdapter)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        actions.onSwiped(viewHolder, direction, listAdapter)
    }

}

enum class Direction(val value: Int) {
    START(ItemTouchHelper.START),
    END(ItemTouchHelper.END),
    LEFT(ItemTouchHelper.LEFT),
    RIGHT(ItemTouchHelper.RIGHT),
    UP(ItemTouchHelper.UP),
    DOWN(ItemTouchHelper.DOWN);

    fun or(direction: Direction): Int {
        return this.value.or(direction.value)
    }

    fun and(direction: Direction): Int {
        return this.value.and(direction.value)
    }

    fun xor(direction: Direction): Int {
        return this.value.xor(direction.value)
    }
}

interface DragActions<T, VH : RecyclerView.ViewHolder> {
    fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder,
        adapter: DragAdapter<T, VH>
    ) {
        adapter.swapItems(viewHolder.adapterPosition, target.adapterPosition)
    }

    fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int, adapter: DragAdapter<T, VH>) {
        adapter.removeItem(viewHolder.adapterPosition, viewHolder, direction)
    }
}

/**
 * Make your Adapter extend this
 */
abstract class DragAdapter<T, VH : RecyclerView.ViewHolder>(var list: ArrayList<T>) : RecyclerView.Adapter<VH>() {
    /**
     * Function called to swap dragged items
     */
    fun swapItems(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                list[i] = list.set(i + 1, list[i])
            }
        } else {
            for (i in fromPosition..toPosition + 1) {
                list[i] = list.set(i - 1, list[i])
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    fun removeItem(position: Int, viewHolder: RecyclerView.ViewHolder, direction: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
    }
}

class DragSwipeHelper() {

    internal lateinit var itemTouchHelper: ItemTouchHelper

    internal constructor(itemTouchHelper: ItemTouchHelper) : this() {
        this.itemTouchHelper = itemTouchHelper
    }
}

class DragSwipeUtils {
    companion object {
        /**
         * Then call this and you are good to go!
         */
        fun <T, VH : RecyclerView.ViewHolder> setDragSwipeUp(
            adapter: DragAdapter<T, VH>,
            recyclerView: RecyclerView,
            dragDirs: Int = 0,
            swipeDirs: Int = 0,
            actions: DragActions<T, VH>? = null
        ): DragSwipeHelper {
            val callback = DragManageAdapter(
                adapter, dragDirs,
                swipeDirs
            )
            callback.actions = actions ?: callback.actions
            val helper = ItemTouchHelper(callback)
            helper.attachToRecyclerView(recyclerView)
            return DragSwipeHelper(helper)
        }

        fun disableDragSwipe(helper: DragSwipeHelper) {
            helper.itemTouchHelper.attachToRecyclerView(null)
        }
    }
}