package com.rofiq.launcherly.features.auth.service

import com.rofiq.launcherly.core.shared_prefs_helper.SharedPrefsHelper
import com.rofiq.launcherly.features.auth.model.UserModel
import com.rofiq.launcherly.features.auth.model.toJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

class AuthService @Inject constructor (val sharedPrefs: SharedPrefsHelper) {

    suspend fun signIn(user: UserModel): Result<Unit> {
        return withContext(Dispatchers.IO){
            try {
                delay(5.seconds)
                sharedPrefs.saveString("user_data", user.toJson())
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun checkLogin(): Result<Unit> {
        return withContext(Dispatchers.IO){
            try {
                delay(5.seconds)
                val data = sharedPrefs.getString("user_data")
                if (data.isEmpty()) {
                    Result.failure(Exception("User not found"))
                } else {
                    Result.success(Unit)
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}