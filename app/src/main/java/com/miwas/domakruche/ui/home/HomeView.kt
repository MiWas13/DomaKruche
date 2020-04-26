package com.miwas.domakruche.ui.home

import com.miwas.domakruche.base.MvpView

interface HomeView : MvpView {
	fun initView()
	fun startTimer(timeInMillis: Long)
	fun updatePointsCounter(count: Int)
	fun blockButton()
	fun activateButton()
	fun hideLocationLoader();
	fun getCurrentLocation()
	fun updateLocationText(locationAddress: String)
	fun showNotInHomeAlert()
	fun showInterstitialAd()
	fun updateUsername(username: String)
	fun setNotification()
	fun checkInternetConnection()
	fun showJoke(joke: CharSequence)
}