package com.miwas.domakruche.main

import android.content.SharedPreferences
import com.miwas.domakruche.database.DBHelper
import com.miwas.domakruche.network.NetworkService
import com.miwas.domakruche.network.model.BaseResult
import com.miwas.domakruche.network.model.Token
import com.miwas.domakruche.network.model.User
import com.miwas.domakruche.utils.Constants.APP_PREFERENCES_TOKEN
import com.miwas.domakruche.utils.Constants.APP_PREFERENCES_USER_ID
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainModel(private val dbHelper: DBHelper, private val sharedPreferences: SharedPreferences) {

	fun registerUser(deviceId: String, callback: (token: Token?) -> Unit) {

		NetworkService.getInstance()?.let { service ->
			service
				.getRequester()
				.registerUser(deviceId)
				.enqueue(object : Callback<BaseResult<Token>> {
					override fun onResponse(call: Call<BaseResult<Token>>, response: Response<BaseResult<Token>>) {
						callback(response.body()?.result)
					}

					override fun onFailure(call: Call<BaseResult<Token>>, t: Throwable) {
						t.printStackTrace()
					}
				})
		}
	}

	fun saveToken(token: String?) {
		sharedPreferences.edit().putString(APP_PREFERENCES_TOKEN, token).apply()
	}

	fun saveUserId(id: Int) {
		sharedPreferences.edit().putInt(APP_PREFERENCES_USER_ID, id).apply()
	}

	fun getToken(): String? {
		return sharedPreferences.getString(APP_PREFERENCES_TOKEN, "")
	}

	fun updateUsername(token: String, username: String, callback: (user: User?) -> Unit) {
		NetworkService.getInstance()?.let { service ->
			service
				.getRequester()
				.updateUsername(token, username)
				.enqueue(object : Callback<BaseResult<User>> {
					override fun onResponse(call: Call<BaseResult<User>>, response: Response<BaseResult<User>>) {
						callback(response.body()?.result)
					}

					override fun onFailure(call: Call<BaseResult<User>>, t: Throwable) {
						t.printStackTrace()
					}
				})
		}
	}

}