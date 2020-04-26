package com.miwas.domakruche.ui.home

import android.location.Address
import android.location.Location
import android.os.Handler
import com.miwas.domakruche.base.BasePresenter
import com.miwas.domakruche.network.model.User
import java.text.SimpleDateFormat
import java.util.*

class HomePresenter(private val homeModel: HomeModel) : BasePresenter<HomeView>() {

	private var currentLocationLatitude: Double = 0.0
	private var currentLocationLongitude: Double = 0.0
	private var savedLocationLatitude: Double = 0.0
	private var savedLocationLongitude: Double = 0.0
	private var isNeedToStartTimerAfterLocationCheck = false
	private var isTimerActive = false
	private var pointsCount = 0

	override fun viewIsReady() {
		baseView?.initView()
		baseView?.checkInternetConnection()
		baseView?.getCurrentLocation()
		baseView?.blockButton()
		checkExistingLocation()
		tryToUpdateHomeScreen()
	}

	fun homeButtonClicked(pointsCount: Int) {
		this.pointsCount = pointsCount
		if (checkIfLocationCorrect()) {
			if (isHomeLocationSetted() && checkIsPermissibleDistanceFromHome() || !isHomeLocationSetted()) {
				baseView?.showInterstitialAd()
				baseView?.setNotification()
			} else {
				baseView?.showNotInHomeAlert()
			}
		} else {
			isNeedToStartTimerAfterLocationCheck = true
			baseView?.getCurrentLocation()
		}
	}

	fun adClosed() {
		tryToSaveHomeLocation()
		Handler().postDelayed({
			homeModel.chargePoints(homeModel.getToken()) { user ->
				user?.let {
					updateHomeScreen(user)
					baseView?.showJoke(user.joke)
				}
			}
		}, 500)
	}

	fun updateLocation(locationLatitude: Double?, locationLongitude: Double?, addresses: List<Address>?) {
		if (isNeedToStartTimerAfterLocationCheck) {
			isNeedToStartTimerAfterLocationCheck = false
			baseView?.showInterstitialAd()
		}

		addresses?.get(0)?.let { address ->
			val text = if (!address.locality.isNullOrEmpty()
				&& !address.thoroughfare.isNullOrEmpty()
				&& !address.featureName.isNullOrEmpty()) {
				address.locality + ", " + address.thoroughfare + " " + address.featureName
			} else {
				address.getAddressLine(0)
			}
			baseView?.hideLocationLoader()
			baseView?.updateLocationText(text)
			if (!isTimerActive) {
				baseView?.activateButton()
			}
		}

		if (locationLatitude != null) {
			currentLocationLatitude = locationLatitude
		}

		if (locationLongitude != null) {
			currentLocationLongitude = locationLongitude
		}

		tryToUpdateHomeScreen()
	}

	fun onTimerFinished() {
		if (checkIfLocationCorrect()) {
			baseView?.activateButton()
		}
	}

	private fun checkIfLocationCorrect(): Boolean {
		return currentLocationLatitude != 0.0 && currentLocationLongitude != 0.0
	}

	private fun startTimer(timeInMillis: Long) {
		isTimerActive = true
		baseView?.startTimer(timeInMillis)
		if (timeInMillis > 0) {
			baseView?.blockButton()
		}
	}

	private fun checkIsPermissibleDistanceFromHome(): Boolean {
		val dist = FloatArray(1)

		Location.distanceBetween(
			savedLocationLatitude,
			savedLocationLongitude,
			currentLocationLatitude,
			currentLocationLongitude,
			dist
		)
		return dist[0] < 100 && savedLocationLatitude != 0.0 && savedLocationLongitude != 0.0
	}

	private fun isHomeLocationSetted(): Boolean {
		return savedLocationLatitude != 0.0 && savedLocationLongitude != 0.0
	}

	private fun checkExistingLocation() {
		val savedLocation = homeModel.getSavedLocationFromDb()
		savedLocationLatitude = savedLocation.first
		savedLocationLongitude = savedLocation.second
	}

	private fun tryToUpdateHomeScreen() {
		homeModel.getUser { user ->
			user?.let {
				updateHomeScreen(user)
			}
		}
	}

	private fun tryToSaveHomeLocation() {
		if (!isHomeLocationSetted()) {
			homeModel.putLocationToDb(currentLocationLatitude, currentLocationLongitude)
		}
	}

	private fun updateHomeScreen(user: User) {
		baseView?.updateUsername(user.username)
		baseView?.updatePointsCounter(user.balance)
		startTimer(getTimeForTimer(parseTime(user.serverTime), parseTime(user.lastCreditTime)))
	}

	private fun parseTime(time: String?): Long? {
		if (time == null) {
			return null
		}
		val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
		return format.parse(time)?.time
	}

	private fun getTimeForTimer(serverTime: Long?, lastCreditTime: Long?): Long {
		return if (serverTime != null && lastCreditTime != null && serverTime > 0 && lastCreditTime > 0) {
			3600000 - (serverTime - lastCreditTime)
		} else 0
	}
}