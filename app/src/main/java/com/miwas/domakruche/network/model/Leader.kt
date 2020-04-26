package com.miwas.domakruche.network.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Leader {
	@SerializedName("username")
	@Expose
	var username = ""

	@SerializedName("balance")
	@Expose
	var balance = 0

	@SerializedName("rating")
	@Expose
	var rating = 0

	@SerializedName("id")
	@Expose
	var id = 0
}