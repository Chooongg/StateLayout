package com.chooongg.widget.stateLayout.simple

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.chooongg.widget.stateLayout.OnStatedChangeListener
import com.chooongg.widget.stateLayout.show
import com.chooongg.widget.stateLayout.simple.databinding.ActivityMainBinding
import com.chooongg.widget.stateLayout.simple.state.LinearProgressState
import com.chooongg.widget.stateLayout.simple.state.LottieLoadingState
import com.chooongg.widget.stateLayout.simple.state.ProgressState
import com.chooongg.widget.stateLayout.state.AbstractState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

class MainActivity : AppCompatActivity(), OnStatedChangeListener {

    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.stateLayout.setOnStatedChangeListener(this)
        binding.switchAnimate.isChecked = binding.stateLayout.isEnableAnimate
        binding.switchAnimate.setOnCheckedChangeListener { _, isChecked ->
            binding.stateLayout.isEnableAnimate = isChecked
        }
        binding.btnProgressState.setOnClickListener {
            binding.stateLayout.show<ProgressState>()
        }
        binding.btnProgressStateProgress.setOnClickListener {
            binding.stateLayout.show<ProgressState>(0)
            lifecycleScope.launch {
                delay(1000)
                binding.stateLayout.show<ProgressState>(20)
                delay(1000)
                binding.stateLayout.show<ProgressState>(40)
                delay(1000)
                binding.stateLayout.show<ProgressState>(60)
                delay(1000)
                binding.stateLayout.show<ProgressState>(80)
                delay(1000)
                binding.stateLayout.show<ProgressState>(100)
            }
        }
        binding.btnLinearProgressState.setOnClickListener {
            binding.stateLayout.show<LinearProgressState>()
        }
        binding.btnLinearProgressStateProgress.setOnClickListener {
            binding.stateLayout.show<LinearProgressState>(0)
            lifecycleScope.launch {
                delay(1000)
                binding.stateLayout.show<LinearProgressState>(20)
                delay(1000)
                binding.stateLayout.show<LinearProgressState>(40)
                delay(1000)
                binding.stateLayout.show<LinearProgressState>(60)
                delay(1000)
                binding.stateLayout.show<LinearProgressState>(80)
                delay(1000)
                binding.stateLayout.show<LinearProgressState>(100)
            }
        }
        binding.btnLottieLoadingState.setOnClickListener {
            binding.stateLayout.show<LottieLoadingState>()
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
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStatedChange(state: KClass<out AbstractState>) {
        Toast.makeText(this, state.simpleName, Toast.LENGTH_SHORT).show()
    }
}