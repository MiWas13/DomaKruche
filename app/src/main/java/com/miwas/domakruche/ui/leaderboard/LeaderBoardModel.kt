package com.miwas.domakruche.ui.leaderboard

import android.content.SharedPreferences
import com.miwas.domakruche.network.NetworkService
import com.miwas.domakruche.network.model.BaseResult
import com.miwas.domakruche.network.model.Leader
import com.miwas.domakruche.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LeaderBoardModel(private val sharedPreferences: SharedPreferences?) {

	fun getLeaders(token: String?, callback: (leaders: Array<Leader>?) -> Unit) {
		NetworkService.getInstance()?.let { service ->
			service
				.getRequester()
				.getLeaders(token)
				.enqueue(object : Callback<BaseResult<Array<Leader>>> {
					override fun onResponse(
						call: Call<BaseResult<Array<Leader>>>,
						response: Response<BaseResult<Array<Leader>>>
					) {
						callback(response.body()?.result)
					}

					override fun onFailure(call: Call<BaseResult<Array<Leader>>>, t: Throwable) {
						t.printStackTrace()
					}
				})
		}
	}

	fun getToken(): String? {
		return sharedPreferences?.getString(Constants.APP_PREFERENCES_TOKEN, "")
	}

	fun getUserId(): Int? {
		return sharedPreferences?.getInt(Constants.APP_PREFERENCES_USER_ID, 0)
	}
}