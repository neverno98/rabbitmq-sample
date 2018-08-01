package com.rabbitmq.sample.pool

import com.rabbitmq.client.Channel
import com.rabbitmq.sample.type.MqConnType
import org.apache.commons.pool.BasePoolableObjectFactory

/**
 * @author DK
 * @since 2018-07-31
 */

class MqChannelFactory(private val mqConnType: MqConnType) : BasePoolableObjectFactory<Channel>() {

    @Throws(Exception::class)
    override fun makeObject(): Channel? {

        return MqConnFactory.getConnManager(mqConnType).connection?.createChannel()
    }
}