package com.rabbitmq.sample.base

import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import java.io.IOException

/**
 * @author DK
 * @since 2018-07-31
 */
object MqBinder {

    @Throws(IOException::class)
    fun declareExchange(exchange: String, type: String, channel: Channel): AMQP.Exchange.DeclareOk {

        return channel.exchangeDeclare(exchange, type, true)
    }

    @Throws(IOException::class)
    fun declareQueue(queue: String, channel: Channel, queueArgs: Map<String, Any>): AMQP.Queue.DeclareOk {

        val durable = true
        val exclusive = false
        val autoDelete = false
        return channel.queueDeclare(queue, durable, exclusive, autoDelete, queueArgs)
    }

    @Throws(IOException::class)
    fun bindQueue(queue: String, exchange: String, routingKey: String, channel: Channel): AMQP.Queue.BindOk {

        return channel.queueBind(queue, exchange, routingKey)
    }
}