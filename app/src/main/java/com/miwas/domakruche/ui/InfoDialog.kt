package com.miwas.domakruche.ui

import android.app.Activity
import android.text.method.ScrollingMovementMethod
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import com.miwas.domakruche.R


class InfoDialog(private val activity: Activity) {

	private lateinit var infoTextView: TextView
	private lateinit var closeButton: TextView

	fun showPopupWindow(view: View, mainText: CharSequence, buttonText: CharSequence) {
		val popupView = activity.layoutInflater.inflate(R.layout.info_dialog, null)
		infoTextView = popupView.findViewById(R.id.info_text_view)
		infoTextView.text = mainText
		closeButton = popupView.findViewById(R.id.close_button)
		closeButton.text = buttonText
		val width = LinearLayout.LayoutParams.MATCH_PARENT
		val height = LinearLayout.LayoutParams.MATCH_PARENT
		val focusable = true
		val popupWindow = PopupWindow(popupView, width, height, focusable)
		popupWindow.animationStyle = R.style.InfoWindowAnimation
		popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
		infoTextView.movementMethod = ScrollingMovementMethod()
		closeButton.setOnClickListener {
			popupWindow.dismiss()
		}
	}

}