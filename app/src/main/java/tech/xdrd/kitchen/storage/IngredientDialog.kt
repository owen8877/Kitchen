package tech.xdrd.kitchen.storage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import tech.xdrd.kitchen.R
import tech.xdrd.kitchen.databinding.DialogStorageIngredientBinding
import tech.xdrd.kitchen.ui.FullScreenDialog

class IngredientDialog(val model: StorageViewModel.IngredientModel) : FullScreenDialog() {
    enum class Mode { Add, Modify }

    private lateinit var binding: DialogStorageIngredientBinding
    private var mode: Mode = model.mode

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.dialog_storage_ingredient, container, false)
        binding.item = model
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.dStorageIngToolbar.inflateMenu(
            when (mode) {
                Mode.Add -> R.menu.storage_add_ingredient
                Mode.Modify -> R.menu.storage_modify_ingredient
            }
        )
        binding.dStorageIngToolbar.setNavigationOnClickListener { dismiss() }
        binding.dStorageIngToolbar.setOnMenuItemClickListener { item ->
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
                            model.delete()
                            dialog.dismiss()
                            dismiss()
                        }
                        .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                        .show()
                }
            }
            true
        }

        binding.dStorageIngTietName.doAfterTextChanged { text ->
            if (text.isNullOrBlank()) {
                binding.dStorageIngTietName.error = "Please input a valid name!"
            }
            model.refresh()
        }
        binding.dStorageIngTietQuantity.doAfterTextChanged { text ->
            if (text.isNullOrBlank() || text.toString().toDouble() < 0.0) {
                binding.dStorageIngTietQuantity.error = "Please input a positive quantity!"
            }
            model.refresh()
        }

        binding.dStorageIngBtnM01.setOnClickListener {
            binding.dStorageIngTietQuantity.setText(
                String.format("%.1f", 0.0.coerceAtLeast(model.quantity - 0.1)),
                TextView.BufferType.EDITABLE
            )
        }
        binding.dStorageIngBtnM1.setOnClickListener {
            binding.dStorageIngTietQuantity.setText(
                String.format("%.1f", 0.0.coerceAtLeast(model.quantity - 1.0)),
                TextView.BufferType.EDITABLE
            )
        }
        binding.dStorageIngBtnP01.setOnClickListener {
            binding.dStorageIngTietQuantity.setText(
                String.format("%.1f", (model.quantity + 0.1)),
                TextView.BufferType.EDITABLE
            )
        }
        binding.dStorageIngBtnP1.setOnClickListener {
            binding.dStorageIngTietQuantity.setText(
                String.format("%.1f", (model.quantity + 1.0)),
                TextView.BufferType.EDITABLE
            )
        }

        if (mode == Mode.Modify) {
            binding.dStorageIngTxtHistory.text = model.getHistory()
        }

        model.valid.observe(viewLifecycleOwner, Observer {
            binding.dStorageIngToolbar.menu.findItem(R.id.m_storage_ingredient_done)?.isEnabled = it
        })
    }
}