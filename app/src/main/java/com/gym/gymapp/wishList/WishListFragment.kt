package com.gym.gymapp.wishList

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gym.gymapp.R
import com.gym.gymapp.wishList.adapter.WishListAdapter
import com.gym.gymapp.databinding.FragmentWishListBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WishListFragment : Fragment(R.layout.fragment_wish_list), View.OnClickListener {
    private lateinit var binding: FragmentWishListBinding
    private var currentView: View? = null

    @Inject
    lateinit var wishLisAdapter: WishListAdapter

    private val wishViewModel: WishListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (currentView == null) {
            currentView = inflater.inflate(R.layout.fragment_wish_list, container, false)
            binding = FragmentWishListBinding.bind(currentView!!)
            init()
            setObservers()
            setOnClickListener()
        }
        return currentView
    }

    private fun init() {
        binding.swipeToRefresh.setOnRefreshListener {
         setObservers()
        }
    }

    private fun setObservers() {
        wishViewModel.wishListData.observe(viewLifecycleOwner) {
            binding.noData.isVisible = it.isEmpty()
            binding.swipeToRefresh.isRefreshing = false
            wishLisAdapter.differ.submitList(it)

            binding.wishListRv.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())
                adapter = wishLisAdapter
            }
        }
    }

    private fun setOnClickListener() {
        binding.backArrow.setOnClickListener(this@WishListFragment)

        wishLisAdapter.setOnProductListener {
            val bundle = Bundle().apply {
                putString("id", it.toString())
            }
            findNavController().navigate(R.id.productDetailsFragment, bundle)
        }

        wishLisAdapter.setOnRemoveProductListener {
            showAlertOnRemove(it)
        }
    }

    private fun showAlertOnRemove(id: Int) {
        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setMessage("Are you sure want remove package?")
        alertDialog.setCancelable(true)

        alertDialog.setPositiveButton(
            "Yes"
        ) { dialog, _ ->
            wishViewModel.removeWishListProduct(id)

            dialog.cancel()
        }

        alertDialog.setNegativeButton(
            "No"
        ) { dialog, _ -> dialog.cancel() }

        val alert11: AlertDialog = alertDialog.create()
        alert11.show()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backArrow -> {
                findNavController().popBackStack()
            }


        }
    }
}