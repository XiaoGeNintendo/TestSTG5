/*
 * MIT License
 *
 * Copyright (c) 2021 Hell Hole Studios
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.hhs.koto.util

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.utils.Logger
import com.badlogic.gdx.utils.ObjectMap

object BGM {

    var bgm: LoopingMusic? = null
    private val bgms: ObjectMap<String, LoopingMusic> = ObjectMap<String, LoopingMusic>()
    val logger = Logger("BGM", config.logLevel)

    fun play(name: String?) {
        if (name == null) {
            stop()
            return
        }
        if (name == "") {
            logger.debug("Keep original BGM.")
            return
        }
        if (bgm != null) {
            if (bgm!!.name == name) {
                logger.debug("Same BGM as before. No changing.")
                return
            }
            stop()
        }
        logger.debug("Playing \"$name\".")
        bgm = bgms.get(name)
        if (bgm == null) {
            logger.error("BGM with this name doesn't exist!")
        } else {
            bgm!!.play()
        }
    }

    fun register(music: LoopingMusic): LoopingMusic {
        bgms.put(music.name, music)
        return music
    }

    fun update() = bgm?.update()

    fun stop() {
        if (bgm != null) {
            logger.debug("Stopping \"$bgm.name\".")
            bgm!!.stop()
            bgm = null
        }
    }

    fun dispose() {
        if (bgm != null) {
            logger.debug("\"$bgm.name\" is stopped and disposed.")
            bgm!!.stop()
            bgm!!.dispose()
        }
    }

    fun pause() = bgm?.pause()

    fun resume() = bgm?.resume()

    fun setVolume(volume: Float) = bgm?.setVolume(volume)
}

class LoopingMusic(val name: String) {
    private lateinit var music: Music
    private var isPlaying = false
    private var isLooping = false
    var loopStart = 0f
    var loopEnd = 0f

    constructor(name: String, loopStart: Float, loopEnd: Float) : this(name) {
        isLooping = true
        isPlaying = false
        this.loopStart = loopStart
        this.loopEnd = loopEnd
    }

    fun stop() {
        isPlaying = false
        music.stop()
    }

    fun load() {
        BGM.logger.debug("Loading music file \"$name\".")
        music = Gdx.audio.newMusic(Gdx.files.internal(name))
        music.setOnCompletionListener { music ->
            if (isLooping) {
                music.volume = config.musicVolume
                music.play()
                music.position = loopStart
            }
        }
    }

    fun dispose() {
        BGM.logger.debug("Disposing music file \"$name\".")
        music.dispose()
    }

    fun play() {
        load()
        isPlaying = true
        music.isLooping = false
        music.volume = config.musicVolume
        music.play()
    }

    fun pause() {
        isPlaying = false
        music.pause()
    }

    fun resume() {
        isPlaying = true
        music.volume = config.musicVolume
        music.play()
    }

    fun update() {
        if (isPlaying && isLooping) {
            if (!music.isPlaying) {
                music.position = loopStart
                music.volume = config.musicVolume
                music.play()
            } else if (music.position >= loopEnd) {
                music.position = loopStart + (music.position - loopEnd)
            }
        }
    }

    fun setVolume(volume: Float) {
        music.volume = volume
    }
}