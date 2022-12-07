package com.gym.gymapp.ui.starIcon

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.gym.gymapp.R
import com.gym.gymapp.databinding.FragmentStarIconBinding
import com.gym.gymapp.databinding.FragmentTrainerBinding
import com.gym.gymapp.ui.ActivePacks.adapter.ActivePacksAdapter
import com.gym.gymapp.ui.commonViewModel.CommonViewModel
import com.gym.gymapp.ui.starIcon.model.StarIconData
import com.gym.gymapp.utils.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StarIconFragment : Fragment(), View.OnClickListener {
    lateinit var binding: FragmentStarIconBinding

    private var currentView: View? = null

    private val commonViewModel: CommonViewModel by viewModels()

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
            currentView = inflater.inflate(R.layout.fragment_star_icon, container, false)
            binding = FragmentStarIconBinding.bind(currentView!!)
            init()
            setObserver()
            setOnClickListener()
        }
        return currentView!!
    }


    private fun init() {
        appProgress = AgileLoader(requireContext())
        binding.emptyView.isVisible = true
    }

    private fun setObserver() {
          commonViewModel.getStarIconDetails(arguments?.getString("id").toString())
          commonViewModel.starIcon.observe(viewLifecycleOwner) { response ->
              when (response) {
                  is Resource.Success -> {
                      appProgress.dismiss()
                      if (response.data!!.success) {
                      binding.emptyView.isVisible = false
                          setStarIconData(response.data.data)
                      } else {
                          requireContext().showToast("Something went Wrong please try later!")
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

    private fun setStarIconData(starIconData: List<StarIconData>) {
        with(binding){
            starIconName.text = starIconData[0].name
            description.text = HtmlCompat.fromHtml(starIconData[0].description, HtmlCompat.FROM_HTML_MODE_LEGACY)
            Glide.with(requireContext()).setDefaultRequestOptions(requestOption()).load(starIconData[0].profile).into(starIconProfile)
        }

    }

    private fun setOnClickListener() {
        binding.backArrow.setOnClickListener(this@StarIconFragment)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backArrow -> {
                findNavController().popBackStack()
            }
        }
    }
}