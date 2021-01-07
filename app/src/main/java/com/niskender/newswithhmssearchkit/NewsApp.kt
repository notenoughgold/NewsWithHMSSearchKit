package com.niskender.newswithhmssearchkit

import android.app.Application
import com.huawei.hms.searchkit.SearchKitInstance
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NewsApp : Application() {

    override fun onCreate() {
        super.onCreate()
        SearchKitInstance.init(this, "103633039")
    }
}