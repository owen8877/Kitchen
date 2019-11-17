package tech.xdrd.kitchen.supply

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import tech.xdrd.kitchen.R
import tech.xdrd.kitchen.databinding.ViewSupplyBoughtNoticeBinding
import tech.xdrd.kitchen.databinding.ViewSupplyIngredientBinding
import tech.xdrd.kitchen.model.SupplyIngredient

class SupplyItemAdapter(private val l: (View, SupplyIngredient) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var source = listOf<SupplyIngredient>()
        set(value) {
            field = value
            beforeNotifyDataSetChanged()
        }
    private val mData = mutableListOf<CachedIngredientOrDecoration>()

    private fun beforeNotifyDataSetChanged() {
        val newDecoratedData = decorateData()
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val old = mData[oldItemPosition]
                val new = newDecoratedData[newItemPosition]
                return old.idCache?.equals(new.idCache) ?: (new.idCache == null)
            }

            override fun getOldListSize() = mData.size

            override fun getNewListSize() = newDecoratedData.size

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val old = mData[oldItemPosition]
                val new = newDecoratedData[newItemPosition]
                return old.contentCache?.equals(new.contentCache) ?: (new.contentCache == null)
            }
        })
        mData.clear()
        mData.addAll(newDecoratedData)
        diff.dispatchUpdatesTo(this)
    }

    private fun decorateData(): List<CachedIngredientOrDecoration> {
        if (source.isEmpty())
            return mutableListOf()

        val sortedList = source.sortedWith(kotlin.Comparator { o1, o2 ->
            run {
                when {
                    o1.bought -> when {
                        o2.bought -> o1.date.compareTo(o2.date)
                        else -> 1
                    }
                    else -> when {
                        o2.bought -> -1
                        else -> o1.date.compareTo(o2.date)
                    }
                }
            }
        })
        val boughtStartIndex = sortedList.indexOfFirst(SupplyIngredient::bought)
        val sortedCachedList = sortedList.map { supplyIngredient ->
            CachedIngredientOrDecoration(
                supplyIngredient,
                R.string.view_supply_decoration_placeholder
            )
        }.toMutableList()
        if (boughtStartIndex >= 0) {
            sortedCachedList.add(
                boughtStartIndex,
                CachedIngredientOrDecoration(null, R.string.view_supply_bought_item_notice)
            )
        }
        return sortedCachedList
    }

    class CachedIngredientOrDecoration(val ingredient: SupplyIngredient?, @StringRes val heading: Int) {
        val decoration = ingredient == null
        val idCache = ingredient?.id
        var contentCache = ingredient?.toString()
    }

    enum class ViewType { Ingredient, BoughtDecoration }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            ViewType.Ingredient.ordinal -> {
                val binding = DataBindingUtil.inflate<ViewSupplyIngredientBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.view_supply_ingredient,
                    parent,
                    false
                )
                return ViewHolder(binding)
            }
            else -> {
                val binding = DataBindingUtil.inflate<ViewSupplyBoughtNoticeBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.view_supply_bought_notice,
                    parent,
                    false
                )
                return BoughtNoticeViewHolder(binding)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (mData[position].decoration) ViewType.BoughtDecoration.ordinal else ViewType.Ingredient.ordinal
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = mData[position]
        if (holder is ViewHolder) {
            holder.bindData(item.ingredient!!, l)
        } else if (holder is BoughtNoticeViewHolder) {
            holder.bindData(item)
        }
    }

    class ViewHolder(val binding: ViewSupplyIngredientBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(item: SupplyIngredient, l: (View, SupplyIngredient) -> Unit) {
            binding.root.setOnClickListener { v -> l(v, item) }
            binding.item = item
            binding.vSupplyIngredientCheckbox.setOnClickListener { v ->
                v as CheckBox
                item.realm.executeTransaction {
                    run {
                        item.bought = v.isChecked
                    }
                }
            }
        }
    }

    class BoughtNoticeViewHolder(val binding: ViewSupplyBoughtNoticeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(item: CachedIngredientOrDecoration) {
            binding.vSupplyBoughtNoticeText.setText(item.heading)
        }
    }
}
