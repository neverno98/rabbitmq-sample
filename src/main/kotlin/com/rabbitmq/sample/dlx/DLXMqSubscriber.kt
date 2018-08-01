package com.rabbitmq.sample.dlx

import com.rabbitmq.client.Channel
import com.rabbitmq.sample.base.IChannelCallable
import com.rabbitmq.sample.base.MqBinder
import com.rabbitmq.sample.base.MqSubscriber
import com.rabbitmq.sample.type.MqConnType
import java.io.IOException
import java.util.*

/**
 * @author DK
 * @since 2018-08-01
 */

class DLXMqSubscriber @JvmOverloads constructor(mqConnType: MqConnType = MqConnType.Type1) : MqSubscriber(mqConnType) {

    companion object {

        private val DLX_EXCHANGE = ".dead"
        private val DLX_TTL_EXCHANGE = ".dead-ttl"

        private val TYPE = "direct"
    }

    override fun bindQueue(queue: String, deadQueue: String, queueArgs: Map<String, Any>): Boolean? {

        return mqConnManager.call(object : IChannelCallable<Boolean> {

            override fun getDescrption(): String {
                return "getDescription() bindQueue() queue=$queue"
            }

            @Throws(IOException::class)
            override fun call(channel: Channel): Boolean {

                MqBinder.declareExchange(queue, TYPE, channel)
                MqBinder.declareExchange(deadQueue + DLX_EXCHANGE, TYPE, channel)
                MqBinder.declareExchange(deadQueue + DLX_TTL_EXCHANGE, TYPE, channel)

                MqBinder.declareQueue(queue, channel, dlxArgs(deadQueue, queueArgs))
                MqBinder.declareQueue(deadQueue + DLX_TTL_EXCHANGE, channel, dlxTtlArgs(deadQueue, queueArgs))

                MqBinder.bindQueue(queue, queue, queue, channel)
                MqBinder.bindQueue(deadQueue + DLX_TTL_EXCHANGE, deadQueue + DLX_EXCHANGE, queue, channel)

                MqBinder.bindQueue(queue, deadQueue + DLX_TTL_EXCHANGE, queue, channel)

                return true
            }
        })
    }

    private fun dlxArgs(queue: String, queueArgs: Map<String, Any>?): Map<String, Any> {

        val args = HashMap<String, Any>()
        args["x-dead-letter-exchange"] = queue + DLX_EXCHANGE
        if (queueArgs != null) {
            args.putAll(queueArgs)
        }
        return args
    }

    private fun dlxTtlArgs(queue: String, queueArgs: Map<String, Any>?): Map<String, Any> {

        val args = HashMap<String, Any>()

        args["x-dead-letter-exchange"] = queue + DLX_TTL_EXCHANGE
        args["x-message-ttl"] = expireTime
        if (queueArgs != null) {
            args.putAll(queueArgs)
        }
        return args
    }
}