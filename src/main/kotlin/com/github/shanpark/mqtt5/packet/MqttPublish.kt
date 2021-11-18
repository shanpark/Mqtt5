package com.github.shanpark.mqtt5.packet

import com.github.shanpark.buffers.ReadBuffer
import com.github.shanpark.buffers.WriteBuffer
import com.github.shanpark.mqtt5.exception.InvalidPacketException
import com.github.shanpark.mqtt5.packet.derivative.MqttProperties
import com.github.shanpark.mqtt5.packet.primitive.OneByteInteger
import com.github.shanpark.mqtt5.packet.primitive.TwoByteInteger
import com.github.shanpark.mqtt5.packet.primitive.Utf8EncodedString
import com.github.shanpark.mqtt5.packet.primitive.VariableByteInteger
import com.github.shanpark.mqtt5.packet.primitive.constants.MqttPacketType
import com.github.shanpark.mqtt5.packet.primitive.constants.MqttQos
import io.netty.buffer.ByteBuf

class MqttPublish(flags: Int = 0, remainingLength: Int = -1): com.github.shanpark.mqtt5.packet.MqttFixedHeader(MqttPacketType.PUBLISH, flags, remainingLength) {
    var topicName: String = ""
    var packetId: Int = 0
    var properties = MqttProperties.EMPTY
    var payload: ByteArray = byteArrayOf() // Mqtt BinaryData가 아니라 그냥 byte array이다.

    constructor(dup: Boolean, qos: MqttQos, retain: Boolean, topicName: String, packetId: Int, properties: MqttProperties = MqttProperties.EMPTY, payload: ByteArray)
            :this((if (dup) 0x08 else 0).or(if (retain) 0x01 else 0).or(qos.level.shl(1))) {
        this.topicName = topicName
        this.packetId = packetId
        this.properties = properties
        this.payload = payload
    }

    constructor(dup: Boolean, qos: MqttQos, retain: Boolean, retainMessage: com.github.shanpark.mqtt5.packet.MqttPublish)
            :this((if (dup) 0x08 else 0).or(if (retain) 0x01 else 0).or(qos.level.shl(1))) {
        this.topicName = retainMessage.topicName
        this.packetId = retainMessage.packetId
        this.properties = retainMessage.properties
        this.payload = retainMessage.payload
    }

    override fun readFrom(buf: ByteBuf) {
        ///////////////////////////////////////////////////////////////////////////////////////
        // Variable Header
        val readableBytes = buf.readableBytes()

        topicName = Utf8EncodedString.readFrom(buf)

        if (qos.level > 0)
            packetId = TwoByteInteger.readFrom(buf)

        val length = VariableByteInteger.readFrom(buf)
        if (length > 0)
            properties = MqttProperties().readFrom(buf.readSlice(length))

        ///////////////////////////////////////////////////////////////////////////////////////
        // Payload
        val payloadLength = remainingLength - (readableBytes - buf.readableBytes())
        payload = ByteArray(payloadLength)
        buf.readBytes(payload) // BinaryData가 아니라 byte array이므로 buf에서 직접 읽는다.
    }

    override fun readFrom(buf: ReadBuffer) {
        ///////////////////////////////////////////////////////////////////////////////////////
        // Variable Header
        val readableBytes = buf.readableBytes

        topicName = Utf8EncodedString.readFrom(buf)

        if (qos.level > 0)
            packetId = TwoByteInteger.readFrom(buf)

        val length = VariableByteInteger.readFrom(buf)
        if (length > 0)
            properties = MqttProperties().readFrom(buf.readSlice(length))

        ///////////////////////////////////////////////////////////////////////////////////////
        // Payload
        val payloadLength = remainingLength - (readableBytes - buf.readableBytes)
        payload = ByteArray(payloadLength)
        buf.read(payload) // BinaryData가 아니라 byte array이므로 buf에서 직접 읽는다.
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
        Utf8EncodedString.writeTo(buf, topicName)

        if (qos.level > 0)
            TwoByteInteger.writeTo(buf, packetId)

        VariableByteInteger.writeTo(buf, properties.length())
        properties.writeTo(buf)

        ///////////////////////////////////////////////////////////////////////////////////////
        // Payload
        buf.writeBytes(payload) // payload는 byte array일 뿐 BinaryData 형식이 아니다. buf에 직접 쓴다.
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
        Utf8EncodedString.writeTo(buf, topicName)

        if (qos.level > 0)
            TwoByteInteger.writeTo(buf, packetId)

        VariableByteInteger.writeTo(buf, properties.length())
        properties.writeTo(buf)

        ///////////////////////////////////////////////////////////////////////////////////////
        // Payload
        buf.write(payload) // payload는 byte array일 뿐 BinaryData 형식이 아니다. buf에 직접 쓴다.
    }

    /**
     * remainingLength를 계산하여 반환한다.
     * 따라서 모든 필요한 값들이 채워진 후에 호출해야한다. flags 상태와 값들의 상태가 일치하지 않으면 예외가 발생한다.
     *
     * @throws InvalidPacketException flags 상태와 값들의 상태가 일치하지 않을 때 발생한다.
     */
    override fun calcLength(): Int {
        val propertiesLength = properties.length()
        var length = Utf8EncodedString.length(topicName)

        if (qos.level > 0)
            length += TwoByteInteger.length(packetId)

        length += (
            VariableByteInteger.length(propertiesLength) +
            propertiesLength +
            payload.size
        )

        return length
    }
}