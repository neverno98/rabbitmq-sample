package com.rabbitmq.sample.type

/**
 * @author DK
 * @since 2018-07-31
 */

enum class MqDeliveryMode(val code: Int) {

    NonPersist(1), Persist(2)
}