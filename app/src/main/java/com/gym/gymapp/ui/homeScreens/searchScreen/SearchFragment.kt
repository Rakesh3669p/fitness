package com.gym.gymapp.ui.homeScreens.searchScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gym.gymapp.R
import com.gym.gymapp.databinding.FragmentSearchBinding
import com.gym.gymapp.ui.commonViewModel.CommonViewModel
import com.gym.gymapp.ui.homeScreens.searchScreen.adapter.SearchSuggestionsAdapter
import com.gym.gymapp.utils.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search), View.OnClickListener {
    private lateinit var binding: FragmentSearchBinding

    private val commonViewModel: CommonViewModel by viewModels()

    @Inject
    lateinit var searchSuggestionsAdapter: SearchSuggestionsAdapter

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
            currentView = inflater.inflate(R.layout.fragment_search, container, false)
            binding = FragmentSearchBinding.bind(currentView!!)
            init()
            setObserver()
            setOnClickListener()
        }
        return currentView!!
    }



    private fun init() {
        appProgress = AgileLoader(requireContext())
        binding.searchEdt.requestFocus()
//        commonViewModel.getSearchSuggestions("")

        openKeyBoard(requireContext())
    }

    private fun setObserver() {
        commonViewModel.searchSuggestions.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    if (response.data!!.success) {
                        searchSuggestionsAdapter.differ.submitList(response.data.data)
                        binding.searchSuggestionsRv.apply {
                            setHasFixedSize(true)
                            layoutManager = LinearLayoutManager(requireContext())
                            adapter = searchSuggestionsAdapter
                        }
                    }
                }
                is Resource.Loading -> {

                }
                is Resource.Error -> {
                    println("Error")
                }
            }

        }
    }

    private fun setOnClickListener() {
        with(binding) {
            backArrow.setOnClickListener(this@SearchFragment)
            searchIcon.setOnClickListener(this@SearchFragment)
            searchEdt.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchQuery()
                    return@OnEditorActionListener true
                }
                false
            })
            searchEdt.doOnTextChanged { text, start, before, count ->
                commonViewModel.getSearchSuggestions(text.toString())
            }
        }

        searchSuggestionsAdapter.setOnSearchClickListener {
            binding.searchEdt.setText(it)
            searchQuery()
        }


    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backArrow -> {
                findNavController().popBackStack()
                hideKeyboard(requireActivity())
            }
            R.id.searchIcon -> {
                searchQuery()
            }

        }
    }

    private fun searchQuery() {
        val searchQuery = binding.searchEdt.text.toString()
        val bundle = Bundle().apply {
            putString("search", searchQuery)
        }
        closeKeyBoard(requireContext())
        findNavController().popBackStack()
        findNavController().navigate(R.id.packageListFragment, bundle)
    }
}

