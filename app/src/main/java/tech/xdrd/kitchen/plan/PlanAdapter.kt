package tech.xdrd.kitchen.plan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import tech.xdrd.kitchen.R
import tech.xdrd.kitchen.databinding.ViewPlanBinding
import tech.xdrd.kitchen.model.Plan

class PlanAdapter(private val l: (View, Plan) -> Unit) :
    PagedListAdapter<Plan, PlanAdapter.ViewHolder>(object : DiffUtil.ItemCallback<Plan>() {
        override fun areItemsTheSame(old: Plan, new: Plan): Boolean = old.id == new.id
        override fun areContentsTheSame(old: Plan, new: Plan): Boolean = old.contentEqual(new)
    }) {
    var data = listOf<Plan>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ViewPlanBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.view_plan,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bindData(item, l)
    }

    class ViewHolder(private val binding: ViewPlanBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(item: Plan, l: (View, Plan) -> Unit) {
            binding.item = item
            binding.root.setOnClickListener { v -> l(v, item) }
        }
    }
}