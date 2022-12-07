package com.gym.gymapp.ui.account

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.gym.gymapp.R
import com.gym.gymapp.databinding.FragmentAccountBinding
import com.gym.gymapp.ui.commonViewModel.CommonViewModel
import com.gym.gymapp.ui.homeScreens.MainActivity
import com.gym.gymapp.utils.*
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject


@RequiresApi(Build.VERSION_CODES.O)
@AndroidEntryPoint
class AccountFragment : Fragment(R.layout.fragment_account), View.OnClickListener {

    private var profileImage: File? = null
    private lateinit var binding: FragmentAccountBinding
    private var currentView: View? = null

    @Inject
    lateinit var session: SessionManager

    lateinit var apProgress: AgileLoader

    private val commonViewModel: CommonViewModel by viewModels()

    private var startedEdit = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (currentView == null) {
            currentView = inflater.inflate(R.layout.fragment_account, container, false)
            binding = FragmentAccountBinding.bind(currentView!!)
            init()
            setObserver()
            setOnClickListener()
        }
        return currentView!!
    }


    private fun init() {
        apProgress = AgileLoader(requireContext())
        commonViewModel.getProfile()
        with(binding) {
            profileName.isEnabled = false
            profileEmail.isEnabled = false
            profilePhone.isEnabled = false
        }

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    hideKeyboard(requireActivity())
                    findNavController()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

    private fun setObserver() {
        commonViewModel.profile.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    binding.loader.isVisible = false

                    apProgress.dismiss()
                    if (response.data!!.success) {
                        Glide.with(requireContext()).load(response.data.data.image).into(binding.profileImage)
                        binding.profileName.setText(response.data.data.name)
                        binding.profileEmail.setText(response.data.data.email)
                        binding.profilePhone.setText(response.data.data.phone_number)
                        updatedProfile.postValue(response.data.data)
                    } else {
                        binding.errorMessage.isVisible = true
                        binding.loader.isVisible = true


                    }
                }
                is Resource.Loading -> {
                    binding.loader.isVisible = true
                    apProgress.show()
                }
                is Resource.Error -> {
                    apProgress.dismiss()
                    binding.errorMessage.isVisible = true
                    binding.loader.isVisible = true
                }
            }

        }
    }

    private fun setOnClickListener() {
        binding.backArrow.setOnClickListener(this@AccountFragment)
        binding.logout.setOnClickListener(this@AccountFragment)
        binding.profileImageEdit.setOnClickListener(this@AccountFragment)
        binding.editAccount.setOnClickListener(this@AccountFragment)
        binding.save.setOnClickListener(this@AccountFragment)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backArrow -> {
                findNavController().popBackStack()
                hideKeyboard(requireActivity())
            }

            R.id.logout -> {
                (activity as MainActivity).logOut()
            }

            R.id.profileImageEdit -> {
                startedEdit = true
                ImagePicker.with(this)
                    .crop()
                    .compress(512)
                    .maxResultSize(720, 720)
                    .start()
            }
            R.id.editAccount -> {
                startedEdit = true
                binding.profileName.isEnabled = true
                binding.profileEmail.isEnabled = true
                binding.profilePhone.isEnabled = true
                binding.profileName.requestFocus()

                binding.logout.isVisible = false
                binding.save.isVisible = true
                binding.profileImageEdit.isVisible = true
                openKeyBoard(requireContext())
            }
            R.id.save -> {
                saveUpdates()
            }
        }
    }

    private fun saveUpdates() {
        val profileName = binding.profileName.text.toString()
        val profileEmail = binding.profileEmail.text.toString()
        val profilePhone = binding.profilePhone.text.toString()


        val params: MutableMap<String, RequestBody> = HashMap()
        params["name"] = getRequestBodyFromString(profileName) as RequestBody
        params["email"] = getRequestBodyFromString(profileEmail) as RequestBody
        params["phone_number"] = getRequestBodyFromString(profilePhone) as RequestBody

        val imageFile = if (profileImage != null) {
            profileImage
        } else {
            File("")
        }
        imageFile?.let { commonViewModel.updateProfile(params, it) }

        commonViewModel.updateProfile.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    requireContext().showToast(response.data?.message.toString())
                    apProgress.dismiss()

                    if (response.data!!.success) {
                        binding.profileName.isEnabled = false
                        binding.profileEmail.isEnabled = false
                        binding.profilePhone.isEnabled = false
                        binding.profilePhone.clearFocus()
                        binding.profileName.clearFocus()
                        binding.profileEmail.clearFocus()
                        commonViewModel.getProfile()

                        binding.logout.isVisible = true
                        binding.save.isVisible = false
                        binding.profileImageEdit.isVisible = false

                    }
                }
                is Resource.Loading -> {
                    apProgress.show()
                }
                is Resource.Error -> {
                    apProgress.dismiss()
                    requireContext().showToast(response.message.toString())
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {

                val uri: Uri = data?.data!!
                binding.profileImage.setImageURI(uri)
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