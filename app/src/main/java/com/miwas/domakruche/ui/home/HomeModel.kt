package com.miwas.domakruche.ui.home

import android.content.ContentValues
import android.content.SharedPreferences
import android.database.Cursor
import com.miwas.domakruche.database.DBHelper
import com.miwas.domakruche.network.NetworkService
import com.miwas.domakruche.network.model.BaseResult
import com.miwas.domakruche.network.model.User
import com.miwas.domakruche.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeModel(private val dbHelper: DBHelper, private val sharedPreferences: SharedPreferences?) {

	fun putLocationToDb(locationLatitude: Double, locationLongitude: Double) {
		val newValues = ContentValues()
		newValues.put(Constants.COLUMN_NAME_LATITUDE, locationLatitude)
		newValues.put(Constants.COLUMN_NAME_LONGITUDE, locationLongitude)
		dbHelper.writableDatabase.insert(Constants.TABLE_NAME, null, newValues)
		dbHelper.writableDatabase.close()
	}

	fun getSavedLocationFromDb(): Pair<Double, Double> {
		val cursor: Cursor = dbHelper.readableDatabase.query(
			Constants.TABLE_NAME, arrayOf(Constants.COLUMN_NAME_LATITUDE, Constants.COLUMN_NAME_LONGITUDE),
			null, null, null, null, null
		)

		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast) {
				return Pair(cursor.getString(0).toDouble(), cursor.getString(1).toDouble())
			}
		}
		cursor.close()
		dbHelper.readableDatabase.close()
		return Pair(0.0, 0.0)
	}

	fun getUser(callback: (user: User?) -> Unit) {
		NetworkService.getInstance()?.let { service ->
			service
				.getRequester()
				.getUserInfo(getToken())
				.enqueue(object : Callback<BaseResult<User>> {
					override fun onResponse(call: Call<BaseResult<User>>, response: Response<BaseResult<User>>) {
						if (!response.isSuccessful) {
							call.clone().enqueue(this)
						}
						callback(response.body()?.result)
					}

					override fun onFailure(call: Call<BaseResult<User>>, t: Throwable) {
						t.printStackTrace()
					}
				})
		}
	}

	fun getToken(): String? {
		return sharedPreferences?.getString(Constants.APP_PREFERENCES_TOKEN, "")
	}

	fun chargePoints(token: String?, callback: (user: User?) -> Unit) {
		NetworkService.getInstance()?.let { service ->
			service
				.getRequester()
				.chargeCredit(token)
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