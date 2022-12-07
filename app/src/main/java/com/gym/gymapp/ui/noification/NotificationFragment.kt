package com.gym.gymapp.ui.noification

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
import com.gym.gymapp.databinding.FragmentNotificationBinding
import com.gym.gymapp.databinding.FragmentPackageListBinding
import com.gym.gymapp.ui.commonViewModel.CommonViewModel
import com.gym.gymapp.ui.noification.adapter.NotificationAdapter
import com.gym.gymapp.ui.noification.model.NotificationData
import com.gym.gymapp.utils.AgileLoader
import com.gym.gymapp.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NotificationFragment : Fragment(R.layout.fragment_notification), View.OnClickListener {

    lateinit var binding: FragmentNotificationBinding

    private var currentView: View? = null

    @Inject
    lateinit var notificationAdapter: NotificationAdapter

    private val commonViewModel: CommonViewModel by viewModels()

    private lateinit var appLoader: AgileLoader
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (currentView == null) {
            currentView = inflater.inflate(R.layout.fragment_notification, container, false)
            binding = FragmentNotificationBinding.bind(currentView!!)
            init()
            setObserver()
            setOnClickListener()
        }
        return currentView!!
    }

    private fun init() {
        appLoader = AgileLoader(requireContext())
        commonViewModel.getNotifications()
    }

    private fun setObserver() {
        commonViewModel.notifications.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    appLoader.dismiss()

                    if (response.data?.success!!) {
                        if (response.data.data.isEmpty()) {
                            binding.noData.isVisible = true
                        } else {
                            binding.noData.isVisible = false
                            setNotificationRv(response.data.data)
                        }
                    } else {
                        binding.noData.isVisible = true
                    }
                }
                is Resource.Loading -> {
                    appLoader.show()
                }
                is Resource.Error -> {
                    appLoader.dismiss()
                }
            }

        }
    }

    private fun setNotificationRv(data: List<NotificationData>) {
        notificationAdapter.differ.submitList(data)
        binding.notificationRv.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = notificationAdapter
        }
    }


    private fun setOnClickListener() {
        binding.backArrow.setOnClickListener(this@NotificationFragment)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backArrow -> {
                findNavController().popBackStack()
            }
        }
    }

}

