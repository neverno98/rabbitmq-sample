package com.rabbitmq.sample.core.mq.dlx.split

import com.rabbitmq.sample.core.mq.dlx.DLXMqWorker

/**
 * @author DK
 * @since 2018-08-06
 */


abstract class DLXSplitMqWorker<T> : DLXMqWorker<T>() {

    override fun createConsumer() {

        if (mqService!!.mqSplitManager.mqSplit <= 0) {
            return
        }

        super.createConsumer()

        for (i in 1 until mqService!!.mqSplitManager.mqSplit) {

            val mqConsumer = mqService!!.mqSubscriber!!.mqConnManager.createMqConsumer("{$mqService!!.name}-$i", this, fetchManager.fetchCount, false)
            consumerList.add(mqConsumer)
        }
    }
}