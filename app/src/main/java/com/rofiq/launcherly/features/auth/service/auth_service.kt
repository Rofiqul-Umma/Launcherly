package com.rofiq.launcherly.features.auth.service

import com.rofiq.launcherly.core.shared_prefs_helper.SharedPrefsHelper
import com.rofiq.launcherly.features.auth.model.UserModel
import com.rofiq.launcherly.features.auth.model.toJson
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

class AuthService @Inject constructor (val sharedPrefs: SharedPrefsHelper) {

    suspend fun signIn(user: UserModel): Result<Unit> {
        return try {
            delay(5.seconds)
            sharedPrefs.saveString("user_data", user.toJson())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}