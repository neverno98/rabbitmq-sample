package com.rabbitmq.sample.core.mq

/**
 * @author DK
 * @since 2018-08-05
 */

class MqFetchManager {

    var fetchCount = 30

    @Throws(Exception::class)
    fun initializeWithConfiguration(fetchCount: Int) {

        if(fetchCount > 0) {
            this.fetchCount = fetchCount
        }
    }
}