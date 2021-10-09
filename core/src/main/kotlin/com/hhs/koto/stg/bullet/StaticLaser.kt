package com.hhs.koto.stg.bullet

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Polygon
import com.hhs.koto.stg.CollisionShape
import com.hhs.koto.stg.NoCollision
import com.hhs.koto.stg.SLCollision
import com.hhs.koto.stg.particle.GrazeParticle
import com.hhs.koto.util.*
import ktx.math.minus
import ktx.math.plus
import ktx.math.vec2

/**
 * Static Laser: laser that has only 1 rectangle collision
 *
 * @author XGN
 */
class StaticLaser(
    override var x: Float,
    override var y: Float,
    var length: Float,
    var laserWidth: Float,
    override var delay: Int,
    data: BulletData,
    var headHit: Float = 0.6f,
    var widthHit: Float = 0.7f,
    /**
     * 0 = become thicker (luaSTG style)
     *
     * 1 = show danger line (danmakufu style)
     */
    var style: Int = 0
) : BasicBullet(x, y, data = data, destroyable = false) {

    override val collision: CollisionShape
        get() = if (t >= delay) SLCollision(angle, length, laserWidth, headHit, widthHit) else NoCollision()

    override fun onGraze() {
        if(t%5==0){
            game.graze++
            game.pointValue = (game.pointValue + 1L).coerceAtMost(game.maxPointValue)
            SE.play("graze")
            game.addParticle(GrazeParticle(x, y))
        }
    }

    override fun draw(batch: Batch, parentAlpha: Float, subFrameTime: Float) {
        val texture = data.texture.getFrame(t)
        tmpColor.set(batch.color)
        val bulletColor = data.color.tintHSV(tint)
        bulletColor.a *= parentAlpha
        batch.color = bulletColor

        if (t >= delay || style == 0) {

            val scaleX = length / data.width
            val scaleY = smoothstep(0.1f, laserWidth / data.height, t * 1f / delay)
            batch.draw(
                texture,
                x - data.originX + length * cos(angle) / 2,
                y - data.originY + length * sin(angle) / 2,
                data.originX,
                data.originY,
                data.width,
                data.height,
                scaleX,
                scaleY,
                angle,
            )
        } else {
            game.drawer.line(
                x,
                y,
                x + smoothstep(0f, length, t * 1f / max(1f, delay - 20f)) * cos(angle),
                y + smoothstep(0f, length, t * 1f / max(1f, delay - 20f)) * sin(angle)
            )
        }

//
//
//        //debug show hitbox
//
//        run {
//            val vlt = vec2(length * cos(angle), length * sin(angle))
//            val vlr = vlt.cpy().setLength(vlt.len() * (1 - headHit) / 2)
//            val vlro = vlt.cpy().rotate90(1).setLength(laserWidth * widthHit / 2)
//            val xx = x + cos(angle) * length
//            val yy = y + sin(angle) * length
//            val v1 = vec2(x, y) + vlr + vlro
//            val v2 = vec2(x, y) + vlr - vlro
//            val v3 = vec2(xx, yy) - vlr - vlro
//            val v4 = vec2(xx, yy) - vlr + vlro
//            game.drawer.polygon(Polygon(floatArrayOf(v1.x, v1.y, v2.x, v2.y, v3.x, v3.y, v4.x, v4.y)))
//        }
    }
}