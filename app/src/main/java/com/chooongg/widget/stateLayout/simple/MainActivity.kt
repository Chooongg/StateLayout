package com.chooongg.widget.stateLayout.simple

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.chooongg.widget.stateLayout.simple.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val inset = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(inset.left, 0, inset.right, inset.bottom)
            insets
        }
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        binding.btnNormal.setOnClickListener {
            startActivity(Intent(this, BasicActivity::class.java))
        }
        binding.btnParam.setOnClickListener {
            startActivity(Intent(this, ParamActivity::class.java))
        }
        binding.btnChildView.setOnClickListener {
            startActivity(Intent(this, ChildViewActivity::class.java))
        }
    }
}