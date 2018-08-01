package com.rabbitmq.sample.base

import com.rabbitmq.client.*
import com.rabbitmq.sample.dto.MqConfDto
import com.rabbitmq.sample.pool.MqChannelPool
import com.rabbitmq.sample.type.MqConnType
import org.slf4j.LoggerFactory
import java.util.*
import java.util.Collections.synchronizedSet
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * @author DK
 * @since 2018-07-31
 */


class MqConnManager @JvmOverloads constructor(mqConnType: MqConnType = MqConnType.Type1) : ShutdownListener {

    private val logger = LoggerFactory.getLogger("MqConnManager")

    protected val executor = Executors.newSingleThreadScheduledExecutor()

    protected val connFactory = ConnectionFactory()
    protected val mqConsumers: MutableSet<MqConsumer> = synchronizedSet<MqConsumer>(HashSet<MqConsumer>())
    protected val mqConfDto = MqConfDto()

    var mqConnType = MqConnType.Type1

    protected var mqChannelPool: MqChannelPool? = null

    @Volatile
    var connection: Connection? = null

    init {

        init(mqConnType)
        this.mqConnType = mqConnType
    }

    private fun init(mqConnType: MqConnType) {

        connFactory.username = mqConfDto.userName
        connFactory.password = mqConfDto.password
        connFactory.virtualHost = mqConfDto.virtualHost
        connFactory.isAutomaticRecoveryEnabled = true
        connFactory.networkRecoveryInterval = mqConfDto.recoveryInternal
        connFactory.connectionTimeout = mqConfDto.connectionTimeout.toInt()
    }

    private fun getAddresses(): Array<Address> {

        return arrayOf(Address(mqConfDto.url1, mqConfDto.port1.toInt()), Address(mqConfDto.url2, mqConfDto.port2.toInt()), Address(mqConfDto.url3, mqConfDto.port3.toInt()))
    }


    fun connect() {

        try {

            connection = connFactory.newConnection(getAddresses())
            connection!!.addShutdownListener(this)

            restart()
            mqChannelPool = MqChannelPool(mqConnType)

        } catch (e: Exception) {

            logger.warn("connect() Exception - ", e)
            asyncWaitAndReconnect()
        }
    }

    override fun shutdownCompleted(cause: ShutdownSignalException) {

        // 예기치 않은 문제가 발생할 때만 재연결한다.
        if (!cause.isInitiatedByApplication) {

            logger.warn("shutdownCompleted() Lost connection to {$connFactory.host}:{$connFactory.port}", cause)
            connection = null
            asyncWaitAndReconnect()
        }
    }

    private fun asyncWaitAndReconnect() {

        executor.schedule({ connect() }, mqConfDto.recoveryInternal, TimeUnit.MILLISECONDS)
    }

    fun stop() {

        executor.shutdownNow()

        try {
            connection?.close() ?: return
        } catch (e: Exception) {
            logger.warn("stop() Failed to close connection - ", e)
        } finally {
            connection = null
        }
    }

    private fun createChannel(): Channel? {

        try {
            return mqChannelPool!!.getBorrowObject()
        } catch (e: Exception) {
            logger.warn("createChannel() Failed to create channel", e)
            return null
        }
    }

    private fun closeChannel(channel: Channel) {

        try {
            mqChannelPool!!.returnObject(channel)
        } catch (e: Exception) {
            logger.warn("closeChannel() Failed to close channel=$channel", e)
        }
    }

    fun <T> call(callable: IChannelCallable<T>): T? {

        val channel = createChannel() ?: return null

        try {
            return callable.call(channel)
        } catch (e: Exception) {
            logger.warn("call() Failed to run: {$callable.description} on channel=$channel", e)
        } finally {
            closeChannel(channel)
        }
        return null
    }

    private fun restart() {

        for (mqConsumer in mqConsumers) {
            startMqConsumer(mqConsumer)
        }
    }

    @JvmOverloads
    fun createMqConsumer(queue: String, handler: IMqListner, qos: Int, autoAck: Boolean = true): MqConsumer {

        val mqConsumer = MqConsumer(queue, handler, qos, autoAck)
        mqConsumers.add(mqConsumer)
        startMqConsumer(mqConsumer)
        return mqConsumer
    }

    private fun startMqConsumer(mqConsumer: MqConsumer) {

        try {

            val channel = connection!!.createChannel() ?: return
            mqConsumer.start(channel)
        } catch (e: Exception) {
            logger.warn("startMqConsumer() Failed to start mqConsumer=$mqConsumer", e)
        }
    }

    fun stopConsumer(consumer: MqConsumer) {

        consumer.stop()
        mqConsumers.remove(consumer)
    }
}