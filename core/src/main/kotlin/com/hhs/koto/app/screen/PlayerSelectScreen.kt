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

package com.hhs.koto.app.screen

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.hhs.koto.app.ui.*
import com.hhs.koto.stg.GameMode
import com.hhs.koto.util.*
import ktx.actors.alpha
import ktx.actors.plusAssign
import ktx.collections.GdxArray

class PlayerSelectScreen : BasicScreen("mus/E0120.ogg", getRegion("bg/generic.png")) {
    private var difficultyLabel: Grid? = null
    private val portraits = Grid(selectSound = null).register(st, input)
    private val descriptions = Grid().register(st, input)

    private val shotType = Group().apply {
        st += this
        alpha = 0f
    }
    private val shotTypeBackground = Image(getRegion("ui/bg.png")).apply {
        setSize(850f, 250f)
        shotType += this
    }
    private val shotTypeGrid = Grid().apply { shotType += this }

    private var switchTarget: String? = null
    var selectedPlayer: String? = null

    companion object {
        fun playerColor(name: String): Color = when (name) {
            "reimu" -> Color(1f, 0f, 0f, 1f)
            "marisa" -> Color(1f, 1f, 0f, 1f)
            "sakuya" -> Color(1f, 1f, 1f, 1f)
            "youmu" -> Color(0.3f, 0.8f, 0.4f, 1f)
            "sanae" -> Color(0f, 0.5f, 1f, 1f)
            else -> Color.WHITE
        }

        fun generatePortrait(
            screen: PlayerSelectScreen,
            name: String,
            selectPortrait: TextureRegion,
            shotTypes: GdxArray<String>,
            gridX: Int,
            gridY: Int,
            staticX: Float,
            staticY: Float,
            width: Float = selectPortrait.regionWidth.toFloat(),
            height: Float = width / selectPortrait.regionWidth * selectPortrait.regionHeight,
        ) = Grid(staticX = staticX, staticY = staticY, gridX = gridX, gridY = gridY).add(
            GridImage(
                selectPortrait,
                width = width,
                height = height,
                tint = Color(1f, 1f, 0f, 0.8f),
            ) {
                if (shotTypes.size == 0) {
                    SystemFlag.player = name
                    app.setScreen("game", 1f)
                } else {
                    screen.selectedPlayer = name
                    screen.showShotType(name, shotTypes)
                }
            }.apply {
                activeAction = {
                    Actions.sequence(
                        Actions.moveTo(this.staticX, this.staticY),
                        Actions.moveTo(this.staticX + 30f, this.staticY, 1f, Interpolation.pow5Out),
                    )
                }
            }
        ).add(
            GridImage(selectPortrait, width = width, height = height, triggerSound = null).apply {
                activeAction = {
                    Actions.sequence(
                        Actions.moveTo(this.staticX, this.staticY),
                        Actions.moveTo(this.staticX - 30f, this.staticY, 1f, Interpolation.pow5Out),
                    )
                }
            }
        ).apply {
            activeAction = { Actions.show() }
            inactiveAction = { Actions.hide() }
        }.selectFirst()

        fun generateDescription(
            name: String,
            gridX: Int,
            gridY: Int,
            staticX: Float = 150f,
            staticY: Float = 300f,
        ) = Grid(staticX = staticX, staticY = staticY, gridX = gridX, gridY = gridY).add(
            GridImage(
                getRegion("ui/arrow.png"),
                width = 48f,
                staticX = -80f,
                staticY = 425f,
                activeAction = null,
                triggerSound = null,
            )
        ).add(
            GridImage(
                getRegion("ui/arrow.png"),
                width = 48f,
                staticX = 680f,
                staticY = 425f,
                activeAction = null,
                triggerSound = null,
            ).apply {
                setScale(-1f, 1f)
            }
        ).add(
            GridLabel(
                bundle["ui.playerSelect.player.$name.title"],
                width = 600f,
                height = 50f,
                staticY = 500f,
                style = Label.LabelStyle(
                    getFont(
                        bundle["font.boldItalic"],
                        36,
                        Color.WHITE,
                        borderColor = Color.BLACK
                    ), Color.WHITE
                ),
            ).apply {
                setAlignment(Align.bottom)
            }
        ).add(
            GridLabel(
                bundle["ui.playerSelect.player.$name.name"],
                width = 600f,
                height = 100f,
                staticY = 400f,
                style = Label.LabelStyle(
                    getFont(
                        bundle["font.boldRegular"],
                        72,
                        playerColor(name),
                        borderColor = Color.BLACK
                    ), Color.WHITE
                ),
            ).apply {
                setAlignment(Align.center)
            }
        ).add(
            GridLabel(bundle["ui.playerSelect.player.$name.description"], 24, width = 600f, height = 400f).apply {
                setAlignment(Align.center)
            }
        ).apply {
            activeAction = { Actions.show() }
            inactiveAction = { Actions.hide() }
        }.selectFirst()
    }

