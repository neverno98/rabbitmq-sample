package com.rabbitmq.sample.core.service

import com.rabbitmq.sample.core.ThreadManager

/**
 * @author DK
 * @since 2018-08-05
 */

open class ThreadFactoryService : Service() {

    private val threadMgt = ThreadManager()

    fun newWorkerThread(name: String, runnable: Runnable): Thread {

        return threadMgt.newThread(name, runnable)
    }
}