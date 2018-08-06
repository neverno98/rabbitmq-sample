package com.rabbitmq.sample.core.mq.dlx.split

import com.rabbitmq.sample.core.mq.dlx.DLXMqService

/**
 * @author DK
 * @since 2018-08-06
 */

abstract class DLXSplitMqService : DLXMqService() {

    override fun bindMq(name: String?) {

        super.bindMq(name)

        for (i in 1 until mqSplitManager.mqSplit) {
            mqSubscriber!!.bindQueue("$name-$i", queueArgs)
        }
    }
}