package com.gym.gymapp.ui.ActivePacks

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.applandeo.materialcalendarview.EventDay
import com.gym.gymapp.R
import com.gym.gymapp.databinding.FragmentAttendanceStatsBinding
import com.gym.gymapp.ui.ActivePacks.model.AttendanceData
import com.gym.gymapp.utils.AgileLoader
import com.gym.gymapp.utils.Resource
import com.gym.gymapp.utils.SessionManager
import com.gym.gymapp.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)

@AndroidEntryPoint
class AttendanceStats : Fragment(R.layout.fragment_attendance_stats), View.OnClickListener{
    lateinit var binding: FragmentAttendanceStatsBinding

    private var currentView: View? = null

    private val activePacksViewModel: ActivePacksViewModel by viewModels()

    @Inject
    lateinit var session: SessionManager

    lateinit var appProgress: AgileLoader

    val events: MutableList<EventDay> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (currentView == null) {
            currentView = inflater.inflate(R.layout.fragment_attendance_stats, container, false)
            binding = FragmentAttendanceStatsBinding.bind(currentView!!)
            init()
            setObserver()
            setOnClickListener()

        }
        return currentView!!
    }



    private fun init() {
        appProgress = AgileLoader(requireContext())
        val orderId = arguments?.getString("orderId").toString()
        val packageId = arguments?.getString("packageId").toString()
        val params: MutableMap<String, String> = HashMap()
        params["order_id"] = orderId
        params["package_id"] = packageId
        activePacksViewModel.getAttendanceStat(params)

    }

    private fun setObserver() {

        activePacksViewModel.attendanceStats.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    if (response.data!!.success) {
                        setAttendanceStat(response.data.data[0])
                    } else {
                        requireContext().showToast("Something went Wrong please try later!")
                    }
                }
                is Resource.Loading -> {
                    binding.loader.isVisible = true
                    appProgress.show()
                }
                is Resource.Error -> {
                    binding.loader.isVisible = true
                    appProgress.dismiss()
                }
            }
        }
    }

    private fun setAttendanceStat(activePacksData: AttendanceData) {
        binding.presentDays.text = activePacksData.total_present.toString()
        binding.absentDays.text = activePacksData.total_absent.toString()

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd",Locale.getDefault())
        activePacksData.attendance_list.forEach {
            val calendar: Calendar = GregorianCalendar()
            var attendanceDate: Date = simpleDateFormat.parse(it.attendance_date.substring(0, 10))
            calendar.time = attendanceDate
            events.add(EventDay(calendar, R.drawable.attendance_mark_icon,
                Color.parseColor("#e65419")))

        }
        countWeekendDays(2022,5)
    }
    fun countWeekendDays(year: Int, month: Int){
        val calendar = Calendar.getInstance()
        calendar[year, month - 1] = 1
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_YEAR)
        for (day in 1..daysInMonth) {
            calendar[year, month - 1] = day
            val dayOfWeek = calendar[Calendar.DAY_OF_WEEK]
            if (dayOfWeek == Calendar.SUNDAY) {
                println(calendar.time)
                val calendarNew = Calendar.getInstance()
                calendarNew.time = calendar.time
                events.add(EventDay(calendarNew, R.drawable.holiday, Color.parseColor("#FFA200")))
            }
        }

        binding.loader.isVisible = false
        binding.calendarView.setEvents(events)
        appProgress.dismiss()
    }

    private fun setOnClickListener() {
        binding.backArrow.setOnClickListener(this@AttendanceStats)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backArrow -> {
                findNavController().popBackStack()
            }
        }
    }

}