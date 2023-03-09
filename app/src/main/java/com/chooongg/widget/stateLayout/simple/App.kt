package com.chooongg.widget.stateLayout.simple

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.chooongg.widget.stateLayout.StateLayoutManager
import com.chooongg.widget.stateLayout.state.ContentState
import com.facebook.stetho.Stetho

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        StateLayoutManager.beginState = ContentState::class
    }
}