package com.example.myapplication

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class DragManageAdapter<T, VH : RecyclerView.ViewHolder>(
    adapter: DragAdapter<T, VH>,
    dragDirs: Int,
    swipeDirs: Int
) :
    ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {
    private var listAdapter = adapter

    var actions: DragActions<T, VH> = object : DragActions<T, VH> {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder,
            adapter: DragAdapter<T, VH>
        ) {
            listAdapter.swapItems(viewHolder.adapterPosition, target.adapterPosition)
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

        }
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        actions.onMove(recyclerView, viewHolder, target, listAdapter)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        actions.onSwiped(viewHolder, direction)
    }

}

interface DragActions<T, VH : RecyclerView.ViewHolder> {
    fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder,
        adapter: DragAdapter<T, VH>
    )

    fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int)
}

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
}

class DraggingUtils {
    companion object {
        fun <T, VH : RecyclerView.ViewHolder> setDragUp(
            adapter: DragAdapter<T, VH>,
            recyclerView: RecyclerView,
            dragDirs: Int = 0,
            swipeDirs: Int = 0,
            actions: DragActions<T, VH>? = null
        ) {
            val callback = DragManageAdapter(
                adapter, dragDirs,
                swipeDirs
            )
            callback.actions = actions ?: callback.actions
            val helper = ItemTouchHelper(callback)
            helper.attachToRecyclerView(recyclerView)
        }
    }
}