package com.rabbitmq.sample.pool

import com.rabbitmq.sample.base.MqConnManager
import com.rabbitmq.sample.type.MqConnType

/**
 * @author DK
 * @since 2018-07-31
 */

object MqConnFactory {

    private val type1Manager = MqConnManager(MqConnType.Type1)
    private val type2Manager = MqConnManager(MqConnType.Type2)

    init {

        type1Manager.connect()
        type2Manager.connect()
    }

    fun getConnManager(mqConnType: MqConnType): MqConnManager {

        return if (mqConnType == MqConnType.Type1) {
            type1Manager
        } else {
            type2Manager
        }
    }
}