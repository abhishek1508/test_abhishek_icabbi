package com.example.icabbitest

import android.app.Application
import com.mapbox.mapboxsdk.Mapbox

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setUpMapbox()
    }

    private fun setUpMapbox() {
        val mapboxAccessToken = "pk.eyJ1IjoiYWJoaXNoaWtoYTE1MDgiLCJhIjoiY2szeXhwbzcxMDAxZTNqbndsOHlkbjU2bCJ9.F46rqDK6572Y2RtvAnjtzg"
        Mapbox.getInstance(applicationContext, mapboxAccessToken)
    }


}