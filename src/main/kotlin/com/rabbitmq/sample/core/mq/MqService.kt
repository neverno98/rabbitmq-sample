package com.rabbitmq.sample.core.mq

import com.rabbitmq.sample.base.MqSubscriber
import com.rabbitmq.sample.core.service.ThreadService

/**
 * @author DK
 * @since 2018-08-05
 */

abstract class MqService : ThreadService() {

    var mqSplitManager = MqSplitManager()
    var queueArgs: Map<String, Any>? = null
    var mqSubscriber: MqSubscriber? = null
    var name: String? = null

    protected abstract fun initName()

    @Throws(Exception::class)
    override fun preStartService() {

        super.preStartService()

        initName()

        setPriorityMq()
        setSubscriber()

        bindMq(name)
    }

    open fun bindMq(name: String?) {

        mqSubscriber!!.bindQueue(name, queueArgs)
    }

    open fun setSubscriber() {

        mqSubscriber = MqSubscriber()
    }

    open fun setPriorityMq() {

    }
}