package com.miwas.domakruche.network

import com.miwas.domakruche.utils.Constants.BASE_API_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetworkService {
	private var mRetrofit: Retrofit = Retrofit.Builder()
		.baseUrl(BASE_API_URL)
		.addConverterFactory(GsonConverterFactory.create())
		.build()

	companion object {
		private var mInstance: NetworkService? = null

		fun getInstance(): NetworkService? {
			if (mInstance == null) {
				mInstance = NetworkService()
			}
			return mInstance
		}
	}

	fun getRequester(): RetrofitRequester {
		return mRetrofit.create(RetrofitRequester::class.java)
	}
}