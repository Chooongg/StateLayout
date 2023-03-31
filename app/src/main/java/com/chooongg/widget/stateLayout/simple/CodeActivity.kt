package com.chooongg.widget.stateLayout.simple

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.chooongg.widget.stateLayout.OnStateChangedListener
import com.chooongg.widget.stateLayout.StateLayout
import com.chooongg.widget.stateLayout.simple.databinding.ActivityCodeBinding
import com.chooongg.widget.stateLayout.simple.state.*
import com.chooongg.widget.stateLayout.state.AbstractState
import kotlin.reflect.KClass

class CodeActivity : AppCompatActivity(), OnStateChangedListener {

    private val binding by lazy { ActivityCodeBinding.inflate(layoutInflater) }
    private val activityStateLayout by lazy { StateLayout.bind(this) }
    private val stateLayout by lazy { StateLayout.bind(binding.nestedScrollView) }

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
            it.subtitle = stateLayout.currentState.simpleName
        }
        stateLayout.setOnStateChangedListener(this)
        stateLayout.bindAppBarLayoutLiftOnScroll(binding.appBarLayout, binding.nestedScrollView.id)
        binding.switchAnimate.isChecked = stateLayout.isEnableAnimate
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        activityStateLayout.show(LinearProgressState::class)
        binding.switchAnimate.setOnCheckedChangeListener { _, isChecked ->
            stateLayout.isEnableAnimate = isChecked
        }
        binding.btnProgressState.setOnClickListener {
            stateLayout.show(ProgressState::class)
        }
        binding.btnLinearProgressState.setOnClickListener {
            stateLayout.show(LinearProgressState::class)
        }
        binding.btnTextState.setOnClickListener {
            stateLayout.show(TextState::class)
        }
        binding.btnEmptyState.setOnClickListener {
            stateLayout.show(EmptyState::class)
        }
        binding.btnNetworkState.setOnClickListener {
            stateLayout.show(NetworkState::class)
        }
        binding.btnErrorState.setOnClickListener {
            stateLayout.show(ErrorState::class)
        }
        binding.btnLottieLoadingState.setOnClickListener {
            stateLayout.show(LottieLoadingState::class)
        }
        binding.btnLottieCelebrationState.setOnClickListener {
            stateLayout.show(LottieCelebrationState::class)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.content, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.contentState) {
            stateLayout.showContent()
            return true
        } else if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStateChanged(state: KClass<out AbstractState>, contentIsShow: Boolean) {
        supportActionBar?.subtitle = state.simpleName
    }
}