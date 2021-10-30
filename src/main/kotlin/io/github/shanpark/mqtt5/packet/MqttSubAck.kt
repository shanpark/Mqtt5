package io.github.shanpark.mqtt5.packet

import io.github.shanpark.mqtt5.exception.InvalidPacketException
import io.github.shanpark.mqtt5.packet.derivative.MqttProperties
import io.github.shanpark.mqtt5.packet.primitive.OneByteInteger
import io.github.shanpark.mqtt5.packet.primitive.TwoByteInteger
import io.github.shanpark.mqtt5.packet.primitive.VariableByteInteger
import io.github.shanpark.mqtt5.packet.primitive.constants.MqttPacketType
import io.github.shanpark.mqtt5.packet.primitive.constants.MqttReasonCode
import io.netty.buffer.ByteBuf

class MqttSubAck(flags: Int, remainingLength: Int = -1): MqttFixedHeader(MqttPacketType.SUBACK, flags, remainingLength) {

    var packetId: Int = 0
    var properties = MqttProperties.EMPTY
    var reasonCodes = listOf<MqttReasonCode>()

    constructor(): this(0) // needed for serialization.

    constructor(packetId: Int, reasonCodes: List<MqttReasonCode>, properties: MqttProperties = MqttProperties.EMPTY)
            :this(0) {
        this.packetId = packetId
        this.reasonCodes = reasonCodes
        this.properties = properties
    }

    override fun readFrom(buf: ByteBuf) {
        ///////////////////////////////////////////////////////////////////////////////////////
        // Variable Header
        val variableHeaderStartIndex = buf.readerIndex()

        packetId = TwoByteInteger.readFrom(buf)

        val length = VariableByteInteger.readFrom(buf)
        if (length > 0)
            properties = MqttProperties().readFrom(buf.readSlice(length))

        ///////////////////////////////////////////////////////////////////////////////////////
        // Payload
        val payloadLength = remainingLength - (buf.readerIndex() - variableHeaderStartIndex)
        val reasonCodes: MutableList<MqttReasonCode> = mutableListOf()
        for (inx in 0 until payloadLength)
            reasonCodes.add(MqttReasonCode.valueOf(OneByteInteger.readFrom(buf)))
        this.reasonCodes = reasonCodes
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
        TwoByteInteger.writeTo(buf, packetId)

        VariableByteInteger.writeTo(buf, properties.length())
        properties.writeTo(buf)

        ///////////////////////////////////////////////////////////////////////////////////////
        // Payload
        for (reasonCode in reasonCodes) {
            OneByteInteger.writeTo(buf, reasonCode.code)
        }
    }

    /**
     * remainingLength를 계산하여 반환한다.
     * 따라서 모든 필요한 값들이 채워진 후에 호출해야한다. flags 상태와 값들의 상태가 일치하지 않으면 예외가 발생한다.
     *
     * @throws InvalidPacketException flags 상태와 값들의 상태가 일치하지 않을 때 발생한다.
     */
    override fun calcLength(): Int {
        val propertiesLength = properties.length()
        var length = TwoByteInteger.length(packetId) +
                VariableByteInteger.length(propertiesLength) +
                propertiesLength

        for (reasonCode in reasonCodes) {
            length += OneByteInteger.length(reasonCode.code)
        }

        return length
    }
}