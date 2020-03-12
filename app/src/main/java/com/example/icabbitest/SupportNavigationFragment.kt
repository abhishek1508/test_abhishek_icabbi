package com.example.icabbitest

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.services.android.navigation.ui.v5.NavigationViewOptions
import com.mapbox.services.android.navigation.ui.v5.OnNavigationReadyCallback
import com.mapbox.services.android.navigation.ui.v5.camera.DynamicCamera
import com.mapbox.services.android.navigation.ui.v5.listeners.NavigationListener
import com.mapbox.services.android.navigation.ui.v5.listeners.SpeechAnnouncementListener
import com.mapbox.services.android.navigation.ui.v5.voice.SpeechAnnouncement
import com.mapbox.services.android.navigation.v5.navigation.camera.RouteInformation
import com.mapbox.services.android.navigation.v5.routeprogress.ProgressChangeListener
import com.mapbox.services.android.navigation.v5.routeprogress.RouteProgress
import kotlinx.android.synthetic.main.fragment_support_navigation.*

private const val DEFAULT_LATITUDE = 37.791920
private const val DEFAULT_LONGITUDE = -122.396790
private const val NAV_ZOOM = 14.5
private const val NAV_TILT = 30.0

class SupportNavigationFragment : Fragment(), OnNavigationReadyCallback, NavigationListener,
    ProgressChangeListener, SpeechAnnouncementListener {

    companion object {
        val TAG: String = SupportNavigationFragment::class.java.simpleName
    }

    private var shouldUseSpeech: Boolean = false
    private var currentRoute: DirectionsRoute? = null
    private var isNavigationReady = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_support_navigation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navigationView.onCreate(savedInstanceState)
        val initialLocation = LatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE)
        val position = CameraPosition.Builder()
            .target(initialLocation)
            .zoom(NAV_ZOOM)
            .tilt(NAV_TILT)
            .build()

        navigationView.initialize(this, position)
    }

    override fun onStart() {
        super.onStart()
        navigationView.onStart()
    }

    override fun onResume() {
        super.onResume()
        navigationView.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        navigationView.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let { navigationView.onRestoreInstanceState(it) }
    }

    override fun onPause() {
        super.onPause()
        navigationView.onPause()
    }

    override fun onStop() {
        super.onStop()
        navigationView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        navigationView.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        navigationView.onDestroy()
    }

    override fun onNavigationReady(isRunning: Boolean) {
        isNavigationReady = true
        currentRoute?.also {
            startNavigation(it)
        }
    }

    override fun willVoice(announcement: SpeechAnnouncement?): SpeechAnnouncement? {
        return if (shouldUseSpeech) {
            announcement
        } else {
            null
        }
    }

    override fun onCancelNavigation() {
        navigationView.stopNavigation()
    }

    override fun onNavigationFinished() {
        // NOP
    }

    override fun onNavigationRunning() {
        // NOP
    }

    override fun onProgressChange(location: Location?, routeProgress: RouteProgress?) {
        // NOP
    }

    fun shouldUseSpeech(doUseSpeech: Boolean) {
        shouldUseSpeech = doUseSpeech
    }

    fun changeDirection(directionsRoute: DirectionsRoute) {
        currentRoute = directionsRoute
        if (isNavigationReady) {
            startNavigation(directionsRoute)
        } else {
            navigationView?.initialize(this)
        }
    }

    private fun startNavigation(directionsRoute: DirectionsRoute) {
        val options = NavigationViewOptions.builder()
            .directionsRoute(directionsRoute)
            .navigationListener(this)
            .progressChangeListener(this)
            .speechAnnouncementListener(this)
            .waynameChipEnabled(false)
            .build()

        // These views are only available once navigation has begun
        navigationView?.apply {
            startNavigation(options)
            retrieveAlertView().updateEnabled(false)
            retrieveSoundButton().hide()
            retrieveFeedbackButton().hide()
            hideRecenterBtn()
            updateWayNameVisibility(false)
            retrieveNavigationMapboxMap()?.apply {
                updateWaynameQueryMap(false)

                retrieveMap()?.also { map ->
                    map.uiSettings.apply {
                        isRotateGesturesEnabled = false
                        isTiltGesturesEnabled = false
                        isQuickZoomGesturesEnabled = false
                    }

                    retrieveMapboxNavigation()?.cameraEngine = object : DynamicCamera(map) {
                        override fun tilt(routeInformation: RouteInformation?): Double {
                            return NAV_TILT
                        }

                        override fun zoom(routeInformation: RouteInformation?): Double {
                            return NAV_ZOOM
                        }
                    }
                }
            }
        }
    }
}