package com.github.shanpark.mqtt5.packet

import com.github.shanpark.buffers.ReadBuffer
import com.github.shanpark.buffers.WriteBuffer
import com.github.shanpark.mqtt5.exception.InvalidPacketException
import com.github.shanpark.mqtt5.packet.derivative.MqttProperties
import com.github.shanpark.mqtt5.packet.derivative.MqttSubscriptionOptions
import com.github.shanpark.mqtt5.packet.primitive.OneByteInteger
import com.github.shanpark.mqtt5.packet.primitive.TwoByteInteger
import com.github.shanpark.mqtt5.packet.primitive.Utf8EncodedString
import com.github.shanpark.mqtt5.packet.primitive.VariableByteInteger
import com.github.shanpark.mqtt5.packet.primitive.constants.MqttPacketType
import io.netty.buffer.ByteBuf

class MqttSubscribe(flags: Int = 0, remainingLength: Int = -1): com.github.shanpark.mqtt5.packet.MqttFixedHeader(MqttPacketType.SUBSCRIBE, flags, remainingLength) {

    var packetId: Int = 0
    var properties = MqttProperties.EMPTY
    var topicFilters = listOf<Pair<String, MqttSubscriptionOptions>>()

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
        val topicFilters: MutableList<Pair<String, MqttSubscriptionOptions>> = mutableListOf()
        while (payloadBuf.isReadable) {
            topicFilters.add(Pair(Utf8EncodedString.readFrom(payloadBuf), MqttSubscriptionOptions(OneByteInteger.readFrom(payloadBuf))))
        }
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
        val topicFilters: MutableList<Pair<String, MqttSubscriptionOptions>> = mutableListOf()
        while (payloadBuf.isReadable) {
            topicFilters.add(Pair(Utf8EncodedString.readFrom(payloadBuf), MqttSubscriptionOptions(OneByteInteger.readFrom(payloadBuf))))
        }
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
            Utf8EncodedString.writeTo(buf, topicFilter.first)
            OneByteInteger.writeTo(buf, topicFilter.second.value)
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
            Utf8EncodedString.writeTo(buf, topicFilter.first)
            OneByteInteger.writeTo(buf, topicFilter.second.value)
        }
    }

    /**
     * remainingLength??? ???????????? ????????????.
     * ????????? ?????? ????????? ????????? ????????? ?????? ??????????????????. flags ????????? ????????? ????????? ???????????? ????????? ????????? ????????????.
     *
     * @throws InvalidPacketException flags ????????? ????????? ????????? ???????????? ?????? ??? ????????????.
     */
    override fun calcLength(): Int {
        val propertiesLength = properties.length()
        var length = TwoByteInteger.length(packetId) +
                VariableByteInteger.length(propertiesLength) +
                propertiesLength

        for (topicFileter in topicFilters) {
            length += (Utf8EncodedString.length(topicFileter.first) +
                    OneByteInteger.length(topicFileter.second.value))
        }

        return length
    }
}