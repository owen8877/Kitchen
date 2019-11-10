package tech.xdrd.kitchen.supply

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import tech.xdrd.kitchen.R

class SupplyFragment : Fragment() {

    companion object {
        fun newInstance() = SupplyFragment()
    }

    private lateinit var viewModel: SupplyViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_supply, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SupplyViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
