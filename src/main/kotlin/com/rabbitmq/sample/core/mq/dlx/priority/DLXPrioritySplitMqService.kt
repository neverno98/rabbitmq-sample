package com.rabbitmq.sample.core.mq.dlx.priority

/**
 * @author DK
 * @since 2018-08-06
 */
abstract class DLXPrioritySplitMqService : DLXPriorityMqService() {

    override fun bindMq(name: String?) {

        if (mqSplitManager.mqSplit > 0) {
            super.bindMq(name)

            for (i in 1 until mqSplitManager.mqSplit) {
                mqSubscriber!!.bindQueue("$name-$i", queueArgs)
            }
        }
    }
}