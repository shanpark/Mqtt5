package io.github.shanpark.mqtt5.packet

import io.github.shanpark.buffers.ReadBuffer
import io.github.shanpark.buffers.WriteBuffer
import io.github.shanpark.mqtt5.exception.InvalidPacketException
import io.github.shanpark.mqtt5.packet.derivative.MqttProperties
import io.github.shanpark.mqtt5.packet.primitive.OneByteInteger
import io.github.shanpark.mqtt5.packet.primitive.TwoByteInteger
import io.github.shanpark.mqtt5.packet.primitive.Utf8EncodedString
import io.github.shanpark.mqtt5.packet.primitive.VariableByteInteger
import io.github.shanpark.mqtt5.packet.primitive.constants.MqttPacketType
import io.netty.buffer.ByteBuf

class MqttUnsubscribe(flags: Int = 0, remainingLength: Int = -1): MqttFixedHeader(MqttPacketType.UNSUBSCRIBE, flags, remainingLength) {

    var packetId: Int = 0
    var properties = MqttProperties.EMPTY
    var topicFilters = listOf<String>()

    override fun readFrom(buf: ByteBuf) {
        ///////////////////////////////////////////////////////////////////////////////////////
        // Variable Header
        val readableBytes = buf.readableBytes()

        packetId = TwoByteInteger.readFrom(buf)

        val length = VariableByteInteger.readFrom(buf)
        if (length > 0)
            properties = MqttProperties().readFrom(buf.readSlice(length))

        ///////////////////////////////////////////////////////////////////////////////////////
        // Payload
        val payloadLength = remainingLength - (readableBytes - buf.readableBytes())
        val payloadBuf = buf.readSlice(payloadLength)
        val topicFilters = mutableListOf<String>()
        while (payloadBuf.isReadable)
            topicFilters.add(Utf8EncodedString.readFrom(payloadBuf))
        this.topicFilters = topicFilters
    }

    override fun readFrom(buf: ReadBuffer) {
        ///////////////////////////////////////////////////////////////////////////////////////
        // Variable Header
        val readableBytes = buf.readableBytes

        packetId = TwoByteInteger.readFrom(buf)

        val length = VariableByteInteger.readFrom(buf)
        if (length > 0)
            properties = MqttProperties().readFrom(buf.readSlice(length))

        ///////////////////////////////////////////////////////////////////////////////////////
        // Payload
        val payloadLength = remainingLength - (readableBytes - buf.readableBytes)
        val payloadBuf = buf.readSlice(payloadLength)
        val topicFilters = mutableListOf<String>()
        while (payloadBuf.isReadable)
            topicFilters.add(Utf8EncodedString.readFrom(payloadBuf))
        this.topicFilters = topicFilters
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
        for (topicFilter in topicFilters) {
            Utf8EncodedString.writeTo(buf, topicFilter)
        }
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
        TwoByteInteger.writeTo(buf, packetId)

        VariableByteInteger.writeTo(buf, properties.length())
        properties.writeTo(buf)

        ///////////////////////////////////////////////////////////////////////////////////////
        // Payload
        for (topicFilter in topicFilters) {
            Utf8EncodedString.writeTo(buf, topicFilter)
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

        for (topicFileter in topicFilters) {
            length += Utf8EncodedString.length(topicFileter)
        }

        return length
    }
}