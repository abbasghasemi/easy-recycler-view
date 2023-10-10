package com.ag.recyclerview.easyadapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.ag.recyclerview.easyadapter.ItemAdapter.ViewBinding
import java.lang.reflect.InvocationTargetException
import java.util.Collections


/**
 * @author Abbas Ghasemi
 * @param items A list of model items
 * @param viewBinding Is a view controller
 * @see ViewBinding
 */
open class ItemAdapter<V, M>(
    private var items: MutableList<M>, val viewBinding: ViewBinding<V, M>
) : RecyclerView.Adapter<ItemAdapter<V, M>.ViewHolder>() {

    /**
     * @param viewBinding Is a view controller.
     * @see ViewBinding
     */
    constructor(viewBinding: ViewBinding<V, M>) : this(ArrayList<M>(), viewBinding)

    /**
     * @return [items]
     */
    open fun getItems(): MutableList<M> = items

    /**
     * Adds new items to the display list.
     * @param items A list of items.
     */
    open fun insertItems(items: List<M>) {
        val size = this.items.size
        this.items.addAll(items)
        notifyItemRangeInserted(size, items.size)
    }

    /**
     * This method is called when your new items contains the current [items] values.
     * First, items will be cleared and then newly added items will be displayed
     * The size of the new items cannot be smaller than the current items size.
     * If the previous values of the items have changed, it is ignored!.
     * It should not be used when the type of past values has been changed.
     * @param items A list of items
     */
    open fun insertIgnoreItems(items: MutableList<M>) {
        val size = this.items.size
        if (size == items.size) return
        if (size > items.size) {
            throw RuntimeException("insertIgnoreItems: The size of the new items cannot be smaller than the current items size.")
        }
//        this.items.clear()
//        this.items.addAll(items)
        this.items = items;
        notifyItemRangeInserted(size, items.size)
    }

    /**
     * Clear all [items].
     */
    open fun clearItems() {
        val size: Int = items.size
        if (size == 0) return
        items.clear()
        notifyItemRangeRemoved(0, size)
    }

    /**
     * @see clearItems
     * @see insertIgnoreItems
     */
    open fun clearAndInsertItems(items: MutableList<M>) {
        clearItems()
        insertIgnoreItems(items)
    }

    /**
     * All changes of [newItems] compared to [items] are displayed by animation.
     * This method automatically removes the items that aren't in the new items and displays the new items.
     * Also, if the position of the items is changed, it will move to the new position.
     */
    open fun animateTo(newItems: List<M>) {
        try {
            applyAndAnimateRemovals(newItems)
            applyAndAnimateAdditions(newItems)
            applyAndAnimateMovedItems(newItems)
        } catch (e: Exception) {
            //
        }
    }

    /**
     * Added new [item].
     */
    open fun insertItem(item: M) = insertItem(items.size, item)

    /**
     * Added new [item] the list at the specified [position].
     */
    open fun insertItem(position: Int, item: M) {
        items.add(position, item)
        notifyItemInserted(position)
    }

    /**
     * Remove item by [position].
     */
    open fun removeItem(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    /**
     * Remove item by item.
     * @return True if item removed.
     */
    open fun removeItem(item: M): Boolean {
        val index = items.indexOf(item)
        if (index == -1) {
            return false
        }
        removeItem(index)
        return true
    }

    /**
     * Move item [fromPosition] -> [toPosition].
     */
    open fun moveItem(fromPosition: Int, toPosition: Int) {
        val model = items.removeAt(fromPosition)
        items.add(toPosition, model)
        notifyItemMoved(fromPosition, toPosition)
    }

    /**
     * Swap item [fromPosition] <-> [toPosition].
     */
    open fun swapItem(fromPosition: Int, toPosition: Int) {
        Collections.swap(items, fromPosition, toPosition)
        notifyItemChanged(fromPosition)
        notifyItemChanged(toPosition)
    }

    /**
     * Update [item] by [position]
     */
    open fun updateItem(position: Int, item: M) {
        if (items.size <= position){
            throw RuntimeException("items length is ${items.size} but position is ${position}.")
        }
        items.apply {
            removeAt(position)
            add(position, item)
        }
        notifyItemChanged(position)
    }

    /**
     * Search to remove items that are not in the new items.
     */
    private fun applyAndAnimateRemovals(newItems: List<M>) {
        for (i in items.size - 1 downTo 0) {
            val model = items[i]
            if (!newItems.contains(model)) {
                removeItem(i)
            }
        }
    }

    /**
     * Search to add items that are not in the current items.
     */
    private fun applyAndAnimateAdditions(newModels: List<M>) {
        var i = 0
        val count = newModels.size
        while (i < count) {
            val model = newModels[i]
            if (!items.contains(model)) {
                insertItem(i, model)
            }
            i++
        }
    }

    /**
     * Search to change the position of items whose position has changed.
     */
    private fun applyAndAnimateMovedItems(newModels: List<M>) {
        for (toPosition in newModels.size - 1 downTo 0) {
            val model = newModels[toPosition]
            val fromPosition: Int = items.indexOf(model)
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(viewBinding.createItem(parent, LayoutInflater.from(parent.context), viewType))
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind()
    }

    override fun getItemViewType(position: Int): Int {
        return viewBinding.itemType(position)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(private val binding: Binding<V>) : RecyclerView.ViewHolder(
        binding.itemView
    ) {
        /**
         * Bind view
         * @see Binding
         * @see ViewBinding
         */
        fun bind() {
            val item = items[layoutPosition]
            if (!viewBinding.bindItem(binding.view, item, layoutPosition, itemViewType)) {
                try {
                    val method = viewBinding.javaClass.getMethod(
                        "bindItem",
                        binding.view!!::class.java,
                        item!!::class.java,
                        Int::class.javaPrimitiveType
                    )
                    method.invoke(viewBinding, binding.view, item, layoutPosition)
                } catch (e: NoSuchMethodException) {
                    //
                } catch (e: IllegalAccessException) {
                    //
                } catch (e: InvocationTargetException) {
                    //
                }
            }
        }
    }

    /**
     * @param itemView The root view
     * @param view The custom object of view or [Object]
     */
    data class Binding<V>(val itemView: View, val view: V)

    interface ViewBinding<V, M> {
        /**
         * Return the item type of the item at [position] for the purposes
         * of view recycling.
         *
         * The default implementation of this method returns 0, making the assumption of
         * a single item type for the adapter. Unlike ListView adapters, types need not
         * be contiguous. Consider using id resources to uniquely identify item item types.
         *
         * @param position position to query
         * @return integer value identifying the type of the view needed to represent the item at [position].
         * Type codes need not be contiguous.
         */
        fun itemType(position: Int): Int {
            return 0
        }

        /**
         * Called when RecyclerView needs a new [Binding] of the given type to represent
         * an item.
         *
         * This new Binding should be constructed with a new View that can represent the items
         * of the given type. You can either create a new View manually or inflate it from an XML
         * layout file.
         *
         * The new Binding will be used to display items of the adapter using
         * [bindItem]. Since it will be re-used to display
         * different items in the data set, it is a good idea to cache references to sub views of
         * the View to avoid unnecessary [View.findViewById] calls.
         * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position.
         * @param inflater The [LayoutInflater]
         * @param itemType The item type of the new View.
         * @return binding
         * @see Binding
         */
        fun createItem(
            parent: ViewGroup, inflater: LayoutInflater, itemType: Int
        ): Binding<V>

        /**
         * Called by RecyclerView to display the data at the specified position. This method should
         * update the contents of the [Binding.itemView] to reflect the item at the given
         * position.
         *
         * *Note* you can call your own custom bindItem(M, V, Int) method by following the example below:

        ```kotlin
        fun bindItem(view: ViewObject1, item: M, position: Int) {
        //
        }

        fun bindItem(view: ViewObject2, item: M, position: Int) {
        //
        }

        fun bindItem(view: ViewObject..., item: M, position: Int) {
        //
        }
        ```
         * @param view The [Binding.view] which should be updated to represent the contents of the
         *          item at the given position in the data set.
         * @param item The Item selected within the position.
         * @param position The position of the item within the adapter's data set.
         * @param itemType [itemType]
         * @return true if you use it but if you return false,[ViewHolder.bind] will try to call your custom
         * bindItem method.
         */
        fun bindItem(view: V, item: M, position: Int, itemType: Int): Boolean {
            return false
        }
    }

    /**
     * Specifies whether long press drag is active or inactive.
     */
    open var longPressDragEnabled: Boolean? = null
        set(value) {
            field = value!!
        }

    /**
     * The user can move items from one position to another by long touching them.
     * @param recyclerView
     * @param dragListener
     * @see LongPressDragListener
     */
    open fun enableLongPressDrag(
        recyclerView: RecyclerView,
        dragListener: LongPressDragListener
    ) {
        if (longPressDragEnabled != null) return
        longPressDragEnabled = true
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {

            private var startPosition = -1
            private var endPosition = -1

            override fun isItemViewSwipeEnabled(): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

            override fun isLongPressDragEnabled(): Boolean {
                return this@ItemAdapter.longPressDragEnabled!!
            }

            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                return if (!dragListener.canDrag(viewHolder.layoutPosition)) {
                    0
                } else makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0)
            }

            override fun onMove(
                recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                if (!dragListener.canDrag(target.layoutPosition)) {
                    return false
                }
                if (startPosition == -1) {
                    startPosition = viewHolder.layoutPosition
                }
                endPosition = target.layoutPosition
                moveItem(viewHolder.layoutPosition, endPosition)
                return true
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)
                if (startPosition != endPosition) {
                    dragListener.itemMoved(startPosition, endPosition)
                } else if (startPosition == -1){
                    dragListener.dragCancelled()
                }
                endPosition = -1
                startPosition = -1
            }
        })
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    interface LongPressDragListener {

        /**
         * Specifies the item aligned with [position] to drag.
         * @return true if you want to drag otherwise false.
         */
        fun canDrag(position: Int): Boolean

        /**
         * When the item is dropped and its position changes.
         * @param startPosition past position
         * @param endPosition new position
         */
        fun itemMoved(startPosition: Int, endPosition: Int)

        /**
         * Called when no movement has occurred.
         */
        fun dragCancelled()

    }
}