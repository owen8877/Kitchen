package tech.xdrd.kitchen.plan

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.material.chip.Chip
import tech.xdrd.kitchen.R
import tech.xdrd.kitchen.databinding.DialogPlanBinding
import tech.xdrd.kitchen.model.Plan.Type.*
import tech.xdrd.kitchen.ui.FullScreenDialog
import java.text.SimpleDateFormat
import java.util.*


class PlanDialog(val model: PlanViewModel.PlanModel) : FullScreenDialog() {
    enum class Mode { Add, Modify }

    private lateinit var binding: DialogPlanBinding
    private var mode: Mode = model.mode

    private val DATE_FORMAT = "EEE, MMM d, yyyy"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.dialog_plan, container, false)
        binding.item = model
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.dPlanToolbar.inflateMenu(
            when (mode) {
                Mode.Add -> R.menu.plan_add
                Mode.Modify -> R.menu.plan_modify
            }
        )
        binding.dPlanToolbar.setNavigationOnClickListener { run { dismiss() } }
        binding.dPlanToolbar.setOnMenuItemClickListener { item ->
            run {
                when (item.itemId) {
                    R.id.m_plan_done -> {
                        when (mode) {
                            Mode.Add -> model.add()
                            Mode.Modify -> model.update()
                        }
                        dismiss()
                    }
                    R.id.m_plan_modify_delete -> {
                        AlertDialog.Builder(context!!)
                            .setTitle("Deletion confirmation")
                            .setMessage("Do you really want to delete this plan?")
                            .setCancelable(true)
                            .setPositiveButton("Yes") { dialog, _ ->
                                run {
                                    model.delete()
                                    dialog.dismiss()
                                    dismiss()
                                }
                            }
                            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                            .show()
                    }
                }
                true
            }
        }

        binding.dPlanTxtDate.text =
            SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(model.date).toString()
        binding.dPlanTxtDate.setOnClickListener {
            val dialog = DatePickerDialog(
                context!!,
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    model.date = Date(year - 1900, month, dayOfMonth)
                    binding.dPlanTxtDate.text =
                        SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(model.date)
                            .toString()
                }, model.date.year + 1900, model.date.month, model.date.date
            )
            dialog.show()
        }

        binding.dPlanChipGroupType.setOnCheckedChangeListener { group, id ->
            if (group.findViewById<Chip>(id)?.isChecked == true)
                model.type = when (id) {
                    R.id.d_plan_chip_breakfast -> Breakfast.ordinal
                    R.id.d_plan_chip_lunch -> Lunch.ordinal
                    R.id.d_plan_chip_dinner -> Dinner.ordinal
                    else -> -1
                }
            else
                model.type = -1

            model.refresh()
        }

        binding.dPlanEdittextContent.addTextChangedListener { run { model.refresh() } }

        model.valid.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.dPlanToolbar.menu.findItem(R.id.m_plan_done)?.isEnabled = it
            }
        })
    }
}