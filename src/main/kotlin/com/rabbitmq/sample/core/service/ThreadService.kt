package com.rabbitmq.sample.core.service

import org.slf4j.LoggerFactory

/**
 * @author DK
 * @since 2018-08-05
 */

open class ThreadService : Service(), Runnable {

    private var thread: Thread? = null

    private val logger = LoggerFactory.getLogger("ThreadService")

    override fun run() {

        runService()
    }
}