package com.miwas.domakruche.main

import com.miwas.domakruche.base.BasePresenter

class MainPresenter(private val mainModel: MainModel) : BasePresenter<MainView>() {

	private var isEditing = false

	override fun viewIsReady() {
		baseView?.initView()
	}

	fun viewInited(deviceId: String) {
		mainModel.registerUser(deviceId) {
			it?.let { notNollToken ->
				mainModel.saveToken(notNollToken.token)
				mainModel.saveUserId(notNollToken.id)
				if (notNollToken.username.isEmpty()) {
					updateUserName(notNollToken.token, "домосед#" + notNollToken.id)
				} else {
					baseView?.updateUsername(notNollToken.username)
				}
			}
		}
	}

	fun editNameButtonClicked(username: String) {
		if (isEditing) {
			baseView?.deactivateTextEditing()
			mainModel.getToken()?.let { token ->
				updateUserName(token, username)
			}
		} else {
			baseView?.activateTextEditing()
		}
		isEditing = !isEditing
	}

	fun infoButtonClicked() {
		baseView?.showInfo()
	}

	private fun updateUserName(token: String, username: String) {
		mainModel.updateUsername(token, username) { user ->
			user?.let {
				baseView?.updateUsername(user.username)
			}
		}
	}

	fun onBackPressed() {
		if (isEditing) {
			baseView?.deactivateTextEditing()
		}
	}
}