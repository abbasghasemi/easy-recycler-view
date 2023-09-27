package com.ag.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.recyclerview.widget.LinearLayoutManager
import com.ag.recyclerview.easyadapter.ItemAdapter
import com.ag.sample.databinding.ActivityMainBinding
import com.ag.sample.databinding.ItemRecycler1Binding
import com.ag.sample.databinding.ItemRecycler2Binding

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private var items = mutableListOf(DataModel("data1"), DataModel("data2"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        with(binding!!) {
            recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
            recyclerView.adapter = customAdapter()
        }

    }

    private fun defaultAdapter(): ItemAdapter<ItemRecycler1Binding, DataModel> {
        return ItemAdapter(
            items,
            object : ItemAdapter.ViewBinding<ItemRecycler1Binding, DataModel>() {

                override fun create(
                    parent: ViewGroup,
                    inflater: LayoutInflater,
                    viewType: Int
                ): ItemAdapter.Binding<ItemRecycler1Binding> {
                    val item = ItemRecycler1Binding.inflate(inflater, parent, false)
                    return ItemAdapter.Binding(item.root, item)
                }

                override fun bind(
                    view: ItemRecycler1Binding,
                    item: DataModel,
                    position: Int,
                    viewType: Int
                ): Boolean {
                    view.sample1.text = item.data
                    return true
                }

            })
    }

    private fun customAdapter(): ItemAdapter<Any, DataModel> {
        return ItemAdapter(items, object : ItemAdapter.ViewBinding<Any, DataModel>() {

            override fun type(position: Int): Int {
                return position
            }

            override fun create(
                parent: ViewGroup,
                inflater: LayoutInflater,
                viewType: Int
            ): ItemAdapter.Binding<Any> {
                return if (viewType == 0) {
                    val item = ItemRecycler1Binding.inflate(inflater, parent, false)
                    ItemAdapter.Binding(item.root, item)
                } else {
                    val item = ItemRecycler2Binding.inflate(inflater, parent, false)
                    ItemAdapter.Binding(item.root, item)
                }
            }

            @Keep
            fun bind(
                view: ItemRecycler1Binding,
                item: DataModel,
                position: Int
            ) {
                view.sample1.text = item.data
            }

            @Keep
            fun bind(
                view: ItemRecycler2Binding,
                item: DataModel,
                position: Int
            ) {
                view.sample2.text = item.data
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    data class DataModel(val data: String)
}