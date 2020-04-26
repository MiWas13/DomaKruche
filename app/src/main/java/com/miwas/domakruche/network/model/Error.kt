package com.miwas.domakruche.network.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Error {

	@SerializedName("code")
	@Expose
	var code: Int = 0
	@SerializedName("message")
	@Expose
	var message = ""
}