package tech.xdrd.kitchen.storage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import tech.xdrd.kitchen.R
import tech.xdrd.kitchen.databinding.FragmentStorageBinding


class StorageFragment : Fragment() {
    private lateinit var binding: FragmentStorageBinding

    private lateinit var storageViewModel: StorageViewModel
    private lateinit var adapter: StorageItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_storage, container, false)

        adapter = StorageItemAdapter { _, item ->
            run {
                val ingredientModel = StorageViewModel.IngredientModel(IngredientDialog.Mode.Modify)
                ingredientModel.adapt(item)
                val dialog = IngredientDialog(ingredientModel)
                dialog.show(fragmentManager!!, dialog.tag)
            }
        }
        binding.fStorageRecyclerview.adapter = adapter

        binding.fStorageFab.setOnClickListener {
            run {
                val ingredientModel = StorageViewModel.IngredientModel(IngredientDialog.Mode.Add)
                val dialog = IngredientDialog(ingredientModel)
                dialog.show(fragmentManager!!, dialog.tag)
            }
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        storageViewModel = ViewModelProviders.of(this).get(StorageViewModel::class.java)

        storageViewModel.storageList.observe(viewLifecycleOwner, Observer {
            it?.let { adapter.data = it }
        })
    }
}