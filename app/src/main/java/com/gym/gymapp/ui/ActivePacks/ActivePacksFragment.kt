package com.gym.gymapp.ui.ActivePacks

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.gym.gymapp.R
import com.gym.gymapp.databinding.ActivityOrderPlacedBinding
import com.gym.gymapp.databinding.FragmentActivePacksBinding
import com.gym.gymapp.databinding.FragmentBlogDetailsBinding
import com.gym.gymapp.ui.ActivePacks.adapter.ActivePacksAdapter
import com.gym.gymapp.ui.ActivePacks.model.ActivePacksData
import com.gym.gymapp.ui.homeScreens.blogScreens.BlogsViewModel
import com.gym.gymapp.ui.homeScreens.blogScreens.model.BlogsDetailsData
import com.gym.gymapp.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

@RequiresApi(Build.VERSION_CODES.O)
@AndroidEntryPoint
class ActivePacksFragment : Fragment(R.layout.fragment_active_packs), View.OnClickListener {
    lateinit var binding: FragmentActivePacksBinding

    private var currentView: View? = null

    private val activePacksViewModel: ActivePacksViewModel by viewModels()

    @Inject
    lateinit var session: SessionManager

    lateinit var appProgress: AgileLoader

    @Inject
    lateinit var activePacksAdapter: ActivePacksAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (currentView == null) {
            currentView = inflater.inflate(R.layout.fragment_active_packs, container, false)
            binding = FragmentActivePacksBinding.bind(currentView!!)
            init()
            setObserver()
            setOnClickListener()
            setRecyclerView()
        }
        return currentView!!
    }

    private fun setRecyclerView() {
        binding.activePackRv.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = activePacksAdapter
        }
    }

    private fun init() {
        appProgress = AgileLoader(requireContext())
        binding.swipeToRefresh.setOnRefreshListener {
            activePacksViewModel.getActivePacks()
        }
    }

    private fun setObserver() {

        activePacksViewModel.activePacks.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    binding.swipeToRefresh.isRefreshing= false
                    appProgress.dismiss()
                    if (response.data!!.success) {
                        binding.noData.isVisible = response.data.data.isEmpty()
                        binding.activePackRv.isVisible = response.data.data.isNotEmpty()
                        setActivePacks(response.data.data)
                    } else {
                        requireContext().showToast("Something went Wrong please try later!")
                    }
                }
                is Resource.Loading -> {
                    if(!binding.swipeToRefresh.isRefreshing)
                    appProgress.show()
                }
                is Resource.Error -> {
                    binding.swipeToRefresh.isRefreshing = false
                    appProgress.dismiss()
                }
            }
        }

        activePacksViewModel.reOrder.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    binding.swipeToRefresh.isRefreshing= false
                    appProgress.dismiss()
                    if (response.data!!.success) {
                      findNavController().navigate(R.id.checkOutFragment)
                    } else {
                        requireContext().showToast("Something went Wrong please try later!")
                    }
                }
                is Resource.Loading -> {
                    if(!binding.swipeToRefresh.isRefreshing)
                    appProgress.show()
                }
                is Resource.Error -> {
                    binding.swipeToRefresh.isRefreshing= false
                    appProgress.dismiss()
                }
            }
        }
    }

    private fun setActivePacks(activePacksData: List<ActivePacksData>) {
        activePacksAdapter.differ.submitList(activePacksData)

        binding.activePackRv.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = activePacksAdapter
        }

    }

    private fun setOnClickListener() {
        binding.backArrow.setOnClickListener(this@ActivePacksFragment)


        activePacksAdapter.setOnPackageClickListener {
            val bundle = Bundle().apply {
                putString("orderId", it.order_id.toString())
                putString("packageId", it.package_id.toString())
            }
            findNavController().navigate(
                R.id.attendanceStats,
                bundle
            )
        }

        activePacksAdapter.setOnReOrderClickListener {
            val params:MutableMap<String,String> = HashMap()
            params["order_id"] =  it.order_id.toString()
            params["device_id"] =  session.deviceId.toString()
            activePacksViewModel.reOrderPack(params)
        }

        activePacksAdapter.setOnAttendanceClickListener {
            val param: MutableMap<String, String> = HashMap()
            param["order_id"] = it.order_id.toString()
            param["package_id"] = it.package_id.toString()
            param["latitude"] = session.userCurrentLat.toString()
            param["longitude"] = session.userCurrentLong.toString()
            activePacksViewModel.setMarkAttendance(param)

            activePacksViewModel.markAttendance.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is Resource.Success -> {
                        appProgress.dismiss()
                        if (response.data!!.success) {
                            requireActivity().showToast(response.data.data)

                            activePacksViewModel.getActivePacks()
                        } else {
                            requireActivity().showToast(response.data.data)
                        }

                    }
                    is Resource.Loading -> {
                        appProgress.show()
                    }
                    is Resource.Error -> {
                        appProgress.dismiss()
                    }
                }

            }

        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backArrow -> {
                findNavController().popBackStack()
            }
        }
    }

    override fun onResume() {
        setObserver()
        super.onResume()
    }
}
