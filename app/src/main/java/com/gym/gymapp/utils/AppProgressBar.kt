package com.gym.gymapp.utils

import android.annotation.SuppressLint

import android.os.Bundle
import android.view.*
import android.widget.PopupWindow
import androidx.fragment.app.DialogFragment
import com.airbnb.lottie.Lottie
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.gym.gymapp.R


class AppProgressBar : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.app_progress_view, container,false)
        Glide.with(this).load(R.drawable.loader).into(v.findViewById(R.id.animationView))
        return v
    }


}