package com.rabbitmq.sample.base

import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import com.rabbitmq.client.DefaultConsumer
import com.rabbitmq.client.Envelope
import org.slf4j.LoggerFactory
import java.io.IOException

/**
 * @author DK
 * @since 2018-07-31
 */

class MqConsumer @JvmOverloads constructor(

        private var queue: String,
        private var handler: IMqListner,
        private var qos: Int = 1,
        private var autoAck: Boolean = true) {

    private val logger = LoggerFactory.getLogger("MqConsumer")

    @Volatile
    private var consumer: DefaultConsumer? = null

    val channel: Channel?
        get() = consumer?.channel

    @Throws(Exception::class)
    fun start(channel: Channel): String {

        consumer = null

        var consumerTag: String = ""
        try {

            consumer = object : DefaultConsumer(channel) {

                @Throws(IOException::class)
                override fun handleDelivery(consumerTag: String, envelope: Envelope, properties: AMQP.BasicProperties, body: ByteArray) {

                    handler.handleDelivery(channel, envelope, properties, body)
                }
            }

            channel.basicQos(qos)
            consumerTag = channel.basicConsume(queue, autoAck, consumer)
        } catch (e: Exception) {

            logger.error("start() Failed to start consuming queue=$queue", e)
            consumer = null
            consumerTag = ""
        }

        return consumerTag
    }

    fun stop() {

        val channel = channel ?: return

        try {
            channel.basicCancel(consumer!!.consumerTag)
        } catch (e: Exception) {
            logger.error("stop() Failed to cancel MqConsumer=$this", e)
        }

        try {
            channel.close()
        } catch (e: Exception) {
            logger.error("stop() Failed to close channel=$channel", e)
        } finally {
            consumer = null
        }
    }

    @Throws(Throwable::class)
    fun finalize() {

        stop()
    }

    override fun toString(): String {

        return StringBuffer(qos).append(queue).append(channel).append(consumer?.consumerTag ?: "").toString();
    }
}