package com.gym.gymapp.ui.loginSignUp.LoginScreen

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.gym.gymapp.R
import com.gym.gymapp.databinding.FragmentLoginBinding
import com.gym.gymapp.ui.homeScreens.MainActivity
import com.gym.gymapp.ui.loginSignUp.viewModel.LoginSignUpViewModel
import com.gym.gymapp.utils.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login), View.OnClickListener {

    private lateinit var googleSignInClient:GoogleSignInClient

    lateinit var binding: FragmentLoginBinding

    private val loginViewModel: LoginSignUpViewModel by viewModels()

    lateinit var appProgress: AgileLoader

    @Inject
    lateinit var session: SessionManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)
        init()
        setOnClickListeners()
        setGoogleLogin()
        setObservers()
    }

    private fun init() {
        appProgress = AgileLoader(requireContext())
    }

    private fun setGoogleLogin() {
        val gso =  GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
    }


    private fun setOnClickListeners() {
        with(binding) {
            loginBtn.setOnClickListener(this@LoginFragment)
            signUp.setOnClickListener(this@LoginFragment)
            verifyBtn.setOnClickListener(this@LoginFragment)
            googleLogin.setOnClickListener(this@LoginFragment)
        }
    }

    private fun setObservers() {
        loginViewModel.login.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    appProgress.dismiss()
                    if (response.data?.success!!) {
                        val bundle = Bundle()
                        bundle.putString("otp", response.data.data.toString())
                        findNavController().navigate(
                            R.id.action_loginFragment_to_otpFragment,
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
                    requireContext().showToast(response.message.toString())


                }
            }
        }

        loginViewModel.socialLogin.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    appProgress.dismiss()
                    if (response.data?.success!!) {

                        session.isLogin = true
                        session.loginImage = response.data.data.image
                        session.loginName = response.data.data.name
                        session.loginEmail = response.data.data.email
                        session.loginId = response.data.data.id.toString()
                        session.token = response.data.token

                        startActivity(Intent(requireActivity(), MainActivity::class.java))
                        requireActivity().finish()

                    }else{
                        requireContext().showToast("Something Went Wrong!!")
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
            R.id.loginBtn -> {
                loginUser()
            }

            R.id.verifyBtn -> {

                requireContext().showToast(binding.otpView.text.toString())
            }

            R.id.signUp -> {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }
            R.id.googleLogin -> {
                loginWithGoogle()
            }
        }
    }

    private fun loginWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        try {
            googleResultLauncher.launch(signInIntent)
        }catch (e:Exception){

        }
    }

    private fun loginUser() {
        val userPhoneEmail = binding.emailEdt.text.toString()

        loginViewModel.validateAndLogin(userPhoneEmail)
    }

    var googleResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            handleLoginResponse(data)
        }
    }

    private fun handleLoginResponse(data: Any?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data as Intent)
        try {
            var googleLoginData = task.getResult(ApiException::class.java)


            val loginType = "google"
            val name = googleLoginData.displayName?:""
            val email = googleLoginData.email
            val oauthId = googleLoginData.id
            val googleImage = googleLoginData.photoUrl?:""

            loginViewModel.validateSocialLogin(loginType,name,email,oauthId,googleImage.toString())

        } catch (e: Exception) {
            println("LoginWithException ${e.message.toString()}")

        }
    }

}