package com.chooongg.widget.stateLayout.simple

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.chooongg.widget.stateLayout.simple.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

    }
}