package com.miwas.domakruche.network

import com.miwas.domakruche.network.model.BaseResult
import com.miwas.domakruche.network.model.Leader
import com.miwas.domakruche.network.model.Token
import com.miwas.domakruche.network.model.User
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RetrofitRequester {
	@GET("api/user/info/v1")
	fun getUserInfo(@Query("token") token: String?): Call<BaseResult<User>>

	@FormUrlEncoded
	@POST("api/user/credit/v1")
	fun chargeCredit(@Field("token") token: String?): Call<BaseResult<User>>

	@FormUrlEncoded
	@POST("api/user/generate/v1")
	fun registerUser(@Field("device_id") deviceId: String): Call<BaseResult<Token>>

	@FormUrlEncoded
	@POST("api/user/update/v1")
	fun updateUsername(@Field("token") token: String?, @Field("username") username: String): Call<BaseResult<User>>

	@GET("api/leaders/v1")
	fun getLeaders(@Query("token") token: String?): Call<BaseResult<Array<Leader>>>
}