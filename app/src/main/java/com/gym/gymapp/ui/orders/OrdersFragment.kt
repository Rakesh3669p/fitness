package com.gym.gymapp.ui.orders

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gym.gymapp.R
import com.gym.gymapp.databinding.FragmentOrdersBinding
import com.gym.gymapp.ui.orders.adapter.OrdersAdapter
import com.gym.gymapp.utils.AgileLoader
import com.gym.gymapp.utils.AppProgressBar
import com.gym.gymapp.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class OrdersFragment : Fragment(R.layout.fragment_orders), View.OnClickListener {
    private lateinit var binding: FragmentOrdersBinding

    private var currentView: View? = null

    private val ordersViewModel: OrdersViewModel by viewModels()

    @Inject
    lateinit var orderHisAdapter: OrdersAdapter

    lateinit var appProgress: AgileLoader

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (currentView == null) {
            currentView = inflater.inflate(R.layout.fragment_orders, container, false)
            binding = FragmentOrdersBinding.bind(currentView!!)
            init()
            setObserver()
            setOnClickListener()
        }
        return currentView!!
    }

    private fun init() {
        appProgress = AgileLoader(requireContext())

        binding.swipeToRefresh.setOnRefreshListener {
            lifecycleScope.launch {
                ordersViewModel.safeCallOrdersHistory()
            }
        }
    }

    private fun setObserver() {
        ordersViewModel.orderHistory.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    binding.swipeToRefresh.isRefreshing = false
                    appProgress.dismiss()
                    if (response.data?.data!!.isNotEmpty()) {
                        orderHisAdapter.differ.submitList(response.data.data)
                        setRecyclerView()
                    } else {
                        binding.noData.isVisible = true
                    }
                }
                is Resource.Loading -> {
                    if (!binding.swipeToRefresh.isRefreshing)
                        appProgress.show()

                }
                is Resource.Error -> {
                    binding.swipeToRefresh.isRefreshing = false
                    appProgress.dismiss()
                }
            }
        }
    }

    private fun setOnClickListener() {
        with(binding) {
            backArrow.setOnClickListener(this@OrdersFragment)
        }


        orderHisAdapter.setOnPackageClickListener {
            val bundle = Bundle().apply {
                putString("id", it.toString())
            }

            findNavController().navigate(R.id.productDetailsFragment, bundle)

        }
    }

    private fun setRecyclerView() {

        binding.ordersRv.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = orderHisAdapter
        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backArrow -> {
                findNavController().popBackStack()
            }
        }
    }

}