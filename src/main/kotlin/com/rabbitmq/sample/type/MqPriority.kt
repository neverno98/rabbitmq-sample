package com.rabbitmq.sample.type

/**
 * @author DK
 * @since 2018-07-31
 */

enum class MqPriority (val code: Int) {

    LEVEL_10(10), LEVEL_20(20), LEVEL_30(30), LEVEL_50(50),
    LEVEL_60(60), LEVEL_70(70), LEVEL_80(80), LEVEL_90(90)
}