package ray.util.tick.manager

import ray.util.tick.api.ITickManager
import ray.util.tick.api.ITickWorker
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.withLock
import kotlin.concurrent.write

class ConcurrentTickManagerKt(private val tickRate: Long = 1000, private val delay: Long = 0) : ITickManager {

    private var currentTick = 0L
    private var tps = 0L
    private var initFlag = false
    private val timer = Timer()
    private val lock = ReentrantReadWriteLock()
    private val tickWorkers = Collections.synchronizedList(LinkedList<ITickWorker>())

    override fun add(worker: ITickWorker) {
        tickWorkers.add(worker)
    }

    override fun remove(worker: ITickWorker): Boolean {
        return tickWorkers.remove(worker)
    }

    override fun contains(worker: ITickWorker): Boolean {
        return tickWorkers.contains(worker)
    }

    override fun isEmpty(): Boolean {
        return tickWorkers.isEmpty()
    }

    override fun getCurrentTick() = lock.read { currentTick }

    override fun getTickRate() = lock.read { tickRate }

    override fun getTps() = lock.read { tps }

    override fun start() {
        timer.schedule(TickHandler(this, 0), delay)
        timer.scheduleAtFixedRate(object : TimerTask() {
            private var oldTick = 0L

            override fun run() {
                if (initFlag) {
                    oldTick = 0L
                }
                lock.write {
                    tps = if (currentTick < oldTick) {
                        (Long.MAX_VALUE - currentTick) + oldTick
                    } else {
                        currentTick - oldTick
                    }
                    oldTick = currentTick
                }
            }
        }, 1000, 1000)
        initFlag = false
    }

    override fun stop() {
        timer.cancel()
        tps = 0
        initFlag = true
    }

    private class TickHandler(val manager: ConcurrentTickManagerKt, val currentTick: Long) : TimerTask() {
        val lock = ReentrantLock()

        override fun run() {
            lock.withLock {
                manager.currentTick = currentTick
                manager.tickWorkers.forEach {
                    it.onTick()
                }
                val execTime = System.currentTimeMillis() - scheduledExecutionTime()
                manager.timer.schedule(
                        TickHandler(manager, if (currentTick == Long.MAX_VALUE) 0 else currentTick + 1),
                        manager.tickRate - execTime + manager.delay)
            }
        }
    }
}