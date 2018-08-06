package com.rabbitmq.sample.core.service

import org.slf4j.LoggerFactory

/**
 * @author DK
 * @since 2018-08-05
 */

open class Service {

    private val logger = LoggerFactory.getLogger("Service")

    var serviceId: String? = null

    @Throws(Exception::class)
    open fun initialize() {

    }


    @Throws(Exception::class)
    open fun start() {

        initialize()

        preStartService()
        startService()
        postStartService()
    }


    @Throws(Exception::class)
    open fun preStartService() {

    }

    @Throws(Exception::class)
    open fun startService() {

    }

    @Throws(Exception::class)
    open fun postStartService() {

    }

    open fun runService() {

        try {
            preRun()
            processRun()
        } catch (e: Exception) {
            logger.error("runService() Exception running worker ", e)
        } catch (t: Throwable) {
            logger.error("runService() Throwable during running thread.", t)
        } finally {
            postRun()
        }
    }

    open fun preRun() {

    }

    open fun postRun() {

    }

    open fun processRun() {

    }
}