package com.rabbitmq.sample.core.mq.dlx

import com.rabbitmq.sample.base.MqMessage
import com.rabbitmq.sample.core.mq.MqWorker
import org.slf4j.LoggerFactory

/**
 * @author DK
 * @since 2018-08-06
 */


abstract class DLXMqWorker<T> : MqWorker<T>() {

    companion object {

        private val DEFAULT_RETRY: Long = 5
    }

    var retry = DEFAULT_RETRY

    private val logger = LoggerFactory.getLogger("DLXMqWorker")

    override fun createConsumer() {

        val mqConsumer = mqService!!.mqSubscriber!!.mqConnManager.createMqConsumer(mqService!!.name!!, this, fetchManager.fetchCount, false)
        consumerList.add(mqConsumer)
        mqConnType = mqService!!.mqSubscriber!!.mqConnType
    }

    override fun consumeTaskQ(json: String?, mqMessage: MqMessage) {

        val t = convertFromJson(json, mqMessage) ?: return
        try {
            consumeTaskQ(mqMessage, t)
            mqMessage.absoluteAck(false)
            logger.info("consumeTaskQ() ")
        } catch (ex: Exception) {
            val retry = mqMessage.basicReject(retry)
            if (retry) {
                logger.warn("consumeTaskQ() Exception - ", ex)
            } else {
                logger.error("consumeTaskQ() Exception - ", ex)
            }
        }
    }

}