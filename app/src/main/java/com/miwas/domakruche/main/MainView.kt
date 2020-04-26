package com.miwas.domakruche.main

import com.miwas.domakruche.base.MvpView

interface MainView : MvpView {
	fun initView()
	fun activateTextEditing()
	fun deactivateTextEditing()
	fun updateUsername(username: String)
	fun showInfo()
}