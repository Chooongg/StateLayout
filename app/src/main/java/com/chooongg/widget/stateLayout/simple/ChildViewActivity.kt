package com.chooongg.widget.stateLayout.simple

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.chooongg.widget.stateLayout.OnStateChangedListener
import com.chooongg.widget.stateLayout.simple.databinding.ActivityBasicBinding
import com.chooongg.widget.stateLayout.simple.databinding.ActivityChildViewBinding
import com.chooongg.widget.stateLayout.simple.state.*
import com.chooongg.widget.stateLayout.state.AbstractState
import kotlin.reflect.KClass

class ChildViewActivity : AppCompatActivity(), OnStateChangedListener {

    private val binding by lazy { ActivityChildViewBinding.inflate(layoutInflater) }

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
        supportActionBar?.let {
            it.setHomeButtonEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
            it.subtitle = binding.stateLayout.currentState.simpleName
        }
        binding.stateLayout.setOnStatedChangeListener(this)
        binding.switchAnimate.isChecked = binding.stateLayout.isEnableAnimate
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        binding.switchAnimate.setOnCheckedChangeListener { _, isChecked ->
            binding.stateLayout.isEnableAnimate = isChecked
        }
        binding.btnProgressState.setOnClickListener {
            binding.stateLayout.show(ProgressState::class)
        }
        binding.btnLinearProgressState.setOnClickListener {
            binding.stateLayout.show(LinearProgressState::class)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.content, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.contentState) {
            binding.stateLayout.showContent()
            return true
        } else if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStateChanged(state: KClass<out AbstractState>) {
        supportActionBar?.subtitle = state.simpleName
    }
}