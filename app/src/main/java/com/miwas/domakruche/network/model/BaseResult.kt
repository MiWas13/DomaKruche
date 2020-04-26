package com.miwas.domakruche.network.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class BaseResult<T> {

	@SerializedName("result")
	@Expose
	var result: T? = null

	@SerializedName("error")
	@Expose
	var error: Error? = null
}