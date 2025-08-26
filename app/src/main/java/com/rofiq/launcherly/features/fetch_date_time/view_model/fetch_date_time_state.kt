package com.rofiq.launcherly.features.fetch_date_time.view_model

import com.rofiq.launcherly.features.fetch_date_time.model.FetchDateTimeModel

interface FetchDateTimeState

// fetch date time initial
object FetchDateTimeInitial : FetchDateTimeState

// fetch date time loading
object FetchDateTimeLoading : FetchDateTimeState

// fetch date time success
data class FetchDateTimeSuccess(val dateTime: FetchDateTimeModel) : FetchDateTimeState

// fetch date time failed
data class FetchDateTimeFailed(val message: String) : FetchDateTimeState