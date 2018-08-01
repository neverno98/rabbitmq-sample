package com.rabbitmq.sample.base

import com.rabbitmq.client.Channel

/**
 * @author DK
 * @since 2018-07-31
 */

interface IChannelCallable<T> {

    val description: String

    @Throws(Exception::class)
    fun call(channel: Channel): T
}