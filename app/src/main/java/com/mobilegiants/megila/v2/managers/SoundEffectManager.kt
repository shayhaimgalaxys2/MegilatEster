package com.mobilegiants.megila.v2.managers

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.mobilegiants.megila.v2.R

class SoundEffectManager(context: Context) {

    private val soundPool = SoundPool.Builder()
        .setMaxStreams(3)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private val noiseId = soundPool.load(context, R.raw.somenoise, 1)
    private val gunId = soundPool.load(context, R.raw.machinegun, 1)
    private val explosionId = soundPool.load(context, R.raw.explosion, 1)

    fun playNoise() {
        soundPool.play(noiseId, 1f, 1f, 0, 0, 1f)
    }

    fun playGunfire() {
        soundPool.play(gunId, 1f, 1f, 0, 0, 1f)
    }

    fun playExplosion() {
        soundPool.play(explosionId, 1f, 1f, 0, 0, 1f)
    }

    fun release() {
        soundPool.release()
    }
}
