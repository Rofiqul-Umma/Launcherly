package com.rofiq.launcherly.features.background_settings.view_model

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
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
import coil.ImageLoader
import com.rofiq.launcherly.features.background_settings.model.BackgroundSetting
import com.rofiq.launcherly.features.background_settings.model.BackgroundType
import com.rofiq.launcherly.features.background_settings.model.MediaItemModel
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
    private val backgroundSettingsService: BackgroundSettingsService,
    val imageLoader: ImageLoader
) : ViewModel() {

    private val _backgroundSettingsState = MutableStateFlow<BackgroundSettingsState>(BackgroundSettingsInitial)
    val backgroundSettingsState: StateFlow<BackgroundSettingsState> = _backgroundSettingsState.asStateFlow()

    private val _exoPlayer = MutableStateFlow<ExoPlayer?>(null)
    val exoPlayer: StateFlow<ExoPlayer?> = _exoPlayer.asStateFlow()

    private var currentPlayerUrl: String? = null
    private var appContext: Context? = null

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

                emit(
                    BackgroundSettingsLoaded(
                        availableBackgrounds = availableBackgrounds,
                        currentBackground = currentBackground
                    )
                )
                
                // Initialize player for video background if needed
                if (currentBackground.type == BackgroundType.VIDEO) {
                    val videoUrl = if (currentBackground.sourceType == com.rofiq.launcherly.features.background_settings.model.BackgroundSourceType.URL) {
                        GoogleDriveUtils.convertSharingUrlToDownloadUrl(currentBackground.resourcePath)
                    } else {
                        currentBackground.resourcePath
                    }
                    if (videoUrl.isNotBlank() && appContext != null) {
                        initializePlayer(appContext!!, videoUrl)
                    }
                }
            } catch (e: Exception) {
                emit(BackgroundSettingsError(e.message ?: "Error loading background settings"))
            }
        }
    }

    fun setBackground(backgroundSetting: BackgroundSetting, context: Context) {
        viewModelScope.launch {
            try {
                backgroundSettingsService.setBackground(backgroundSetting)
                
                // Update the state with the new background
                val availableBackgrounds = (_backgroundSettingsState.value as? BackgroundSettingsLoaded)?.availableBackgrounds ?: emptyList()
                val newState = BackgroundSettingsLoaded(
                    availableBackgrounds = availableBackgrounds,
                    currentBackground = backgroundSetting
                )
                emit(newState)
                
                // Handle player initialization/switching based on new background type
                if (backgroundSetting.type == BackgroundType.VIDEO) {
                    val videoUrl = if (backgroundSetting.sourceType == com.rofiq.launcherly.features.background_settings.model.BackgroundSourceType.URL) {
                        GoogleDriveUtils.convertSharingUrlToDownloadUrl(backgroundSetting.resourcePath)
                    } else {
                        backgroundSetting.resourcePath
                    }
                    initializePlayer(context.applicationContext, videoUrl)
                } else {
                    // For non-video backgrounds, pause the player but don't release it
                    _exoPlayer.value?.playWhenReady = false
                }
            } catch (e: Exception) {
                emit(BackgroundSettingsError(e.message ?: "Error setting background"))
            }
        }
    }

    /**
     * Sets a local file as the background
     */
    fun setLocalBackground(context: Context, uri: Uri, type: BackgroundType, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val success = backgroundSettingsService.setLocalBackground(context, uri, type)
                if (success) {
                    // Reload background settings to reflect the change
                    loadBackgroundSettings()
                }
                callback(success)
            } catch (e: Exception) {
                callback(false)
            }
        }
    }

    @OptIn(UnstableApi::class)
    fun initializePlayer(context: Context, videoUrl: String) {
        // Store application context for later use
        if (appContext == null) {
            appContext = context.applicationContext
        }

        // If player already exists and is for the same URL and not released, just resume it
        if (_exoPlayer.value != null && currentPlayerUrl == videoUrl && !_exoPlayer.value!!.isReleased) {
            _exoPlayer.value?.playWhenReady = true
            return
        }

        // If we're switching to a different video URL or the player is released, we need a new player
        if ((_exoPlayer.value != null && (currentPlayerUrl != videoUrl || _exoPlayer.value!!.isReleased))) {
            // Release the old player
            _exoPlayer.value?.let {
                if (!it.isReleased) {
                    it.release()
                }
            }
            _exoPlayer.value = null
        }

        // Create new player only if needed
        if (_exoPlayer.value == null) {
            currentPlayerUrl = videoUrl
            val player = ExoPlayer.Builder(context.applicationContext).build().apply {
                val mediaItem = MediaItem.fromUri(videoUrl.toUri())
                setMediaItem(mediaItem)
                repeatMode = Player.REPEAT_MODE_ONE
                videoScalingMode = androidx.media3.common.C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
                playWhenReady = true

                addListener(object : Player.Listener {
                    override fun onPlayerError(error: PlaybackException) {
                      Log.e("BackgroundVM_ExoPlayer", "Player error: ${error.message}", error)
                        currentPlayerUrl = null
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
    }

    @OptIn(UnstableApi::class)
    fun releasePlayer() {
        // Only release if we're changing contexts or the ViewModel is being cleared
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

     fun loadMediaFromMediaStore(context: Context): List<MediaItemModel> {
        val mediaList = mutableListOf<MediaItemModel>()
        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.MEDIA_TYPE
        )

        val selection = "${MediaStore.Files.FileColumns.MEDIA_TYPE} = ? OR ${MediaStore.Files.FileColumns.MEDIA_TYPE} = ?"
        val selectionArgs = arrayOf(
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
        )

        val sortOrder = "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"

        val queryUri = MediaStore.Files.getContentUri("external")

        context.contentResolver.query(
            queryUri,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            val mediaTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val mediaType = cursor.getInt(mediaTypeColumn)
                val contentUri = ContentUris.withAppendedId(queryUri, id)
                mediaList.add(MediaItemModel(contentUri, mediaType))
            }
        }
        return mediaList
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }
}