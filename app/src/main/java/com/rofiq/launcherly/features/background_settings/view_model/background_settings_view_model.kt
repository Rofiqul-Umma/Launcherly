package com.rofiq.launcherly.features.background_settings.view_model

import android.content.Context
import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.rofiq.launcherly.features.background_settings.model.BackgroundSetting
import com.rofiq.launcherly.features.background_settings.model.BackgroundType
import com.rofiq.launcherly.features.background_settings.service.BackgroundSettingsService
import com.rofiq.launcherly.utils.GoogleDriveUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class BackgroundSettingsViewModel @Inject constructor(
    private val backgroundSettingsService: BackgroundSettingsService
) : ViewModel() {

    private val _backgroundSettingsState = MutableStateFlow<BackgroundSettingsState>(BackgroundSettingsInitial)
    val backgroundSettingsState: StateFlow<BackgroundSettingsState> = _backgroundSettingsState.asStateFlow()

    private val _exoPlayer = MutableStateFlow<ExoPlayer?>(null)
    val exoPlayer: StateFlow<ExoPlayer?> = _exoPlayer.asStateFlow()

    private var currentPlayerUrl: String? = null

    init {
        loadBackgroundSettings()
    }

    fun emit(state: BackgroundSettingsState) {
        viewModelScope.launch {
            _backgroundSettingsState.emit(state)
        }
    }

    private fun loadBackgroundSettings() {
        viewModelScope.launch {
            try {
                emit(BackgroundSettingsLoading)

                val availableBackgrounds = backgroundSettingsService.getAvailableBackgrounds()
                val currentBackground = backgroundSettingsService.getCurrentBackground()

                if (currentBackground.type != BackgroundType.VIDEO) {
                    releasePlayer()
                }

                emit(
                    BackgroundSettingsLoaded(
                        availableBackgrounds = availableBackgrounds,
                        currentBackground = currentBackground
                    )
                )
            } catch (e: Exception) {
                emit(BackgroundSettingsError(e.message ?: "Error loading background settings"))
            }
        }
    }

    fun setBackground(backgroundSetting: BackgroundSetting, context: Context) {
        viewModelScope.launch {
            try {
                backgroundSettingsService.setBackground(backgroundSetting)
                if (backgroundSetting.type == BackgroundType.VIDEO) {
                    val videoUrl = if (backgroundSetting.sourceType == com.rofiq.launcherly.features.background_settings.model.BackgroundSourceType.URL) {
                        GoogleDriveUtils.convertSharingUrlToDownloadUrl(backgroundSetting.resourcePath)
                    } else {
                        backgroundSetting.resourcePath
                    }
                    initializePlayer(context.applicationContext, videoUrl)
                } else {
                    releasePlayer()
                }
                 val currentBg = backgroundSettingsService.getCurrentBackground()
                 val availableBackgrounds = (_backgroundSettingsState.value as? BackgroundSettingsLoaded)?.availableBackgrounds ?: emptyList()
                _backgroundSettingsState.value = BackgroundSettingsLoaded(
                    availableBackgrounds = availableBackgrounds,
                    currentBackground = currentBg
                )

            } catch (e: Exception) {
                emit(BackgroundSettingsError(e.message ?: "Error setting background"))
            }
        }
    }

    @OptIn(UnstableApi::class)
    fun initializePlayer(context: Context, videoUrl: String) {
        // Ensure context is application context
        val appContext = context.applicationContext

        if (_exoPlayer.value != null && currentPlayerUrl == videoUrl && !_exoPlayer.value!!.isReleased) {
            // Player already initialized with the same URL and not released
            _exoPlayer.value?.playWhenReady = true // Ensure it's set to play
            return
        }

        releasePlayer() // Release any existing player first

        currentPlayerUrl = videoUrl
        val player = ExoPlayer.Builder(appContext).build().apply {
            val mediaItem = MediaItem.fromUri(videoUrl.toUri())
            setMediaItem(mediaItem)
            repeatMode = Player.REPEAT_MODE_ONE
            // Use the correct constant from androidx.media3.common.C
            videoScalingMode = androidx.media3.common.C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
            playWhenReady = true

            addListener(object : Player.Listener {
                override fun onPlayerError(error: PlaybackException) {
                  Log.e("BackgroundVM_ExoPlayer", "Player error: ${error.message}", error)
                    currentPlayerUrl = null // Invalidate current URL on error to allow re-initialization
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_READY) {
                        Log.d("BackgroundVM_ExoPlayer", "Player is ready and will play when ready.")
                    }
                }
            })
            prepare()
        }
        _exoPlayer.value = player
    }

    @OptIn(UnstableApi::class)
    fun releasePlayer() {
        _exoPlayer.value?.let {
            if (!it.isReleased) {
                it.release()
            }
        }
        _exoPlayer.value = null
        currentPlayerUrl = null
    }

    @OptIn(UnstableApi::class)
    fun playPlayer() {
        _exoPlayer.value?.let {
            if (!it.isReleased) {
                it.playWhenReady = true
            }
        }
    }

    @OptIn(UnstableApi::class)
    fun pausePlayer() {
        _exoPlayer.value?.let {
            if (!it.isReleased) {
                it.playWhenReady = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }
}