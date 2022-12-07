package com.gym.gymapp.ui.loginSignUp.RegisterScreen

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.gym.gymapp.R
import com.gym.gymapp.databinding.FragmentRegisterBinding
import com.gym.gymapp.ui.loginSignUp.viewModel.LoginSignUpViewModel
import com.gym.gymapp.utils.AgileLoader
import com.gym.gymapp.utils.Resource
import com.gym.gymapp.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import java.io.File


@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.fragment_register), View.OnClickListener {

    lateinit var binding: FragmentRegisterBinding
    private var profileImage: File? = null
    private val loginSignUpViewModel: LoginSignUpViewModel by viewModels()

    lateinit var appProgress: AgileLoader

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRegisterBinding.bind(view)

        init()
        setOnClickListener()
        setObservers()
    }


    private fun init() {
        appProgress = AgileLoader(requireContext())
        appProgress.dismiss()
    }


    private fun setOnClickListener() {
        with(binding) {
            userProfileImage.setOnClickListener(this@RegisterFragment)
            userProfilePlusIcon.setOnClickListener(this@RegisterFragment)
            registerBtn.setOnClickListener(this@RegisterFragment)
            loginBtn.setOnClickListener(this@RegisterFragment)
        }
    }

    private fun setObservers() {
        loginSignUpViewModel.register.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    appProgress.dismiss()
                    if (response.data?.success!!) {

                        val bundle = Bundle()
                        bundle.putString("otp", response.data.otp)
                        findNavController().navigate(
                            R.id.action_registerFragment_to_otpFragment,
                            bundle
                        )
                    } else {
                        requireContext().showToast(response.data.message)
                    }
                }
                is Resource.Loading -> {
                    appProgress.show()
                }
                is Resource.Error -> {
                    appProgress.dismiss()
                    requireContext().showToast(msg = response.message.toString())
                }
            }
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.userProfileImage -> {
                pickImage()
            }
            R.id.userProfilePlusIcon -> {
                pickImage()
            }
            R.id.registerBtn -> {
                registerUser()
            }
            R.id.loginBtn -> {
                findNavController().popBackStack()
            }
        }
    }

    private fun registerUser() {
        with(binding) {
            val userName = userNameEdt.text.toString()
            val userEmail = emailEdt.text.toString()
            val userPhone = phoneEdt.text.toString()
            val referCode = couponEdt.text.toString()

            if(userPhone.length<10){
                requireContext().showToast("Please enter a Valid Mobile No.")
            }else{
                loginSignUpViewModel.validateAndRegister(
                    userName,
                    userEmail,
                    userPhone,
                    referCode,
                    profileImage
                )
            }


        }
    }

    private fun pickImage() {
        ImagePicker.with(this)
            .crop()
            .compress(512)
            .maxResultSize(720, 720)
            .start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                val uri: Uri = data?.data!!
                binding.userProfileImage.setImageURI(uri)
                profileImage = File(uri.path)
            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT)
                    .show()
            }
            else -> {
                Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }
}