    init {
        switchTarget = when (SystemFlag.gamemode) {
            GameMode.SPELL_PRACTICE -> "spellSelect"
            GameMode.STAGE_PRACTICE -> "stageSelect"
            else -> "game"
        }

        portraits.add(
            generatePortrait(
                this, "reimu", getRegion("portrait/reimu/select.png"),
                GdxArray.with("reimuA", "reimuB", "reimu"), 0, 0, 850f, 50f, 560f,
            )
        )
        portraits.add(
            generatePortrait(
                this, "marisa", getRegion("portrait/marisa/select.png"),
                GdxArray.with("marisaA", "marisaB", "marisa"), 1, 0, 850f, 50f, 560f,
            )
        )
        portraits.add(
            generatePortrait(
                this, "sakuya", getRegion("portrait/sakuya/select.png"),
                GdxArray.with("sakuyaA", "sakuyaB", "sakuya"), 2, 0, 900f, 50f, 460f,
            )
        )
        portraits.add(
            generatePortrait(
                this, "youmu", getRegion("portrait/youmu/select.png"),
                GdxArray.with("youmuA", "youmuB", "youmu"), 3, 0, 800f, 50f, 720f,
            )
        )
        portraits.add(
            generatePortrait(
                this, "sanae", getRegion("portrait/sanae/select.png"),
                GdxArray.with("sanaeA", "sanaeB", "sanae"), 4, 0, 770f, 50f, 680f,
            )
        )
        portraits.selectFirst()

        descriptions.add(generateDescription("reimu", 0, 0))
        descriptions.add(generateDescription("marisa", 1, 0))
        descriptions.add(generateDescription("sakuya", 2, 0))
        descriptions.add(generateDescription("youmu", 3, 0))
        descriptions.add(generateDescription("sanae", 4, 0))
        descriptions.selectFirst()
    }

    override fun fadeIn(oldScreen: KotoScreen?, duration: Float) {
        super.fadeIn(oldScreen, duration)

        difficultyLabel?.remove()
        difficultyLabel = DifficultySelectScreen.generateButton(SystemFlag.difficulty!!, 0, 0)
        st += difficultyLabel!!
        difficultyLabel!!.staticX = 150f
        difficultyLabel!!.staticY = 50f
        difficultyLabel!!.setScale(0.5f)
        difficultyLabel!!.clearActions()
        difficultyLabel!!.setPosition(difficultyLabel!!.staticX, difficultyLabel!!.staticY - 300f)
        difficultyLabel!!.addAction(
            Actions.moveTo(
                difficultyLabel!!.staticX,
                difficultyLabel!!.staticY,
                duration,
                Interpolation.pow5Out
            )
        )

        portraits.clearActions()
        portraits.setPosition(portraits.staticX + 800f, portraits.staticY)
        portraits.addAction(Actions.moveTo(portraits.staticX, portraits.staticY, duration, Interpolation.pow5Out))

        descriptions.clearActions()
        descriptions.setPosition(descriptions.staticX - 800f, descriptions.staticY)
        descriptions.addAction(
            Actions.moveTo(
                descriptions.staticX,
                descriptions.staticY,
                duration,
                Interpolation.pow5Out
            )
        )
    }

    override fun fadeOut(newScreen: KotoScreen?, duration: Float) {
        super.fadeOut(newScreen, duration)

        difficultyLabel!!.clearActions()
        difficultyLabel!!.addAction(
            Actions.moveTo(
                difficultyLabel!!.staticX,
                difficultyLabel!!.staticY - 300f,
                duration,
                Interpolation.pow5Out
            )
        )

        portraits.clearActions()
        portraits.addAction(
            Actions.moveTo(
                portraits.staticX + 800f,
                portraits.staticY,
                duration,
                Interpolation.pow5Out
            )
        )

        descriptions.clearActions()
        descriptions.addAction(
            Actions.moveTo(
                descriptions.staticX - 800f,
                descriptions.staticY,
                duration,
                Interpolation.pow5Out
            )
        )
    }

    private fun showShotType(name: String, shotTypes: GdxArray<String>) {
        shotType.clearActions()
        shotType.addAction(Actions.fadeIn(0.5f, Interpolation.pow5Out))
        input.addProcessor(shotTypeGrid)

        descriptions.clearActions()
        descriptions.addAction(Actions.fadeOut(0.5f, Interpolation.pow5Out))
        input.removeProcessor(descriptions)
        input.removeProcessor(portraits)

        shotTypeGrid.clear()

        val baseY = (250f * shotTypes.size - 50f) / 2f + 600f
        shotTypeBackground.color = playerColor(name).toHSVColor()
        shotTypeBackground.y = baseY - 180f

        for (i in 0 until shotTypes.size) {
            shotTypeGrid.add(
                GridButton(
                    bundle["ui.playerSelect.shotType.${shotTypes[i]}.name"],
                    48,
                    0,
                    i,
                    staticX = 100f,
                    staticY = baseY - i * 250f,
                ) {
                    SystemFlag.player = shotTypes[i]
                    app.setScreen(switchTarget, 1f)
                }.apply {
                    activeAction = getActiveAction({
                        Actions.run {
                            shotTypeBackground.clearActions()
                            shotTypeBackground.addAction(
                                Actions.moveTo(
                                    0f,
                                    baseY - i * 250f - 180f,
                                    1f,
                                    Interpolation.pow5Out
                                )
                            )
                        }
                    })
                }
            )
            shotTypeGrid.add(
                GridButton(
                    bundle["ui.playerSelect.shotType.${shotTypes[i]}.description"],
                    24,
                    0,
                    i,
                    staticX = 100f,
                    staticY = baseY - i * 250f - 170f,
                    height = 150f,
                    triggerSound = null,
                ).apply {
                    setAlignment(Align.left)
                }
            )
        }
        shotTypeGrid.selectFirst()
    }

    private fun hideShotType() {
        shotType.clearActions()
        shotType.addAction(Actions.fadeOut(0.5f, Interpolation.pow5Out))
        input.removeProcessor(shotTypeGrid)

        descriptions.clearActions()
        descriptions.addAction(Actions.fadeIn(0.5f, Interpolation.pow5Out))
        input.addProcessor(descriptions)
        input.addProcessor(portraits)
    }

    override fun onQuit() {
        if (selectedPlayer == null) {
            super.onQuit()
            app.setScreen("difficultySelect", 1f)
        } else {
            SE.play("cancel")
            selectedPlayer = null
            hideShotType()
        }
    }
}