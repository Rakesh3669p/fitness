package com.gym.gymapp.ui.ActivePacks.adapter

import android.R.attr.button
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gym.gymapp.R
import com.gym.gymapp.databinding.ItemActivePacksBinding
import com.gym.gymapp.ui.ActivePacks.model.ActivePacksData
import com.gym.gymapp.utils.requestOption
import javax.inject.Inject


class ActivePacksAdapter @Inject constructor() : RecyclerView.Adapter<ActivePacksAdapter.ViewHolder>() {

    lateinit var context: Context
    inner class ViewHolder(val binding: ItemActivePacksBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ActivePacksData) {
            with(binding) {
                Glide.with(context).setDefaultRequestOptions(requestOption()).load(data.package_image).into(logo)
                packageName.text = data.package_name
                organisationName.text = data.organization_name
                organisationAddress.text = data.organization_addresse
                if(data.is_expire==1){
                    reOrder.isVisible = true
                    attendanceStatus.isVisible  = false
                }else if(data.is_expire==0){
                    reOrder.isVisible = false
                    attendanceStatus.isVisible  = true
                }

                if(!data.package_strat_end_time.isNullOrBlank()){
                    packageTime.text = "Time:  ${data.package_strat_end_time}"
                }else{
                    packageTime.text = "Time:  Not Available"
                }

                if(data.today_attendance_status){
                    var buttonDrawable: Drawable? = attendanceStatus.background
                    buttonDrawable = DrawableCompat.wrap(buttonDrawable!!)
                    DrawableCompat.setTint(buttonDrawable, getColor(context,R.color.green))
                    attendanceStatus.background = buttonDrawable
                    attendanceStatus.text = "Checked In"
                }else{
                    var buttonDrawable: Drawable? = attendanceStatus.background
                    buttonDrawable = DrawableCompat.wrap(buttonDrawable!!)
                    DrawableCompat.setTint(buttonDrawable, getColor(context,R.color.themeOrange))
                    attendanceStatus.background = buttonDrawable
                    attendanceStatus.text = "Check In"
                }


                root.setOnClickListener {
                    packageClickListener?.let {
                        it(data)
                    }
                }

                attendanceStatus.setOnClickListener {
                    attendanceClickListener?.let {
                        it(data)
                    }
                }

                reOrder.setOnClickListener {
                    reOrderClickListener?.let {
                        it(data)
                    }
                }
            }
        }
    }

    private val differCallBack = object : DiffUtil.ItemCallback<ActivePacksData>() {
        override fun areItemsTheSame(
            oldItem: ActivePacksData,
            newItem: ActivePacksData
        ): Boolean {
            return oldItem.package_id == newItem.package_id
        }

        override fun areContentsTheSame(
            oldItem: ActivePacksData,
            newItem: ActivePacksData
        ): Boolean {
            return oldItem == newItem
        }
    }


    val differ = AsyncListDiffer(this, differCallBack)

    private var attendanceClickListener: ((activePackData: ActivePacksData) -> Unit)? = null
    private var packageClickListener: ((activePackData: ActivePacksData) -> Unit)? = null
    private var reOrderClickListener: ((activePackData: ActivePacksData) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ActivePacksAdapter.ViewHolder {
        context = parent.context
        return ViewHolder(
            ItemActivePacksBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ActivePacksAdapter.ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int = differ.currentList.size

    fun setOnPackageClickListener(listener: (activePackData: ActivePacksData) -> Unit) {
        packageClickListener = listener
    }

    fun setOnAttendanceClickListener(listener: (activePackData: ActivePacksData) -> Unit) {
        attendanceClickListener = listener
    }
    fun setOnReOrderClickListener(listener: (activePackData: ActivePacksData) -> Unit) {
        reOrderClickListener = listener
    }
}