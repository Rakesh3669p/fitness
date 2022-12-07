package com.gym.gymapp.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.gym.gymapp.R
import com.gym.gymapp.databinding.ActivitySplashBinding
import com.gym.gymapp.ui.homeScreens.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    lateinit var binding:ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)


        lifecycleScope.launch {
            delay(1000)
            startActivity(Intent(this@SplashActivity,MainActivity::class.java))
            finish()
        }
    }
}