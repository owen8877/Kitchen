package tech.xdrd.kitchen.plan

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import tech.xdrd.kitchen.R
import tech.xdrd.kitchen.Util
import tech.xdrd.kitchen.databinding.ViewPlanBinding
import tech.xdrd.kitchen.databinding.ViewPlanDayTextBinding
import tech.xdrd.kitchen.model.Plan
import tech.xdrd.kitchen.ui.StickyItemDecoration
import java.text.SimpleDateFormat
import java.util.*


class PlanAdapter(private val l: (View, Plan) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), StickyItemDecoration.StickyHeaderInterface {

    // Date sources and decorations
    var showPast = true // false for only future
        set(value) {
            field = value
            beforeNotifyDataSetChanged()
        }
    var source = listOf<Plan>()
        set(value) {
            field = value
            beforeNotifyDataSetChanged()
        }
    private val mData = mutableListOf<PlanOrDecoration>()

//    companion object {
//        val ITEM(old: Plan, new: Plan): Boolean = old.id == new.id
//            override fun areContentsTheSame(old: Plan, new: Plan): Boolean = old.contentEqual(new)
//        }
//    }

    private fun beforeNotifyDataSetChanged() {
        decorateData()
        val newDecoratedData = decorateData()
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val old = mData[oldItemPosition]
                val new = newDecoratedData[newItemPosition]
                return old.idCache?.equals(new.idCache) ?: new.idCache == null
            }

            override fun getOldListSize() = mData.size

            override fun getNewListSize() = newDecoratedData.size

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val old = mData[oldItemPosition]
                val new = newDecoratedData[newItemPosition]
                return old.contentCache?.equals(new.contentCache) ?: new.contentCache == null
            }
        })
        mData.clear()
        mData.addAll(newDecoratedData)
        diff.dispatchUpdatesTo(this)
    }

    private fun decorateData(): MutableList<PlanOrDecoration> {
        if (source.isEmpty())
            return mutableListOf()
        val today = Util.getToday()
        val sourceList = if (showPast) source else
            source.filter { plan -> plan.date >= today }

        val sortedData = sourceList.sortedWith(kotlin.Comparator { o1, o2 ->
            run {
                if (o1.date == o2.date) o1.type.ordinal.compareTo(o2.type.ordinal)
                else o1.date.compareTo(o2.date)
            }
        })
        var currentDate = sortedData[0].date
        val data = mutableListOf(PlanOrDecoration(null, currentDate))
        for (plan in sortedData) {
            if (currentDate < plan.date) {
                currentDate = plan.date
                data.add(PlanOrDecoration(null, currentDate))
            }
            data.add(PlanOrDecoration(plan, plan.date))
        }
        return data
    }

    class PlanOrDecoration(val plan: Plan?, val date: Date) {
        val idCache = plan?.id
        var contentCache = plan?.getContent()
        val decoration = plan == null
    }

    // Recyclerview adapter override
    override fun getItemCount(): Int {
        return mData.size
    }

    override fun getItemViewType(position: Int) =
        if (mData[position].plan == null) ViewType.Decoration.ordinal else ViewType.Plan.ordinal

    enum class ViewType { Plan, Decoration }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ViewType.Decoration.ordinal ->
                HeaderViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.view_plan_day_text,
                        parent,
                        false
                    )
                )
            else ->
                ViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context), R.layout.view_plan, parent, false
                    )
                )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder)
            holder.bindData(mData[position].plan!!, l)
        else if (holder is HeaderViewHolder)
            holder.bindData(mData[position].date)
    }

    // Sticky
    override fun getHeaderPositionForItem(itemPosition: Int): Int {
        for (index in itemPosition downTo 0) {
            if (isHeader(index)) return index
        }
        return 0
    }

    override fun getHeaderLayout(headerPosition: Int) = R.layout.view_plan_day_text

    override fun bindHeaderData(header: View, headerPosition: Int) {
        val item = HeaderViewHolder.DateInformation(mData[headerPosition].date)
        header.findViewById<TextView>(R.id.v_plan_txt_date).text = item.getDay()
        header.findViewById<TextView>(R.id.v_plan_txt_dayofweek).text = item.getWeekDay()
    }

    override fun isHeader(itemPosition: Int) = mData[itemPosition].decoration

    override fun headerOffset(outRect: Rect) {
        // TODO: Find some way to deal with this MAGIC number.
        outRect.bottom = -100
    }

    // ViewHolders
    class HeaderViewHolder(private val binding: ViewPlanDayTextBinding) :
        RecyclerView.ViewHolder(binding.root) {
        data class DateInformation(val date: Date) {
            companion object {
                private val DAY_FORMAT = SimpleDateFormat("dd")
                private val WEEKDAY_FORMAT = SimpleDateFormat("EEE")
            }

            fun getDay(): String {
                return DAY_FORMAT.format(date).toString()
            }

            fun getWeekDay(): String {
                return WEEKDAY_FORMAT.format(date).toString()
            }
        }

        fun bindData(item: Date) {
            binding.item = DateInformation(item)
            binding.valid = item >= Util.getToday()
        }
    }

    class ViewHolder(private val binding: ViewPlanBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(item: Plan, l: (View, Plan) -> Unit) {
            binding.item = item
            binding.valid = item.date >= Util.getToday()
            binding.root.setOnClickListener { v -> l(v, item) }
        }
    }
}