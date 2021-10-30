package io.github.shanpark.mqtt5.packet

import io.github.shanpark.mqtt5.exception.InvalidPacketException
import io.github.shanpark.mqtt5.packet.derivative.MqttProperties
import io.github.shanpark.mqtt5.packet.primitive.*
import io.github.shanpark.mqtt5.packet.primitive.constants.MqttPacketType
import io.github.shanpark.mqtt5.packet.primitive.constants.MqttQos
import io.netty.buffer.ByteBuf

class MqttConnect(flags: Int, remainingLength: Int = -1): MqttFixedHeader(MqttPacketType.CONNECT, flags, remainingLength) {
    var protocolName: String = ""
    var protocolVersion: Int = 0
    var connectFlags: Int = 0
    var keepAlive: Int = 0
    var properties: MqttProperties = MqttProperties.EMPTY
    var clientId: String = ""

    var willProperties: MqttProperties? = null
    var willTopic: String? = null
    var willPayload: ByteArray? = null
    var userName: String? = null
    var password: ByteArray? = null

    val cleanStart: Boolean
        get() = connectFlags.and(0x02) > 0
    val willFlag: Boolean
        get() = connectFlags.and(0x04) > 0
    val willQos: MqttQos
        get() = MqttQos.valueOf(connectFlags.and(0x18).shr(3))
    val willRetain: Boolean
        get() = connectFlags.and(0x20) > 0
    val passwordFlag: Boolean
        get() = connectFlags.and(0x40) > 0
    val userNameFlag: Boolean
        get() = connectFlags.and(0x80) > 0

    constructor(): this(0) // needed for serialization.

    override fun readFrom(buf: ByteBuf) {
        ///////////////////////////////////////////////////////////////////////////////////////
        // Variable Header
        protocolName = Utf8EncodedString.readFrom(buf) // MQTT
        protocolVersion = OneByteInteger.readFrom(buf) // Ver 5
        connectFlags = OneByteInteger.readFrom(buf)
        keepAlive = TwoByteInteger.readFrom(buf)

        var length = VariableByteInteger.readFrom(buf)
        if (length > 0)
            properties = MqttProperties().readFrom(buf.readSlice(length))

        ///////////////////////////////////////////////////////////////////////////////////////
        // Payload
        clientId = Utf8EncodedString.readFrom(buf)

        // optional payload
        if (willFlag) {
            length = VariableByteInteger.readFrom(buf)
            if (length > 0) {
                willProperties = MqttProperties().readFrom(buf.readSlice(length))
            }
            willTopic = Utf8EncodedString.readFrom(buf)
            willPayload = BinaryData.readFrom(buf)
        }

        if (userNameFlag) {
            userName = Utf8EncodedString.readFrom(buf)
        }

        if (passwordFlag) {
            password = BinaryData.readFrom(buf)
        }
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
        Utf8EncodedString.writeTo(buf, "MQTT") // use hard coded value for MQTT5
        OneByteInteger.writeTo(buf, 0x05) // use hard coded value for MQTT5
        OneByteInteger.writeTo(buf, connectFlags)
        TwoByteInteger.writeTo(buf, keepAlive)

        VariableByteInteger.writeTo(buf, properties.length())
        properties.writeTo(buf)

        ///////////////////////////////////////////////////////////////////////////////////////
        // Payload
        Utf8EncodedString.writeTo(buf, clientId)

        if (willFlag) {
            VariableByteInteger.writeTo(buf, willProperties?.length() ?: 0) // Will properties may be empty.
            willProperties?.writeTo(buf)
            Utf8EncodedString.writeTo(buf, willTopic ?: throw InvalidPacketException("Will Flag is set but will topic field is null."))
            BinaryData.writeTo(buf, willPayload ?: throw InvalidPacketException("Will Flag is set but will payload field is null."))
        }

        if (userNameFlag) {
            Utf8EncodedString.writeTo(buf, userName ?: throw InvalidPacketException("User Name Flag is set but user name field is null."))
        }

        if (passwordFlag) {
            BinaryData.writeTo(buf, password ?: throw InvalidPacketException("Password Flag is set but password field is null."))
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
        var length =
            Utf8EncodedString.length("MQTT") +
                    OneByteInteger.length(0x05) +
                    OneByteInteger.length(connectFlags) +
                    TwoByteInteger.length(keepAlive) +

                    VariableByteInteger.length(propertiesLength) +
                    propertiesLength +

                    Utf8EncodedString.length(clientId)

        if (willFlag) {
            length += (
                    VariableByteInteger.length(willProperties?.length() ?: 0) + // Will properties may be empty.
                            (willProperties?.length() ?: 0) +
                            Utf8EncodedString.length(willTopic ?: throw InvalidPacketException("Will Flag is set but will topic field is null.")) +
                            BinaryData.length(willPayload ?: throw InvalidPacketException("Will Flag is set but will payload field is null."))
                    )
        }

        if (userNameFlag) {
            length += Utf8EncodedString.length(userName ?: throw InvalidPacketException("User Name Flag is set but user name field is null."))
        }

        if (passwordFlag) {
            length += BinaryData.length(password ?: throw InvalidPacketException("Password Flag is set but password field is null."))
        }

        return length
    }
}