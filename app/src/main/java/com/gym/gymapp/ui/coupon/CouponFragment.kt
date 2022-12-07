package com.gym.gymapp.ui.coupon

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.internal.service.Common
import com.gym.gymapp.R
import com.gym.gymapp.databinding.FragmentActivePacksBinding
import com.gym.gymapp.databinding.FragmentCouponBinding
import com.gym.gymapp.ui.ActivePacks.ActivePacksViewModel
import com.gym.gymapp.ui.ActivePacks.adapter.ActivePacksAdapter
import com.gym.gymapp.ui.ActivePacks.model.ActivePacksData
import com.gym.gymapp.ui.commonViewModel.CommonViewModel
import com.gym.gymapp.ui.coupon.model.CouponData
import com.gym.gymapp.utils.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CouponFragment : Fragment(), View.OnClickListener {

    lateinit var binding: FragmentCouponBinding

    private var currentView: View? = null

    private val commonViewModel:CommonViewModel by viewModels()

    @Inject
    lateinit var session: SessionManager

    lateinit var appProgress: AgileLoader

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (currentView == null) {
            currentView = inflater.inflate(R.layout.fragment_coupon, container, false)
            binding = FragmentCouponBinding.bind(currentView!!)
            init()
            setOnClickListener()
        }
        return currentView!!
    }

    private fun init() {
        appProgress = AgileLoader(requireContext())
        getCouponCode()
    }

    private fun getCouponCode() {
        commonViewModel.getCoupon()

        commonViewModel.getCouponCode.observe(viewLifecycleOwner){response->
            when(response){
                is Resource.Success->{
                    binding.loading.isVisible = false
                    appProgress.dismiss()
                    if (response.data?.success!!){
                        setCouponData(response.data.data[0])
                    }
                }
                is Resource.Loading->{
                    binding.loading.isVisible = true
                    appProgress.show()
                }
                is Resource.Error->{
                    binding.loading.isVisible = false
                    appProgress.dismiss()
                }
            }
        }
    }

    private fun setCouponData(couponData: CouponData) {
        with(binding){
            thankYouLblb.text = couponData.title
            getPercentLlb.text = couponData.sub_title
            redeemInstructions.text =  HtmlCompat.fromHtml(couponData.description, HtmlCompat.FROM_HTML_MODE_LEGACY)
            couponCode.text =couponData.coupon_code
        }

    }

    private fun setOnClickListener() {
        binding.backArrow.setOnClickListener(this@CouponFragment)
        binding.tapToCopy.setOnClickListener(this@CouponFragment)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backArrow -> {
                findNavController().popBackStack()
            }
            R.id.tapToCopy -> {
                val clipboardManager = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText("text", binding.couponCode.text)
                clipboardManager.setPrimaryClip(clipData)
            }
        }
    }
}