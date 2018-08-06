package com.rabbitmq.sample.core.mq.split

import com.rabbitmq.sample.core.mq.MqWorker

/**
 * @author DK
 * @since 2018-08-05
 */


abstract class SplitMqWorker<T> : MqWorker<T>() {

    override fun createConsumer() {

        super.createConsumer()

        val mqSplit = mqService!!.mqSplitManager.mqSplit
        for (i in 1 until mqSplit) {

            val mqConsumer = mqService!!.mqSubscriber!!.mqConnManager.createMqConsumer("${mqService!!.name}-$i", this, fetchManager.fetchCount)
            consumerList.add(mqConsumer)
        }

    }
}