package tech.xdrd.kitchen.plan

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import tech.xdrd.kitchen.R
import tech.xdrd.kitchen.databinding.FragmentPlanBinding
import tech.xdrd.kitchen.ui.StickyItemDecoration


class PlanFragment : Fragment() {
    private lateinit var binding: FragmentPlanBinding

    private lateinit var viewModel: PlanViewModel
    private lateinit var adapter: PlanAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

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

        binding.fPlanRecyclerview.layoutManager = LinearLayoutManager(context)
        binding.fPlanRecyclerview.addItemDecoration(StickyItemDecoration(adapter))

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
            Observer { it?.let { adapter.source = it } })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.plan_show_past, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.m_plan_show_past -> {
                adapter.showPast = item.isChecked
                item.isChecked = !item.isChecked
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }
}
