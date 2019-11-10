package tech.xdrd.kitchen.storage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import tech.xdrd.kitchen.R
import tech.xdrd.kitchen.databinding.ViewStorageIngredientBinding
import tech.xdrd.kitchen.model.StorageIngredient

class StorageItemAdapter(private val l: (View, StorageIngredient) -> Unit) :
    RecyclerView.Adapter<StorageItemAdapter.ViewHolder>() {
    var data = listOf<StorageIngredient>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ViewStorageIngredientBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.view_storage_ingredient,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bindData(item, l)
    }

    class ViewHolder(private val binding: ViewStorageIngredientBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(item: StorageIngredient, l: (View, StorageIngredient) -> Unit) {
            binding.item = item
            binding.root.setOnClickListener { v -> l(v, item) }
        }
    }
}
