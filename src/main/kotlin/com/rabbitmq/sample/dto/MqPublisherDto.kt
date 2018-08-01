package com.rabbitmq.sample.dto

/**
 * @author DK
 * @since 2018-07-31
 */


data class MqPublisherDto(

        var queue: String = "",
        var messageId: String = "",
        var message: String = "",
        var priority: Int = 0,
        var queueArgs: Map<String, Any>? = null
) {
}