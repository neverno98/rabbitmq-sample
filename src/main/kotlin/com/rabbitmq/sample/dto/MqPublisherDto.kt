package com.rabbitmq.sample.dto

/**
 * @author DK
 * @since 2018-07-31
 */


data class MqPublisherDto(

        private var queue: String = "",
        private var messageId: String = "",
        private var message: String = "",
        private var priority: Int = 0,
        private var queueArgs: Map<String, Any>? = null
) {
}