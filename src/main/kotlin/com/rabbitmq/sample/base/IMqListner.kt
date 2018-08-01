package com.rabbitmq.sample.base

import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Envelope

/**
 * @author DK
 * @since 2018-07-31
 */

interface IMqListner {

    fun handleDelivery(channel: Channel, envelope: Envelope, properties: AMQP.BasicProperties, body: ByteArray)
}