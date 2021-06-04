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

package com.hhs.koto.app

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.ScalingViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.hhs.koto.app.screen.BasicScreen
import com.hhs.koto.util.A
import com.hhs.koto.util.InputBlocker
import com.hhs.koto.util.config
import com.hhs.koto.util.loadAssetIndex
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.async.KtxAsync

class AppMain : KtxGame<KtxScreen>() {
    lateinit var batch: SpriteBatch
    lateinit var viewport: Viewport
    lateinit var input: InputMultiplexer
    lateinit var blocker: InputBlocker

    override

    fun create() {
        Gdx.app.logLevel = config.logLevel;

        batch = SpriteBatch()
        viewport = ScalingViewport(config.windowScaling, config.screenWidth, config.screenHeight)
        input = InputMultiplexer()
        blocker = InputBlocker()
        input.addProcessor(blocker)
        Gdx.input.inputProcessor = input

        KtxAsync.initiate()

        loadAssetIndex(Gdx.files.internal(".assets.json"))
        A.finishLoading()

        addScreen(BasicScreen(this))
        setScreen<BasicScreen>()
        super.create()
    }

    override fun render() {
        A.update()
        super.render()
    }

    override fun dispose() {
        batch.dispose()
    }
}