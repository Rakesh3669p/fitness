package com.gym.gymapp.OrderPlaced

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.gym.gymapp.R
import com.gym.gymapp.databinding.ActivityOrderPlacedBinding
import com.gym.gymapp.ui.homeScreens.MainActivity
import com.gym.gymapp.utils.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OrderPlacedActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var binding: ActivityOrderPlacedBinding

    @Inject
    lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderPlacedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setOnClickListener()
    }

    private fun setOnClickListener() {
        binding.homeBtn.setOnClickListener(this@OrderPlacedActivity)
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.homeBtn -> {
                startActivity(Intent(this@OrderPlacedActivity, MainActivity::class.java))
                finish()
            }
        }
    }
}