package com.rabbitmq.sample.publisher

import org.slf4j.LoggerFactory

/**
 * @author DK
 * @since 2018-08-01
 */
class SimpleMqPublisher(private val queueName: String) : MqGenericPublisher() {

    private val logger = LoggerFactory.getLogger("SimpleMqPublisher")

    fun insert(message: String): Boolean {

        try {
            publish(queueName, message)
        } catch (e: Exception) {
            logger.error("insert() Exception occurred by $queueName - ", e)
            return false
        }
        return true
    }
}