package com.miwas.domakruche.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.O)
class NotificationUtils(base: Context) : ContextWrapper(base) {

	private var mManager: NotificationManager? = null

	init {
		createChannel()
	}

	private fun createChannel() {
		val androidChannel =
			NotificationChannel(ANDROID_CHANNEL_ID, ANDROID_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
		androidChannel.enableLights(true)
		androidChannel.enableVibration(true)
		androidChannel.lightColor = Color.GREEN
		androidChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
		getManager()?.createNotificationChannel(androidChannel)
	}

	fun getManager(): NotificationManager? {
		if (mManager == null) {
			mManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
		}
		return mManager
	}

	companion object {
		const val ANDROID_CHANNEL_ID = "com.example.domakruche.ANDROID"
		const val ANDROID_CHANNEL_NAME = "ANDROID_CHANNEL"
	}

}