package com.ag.recyclerview.easyadapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.lang.reflect.InvocationTargetException

/**
 * @author Abbas Ghasemi
 * @param items A list of model items
 * @param viewBinding Is a view controller
 * @see ViewBinding
 */
open class ItemAdapter<V, M>(
    val items: MutableList<M>, val viewBinding: ViewBinding<V, M>
) : RecyclerView.Adapter<ItemAdapter<V, M>.ViewHolder>() {

    /**
     * @param viewBinding Is a view controller
     * @see ViewBinding
     */
    constructor(viewBinding: ViewBinding<V, M>) : this(ArrayList<M>(), viewBinding)

    /**
     * Adds and displays new items to the list
     * @param items A list of items
     */
    fun insertItems(items: List<M>) {
        val size = this.items.size
        this.items.addAll(items)
        notifyItemRangeInserted(size, items.size)
    }

    /**
     * This method is called when your new items contains the current [items] values.
     * First, the items is emptied and then a new items is added and displayed.
     * The size of the new items cannot be smaller than the current items size.
     * If the previous values of the items have changed, it is ignored!.
     * It should not be used when the type of past values has been changed.
     * @param items A list of items
     */
    fun insertIgnoreItems(items: List<M>) {
        val size = this.items.size
        if (size > items.size) {
            throw RuntimeException("insertIgnoreItems: The size of the new items cannot be smaller than the current items size.")
        }
        this.items.clear()
        this.items.addAll(items)
        notifyItemRangeInserted(size, items.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(viewBinding.create(parent, LayoutInflater.from(parent.context), viewType))
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind()
    }

    override fun getItemViewType(position: Int): Int {
        return viewBinding.type(position)
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
            if (!viewBinding.bind(binding.view, item, layoutPosition, itemViewType)) {
                try {
                    val method = viewBinding.javaClass.getMethod(
                        "bind",
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

    abstract class ViewBinding<V, M> {
        /**
         * Return the view type of the item at [position] for the purposes
         * of view recycling.
         *
         * The default implementation of this method returns 0, making the assumption of
         * a single view type for the adapter. Unlike ListView adapters, types need not
         * be contiguous. Consider using id resources to uniquely identify item view types.
         *
         * @param position position to query
         * @return integer value identifying the type of the view needed to represent the item at [position].
         * Type codes need not be contiguous.
         */
        open fun type(position: Int): Int {
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
         * [bind]. Since it will be re-used to display
         * different items in the data set, it is a good idea to cache references to sub views of
         * the View to avoid unnecessary [View.findViewById] calls.
         * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position.
         * @param inflater The [LayoutInflater]
         * @param viewType The view type of the new View.
         * @return binding
         * @see Binding
         */
        abstract fun create(
            parent: ViewGroup, inflater: LayoutInflater, viewType: Int
        ): Binding<V>

        /**
         * Called by RecyclerView to display the data at the specified position. This method should
         * update the contents of the [Binding.itemView] to reflect the item at the given
         * position.
         *
         * *Note* you can call your own custom bind(M, V, Int) method by following the example below:

        ```kotlin
        fun bind(view: ViewObject1, item: M, position: Int) {
        //
        }

        fun bind(view: ViewObject2, item: M, position: Int) {
        //
        }

        fun bind(view: ViewObject..., item: M, position: Int) {
        //
        }
        ```
         * @param view The [Binding.view] which should be updated to represent the contents of the
         *          item at the given position in the data set.
         * @param item The Item selected within the position.
         * @param position The position of the item within the adapter's data set.
         * @param viewType [type]
         * @return true if you use it but if you return false,[ViewHolder.bind] will try to call your custom
         * bind method.
         */
        open fun bind(view: V, item: M, position: Int, viewType: Int): Boolean {
            return false
        }
    }
}