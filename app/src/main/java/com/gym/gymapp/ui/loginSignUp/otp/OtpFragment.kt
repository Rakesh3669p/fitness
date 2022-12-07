package com.gym.gymapp.ui.loginSignUp.otp

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import com.gym.gymapp.R
import com.gym.gymapp.databinding.FragmentOtpBinding
import com.gym.gymapp.ui.homeScreens.MainActivity
import com.gym.gymapp.ui.loginSignUp.viewModel.LoginSignUpViewModel
import com.gym.gymapp.utils.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OtpFragment : Fragment(R.layout.fragment_otp), View.OnClickListener {
    private lateinit var binding: FragmentOtpBinding

    @Inject
    lateinit var appProgress: AgileLoader

    private val loginViewModel: LoginSignUpViewModel by viewModels()
    private var otp = ""
    private lateinit var session: SessionManager
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentOtpBinding.bind(view)
        init()
        setOnClickListener()
        setSubscribers()
    }


    private fun init() {
        appProgress = AgileLoader(requireContext())
        session = SessionManager(requireContext())
        loginViewModel.login
        otp = arguments?.get("otp").toString()
//        binding.otpView.setText(otp)
    }

    private fun setOnClickListener() {
        binding.verifyBtn.setOnClickListener(this@OtpFragment)
    }

    private fun setSubscribers() {
        loginViewModel.otp.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    appProgress.dismiss()
                    if (response.data!!.success) {

                        session.isLogin = true
                        session.loginImage = response.data.data.image
                        session.loginName = response.data.data.name
                        session.loginEmail = response.data.data.email
                        session.loginId = response.data.data.id.toString()
                        session.token = response.data.token

                        startActivity(Intent(requireActivity(), MainActivity::class.java))
                        requireActivity().finish()
                    }
                }
                is Resource.Loading -> {
                    appProgress.show()
                }
                is Resource.Error -> {
                    appProgress.dismiss()
                    requireContext().showToast(response.message.toString())
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.verifyBtn -> {
                loginViewModel.validateOTP(binding.otpView.text.toString())
            }
        }
    }
}