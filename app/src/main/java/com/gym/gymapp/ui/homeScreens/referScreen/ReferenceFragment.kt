package com.gym.gymapp.ui.homeScreens.referScreen

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.navigation.fragment.findNavController
import com.gym.gymapp.R
import com.gym.gymapp.databinding.FragmentReferenceBinding
import com.gym.gymapp.databinding.FragmentSearchBinding
import com.gym.gymapp.utils.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ReferenceFragment : Fragment(R.layout.fragment_reference), View.OnClickListener {

    private lateinit var binding: FragmentReferenceBinding

    @Inject
    lateinit var session: SessionManager


    lateinit var appProgress: AgileLoader

    private var currentView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (currentView == null) {
            currentView = inflater.inflate(R.layout.fragment_reference, container, false)
            binding = FragmentReferenceBinding.bind(currentView!!)
            init()
            setOnClickListener()
        }
        return currentView!!
    }

    private fun init() {
        appProgress = AgileLoader(requireContext())
    }




    private fun setOnClickListener() {
        with(binding) {
            referBtn.setOnClickListener(this@ReferenceFragment)
            backArrow.setOnClickListener(this@ReferenceFragment)
        }



    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.referBtn -> {
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    "Hey check this awesome App at: https://play.google.com/store/apps/details?id="
                )
                sendIntent.type = "text/plain"
                startActivity(sendIntent)

            }
            R.id.backArrow -> {
                findNavController().popBackStack()

            }
        }
    }
}