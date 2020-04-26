package com.miwas.domakruche.notifications

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.miwas.domakruche.R
import com.miwas.domakruche.main.MainActivity
import com.miwas.domakruche.notifications.NotificationUtils.Companion.ANDROID_CHANNEL_ID

class ReminderBroadcast : BroadcastReceiver() {
	override fun onReceive(context: Context, intent: Intent) {
		val mainIntent = Intent(context, MainActivity::class.java)
		val pendingIntent = PendingIntent.getActivity(context, 100, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT)
		val notificationBuilder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			generateNotification(context, ANDROID_CHANNEL_ID, pendingIntent)
		} else {
			generateNotification(context, "notifyDomaKruche", pendingIntent)
		}


		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val notificationUtils = NotificationUtils(context)
			notificationUtils.getManager()?.notify(101, notificationBuilder.build())
		} else {
			val notificationCompatManager = NotificationManagerCompat.from(context)
			notificationCompatManager.notify(200, notificationBuilder.build())
		}
	}

	private fun generateNotification(
		context: Context,
		channelId: String,
		pendingIntent: PendingIntent
	) =
		NotificationCompat.Builder(context, channelId)
			.setContentIntent(pendingIntent)
			.setSmallIcon(R.drawable.ic_home)
			.setContentTitle("Вы дома?")
			.setContentText("Можно снова отметиться и получить баллы!")
			.setPriority(NotificationCompat.PRIORITY_DEFAULT)

}