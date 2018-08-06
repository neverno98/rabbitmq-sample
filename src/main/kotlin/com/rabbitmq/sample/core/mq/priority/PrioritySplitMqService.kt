package com.rabbitmq.sample.core.mq.priority

/**
 * @author DK
 * @since 2018-08-06
 */

abstract class PrioritySplitMqService : PriorityMqService() {

    override fun bindMq(name: String?) {

        super.bindMq(name)

        for (i in 1 until mqSplitManager.mqSplit) {
            mqSubscriber!!.bindQueue("$name-$i", queueArgs)
        }
    }
}