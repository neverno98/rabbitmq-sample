package com.rabbitmq.sample.core.worker

import com.rabbitmq.sample.core.service.ThreadFactoryService
import org.slf4j.LoggerFactory

/**
 * @author DK
 * @since 2018-08-05
 */

class ThreadWorker : Worker(), Runnable, Thread.UncaughtExceptionHandler {

    private val logger = LoggerFactory.getLogger("ThreadWorker")

    private var thread: Thread? = null

    @Throws(Exception::class)
    override fun start() {

        super.start()

        val aes = service as ThreadFactoryService
        thread = aes.newWorkerThread(workerId!!, this)
        thread!!.start()
    }

    override fun run() {

        runWorker()
    }

    override fun uncaughtException(t: Thread, e: Throwable) {

        logger.error("uncaughtException() name={}", t.name, e)
    }
}