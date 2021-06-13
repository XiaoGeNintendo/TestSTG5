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

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.WindowedMean
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Logger
import com.badlogic.gdx.utils.viewport.ScalingViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.hhs.koto.app.screen.BasicScreen
import com.hhs.koto.app.screen.BlankScreen
import com.hhs.koto.app.screen.KotoScreen
import com.hhs.koto.app.screen.ScreenState
import com.hhs.koto.app.ui.FPSDisplay
import com.hhs.koto.util.*
import ktx.app.clearScreen
import ktx.async.KtxAsync
import ktx.collections.GdxArray


class KotoApp(val restartCallback: (Boolean) -> Unit) : ApplicationListener {

    lateinit var batch: SpriteBatch
    lateinit var viewport: Viewport
    lateinit var st: Stage
    private lateinit var fps: FPSDisplay
    lateinit var fpsCounter: WindowedMean

    val screens = GdxArray<KotoScreen>()
    var input = InputMultiplexer()
    var blocker = InputBlocker()
    val logger = Logger("Main", Config.logLevel)

    override fun create() {
        koto = this

        loadOptions()
        initAll()

        Gdx.app.logLevel = Config.logLevel

        logger.info("Game start.")

        Gdx.graphics.setWindowedMode(options.startupWindowWidth, options.startupWindowHeight)
        Gdx.graphics.setVSync(options.vsyncEnabled)
        Gdx.graphics.setForegroundFPS(options.fpsLimit)

        batch = SpriteBatch()
        fpsCounter = WindowedMean(10);
        viewport = ScalingViewport(Config.windowScaling, Config.screenWidth, Config.screenHeight)
        st = Stage(viewport);
        st.isDebugAll = Config.debugActorLayout

        input.addProcessor(blocker)
        Gdx.input.inputProcessor = input

        KtxAsync.initiate()

        loadAssetIndex(Gdx.files.internal(".assets.json"))
        A.finishLoading()

        fps = FPSDisplay()
        st.addActor(fps)

        SE.register("cancel", "snd/se_cancel00.wav")
        SE.register("invalid", "snd/se_invalid.wav")
        SE.register("ok", "snd/se_ok00.wav")
        SE.register("select", "snd/se_select00.wav")
        SE.register("pldead", "snd/se_pldead00.wav")
        SE.register("item", "snd/se_item00.wav")
        SE.register("graze", "snd/se_graze.wav")
        SE.register("shoot", "snd/se_plst00.wav")
        3
        BGM.register(LoopingMusic("mus/E.0120.ogg", 2f, 58f))

        B.setSheet(Config.defaultShotSheet);

        screens.add(BlankScreen())
        screens.add(BasicScreen("mus/E.0120.ogg", getRegion("bg/title.png"), "zjs"))
        setScreen("blank")
        setScreen("zjs", 0.5f)
    }

    override fun resize(width: Int, height: Int) {
        screens.filter { it.state.isRendered() }.forEach { it.resize(width, height) }
        viewport.update(width, height)
    }

    override fun render() {
        A.update()
        BGM.update()
        fpsCounter.addValue(Gdx.graphics.deltaTime);

        clearScreen(0f, 0f, 0f, 1f)
        var flag1 = false
        var flag2 = false
        screens.filter { it.state == ScreenState.FADING_IN }.forEach {
            flag1 = true
            it.render(safeDeltaTime())
        }
        screens.filter { it.state == ScreenState.SHOWN }.forEach {
            flag2 = true
            it.render(safeDeltaTime())
        }
        screens.filter { it.state == ScreenState.FADING_OUT }.forEach {
            flag1 = true
            it.render(safeDeltaTime())
        }
        blocker.isBlocking = flag1
        if (!flag1 && !flag2) {
            exitApp()
        }
        st.act(safeDeltaTime())
        st.draw()
    }

    override fun pause() {
        screens.filter { it.state.isRendered() }.forEach { it.pause() }
    }

    override fun resume() {
        screens.filter { it.state.isRendered() }.forEach { it.resume() }
    }

    override fun dispose() {
        batch.dispose()
        BGM.dispose()
    }

    fun setScreen(name: String?) {
        val scr: KotoScreen? = screens.find {
            it.name == name
        }
        blocker.isBlocking = true
        screens.filter { it.state.isRendered() }.forEach {
            it.hide()
            it.state = ScreenState.HIDDEN
        }
        if (scr != null) {
            logger.info("Switching to screen $name")
            scr.resize(Gdx.graphics.width, Gdx.graphics.height)
            scr.show()
            scr.state = ScreenState.SHOWN
        } else {
            logger.info("Switching to no screen")
        }
    }

    fun setScreen(name: String?, fadeTime: Float) {
        val scr: KotoScreen? = screens.find {
            it.name == name
        }
        blocker.isBlocking = true
        var oldScreen: KotoScreen? = null
        screens.filter { it.state.isRendered() }.forEach {
            it.fadeOut(scr, fadeTime)
            oldScreen = it
        }
        if (scr != null) {
            logger.info("Switching to screen \"$name\" with fading time $fadeTime")
            scr.resize(Gdx.graphics.width, Gdx.graphics.height)
            scr.fadeIn(oldScreen, fadeTime)
            scr.state = ScreenState.SHOWN
        } else {
            logger.info("Switching to no screen")
        }
    }
}