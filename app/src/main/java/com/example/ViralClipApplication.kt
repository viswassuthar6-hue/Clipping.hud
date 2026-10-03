package com.example

import android.app.Application
import com.example.data.credit.AppDatabase

class ViralClipApplication : Application() {

    companion object {
        lateinit var instance: ViralClipApplication
            private set

        val database: AppDatabase by lazy {
            AppDatabase.getInstance(instance)
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
