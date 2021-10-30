package io.github.shanpark.mqtt5.packet

import io.github.shanpark.mqtt5.exception.InvalidPacketException
import io.github.shanpark.mqtt5.packet.MqttFixedHeader
import io.github.shanpark.mqtt5.packet.primitive.OneByteInteger
import io.github.shanpark.mqtt5.packet.primitive.TwoByteInteger
import io.github.shanpark.mqtt5.packet.primitive.constants.MqttReasonCode
import io.github.shanpark.mqtt5.packet.derivative.MqttProperties
import io.github.shanpark.mqtt5.packet.primitive.VariableByteInteger
import io.github.shanpark.mqtt5.packet.primitive.constants.MqttPacketType
import io.netty.buffer.ByteBuf

class MqttPubRec(flags: Int, remainingLength: Int = -1): MqttFixedHeader(MqttPacketType.PUBREC, flags, remainingLength) {

    var packetId: Int = 0
    var pubRecReasonCode: MqttReasonCode = MqttReasonCode.SUCCESS_OR_GRANTED_QOS_0
    var properties = MqttProperties.EMPTY

    constructor(): this(0) // needed for serialization.

    constructor(packetId: Int, reasonCode: MqttReasonCode, properties: MqttProperties = MqttProperties.EMPTY)
            :this(0) {
        this.packetId = packetId
        this.pubRecReasonCode = reasonCode
        this.properties = properties
    }

    override fun readFrom(buf: ByteBuf) {
        ///////////////////////////////////////////////////////////////////////////////////////
        // Variable Header
        packetId = TwoByteInteger.readFrom(buf)

        if (buf.isReadable) { // reason code가 0이고 property가 없는 경우 나머지는 생략될 수 있다.
            pubRecReasonCode = MqttReasonCode.valueOf(OneByteInteger.readFrom(buf))

            val length = VariableByteInteger.readFrom(buf)
            if (length > 0)
                properties = MqttProperties().readFrom(buf.readSlice(length))
        }

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
        TwoByteInteger.writeTo(buf, packetId)

        OneByteInteger.writeTo(buf, pubRecReasonCode.code)

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
        return  TwoByteInteger.length(packetId) +
                OneByteInteger.length(pubRecReasonCode.code) +
                VariableByteInteger.length(propertiesLength) +
                propertiesLength
    }
}