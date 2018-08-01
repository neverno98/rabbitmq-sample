package com.rabbitmq.sample.pool

import com.rabbitmq.client.Channel
import com.rabbitmq.sample.type.MqConnType
import org.apache.commons.pool.impl.GenericObjectPool

/**
 * @author DK
 * @since 2018-07-31
 */

class MqChannelPool(private val mqConnType: MqConnType) {

    companion object {

        private val DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS = -1L
        private val DEFAULT_MIN_IDLE = 0
        private val DEFAULT_MAX_WAIT = -1L
    }

    private var pool: GenericObjectPool<Channel> = GenericObjectPool(MqChannelFactory(this.mqConnType))

    init {

        val maxActive = 30
        pool.timeBetweenEvictionRunsMillis = DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS
        pool.maxActive = maxActive
        pool.maxIdle = maxActive
        pool.minIdle = maxActive
        pool.minIdle = DEFAULT_MIN_IDLE
        pool.maxWait = DEFAULT_MAX_WAIT
    }

    @Throws(Exception::class)
    fun getBorrowObject(): Channel {

        return pool.borrowObject()
    }

    @Throws(Exception::class)
    fun returnObject(channel: Channel) {

        pool.returnObject(channel)
    }
}