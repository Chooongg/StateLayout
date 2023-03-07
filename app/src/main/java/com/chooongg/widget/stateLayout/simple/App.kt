package com.chooongg.widget.stateLayout.simple

import android.app.Application
import com.chooongg.widget.stateLayout.StateLayoutManager
import com.chooongg.widget.stateLayout.state.ContentState

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        StateLayoutManager.beginState = ContentState::class
    }
}