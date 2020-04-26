package com.miwas.domakruche.network.model;

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SimpleResult {
	@SerializedName("result")
	@Expose
	val result = ""
	@SerializedName("error")
	@Expose
	val error = ""
}
