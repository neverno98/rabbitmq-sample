package com.rabbitmq.sample.publisher

import com.fasterxml.jackson.core.JsonProcessingException
import com.rabbitmq.client.Channel
import com.rabbitmq.sample.base.MqConnManager
import com.rabbitmq.sample.base.MqPublisher
import com.rabbitmq.sample.base.ObjectMapperWrapper
import com.rabbitmq.sample.dto.MqPublisherDto
import com.rabbitmq.sample.pool.MqConnFactory
import com.rabbitmq.sample.type.MqConnType
import org.slf4j.LoggerFactory
import java.util.*

/**
 * @author DK
 * @since 2018-08-01
 */


open class MqGenericPublisher {

    companion object {

        protected val INSERT_MQ_TIME = 300L

        protected val mapper = ObjectMapperWrapper.mapper;

        @Throws(JsonProcessingException::class)
        fun makeJson(value: Any): String {

            return mapper.writeValueAsString(value)
        }

    }

    private val logger = LoggerFactory.getLogger("MqGenericPublisher")

    protected val mqPublisher = MqPublisher()
    protected var queueArgs: MutableMap<String, Any>? = null

    var mqConnType: MqConnType = MqConnType.Type1

    @Throws(Exception::class)
    protected fun getConnection(): MqConnManager {

        return MqConnFactory.getConnManager(mqConnType)
    }


    @Throws(Exception::class)
    @JvmOverloads
    fun publish(qName: String, message: String, priority: Int = 0): Boolean {

        try {

            val mqPublisherDto = MqPublisherDto()
            mqPublisherDto.queue = qName
            mqPublisherDto.message = message
            mqPublisherDto.messageId = UUID.randomUUID().toString()
            mqPublisherDto.queueArgs = queueArgs


            val mqConnection = getConnection()
            mqConnection.connection ?: return false

            mqPublisher.sendMessage(mqConnection, mqPublisherDto)
        } catch (ex: Exception) {

            logger.error("publish() Exception - ", ex)
            return false
        }
        return true
    }

    @Throws(Exception::class)
    fun publish(channel: Channel, qName: String, message: String, priority: Int): Boolean {

        try {

            val mqPublisherDto = MqPublisherDto()
            mqPublisherDto.queue = qName
            mqPublisherDto.message = message
            mqPublisherDto.messageId = UUID.randomUUID().toString()
            mqPublisherDto.queueArgs = queueArgs

            val props = mqPublisher.makePropertiesBuilder(mqPublisherDto)
            channel.basicPublish("", qName, props, message.toByteArray())
        } catch (ex: Exception) {

            logger.error("publish() Exception - ", ex)
            return false
        }
        return true
    }

    protected fun setPriorityMq() {

        val args = HashMap<String, Any>()
        args["x-max-priority"] = 255
        if (this.queueArgs == null) {
            this.queueArgs = args
        } else {
            this.queueArgs!!.putAll(args)
        }
    }

    protected fun setDLXMq(mqName: String) {

        val args = HashMap<String, Any>()
        args["x-dead-letter-exchange"] = "$mqName.dead"
        if (this.queueArgs == null) {
            this.queueArgs = args
        } else {
            this.queueArgs!!.putAll(args)
        }
    }

    protected fun makeSplitMqName(queueName: String, divideNumber: Int): String {

        val qName = StringBuilder(queueName)
        val index = (System.currentTimeMillis() % divideNumber).toInt()
        if (index > 0) {
            qName.append("-").append(index)
        }
        return qName.toString()
    }
}