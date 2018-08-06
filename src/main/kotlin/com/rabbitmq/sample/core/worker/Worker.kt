package com.rabbitmq.sample.core.worker

import com.rabbitmq.sample.core.service.Service
import org.slf4j.LoggerFactory

/**
 * @author DK
 * @since 2018-08-05
 */


open class Worker {

    private val logger = LoggerFactory.getLogger("Worker")

    var service: Service? = null
    var workerId: String? = null

    @Throws(Exception::class)
    open fun initialize(service: Service, id: String) {

        this.service = service
        this.workerId = id
    }

    @Throws(Exception::class)
    open fun initialize() {

    }

    @Throws(Exception::class)
    open fun start() {

        initialize()
    }

    open fun runWorker() {

        try {
            preRun()
            processRun()
        } catch (e: Exception) {
            logger.error("runWorker() Exception running worker ", e)
        } catch (t: Throwable) {
            logger.error("runWorker() Throwable during running thread.", t)
        } finally {
            postRun()
        }
    }

    open fun preRun() {

    }

    open fun processRun() {

        val t = Thread.currentThread()
        while (!t.isInterrupted && !stopped()) {

            try {
                consumeTask()
            } catch (e: Exception) {
                logger.error("processRun() exception", e)
            }
        }
        logger.debug("processRun() end {}", workerId)
    }

    @Throws(Exception::class)
    open fun consumeTask() {

    }

    open fun postRun() {

    }

    open fun stopped(): Boolean {
        return false
    }
}