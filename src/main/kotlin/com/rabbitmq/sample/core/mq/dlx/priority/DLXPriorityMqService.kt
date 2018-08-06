package com.rabbitmq.sample.core.mq.dlx.priority

import com.rabbitmq.sample.core.mq.dlx.DLXMqService
import java.util.*

/**
 * @author DK
 * @since 2018-08-06
 */
abstract class DLXPriorityMqService : DLXMqService() {

    override fun setPriorityMq() {

        queueArgs = HashMap()
        queueArgs!!.put("x-max-priority", 255)
    }
}