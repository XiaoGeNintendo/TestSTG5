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

package com.hhs.koto.stg.task

import com.hhs.koto.util.KotoRuntimeException
import ktx.collections.GdxArray

class SequenceTask(vararg task: Task) : Task {
    val tasks = GdxArray<Task>(8)
    private var currentIndex: Int = 0
    override var alive: Boolean = true

    init {
        task.forEach { tasks.add(it) }
        tasks.shrink()
    }

    override fun tick() {
        while (currentIndex < tasks.size) {
            if (!tasks[currentIndex].alive) {
                tasks[currentIndex] = null
                currentIndex++
                continue
            }
            tasks[currentIndex].tick()
            if (!tasks[currentIndex].alive) {
                tasks[currentIndex] = null
                currentIndex++
            } else {
                break
            }
        }
        if (currentIndex >= tasks.size) {
            alive = false
            return
        }
    }

    override fun kill(): Boolean {
        tasks.forEach {
            if (it.alive) it.kill()
        }
        tasks.clear()
        alive = false
        return true
    }

    fun addTask(vararg task: Task) {
        if (!alive) {
            throw KotoRuntimeException("Cannot add task to a completed SequenceTask!")
        }
        task.forEach {
            tasks.add(it)
        }
    }

    fun addTask(task: Task) {
        if (!alive) {
            throw KotoRuntimeException("Cannot add task to a completed SequenceTask!")
        }
        tasks.add(task)
    }

    operator fun plusAssign(task: Task) = addTask(task)

    operator fun plus(other: SequenceTask): SequenceTask {
        val tmp = SequenceTask()
        tasks.forEach { tmp += it }
        other.tasks.forEach { tmp += it }
        return tmp
    }
}