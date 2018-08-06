package com.rabbitmq.sample.core

import jersey.repackaged.com.google.common.collect.Maps
import java.util.*
import java.util.concurrent.locks.ReentrantLock

/**
 * @author DK
 * @since 2018-08-05
 */

class ThreadManager {

    var threadMap: MutableMap<String, Thread> = Maps.newConcurrentMap()

    private val lock = ReentrantLock()

    fun newThread(name: String, runnable: Runnable): Thread {

        if (Objects.nonNull(threadMap[name])) {
            return threadMap[name]!!
        }

        lock.lock()
        try {
            threadMap[name] = Thread(runnable, name)
        } finally {
            lock.unlock()
        }
        return threadMap[name]!!
    }
}