# Implement RecyclerView Adapter with minimum complexity
*Reduce Adapter creation speed to two methods.*
### Sample
There is a [sample](https://github.com/abbasghasemi/easy-recyclerview-adapter/tree/master/sample) module.
## Single view type
```kotlin
ItemAdapter(items, object : ItemAdapter.ViewBinding<ItemRecycler1Binding, DataModel>() {
    override fun create(parent: ViewGroup, inflater: LayoutInflater, viewType: Int): ItemAdapter.Binding<ItemRecycler1Binding> {
        val item = ItemRecycler1Binding.inflate(inflater, parent, false)
        return ItemAdapter.Binding(item.root, item)
    }

    override fun bind(view: ItemRecycler1Binding, item: DataModel, position: Int, viewType: Int): Boolean {
        view.sample1.text = item.data
        return true
    }
})
```
## Custom view type:
```kotlin
ItemAdapter(items, object : ItemAdapter.ViewBinding<Any, DataModel>() {
    override fun type(position: Int): Int {
        return position
    }

    override fun create(parent: ViewGroup, inflater: LayoutInflater, viewType: Int): ItemAdapter.Binding<Any> {
        return if (viewType == 0) {
            val item = ItemRecycler1Binding.inflate(inflater, parent, false)
            ItemAdapter.Binding(item.root, item)
        } else {
            val item = ItemRecycler2Binding.inflate(inflater, parent, false)
            ItemAdapter.Binding(item.root, item)
        }
    }
    
    @Keep
    fun bind(view: ItemRecycler1Binding, item: DataModel, position: Int) {
        view.sample1.text = item.data
    }

    @Keep
    fun bind(view: ItemRecycler2Binding, item: DataModel, position: Int) {
        view.sample2.text = item.data
    }
})
```
## Methods:

```kotlin
    animateTo(newItems) //Display new list changes with animation

    enableLongPressDrag(recyclerView,longPressDragListener) //The user can move items from one position to another

    insertItems(newItems)

    insertIgnoreItems(items)

    clearItems()
    
    clearAndInsertItems(newItems)

    insertItem(item)

    removeItem(position)

    moveItem(fromPosition,toPosition)
    
    swapItem(fromPosition,toPosition)
```