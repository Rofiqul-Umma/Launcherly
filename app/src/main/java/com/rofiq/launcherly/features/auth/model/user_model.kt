package com.rofiq.launcherly.features.auth.model

import com.google.gson.Gson

data class UserModel(
    val userName: String
)

fun UserModel.toJson(): String = Gson().toJson(this)
