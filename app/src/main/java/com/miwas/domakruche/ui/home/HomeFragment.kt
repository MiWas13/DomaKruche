package com.miwas.domakruche.ui.home

import android.Manifest
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.miwas.domakruche.BuildConfig
import com.miwas.domakruche.R
import com.miwas.domakruche.database.DBHelper
import com.miwas.domakruche.notifications.ReminderBroadcast
import com.miwas.domakruche.utils.Constants
import com.miwas.domakruche.utils.Constants.DB_NAME
import com.miwas.domakruche.utils.Constants.DEFAULT_DB_VERSION
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.miwas.domakruche.ui.InfoDialog
import com.miwas.domakruche.utils.Utils
import java.util.*
import java.util.concurrent.TimeUnit

class HomeFragment : Fragment(), HomeView {

	private lateinit var presenter: HomePresenter
	private lateinit var timerTextView: TextView
	private lateinit var pointsCounterTextView: TextView
	private lateinit var addressTextView: TextView
	private lateinit var homeButton: Button
	private lateinit var locationProgressBar: ProgressBar
	private lateinit var locationListener: LocationListener
	private lateinit var locationManager: LocationManager
	private lateinit var interstitialAd: InterstitialAd
	private var counter: CountDownTimer? = null
	private lateinit var geocoder: Geocoder
	private var sharedPreferences: SharedPreferences? = null
	private var nicknameEditText: EditText? = null
	private lateinit var infoDialog: InfoDialog

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		val root = inflater.inflate(R.layout.fragment_home, container, false)
		timerTextView = root.findViewById(R.id.timer_text_view)
		pointsCounterTextView = root.findViewById(R.id.points_counter_text_view)
		addressTextView = root.findViewById(R.id.address_text_view)
		homeButton = root.findViewById(R.id.im_home_button)
		locationProgressBar = root.findViewById(R.id.progress_bar)
		nicknameEditText = activity?.findViewById(R.id.nickname_edit_text)
		activity?.let {
			infoDialog = InfoDialog(it)
		}
		sharedPreferences = context?.getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE)
		preparePresenter()
		presenter.attachView(this)
		presenter.viewIsReady()
		return root
	}

	override fun showInterstitialAd() {
		if (interstitialAd.isLoaded) {
			interstitialAd.show()
		} else {
			presenter.adClosed()
		}
	}

	override fun updateUsername(username: String) {
		nicknameEditText?.let {
			it.text = SpannableStringBuilder(username)
		}
	}

	private fun preparePresenter() {
		presenter = HomePresenter(
			HomeModel(
				DBHelper(
					context,
					DB_NAME,
					null,
					DEFAULT_DB_VERSION,
					null
				),
				sharedPreferences
			)
		)
	}

	override fun initView() {
		homeButton.setOnClickListener {
			presenter.homeButtonClicked(Integer.parseInt(pointsCounterTextView.text.toString()))
		}
		initAd()
	}

	private fun initAd() {
		val request = AdRequest.Builder().build()
		interstitialAd = InterstitialAd(context)
		interstitialAd.adUnitId = if (BuildConfig.DEBUG) {
			"ca-app-pub-3940256099942544/1033173712"
		} else {
			"ca-app-pub-8186063456501670/1951227680"
		}
		interstitialAd.loadAd(request)
		interstitialAd.adListener = object : AdListener() {
			override fun onAdClosed() {
				interstitialAd.loadAd(AdRequest.Builder().build())
				presenter.adClosed()
			}
		}
	}

	override fun startTimer(timeInMillis: Long) {
		counter = null
		counter = object : CountDownTimer(timeInMillis, 1000) {

			override fun onTick(millisUntilFinished: Long) {
				timerTextView.text = String.format(
					"%d:%d",
					TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
					TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
						TimeUnit.MILLISECONDS.toMinutes(
							millisUntilFinished
						)
					)
				)
			}

			override fun onFinish() {
				timerTextView.text = resources.getString(R.string.timer_start_time)
				presenter.onTimerFinished()
			}
		}.start()
	}

	override fun updatePointsCounter(count: Int) {
		pointsCounterTextView.text = count.toString()
	}

	override fun blockButton() {
		homeButton.isClickable = false
		homeButton.background = context?.getDrawable(R.drawable.rounded_button_inactive)
	}

	override fun activateButton() {
		homeButton.isClickable = true
		homeButton.background = context?.getDrawable(R.drawable.rounded_button_active)
	}

	override fun hideLocationLoader() {
		locationProgressBar.visibility = GONE
	}

	override fun getCurrentLocation() {
		geocoder = Geocoder(context, Locale.getDefault())
		locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
		locationListener = LocationListener(locationManager, presenter, geocoder)
		if (isLocationServiceEnabled()) {
			tryToGetLocation()
		} else {
			showEnableLocationWarning()
		}
	}

	override fun updateLocationText(locationAddress: String) {
		addressTextView.text = locationAddress
	}

	override fun showNotInHomeAlert() {
		AlertDialog.Builder(context)
			.setTitle("Вы не дома!")
			.setMessage("Пожалуйста, вернитесь домой, а затем нажмите кнопку")
			.setPositiveButton("Хорошо, иду") { _, _ -> }
			.create()
			.show()
	}

	private fun isLocationServiceEnabled(): Boolean {
		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
	}

	override fun setNotification() {
		val intent = Intent(context, ReminderBroadcast::class.java)
		val pendingNotificationIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
		val notificationAlarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
		val currentTime = System.currentTimeMillis()
		val twoOurs = 7200000
		notificationAlarmManager.set(
			AlarmManager.RTC_WAKEUP,
			currentTime + twoOurs,
			pendingNotificationIntent
		)
	}

	override fun checkInternetConnection() {
		if (!Utils.isNetworkAvailable(context)) {
			showNoConnectionAlert()
		}
	}

	override fun showJoke(joke: CharSequence) {
		infoDialog.showPopupWindow(pointsCounterTextView, joke, "Прикольно")
	}

	private fun showNoConnectionAlert() {
		AlertDialog.Builder(context)
			.setTitle("Кажется, нет соединения")
			.setMessage("Пожалуйста, включите интернет")
			.setPositiveButton("Давайте включим") { _, _ ->
				startActivity(Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
			}
			.create()
			.show()
	}

	private fun tryToGetLocation() {
		if (checkLocationPermission()) {
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, locationListener)
		}
	}

	private fun checkLocationPermission(): Boolean {
		return if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION)
			!= PackageManager.PERMISSION_GRANTED) {
			requestPermissions(
				arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
				PERMISSIONS_REQUEST_LOCATION
			)
			false
		} else {
			true
		}
	}

	private fun showEnableLocationWarning() {
		AlertDialog.Builder(context)
			.setTitle("Не можем найти вас")
			.setMessage("Включите, пожалуйста, службы геолокации")
			.setPositiveButton("Пойдём, включим") { _, _ ->
				val settingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
				startActivity(settingsIntent)
			}
			.create()
			.show()
	}

	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
		when (requestCode) {
			PERMISSIONS_REQUEST_LOCATION -> {
				context?.let { context ->
					if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
						if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
							== PackageManager.PERMISSION_GRANTED) {
							tryToGetLocation()
						}
					} else {
						showEnableLocationWarning()
					}
				}
			}
		}
	}


	class LocationListener(
		private val locationManager: LocationManager,
		private val presenter: HomePresenter,
		private val geocoder: Geocoder
	) : android.location.LocationListener {
		override fun onLocationChanged(location: Location?) {
			location?.let {
				try {
					presenter.updateLocation(
						location.latitude,
						location.longitude,
						geocoder.getFromLocation(location.latitude, location.longitude, 1)
					)
				} catch (e: Exception) {
					//ignore
				}
			}
			locationManager.removeUpdates(this)
		}

		override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

		}

		override fun onProviderEnabled(provider: String?) {

		}

		override fun onProviderDisabled(provider: String?) {

		}

	}

	companion object {
		const val PERMISSIONS_REQUEST_LOCATION = 99
	}

}
