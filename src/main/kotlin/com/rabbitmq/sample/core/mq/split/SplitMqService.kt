package com.rabbitmq.sample.core.mq.split

import com.rabbitmq.sample.core.mq.MqService

/**
 * @author DK
 * @since 2018-08-05
 */
abstract class SplitMqService : MqService() {

    override fun bindMq(name: String?) {

        super.bindMq(name)

        for (i in 1 until mqSplitManager.mqSplit) {
            mqSubscriber!!.bindQueue(StringBuilder(name).append("-").append(i).toString(), queueArgs)
        }
    }
}