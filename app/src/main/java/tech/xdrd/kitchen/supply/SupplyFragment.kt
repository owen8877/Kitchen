package tech.xdrd.kitchen.supply

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import tech.xdrd.kitchen.R
import tech.xdrd.kitchen.databinding.FragmentSupplyBinding

class SupplyFragment : Fragment() {
    private lateinit var binding: FragmentSupplyBinding

    private lateinit var supplyViewModel: SupplyViewModel
    private lateinit var adapter: SupplyItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_supply, container, false)

        adapter = SupplyItemAdapter { _, item ->
            run {
                val ingredientModel =
                    SupplyViewModel.SupplyIngredientModel(SupplyIngredientDialog.Mode.Modify)
                ingredientModel.adapt(item)
                val dialog = SupplyIngredientDialog(ingredientModel)
                dialog.show(fragmentManager!!, dialog.tag)
            }
        }
        binding.fSupplyRecyclerview.adapter = adapter

        binding.fSupplyFab.setOnClickListener {
            run {
                val ingredientModel =
                    SupplyViewModel.SupplyIngredientModel(SupplyIngredientDialog.Mode.Add)
                val dialog = SupplyIngredientDialog(ingredientModel)
                dialog.show(fragmentManager!!, dialog.tag)
            }
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        supplyViewModel = ViewModelProviders.of(this).get(SupplyViewModel::class.java)

        supplyViewModel.supplyList.observe(viewLifecycleOwner, Observer {
            it?.let { adapter.source = it }
        })
    }
}
