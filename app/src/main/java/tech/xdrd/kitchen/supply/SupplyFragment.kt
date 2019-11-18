package tech.xdrd.kitchen.supply

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_supply, container, false)

        adapter = SupplyItemAdapter { _, item ->
            val ingredientModel =
                SupplyViewModel.SupplyIngredientModel(SupplyIngredientDialog.Mode.Modify)
            ingredientModel.adapt(item)
            val dialog = SupplyIngredientDialog(ingredientModel)
            dialog.show(fragmentManager!!, dialog.tag)
        }
        binding.fSupplyRecyclerview.adapter = adapter

        binding.fSupplyFab.setOnClickListener {
            val ingredientModel =
                SupplyViewModel.SupplyIngredientModel(SupplyIngredientDialog.Mode.Add)
            val dialog = SupplyIngredientDialog(ingredientModel)
            dialog.show(fragmentManager!!, dialog.tag)
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        supplyViewModel = ViewModelProviders.of(this).get(SupplyViewModel::class.java)

        supplyViewModel.supplyList.observe(viewLifecycleOwner, Observer {
            adapter.source = it
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.supply_add_to_storage, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.m_supply_add_to_storage_action -> {
                noticeAndAddSupplyIngredientToStorage()
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun noticeAndAddSupplyIngredientToStorage() {
        AlertDialog.Builder(context!!)
            .setTitle("Archive confirmation")
            .setMessage("Have you added all things bought?")
            .setCancelable(true)
            .setPositiveButton("Yes") { dialog, _ ->
                supplyViewModel.addSupplyIngredientToStorage()
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}
