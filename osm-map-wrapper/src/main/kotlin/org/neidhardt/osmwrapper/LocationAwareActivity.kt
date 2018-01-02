package org.neidhardt.osmwrapper

import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.android.gms.location.*


/**
 * Created by neid_ei (eric.neidhardt@dlr.de)
 * on 21.12.2017.
 */
@Suppress("unused")
abstract class LocationAwareActivity : AppCompatActivity() {

	private val logTag = javaClass.simpleName

	private lateinit var googleApiClient: FusedLocationProviderClient
	private lateinit var locationUpdateCallback: LocationCallback

	private var locationRequest = LocationRequest().apply {
		this.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		this.googleApiClient = LocationServices.getFusedLocationProviderClient(this)

		this.locationUpdateCallback = object : LocationCallback() {
			override fun onLocationResult(locationResult: LocationResult?) {
				if (locationResult == null) {
					return
				}
				val mostRecentLocation = locationResult.lastLocation
				onLocationUpdateAvailable(mostRecentLocation)
			}
		}
	}

	/**
	 * requestLastKnowLocationAsync uses google's [FusedLocationProviderClient] to retrieve
	 * the last known location. The callback is invoked with the received location.
	 * Note that this location may be null.
	 * The callback's invocation is tied to the activity lifecycle, therefore checking onPause is
	 * not required.
	 *
	 * @param onSuccess callback to be invoked with received location
	 */
	protected fun requestLastKnowLocationAsync(onSuccess: (Location?) -> Unit) {
		try {
			this.googleApiClient.lastLocation.addOnCompleteListener(this, { task ->
				if (task.isSuccessful) {
					onSuccess(task.result)
				} else {
					Log.d(this.logTag, "requestLastKnowLocationAsync(): failed")
				}
			})
		} catch (e: SecurityException) {
			// should not happen
			Log.e(this.logTag, e.message, e)
		}
	}

	protected fun requestContinousLocationUpdates() {
		try {
			this.googleApiClient.requestLocationUpdates(
					this.locationRequest,
					this.locationUpdateCallback,
					null)
		} catch (e: SecurityException) {
			// should not happen
			Log.e(this.logTag, e.message, e)
		}
	}

	protected fun stopContinousLocationUpdates() {
		this.googleApiClient.removeLocationUpdates(this.locationUpdateCallback)
	}

	open fun onLocationUpdateAvailable(location: Location) {}
}