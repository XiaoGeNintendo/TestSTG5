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

package com.hhs.koto.stg.bullet

import com.badlogic.gdx.graphics.Color
import com.hhs.koto.stg.graphics.Enemy
import com.hhs.koto.util.game
import kotlin.math.roundToLong

open class PlayerBullet(
    x: Float,
    y: Float,
    var damage: Float,
    var penetration: Int = 1,
    speed: Float = 0f,
    angle: Float = 0f,
    data: BulletData,
    scaleX: Float = 1f,
    scaleY: Float = 1f,
    rotation: Float = 0f,
    color: Color = Color.WHITE,
    delay: Int = 0,
) : BasicBullet(x, y, speed, angle, data, scaleX, scaleY, rotation = rotation, tint = color,delay = delay) {
    open fun hit(enemy: Enemy) {
        enemy.onHit(damage)
        game.score += (damage * 10).roundToLong()
        penetration--
        if (penetration <= 0) {
            kill()
        }
    }
}