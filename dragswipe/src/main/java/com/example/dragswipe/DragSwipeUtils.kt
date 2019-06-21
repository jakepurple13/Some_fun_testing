package com.example.dragswipe

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

private class DragSwipeManageAdapter<T, VH : RecyclerView.ViewHolder>(
    dragSwipeAdapter: DragSwipeAdapter<T, VH>,
    dragDirs: Int,
    swipeDirs: Int
) :
    ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {
    private var listAdapter = dragSwipeAdapter

    var dragSwipeActions: DragSwipeActions<T, VH> = object : DragSwipeActions<T, VH> {}

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        dragSwipeActions.onMove(recyclerView, viewHolder, target, listAdapter)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        dragSwipeActions.onSwiped(viewHolder, direction, listAdapter)
    }

}

enum class Direction(val value: Int) {
    START(ItemTouchHelper.START),
    END(ItemTouchHelper.END),
    LEFT(ItemTouchHelper.LEFT),
    RIGHT(ItemTouchHelper.RIGHT),
    UP(ItemTouchHelper.UP),
    DOWN(ItemTouchHelper.DOWN),
    NOTHING(0);

    fun or(direction: Direction): Int {
        return if (direction == NOTHING || this == NOTHING)
            NOTHING.value
        else
            this.value.or(direction.value)
    }

    operator fun plus(direction: Direction): Int {
        return or(direction)
    }
}

interface DragSwipeActions<T, VH : RecyclerView.ViewHolder> {
    fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder,
        dragSwipeAdapter: DragSwipeAdapter<T, VH>
    ) {
        dragSwipeAdapter.swapItems(viewHolder.adapterPosition, target.adapterPosition)
    }

    fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int, dragSwipeAdapter: DragSwipeAdapter<T, VH>) {
        dragSwipeAdapter.removeItem(viewHolder.adapterPosition)
    }
}

/**
 * Make your Adapter extend this
 */
abstract class DragSwipeAdapter<T, VH : RecyclerView.ViewHolder>(var list: ArrayList<T>) : RecyclerView.Adapter<VH>() {

    fun setListNotify(genericList: ArrayList<T>) {
        list = genericList
        notifyDataSetChanged()
    }

    fun addItem(item: T, position: Int = list.size - 1) {
        list.add(position, item)
        notifyItemInserted(position)
    }

    fun removeItem(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
    }

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
}

class DragSwipeHelper internal constructor(internal var itemTouchHelper: ItemTouchHelper)

class DragSwipeUtils {
    companion object {
        /**
         * Then call this and you are good to go!
         */
        fun <T, VH : RecyclerView.ViewHolder> setDragSwipeUp(
            dragSwipeAdapter: DragSwipeAdapter<T, VH>,
            recyclerView: RecyclerView,
            dragDirs: Int = Direction.NOTHING.value,
            swipeDirs: Int = Direction.NOTHING.value,
            dragSwipeActions: DragSwipeActions<T, VH>? = null
        ): DragSwipeHelper {
            val callback = DragSwipeManageAdapter(
                dragSwipeAdapter, dragDirs,
                swipeDirs
            )
            callback.dragSwipeActions = dragSwipeActions ?: callback.dragSwipeActions
            val helper = ItemTouchHelper(callback)
            helper.attachToRecyclerView(recyclerView)
            return DragSwipeHelper(helper)
        }

        fun disableDragSwipe(helper: DragSwipeHelper) {
            helper.itemTouchHelper.attachToRecyclerView(null)
        }
    }
}