package com.gym.gymapp.ui.socialRefer

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gym.gymapp.R
import com.gym.gymapp.databinding.FragmentSocialReferBinding
import com.gym.gymapp.ui.commonViewModel.CommonViewModel
import com.gym.gymapp.ui.socialRefer.model.ReferCodeData
import com.gym.gymapp.utils.AgileLoader
import com.gym.gymapp.utils.Resource
import com.gym.gymapp.utils.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SocialReferFragment : Fragment(R.layout.fragment_social_refer), View.OnClickListener {

    lateinit var binding: FragmentSocialReferBinding

    private var currentView: View? = null

    private val commonViewModel: CommonViewModel by viewModels()

    @Inject
    lateinit var session: SessionManager

    lateinit var appProgress: AgileLoader


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (currentView == null) {
            currentView = inflater.inflate(R.layout.fragment_social_refer, container, false)
            binding = FragmentSocialReferBinding.bind(currentView!!)
            init()
            setOnClickListener()
        }
        return currentView!!
    }

    private fun init(){
        appProgress = AgileLoader(requireContext())
        if(session.isLogin!!) {
            getReferCode()
        }else{
            binding.loading.isVisible = true
            binding.noData.isVisible = true
        }
    }

    private fun getReferCode() {
        commonViewModel.getReferCode()
        commonViewModel.referCode.observe(viewLifecycleOwner){resposne->
            when(resposne){
                is Resource.Success->{
                    binding.loading.isVisible = false
                    appProgress.dismiss()
                    if(resposne.data!!.success){
                        setReferCode(resposne.data.data[0])
                    }
                }
                is Resource.Loading->{
                    binding.loading.isVisible = true
                    appProgress.show()
                }
                is Resource.Error->{
                    binding.loading.isVisible = true
                    appProgress.dismiss()
                }
            }

        }
    }

    private fun setReferCode(referCodeData: ReferCodeData) {
        with(binding){
            couponCode.text = referCodeData.refrel_code
        }
    }

    private fun setOnClickListener() {
        binding.backArrow.setOnClickListener(this@SocialReferFragment)
        binding.tapToCopy.setOnClickListener(this@SocialReferFragment)
        binding.insta.setOnClickListener(this@SocialReferFragment)
        binding.fb.setOnClickListener(this@SocialReferFragment)
        binding.wp.setOnClickListener(this@SocialReferFragment)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backArrow -> {
                findNavController().popBackStack()
            }
            R.id.tapToCopy -> {
                val clipboardManager =
                    requireActivity().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText("text", binding.couponCode.text)
                clipboardManager.setPrimaryClip(clipData)
            }
            R.id.insta -> {

                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                val text = " Login with my Coupon Code\n${binding.couponCode.text}"
                intent.setPackage("com.instagram.android")
                if(intent!=null){
                    intent.putExtra(Intent.EXTRA_TEXT, text)
                    startActivity(Intent.createChooser(intent, text))
                }else{
                    Toast.makeText(requireContext(), "No App Found", Toast.LENGTH_SHORT).show()
                }



            }
            R.id.fb -> {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                val text = " Login with my Coupon Code\n${binding.couponCode.text}"
                intent.setPackage("com.facebook.katana")
                if(intent!=null){
                    intent.putExtra(Intent.EXTRA_TEXT, text)
                    startActivity(Intent.createChooser(intent, text))
                }else{
                    Toast.makeText(requireContext(), "No App Found", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.wp -> {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                val text = " Login with my Coupon Code\n${binding.couponCode.text}"
                intent.setPackage("com.whatsapp")
                if(intent!=null){
                    intent.putExtra(Intent.EXTRA_TEXT, text)
                    startActivity(Intent.createChooser(intent, text))
                }else{
                    Toast.makeText(requireContext(), "No App Found", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}