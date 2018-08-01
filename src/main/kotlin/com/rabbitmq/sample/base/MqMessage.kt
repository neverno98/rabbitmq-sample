package com.rabbitmq.sample.base

import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Envelope
import com.rabbitmq.sample.type.MqConnType
import org.slf4j.LoggerFactory
import java.util.*

/**
 * @author DK
 * @since 2018-07-31
 */

class MqMessage(

        private val mqConnType: MqConnType = MqConnType.Type1,
        private val channel: Channel,
        private val envelope: Envelope,
        private val properties: AMQP.BasicProperties,
        private val body: ByteArray) {

    companion object {

        private val EXPIRE_TIME = 10 * 60
    }

    private val logger = LoggerFactory.getLogger("MqMessage")
    private val autoAck: Boolean = true

    @Throws(Exception::class)
    fun decodeMessage(): String {

        var message: String = ""
        try {
            message = String(body)
        } catch (ex: Exception) {

            logger.error("decodeMessage() Exception - ", ex)
            basicAck()
            message = ""
        }
        return message
    }

    fun basicNack() {

        if (!autoAck) {

            try {
                channel.basicNack(envelope.deliveryTag, false, true)
            } catch (ex: Exception) {
                logger.error("basicNack() Exception - ", ex)
            }
        }
    }

    fun basicReject(retry: Long): Boolean {

        try {

            if (properties.headers != null && properties.headers.containsKey("x-death")) {

                val deadLetterData = properties.headers["x-death"] as ArrayList<HashMap<String, Any>>
                if (deadLetterData[0]["count"] as Long >= retry) {
                    absoluteAck(false)
                    return false
                }
            }
            channel.basicReject(envelope.deliveryTag, false)
        } catch (ex: Exception) {
            logger.error("basicReject() Exception - ", ex)
        }
        return true
    }

    @JvmOverloads
    fun basicAck(redelivery: Boolean = false) {

        if (!autoAck) {
            absoluteAck(redelivery)
        }
    }

    fun absoluteAck(redelivery: Boolean) {

        try {
            channel.basicAck(envelope.deliveryTag, false)
        } catch (ex: Exception) {
            logger.error("basicAck() Exception - ", ex)
        }
    }

    fun redeliveryAck(json: String) {

        try {
            basicAck()
        } catch (e: Exception) {
            logger.warn("redeliveryAck() catch Exception - ", e)
        }
    }

    fun isSendToRedelivery(): Boolean {

        return false
    }
}