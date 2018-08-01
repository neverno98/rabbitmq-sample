package com.rabbitmq.sample.dto

/**
 * @author DK
 * @since 2018-07-31
 */

data class MqConfDto(

        var url1: String = "",
        var url2: String = "",
        var url3: String = "",
        var port1: Long = 5672,
        var port2: Long = 5672,
        var port3: Long = 5672,
        var virtualHost: String = "",
        var userName: String = "",
        var password: String = "",
        var recoveryInternal: Long = 3000,
        var connectionTimeout: Long = 1000) {

}