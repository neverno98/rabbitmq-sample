package com.rabbitmq.sample.core.mq.dlx

import com.rabbitmq.sample.core.mq.MqService
import com.rabbitmq.sample.dlx.DLXMqSubscriber

/**
 * @author DK
 * @since 2018-08-06
 */

abstract class DLXMqService : MqService() {

    override fun bindMq(name: String?) {

        mqSubscriber!!.bindQueue(name, queueArgs)
    }

    override fun setSubscriber() {

        mqSubscriber = DLXMqSubscriber()
    }
}
