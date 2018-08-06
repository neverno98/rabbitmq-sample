package com.rabbitmq.sample.core.mq

import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Envelope
import com.rabbitmq.sample.base.IMqListner
import com.rabbitmq.sample.base.MqConsumer
import com.rabbitmq.sample.base.MqMessage
import com.rabbitmq.sample.base.ObjectMapperWrapper
import com.rabbitmq.sample.core.worker.Worker
import com.rabbitmq.sample.type.MqConnType
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock

/**
 * @author DK
 * @since 2018-08-05
 */

abstract class MqWorker<T> : Worker(), IMqListner {

    companion object {

        protected var mqTask = AtomicInteger(0)
    }

    var mapper = ObjectMapperWrapper.mapper
    var consumerList: MutableList<MqConsumer> = ArrayList()
    var mqConnType = MqConnType.Type1
    var fetchManager = MqFetchManager()
    var mqService: MqService? = null

    private val lock = ReentrantLock()

    private val logger = LoggerFactory.getLogger("MqWorker")

    @Throws(Exception::class)
    override fun start() {

        super.start()
        createConsumer()
    }

    open fun createConsumer() {

        val mqConsumer = mqService!!.mqSubscriber!!.mqConnManager.createMqConsumer(mqService!!.name!!, this, fetchManager.fetchCount)
        consumerList.add(mqConsumer)
        mqConnType = mqService!!.mqSubscriber!!.mqConnType
    }

    override fun handleDelivery(channel: Channel, envelope: Envelope, properties: AMQP.BasicProperties, body: ByteArray) {

        val mqMessage = MqMessage(mqConnType, channel, envelope, properties, body)

        try {

            val before: Int
            lock.lock()
            try {
                before = mqTask.incrementAndGet()
            } finally {
                lock.unlock()
            }

            consumeTaskQ(mqMessage)
        } catch (ex: Exception) {
            logger.error("handleDelivery() mqMessage={}, {}", mqMessage)
        } finally {

            val after: Int
            lock.lock()
            try {
                after = mqTask.decrementAndGet()
            } finally {
                lock.unlock()
            }
        }
    }

    open fun consumeTaskQ(mqMessage: MqMessage) {

        var json: String? = null
        try {

            json = mqMessage.decodeMessage()
            if (json == null || mqMessage.isSendToRedelivery()) {
                mqMessage.redeliveryAck(json)
            } else {
                consumeTaskQ(json, mqMessage)
            }
        } catch (ex: Exception) {
            mqMessage.redeliveryAck(json!!)
            logger.error("consumeTaskQ() Exception - ", ex)
        }
    }

    open fun consumeTaskQ(json: String?, mqMessage: MqMessage) {

        val t = convertFromJson(json, mqMessage) ?: return
        try {
            consumeTaskQ(mqMessage, t)
            mqMessage.basicAck()
        } catch (ex: Exception) {
            logger.error("consumeTaskQ() Exception - ", ex)
            mqMessage.basicNack()
        }
    }

    @Throws(Exception::class)
    open fun consumeTaskQ(mqMessage: MqMessage, t: T) {

    }

    @Throws(Exception::class)
    abstract fun convertFromJson(json: String?): T

    fun convertFromJson(json: String?, mqMessage: MqMessage): T? {

        try {
            return convertFromJson(json)
        } catch (ex: Exception) {
            mqMessage.basicAck()
        }
        return null
    }
}