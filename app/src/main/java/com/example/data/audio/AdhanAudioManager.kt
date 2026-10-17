package com.example.data.audio

import android.content.Context
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.util.Log
import com.example.R
import com.example.data.model.AdhanMuezzin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AdhanAudioManager(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _currentPlayingMuezzin = MutableStateFlow<AdhanMuezzin?>(null)
    val currentPlayingMuezzin: StateFlow<AdhanMuezzin?> = _currentPlayingMuezzin

    fun playAdhan(muezzin: AdhanMuezzin, onCompletion: (() -> Unit)? = null) {
        stopAdhan()
        _currentPlayingMuezzin.value = muezzin
        _isPlaying.value = true

        try {
            // Play authentic bundled human vocal Adhan recitation
            val player = MediaPlayer.create(context, R.raw.adhan_qatami)
            if (player != null) {
                mediaPlayer = player
                player.setOnCompletionListener {
                    _isPlaying.value = false
                    _currentPlayingMuezzin.value = null
                    onCompletion?.invoke()
                }
                player.setOnErrorListener { _, what, extra ->
                    Log.w("AdhanAudioManager", "MediaPlayer error $what $extra")
                    _isPlaying.value = false
                    _currentPlayingMuezzin.value = null
                    fallbackToSystemRingtone(onCompletion)
                    true
                }
                player.start()
            } else {
                fallbackToSystemRingtone(onCompletion)
            }
        } catch (e: Exception) {
            Log.e("AdhanAudioManager", "Error playing vocal adhan", e)
            fallbackToSystemRingtone(onCompletion)
        }
    }

    private fun fallbackToSystemRingtone(onCompletion: (() -> Unit)? = null) {
        try {
            val alert: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            val ringtone = RingtoneManager.getRingtone(context, alert)
            ringtone?.play()
        } catch (e: Exception) {
            Log.e("AdhanAudioManager", "Failed to play fallback tone", e)
        } finally {
            _isPlaying.value = false
            _currentPlayingMuezzin.value = null
            onCompletion?.invoke()
        }
    }

    fun stopAdhan() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.reset()
                it.release()
            }
        } catch (e: Exception) {
            Log.e("AdhanAudioManager", "Error stopping player", e)
        } finally {
            mediaPlayer = null
            _isPlaying.value = false
            _currentPlayingMuezzin.value = null
        }
    }
}
