package com.rabbitmq.sample.base

import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import com.rabbitmq.sample.dto.MqPublisherDto
import com.rabbitmq.sample.type.MqDeliveryMode
import java.io.IOException

/**
 * @author DK
 * @since 2018-08-01
 */


class MqPublisher {

    fun sendMessage(mqConnManager: MqConnManager, mqPublisherDto: MqPublisherDto): String? {

        return mqConnManager.call(object : IChannelCallable<String> {

            override fun getDescrption(): String {
                return "sendMessage() $mqPublisherDto"
            }

            @Throws(IOException::class)
            override fun call(channel: Channel): String {

                MqBinder.declareQueue(mqPublisherDto.queue, channel, mqPublisherDto.queueArgs)
                val props = makePropertiesBuilder(mqPublisherDto)
                channel.basicPublish("", mqPublisherDto.queue, props, mqPublisherDto.message.toByteArray())
                return mqPublisherDto.messageId
            }
        })
    }

    fun makePropertiesBuilder(mqPublisherDto: MqPublisherDto): AMQP.BasicProperties {

        val propsBuilder = AMQP.BasicProperties.Builder()
        propsBuilder.messageId(mqPublisherDto.messageId)
        propsBuilder.deliveryMode(MqDeliveryMode.Persist.code)
        setPriority(propsBuilder, mqPublisherDto)
        return propsBuilder.build()
    }

    private fun setPriority(propsBuilder: AMQP.BasicProperties.Builder, mqPublisherDto: MqPublisherDto) {

        mqPublisherDto.queueArgs ?: return

        var mqPriority = 255 - mqPublisherDto.priority
        if (mqPriority < 0) {
            mqPriority = 0
        } else if (mqPriority > 255) {
            mqPriority = 255
        }
        propsBuilder.priority(mqPriority)
    }
}