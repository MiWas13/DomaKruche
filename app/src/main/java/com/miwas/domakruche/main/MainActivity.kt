package com.miwas.domakruche.main

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.SpannableStringBuilder
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.ads.MobileAds
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.miwas.domakruche.R
import com.miwas.domakruche.database.DBHelper
import com.miwas.domakruche.ui.InfoDialog
import com.miwas.domakruche.utils.Constants
import com.miwas.domakruche.utils.Constants.APP_PREFERENCES

class MainActivity : AppCompatActivity(), MainView {

	private lateinit var presenter: MainPresenter
	private lateinit var infoButton: ImageView
	private lateinit var editNameButton: ImageView
	private lateinit var nicknameEditText: EditText
	private lateinit var sharedPreferences: SharedPreferences
	private lateinit var infoDialog: InfoDialog

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		MobileAds.initialize(this) {}
		val navView: BottomNavigationView = findViewById(R.id.nav_view)
		val navController = findNavController(R.id.nav_host_fragment)
		infoDialog = InfoDialog(this)
		setSupportActionBar(findViewById(R.id.toolbar))
		supportActionBar?.let { bar ->
			bar.setDisplayShowTitleEnabled(false)
			bar.setDisplayShowHomeEnabled(false)
		}
		window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
		window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
		navView.setupWithNavController(navController)
		sharedPreferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
		preparePresenter()
		presenter.attachView(this)
		presenter.viewIsReady()
	}

	private fun preparePresenter() {
		presenter = MainPresenter(
			MainModel(
				DBHelper(
					this,
					Constants.DB_NAME,
					null,
					Constants.DEFAULT_DB_VERSION,
					null
				),
				sharedPreferences
			)
		)
	}

	override fun initView() {
		editNameButton = findViewById(R.id.edit_name_button)
		infoButton = findViewById(R.id.info_button)
		nicknameEditText = findViewById(R.id.nickname_edit_text)
		editNameButton.setOnClickListener {
			presenter.editNameButtonClicked(nicknameEditText.text.toString())
		}
		infoButton.setOnClickListener {
			presenter.infoButtonClicked()
		}
		val androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
		if (!androidId.isNullOrEmpty()) {
			presenter.viewInited(androidId)
		} else {
			val possibleId = "22" + Build.BOARD.length % 10 + Build.BRAND.length % 10 +
				Build.SUPPORTED_ABIS[0].length % 10 + Build.DEVICE.length % 10 +
				Build.DISPLAY.length % 10 + Build.HOST.length % 10 +
				Build.ID.length % 10 + Build.MANUFACTURER.length % 10 +
				Build.MODEL.length % 10 + Build.PRODUCT.length % 10 +
				Build.TAGS.length % 10 + Build.TYPE.length % 10 +
				Build.USER.length % 10
			presenter.viewInited(possibleId)
		}

	}

	override fun activateTextEditing() {
		nicknameEditText.isEnabled = true
		nicknameEditText.requestFocus()
		val inputManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
		inputManager.showSoftInput(nicknameEditText, InputMethodManager.SHOW_IMPLICIT)
		editNameButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_ready))
	}

	override fun deactivateTextEditing() {
		nicknameEditText.isEnabled = false
		val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
		inputManager.hideSoftInputFromWindow(nicknameEditText.windowToken, 0)
		editNameButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_pencil))
	}

	override fun updateUsername(username: String) {
		nicknameEditText.text = SpannableStringBuilder(username)
	}

	override fun showInfo() {
		infoDialog.showPopupWindow(nicknameEditText, getText(R.string.info_text), getText(R.string.want_points))
	}

	override fun onBackPressed() {
		presenter.onBackPressed()
		super.onBackPressed()
	}

}
