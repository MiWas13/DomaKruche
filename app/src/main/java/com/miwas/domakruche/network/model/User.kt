package com.miwas.domakruche.network.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class User {
	@SerializedName("balance")
	@Expose
	val balance = 0
	@SerializedName("last_credit_time")
	@Expose
	val lastCreditTime = ""
	@SerializedName("server_time")
	@Expose
	val serverTime = ""
	@SerializedName("username")
	@Expose
	val username = ""
	@SerializedName("joke")
	@Expose
	val joke = ""
}