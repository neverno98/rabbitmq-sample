package com.rabbitmq.sample.base

import com.rabbitmq.client.Channel
import com.rabbitmq.sample.type.MqConnType
import java.io.IOException

/**
 * @author DK
 * @since 2018-08-01
 */

open class MqSubscriber @JvmOverloads constructor(mqConnType: MqConnType = MqConnType.Type1) {

    companion object {

        private val DEFAULT_EXPRIER_TIME: Long = 6000
    }

    var mqConnManager: MqConnManager = MqConnManager(mqConnType)
    var expireTime = DEFAULT_EXPRIER_TIME

    init {

        mqConnManager.connect()
    }

    fun bindQueue(queue: String, queueArgs: Map<String, Any>): Boolean? {

        return bindQueue(queue, queue, queueArgs)
    }

    open fun bindQueue(queue: String, deadQueue: String, queueArgs: Map<String, Any>): Boolean? {

        return mqConnManager.call(object : IChannelCallable<Boolean> {

            override fun getDescrption(): String {
                return "getDescription() bindQueue() queue=$queue"
            }

            @Throws(IOException::class)
            override fun call(channel: Channel): Boolean {

                MqBinder.declareQueue(queue, channel, queueArgs)
                return true
            }
        })
    }
}