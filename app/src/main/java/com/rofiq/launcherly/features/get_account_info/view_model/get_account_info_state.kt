package com.rofiq.launcherly.features.get_account_info.view_model

import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

interface GetAccountInfoState

// Initial state
object GetAccountInfoInitial : GetAccountInfoState

// Loading state
object GetAccountInfoLoading : GetAccountInfoState

// Success state
data class GetAccountInfoSuccess(val accountInfo: GoogleIdTokenCredential) : GetAccountInfoState

// Empty state
object GetAccountInfoEmpty : GetAccountInfoState

// Error state
data class GetAccountInfoError(val error: String) : GetAccountInfoState