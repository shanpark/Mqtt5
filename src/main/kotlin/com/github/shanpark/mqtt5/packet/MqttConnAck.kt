package com.github.shanpark.mqtt5.packet

import com.github.shanpark.buffers.ReadBuffer
import com.github.shanpark.buffers.WriteBuffer
import com.github.shanpark.mqtt5.exception.InvalidPacketException
import com.github.shanpark.mqtt5.packet.derivative.MqttProperties
import com.github.shanpark.mqtt5.packet.primitive.OneByteInteger
import com.github.shanpark.mqtt5.packet.primitive.VariableByteInteger
import com.github.shanpark.mqtt5.packet.primitive.constants.MqttPacketType
import com.github.shanpark.mqtt5.packet.primitive.constants.MqttReasonCode
import io.netty.buffer.ByteBuf

class MqttConnAck(flags: Int = 0, remainingLength: Int = -1): com.github.shanpark.mqtt5.packet.MqttFixedHeader(MqttPacketType.CONNACK, flags, remainingLength) {
    var connAckFlags: Int = 0
    var connectReasonCode = MqttReasonCode.SUCCESS_OR_GRANTED_QOS_0
    var properties = MqttProperties.EMPTY

    val sessionPresent: Boolean
        get() = connAckFlags.and(0x01) > 0

    constructor(sessionPresent: Boolean, connectReasonCode: MqttReasonCode, properties: MqttProperties = MqttProperties.EMPTY): this(0) {
        this.connAckFlags = if (sessionPresent) 0x01 else 0x00
        this.connectReasonCode = connectReasonCode
        this.properties = properties
    }

    override fun readFrom(buf: ByteBuf) {
        ///////////////////////////////////////////////////////////////////////////////////////
        // Variable Header
        connAckFlags = OneByteInteger.readFrom(buf)
        connectReasonCode = MqttReasonCode.valueOf(OneByteInteger.readFrom(buf))

        val length = VariableByteInteger.readFrom(buf)
        if (length > 0)
            properties = MqttProperties().readFrom(buf.readSlice(length))

        ///////////////////////////////////////////////////////////////////////////////////////
        // No Payload
    }

    override fun readFrom(buf: ReadBuffer) {
        ///////////////////////////////////////////////////////////////////////////////////////
        // Variable Header
        connAckFlags = OneByteInteger.readFrom(buf)
        connectReasonCode = MqttReasonCode.valueOf(OneByteInteger.readFrom(buf))

        val length = VariableByteInteger.readFrom(buf)
        if (length > 0)
            properties = MqttProperties().readFrom(buf.readSlice(length))

        ///////////////////////////////////////////////////////////////////////////////////////
        // No Payload
    }

    override fun writeTo(buf: ByteBuf) {
        if (remainingLength < 0)
            remainingLength = calcLength()

        ///////////////////////////////////////////////////////////////////////////////////////
        // Fixed Header
        OneByteInteger.writeTo(buf, type.value + flags)
        VariableByteInteger.writeTo(buf, remainingLength)

        ///////////////////////////////////////////////////////////////////////////////////////
        // Variable Header
        OneByteInteger.writeTo(buf, connAckFlags)
        OneByteInteger.writeTo(buf, connectReasonCode.code)

        VariableByteInteger.writeTo(buf, properties.length())
        properties.writeTo(buf)

        ///////////////////////////////////////////////////////////////////////////////////////
        // No Payload
    }

    override fun writeTo(buf: WriteBuffer) {
        if (remainingLength < 0)
            remainingLength = calcLength()

        ///////////////////////////////////////////////////////////////////////////////////////
        // Fixed Header
        OneByteInteger.writeTo(buf, type.value + flags)
        VariableByteInteger.writeTo(buf, remainingLength)

        ///////////////////////////////////////////////////////////////////////////////////////
        // Variable Header
        OneByteInteger.writeTo(buf, connAckFlags)
        OneByteInteger.writeTo(buf, connectReasonCode.code)

        VariableByteInteger.writeTo(buf, properties.length())
        properties.writeTo(buf)

        ///////////////////////////////////////////////////////////////////////////////////////
        // No Payload
    }

    /**
     * remainingLength를 계산하여 반환한다.
     * 따라서 모든 필요한 값들이 채워진 후에 호출해야한다. flags 상태와 값들의 상태가 일치하지 않으면 예외가 발생한다.
     *
     * @throws InvalidPacketException flags 상태와 값들의 상태가 일치하지 않을 때 발생한다.
     */
    override fun calcLength(): Int {
        val propertiesLength = properties.length()
        return OneByteInteger.length(connAckFlags) +
                OneByteInteger.length(connectReasonCode.code) +
                VariableByteInteger.length(propertiesLength) +
                propertiesLength
    }
}