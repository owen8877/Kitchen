package tech.xdrd.kitchen.plan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import tech.xdrd.kitchen.R
import tech.xdrd.kitchen.databinding.FragmentPlanBinding

class PlanFragment : Fragment() {
    private lateinit var binding: FragmentPlanBinding

    private lateinit var viewModel: PlanViewModel
    private lateinit var adapter: PlanAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_plan, container, false)

        adapter = PlanAdapter { _, item ->
            run {
                val planModel = PlanViewModel.PlanModel(PlanDialog.Mode.Modify)
                planModel.adapt(item)
                val dialog = PlanDialog(planModel)
                dialog.show(fragmentManager!!, dialog.tag)
            }
        }
        binding.fPlanRecyclerview.adapter = adapter

        binding.fPlanFab.setOnClickListener {
            run {
                val planModel = PlanViewModel.PlanModel(PlanDialog.Mode.Add)
                val dialog = PlanDialog(planModel)
                dialog.show(fragmentManager!!, dialog.tag)
            }
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(PlanViewModel::class.java)

        viewModel.planCollectionList.observe(
            viewLifecycleOwner,
            Observer { it?.let { adapter.data = it } })
    }
}
