package com.example.dragswipe

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.dragswipe.Direction.NOTHING

/**
 * this class is to set up onMove(for dragging) and onSwiped(for swiping) methods
 */
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

/**
 * these are the directions you can swipe/drag
 * they all are hooked with [ItemTouchHelper] but made them separate so its all in house and a bit easier to use
 * Except for [NOTHING], that's equal to 0.
 */
enum class Direction(val value: Int) {
    START(ItemTouchHelper.START),
    END(ItemTouchHelper.END),
    LEFT(ItemTouchHelper.LEFT),
    RIGHT(ItemTouchHelper.RIGHT),
    UP(ItemTouchHelper.UP),
    DOWN(ItemTouchHelper.DOWN),
    NOTHING(0);

    /**
     * use this when you want to add more than one action (or use [plus])
     */
    fun or(direction: Direction): Int {
        return if (direction == NOTHING || this == NOTHING)
            NOTHING.value
        else
            this.value.or(direction.value)
    }

    /**
     * use this when you want to add more than one action (or use [or])
     */
    operator fun plus(direction: Direction): Int {
        return or(direction)
    }
}

/**
 * This is so you can create your actions for [onMove] and [onSwiped]
 */
interface DragSwipeActions<T, VH : RecyclerView.ViewHolder> {
    fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder,
        dragSwipeAdapter: DragSwipeAdapter<T, VH>
    ) {
        dragSwipeAdapter.swapItems(viewHolder.adapterPosition, target.adapterPosition)
    }

    fun onSwiped(
        viewHolder: RecyclerView.ViewHolder,
        direction: Int,
        dragSwipeAdapter: DragSwipeAdapter<T, VH>
    ) {
        dragSwipeAdapter.removeItem(viewHolder.adapterPosition)
    }
}

/**
 * # Make your Adapter extend this!!!
 * This is the big kahuna, extending this allows your adapter to work with the rest of these Utils.
 *
 * This is a simple one that adds 4 different methods.
 *
 * [setListNotify],
 * [addItem],
 * [removeItem],
 * [swapItems]
 *
 */
abstract class DragSwipeAdapter<T, VH : RecyclerView.ViewHolder>(var list: ArrayList<T>) :
    RecyclerView.Adapter<VH>() {

    /**
     * sets the list with new data and then notifies that the data changed
     */
    fun setListNotify(genericList: ArrayList<T>) {
        list = genericList
        notifyDataSetChanged()
    }

    /**
     * adds an item to position and then notifies
     */
    fun addItem(item: T, position: Int = list.size - 1) {
        list.add(position, item)
        notifyItemInserted(position)
    }

    /**
     * removes an item at position then notifies
     */
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

/**
 * This class should never be created by you. This is to keep things in house.
 * Even though this class contains a single variable, its just so you, the developer, don't have to look at what is
 * really going on behind the scenes.
 */
class DragSwipeHelper internal constructor(internal var itemTouchHelper: ItemTouchHelper)

/**
 * The actual utility
 */
object DragSwipeUtils {
    /**
     * Then call this and you are good to go!
     *
     * This actually sets up the drag/swipe ability.
     *
     * @param dragDirs if you leave this blank, [Direction.NOTHING] is defaulted
     *
     * @param swipeDirs if you leave this blank, [Direction.NOTHING] is defaulted
     *
     * @param dragSwipeActions if you leave this blank, null is defaulted
     * (but its alright because there are built in methods for dragging and swiping. Of course, those won't work if
     * [dragDirs] and [swipeDirs] are nothing)
     *
     * @return an instance of [DragSwipeHelper]. Use this if you want to disable drag/swipe at any point
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

    /**
     * This will disable the drag/swipe ability
     */
    fun disableDragSwipe(helper: DragSwipeHelper) {
        helper.itemTouchHelper.attachToRecyclerView(null)
    }
}