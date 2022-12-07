package com.gym.gymapp.ui.loginSignUp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.gym.gymapp.R
import com.gym.gymapp.databinding.ActivityLoginSignUpBinding
import com.gym.gymapp.databinding.ActivityMainBinding
import com.gym.gymapp.utils.DataStoreManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginSignUp : AppCompatActivity() {
    lateinit var binding: ActivityLoginSignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginSignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.loginNavHostFragment)

        if (intent.hasExtra("from")) {
            if (intent.getStringExtra("from") == "Login") {
                navController.navigate(R.id.loginFragment)
            } else {
                navController.navigate(R.id.registerFragment)
            }
        }
    }
}