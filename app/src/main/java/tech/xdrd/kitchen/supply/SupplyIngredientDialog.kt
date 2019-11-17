package tech.xdrd.kitchen.supply

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import tech.xdrd.kitchen.R
import tech.xdrd.kitchen.databinding.DialogSupplyIngredientBinding
import tech.xdrd.kitchen.ui.FullScreenDialog

class SupplyIngredientDialog(val model: SupplyViewModel.SupplyIngredientModel) :
    FullScreenDialog() {
    enum class Mode { Add, Modify }

    private lateinit var binding: DialogSupplyIngredientBinding
    private var mode: Mode = model.mode

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.dialog_supply_ingredient, container, false)
        binding.item = model
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.dSupplyIngToolbar.inflateMenu(
            when (mode) {
                Mode.Add -> R.menu.storage_add_ingredient
                Mode.Modify -> R.menu.storage_modify_ingredient
            }
        )
        binding.dSupplyIngToolbar.setNavigationOnClickListener { run { dismiss() } }
        binding.dSupplyIngToolbar.setOnMenuItemClickListener { item ->
            run {
                when (item.itemId) {
                    R.id.m_storage_ingredient_done -> {
                        when (mode) {
                            Mode.Add -> model.add()
                            Mode.Modify -> model.update()
                        }
                        dismiss()
                    }
                    R.id.m_storage_modify_ingredient_delete -> {
                        AlertDialog.Builder(context!!)
                            .setTitle("Deletion confirmation")
                            .setMessage("Do you really want to delete this ingredient?")
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

        binding.dSupplyIngTietName.addTextChangedListener(
            afterTextChanged = { text ->
                run {
                    if (text.isNullOrBlank()) {
                        binding.dSupplyIngTietName.error = "Please input a valid name!"
                    }
                    model.refresh()
                }
            })
        binding.dSupplyIngTietQuantity.addTextChangedListener(
            afterTextChanged = { text ->
                run {
                    if (text.isNullOrBlank() || text.toString().toDouble() < 0.0) {
                        binding.dSupplyIngTietQuantity.error = "Please input a positive quantity!"
                    }
                    model.refresh()
                }
            })

        binding.dSupplyIngBtnM01.setOnClickListener { _ ->
            run {
                binding.dSupplyIngTietQuantity.setText(
                    String.format("%.1f", 0.0.coerceAtLeast(model.quantity - 0.1)),
                    TextView.BufferType.EDITABLE
                )
            }
        }
        binding.dSupplyIngBtnM1.setOnClickListener { _ ->
            run {
                binding.dSupplyIngTietQuantity.setText(
                    String.format("%.1f", 0.0.coerceAtLeast(model.quantity - 1.0)),
                    TextView.BufferType.EDITABLE
                )
            }
        }
        binding.dSupplyIngBtnP01.setOnClickListener { _ ->
            run {
                binding.dSupplyIngTietQuantity.setText(
                    String.format("%.1f", (model.quantity + 0.1)),
                    TextView.BufferType.EDITABLE
                )
            }
        }
        binding.dSupplyIngBtnP1.setOnClickListener { _ ->
            run {
                binding.dSupplyIngTietQuantity.setText(
                    String.format("%.1f", (model.quantity + 1.0)),
                    TextView.BufferType.EDITABLE
                )
            }
        }

        model.valid.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.dSupplyIngToolbar.menu.findItem(R.id.m_storage_ingredient_done)
                    ?.isEnabled = it
            }
        })
    }
}