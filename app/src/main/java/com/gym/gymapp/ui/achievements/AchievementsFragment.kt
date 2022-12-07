package com.gym.gymapp.ui.achievements

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gym.gymapp.R
import com.gym.gymapp.databinding.FragmentAchievementsBinding
import com.gym.gymapp.ui.achievements.adapter.AchievementsAdapter
import com.gym.gymapp.ui.achievements.model.AchievementsData
import com.gym.gymapp.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


@RequiresApi(Build.VERSION_CODES.N)
@AndroidEntryPoint
class AchievementsFragment : Fragment(R.layout.fragment_achievements), View.OnClickListener {

    private lateinit var addAchievementsDialogue: Dialog
    private lateinit var endDate: TextView
    private lateinit var startDate: TextView
    private var startDateSelected: Boolean = false
    val myCalendar = Calendar.getInstance()

    lateinit var binding: FragmentAchievementsBinding

    private var currentView: View? = null

    private val achievementsViewModel: AchievementsViewModel by viewModels()

    @Inject
    lateinit var session: SessionManager


    lateinit var appProgress: AgileLoader

    @Inject
    lateinit var achievementsAdapter: AchievementsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (currentView == null) {
            currentView = inflater.inflate(R.layout.fragment_achievements, container, false)
            binding = FragmentAchievementsBinding.bind(currentView!!)
            init()
            setObserver()
            setOnClickListener()
        }
        return currentView!!

    }


    private fun init() {
        appProgress = AgileLoader(requireContext())
    }

    private fun setObserver() {

        achievementsViewModel.achievements.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    appProgress.dismiss()
                    if (response.data!!.success) {
                        binding.noData.isVisible = response.data.data.isEmpty()
                        setAchievements(response.data.data)
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

        achievementsViewModel.addAchievements.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    appProgress.dismiss()
                    requireActivity().showToast(response.data?.message.toString())

                    achievementsViewModel.getAchievements()
                    addAchievementsDialogue.cancel()

                }
                is Resource.Loading -> {
                    appProgress.show()
                }
                is Resource.Error -> {
                    appProgress.dismiss()
                    requireActivity().showToast(response.message.toString())
                    addAchievementsDialogue.cancel()

                }
            }
        }

        achievementsViewModel.deleteAchievements.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    appProgress.dismiss()
                    achievementsViewModel.getAchievements()

                }
                is Resource.Loading -> {
                    appProgress.show()
                }
                is Resource.Error -> {
                    appProgress.dismiss()
                    requireActivity().showToast(response.message.toString())

                }
            }
        }

        achievementsViewModel.updateAchievements.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    appProgress.dismiss()
                    addAchievementsDialogue.cancel()
                    achievementsViewModel.getAchievements()

                }
                is Resource.Loading -> {
                    appProgress.show()
                }
                is Resource.Error -> {
                    appProgress.dismiss()
                    requireActivity().showToast(response.message.toString())

                }
            }
        }

    }

    private fun setAchievements(achievementsData: List<AchievementsData>) {
        achievementsAdapter.differ.submitList(achievementsData)
        binding.achievementsRv.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = achievementsAdapter
        }
    }

    private fun setOnClickListener() {
        binding.backArrow.setOnClickListener(this@AchievementsFragment)
        binding.addAchievements.setOnClickListener(this@AchievementsFragment)
        achievementsAdapter.setOnAchievementsClickListener {
            achievementsViewModel.deleteAchievements(it.toString())
        }
        achievementsAdapter.setOnAchievementEditClickListener {
            showAddAchievementsDialogue(it, "update")
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backArrow -> {
                findNavController().popBackStack()
            }

            R.id.addAchievements -> {
                showAddAchievementsDialogue("", "")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun showAddAchievementsDialogue(achievementsData: Any, from: String) {
        addAchievementsDialogue = Dialog(requireActivity())
        addAchievementsDialogue.requestWindowFeature(Window.FEATURE_NO_TITLE)
        addAchievementsDialogue.setCancelable(false)


        addAchievementsDialogue.setContentView(R.layout.add_achievements)
        addAchievementsDialogue.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val submitAchievement = addAchievementsDialogue.findViewById<View>(R.id.submitAchievement)

        val title = addAchievementsDialogue.findViewById<EditText>(R.id.titleEdt)
        val desc = addAchievementsDialogue.findViewById<EditText>(R.id.descEdt)
        val radioGroup =
            addAchievementsDialogue.findViewById<RadioGroup>(R.id.achievementsRadioGroup)
        val startRadio = addAchievementsDialogue.findViewById<RadioButton>(R.id.startRadioButton)
        val completedRadio = addAchievementsDialogue.findViewById<RadioButton>(R.id.endRadioButton)

        val closeDialogue =
            addAchievementsDialogue.findViewById<ImageFilterView>(R.id.closeDialogue)
        startDate = addAchievementsDialogue.findViewById(R.id.startDateEdt)
        endDate = addAchievementsDialogue.findViewById(R.id.endDateEdt)
        radioGroup.isVisible = from == "update"
        var status = "1"
        var achievementId = ""
        if (from == "update") {
            val achievements = achievementsData as AchievementsData
            achievementId = achievements.id.toString()
            title.setText(achievements.title)
            desc.setText(achievements.description)
            startDate.text = achievements.start_date
            endDate.text = achievements.complete_date

            startRadio.setOnClickListener {
                completedRadio.isChecked = false
                status = "1"
            }

            completedRadio.setOnClickListener {
                startRadio.isChecked = false
                status = "2"
            }


        }

        closeDialogue.setOnClickListener {
            addAchievementsDialogue.cancel()
        }

        startDate.setOnClickListener {
            startDateSelected = true

            DatePickerDialog(
                requireContext(),
                date,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        endDate.setOnClickListener {
            startDateSelected = false
            DatePickerDialog(
                requireContext(),
                date,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        submitAchievement.setOnClickListener {
            val achievementTitle = title.text.toString()
            val achievementDesc = desc.text.toString()

            submitNewAchievement(achievementTitle, achievementDesc, from, status,achievementId)

        }

        addAchievementsDialogue.show()
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val displayWidth = displayMetrics.widthPixels
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(addAchievementsDialogue.window!!.attributes)
        val dialogWindowWidth = (displayWidth * 0.9f).toInt()

        layoutParams.width = dialogWindowWidth
        addAchievementsDialogue.window!!.attributes = layoutParams

    }

    private fun submitNewAchievement(
        achievementTitle: String,
        achievementDesc: String,
        from: String,
        status: String,
        achievementId: String
    ) {
        val params: MutableMap<String, String> = HashMap()

        params["title"] = achievementTitle
        params["description"] = achievementDesc
        params["status"] = status
        params["start_date"] = startDate.text.toString().trim()
        params["complete_date"] = endDate.text.toString().trim()
        if (from == "update") {
            params["achievement_id"] = achievementId
            achievementsViewModel.updateAchievements(params)
        } else {
            achievementsViewModel.addAchievements(params)
        }
    }


    var date = OnDateSetListener { view, year, month, day ->
        myCalendar?.set(Calendar.YEAR, year)
        myCalendar?.set(Calendar.MONTH, month)
        myCalendar?.set(Calendar.DAY_OF_MONTH, day)
        updateLabel()
    }

    private fun updateLabel() {
        val myFormat = "yyyy-MM-dd"
        val dateFormat = SimpleDateFormat(myFormat, Locale.getDefault())
        if (startDateSelected) {
            startDate.text = dateFormat.format(myCalendar.time)
        } else {
            endDate.text = dateFormat.format(myCalendar.time)
        }
    }
}