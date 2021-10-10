package com.hhs.koto.stg.pattern

import com.hhs.koto.stg.Movable
import com.hhs.koto.stg.particle.CastParticle
import com.hhs.koto.stg.task.self
import com.hhs.koto.stg.task.waitForFinish
import com.hhs.koto.util.SE
import com.hhs.koto.util.game
import com.hhs.koto.util.random
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.yield

/**
 * declare a cast animation. This does not need cast posture to be on to use (though it is better on)
 */
suspend fun CoroutineScope.cast(x: Float, y: Float, intensity:Int = 10, length: Int = 10){
    val task=object: TemporalPattern(length){
        override fun action() {
            repeat(intensity){
                val ax=random(-1000f,1000f)
                val ay=random(-1000f,1000f)
                game.addParticle(CastParticle(ax,ay,x,y))
            }
        }
    }
    SE.play("charge")
    self.attachTask(task)
    task.waitForFinish()
}
