package com.miwas.domakruche.network.model;

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Token {
	@SerializedName("user_id")
	@Expose
	val id = 0
	@SerializedName("token")
	@Expose
	val token = ""
	@SerializedName("username")
	@Expose
	val username = ""
}
