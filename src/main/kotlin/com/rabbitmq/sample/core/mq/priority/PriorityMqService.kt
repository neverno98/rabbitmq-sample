package com.rabbitmq.sample.core.mq.priority

import com.rabbitmq.sample.core.mq.MqService
import java.util.*

/**
 * @author DK
 * @since 2018-08-05
 */

abstract class PriorityMqService : MqService() {

    override fun setPriorityMq() {

        queueArgs = HashMap()
        queueArgs!!.put("x-max-priority", 255)
    }
}