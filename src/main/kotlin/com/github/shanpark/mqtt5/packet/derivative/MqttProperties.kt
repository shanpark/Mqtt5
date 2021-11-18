package com.github.shanpark.mqtt5.packet.derivative

import com.github.shanpark.buffers.ReadBuffer
import com.github.shanpark.buffers.WriteBuffer
import com.github.shanpark.mqtt5.exception.InvalidPropertyValueException
import com.github.shanpark.mqtt5.packet.primitive.*
import com.github.shanpark.mqtt5.packet.primitive.constants.MqttProperty
import io.netty.buffer.ByteBuf
import java.lang.Exception

class MqttProperties : HashMap<MqttProperty, Any>() {

    fun length(): Int {
        var total = 0
        for (property in keys) {
            val value = get(property)!!
            total += (VariableByteInteger.length(property.id) +
                    when (property) {
                        MqttProperty.PAYLOAD_FORMAT_INDICATOR -> OneByteInteger.length(value as Int)
                        MqttProperty.MESSAGE_EXPIRY_INTERVAL -> FourByteInteger.length(value as Long)
                        MqttProperty.CONTENT_TYPE -> Utf8EncodedString.length(value as String)
                        MqttProperty.RESPONSE_TOPIC -> Utf8EncodedString.length(value as String)
                        MqttProperty.CORRELATION_DATA -> BinaryData.length(value as ByteArray)
                        MqttProperty.SUBSCRIPTION_IDENTIFIER -> VariableByteInteger.length(value as Int)
                        MqttProperty.SESSION_EXPIRY_INTERVAL -> FourByteInteger.length(value as Long)
                        MqttProperty.ASSIGNED_CLIENT_IDENTIFIER -> Utf8EncodedString.length(value as String)
                        MqttProperty.SERVER_KEEP_ALIVE -> TwoByteInteger.length(value as Int)
                        MqttProperty.AUTHENTICATION_METHOD -> Utf8EncodedString.length(value as String)
                        MqttProperty.AUTHENTICATION_DATA -> BinaryData.length(value as ByteArray)
                        MqttProperty.REQUEST_PROBLEM_INFORMATION -> OneByteInteger.length(value as Int)
                        MqttProperty.WILL_DELAY_INTERVAL -> FourByteInteger.length(value as Long)
                        MqttProperty.REQUEST_RESPONSE_INFORMATION -> OneByteInteger.length(value as Int)
                        MqttProperty.RESPONSE_INFORMATION -> Utf8EncodedString.length(value as String)
                        MqttProperty.SERVER_REFERENCE -> Utf8EncodedString.length(value as String)
                        MqttProperty.REASON_STRING -> Utf8EncodedString.length(value as String)
                        MqttProperty.RECEIVE_MAXIMUM -> TwoByteInteger.length(value as Int)
                        MqttProperty.TOPIC_ALIAS_MAXIMUM -> TwoByteInteger.length(value as Int)
                        MqttProperty.TOPIC_ALIAS -> TwoByteInteger.length(value as Int)
                        MqttProperty.MAXIMUM_QOS -> OneByteInteger.length(value as Int)
                        MqttProperty.RETAIN_AVAILABLE -> OneByteInteger.length(value as Int)
                        MqttProperty.USER_PROPERTY -> Utf8StringPair.length(value as Utf8StringPair)
                        MqttProperty.MAXIMUM_PACKET_SIZE -> FourByteInteger.length(value as Long)
                        MqttProperty.WILDCARD_SUBSCRIPTION_AVAILABLE -> OneByteInteger.length(value as Int)
                        MqttProperty.SUBSCRIPTION_IDENTIFIER_AVAILABLE -> OneByteInteger.length(value as Int)
                        MqttProperty.SHARED_SUBSCRIPTION_AVAILABLE -> OneByteInteger.length(value as Int)
                    })
        }
        return total
    }

    fun readFrom(buf: ByteBuf): MqttProperties {
        while (buf.isReadable) {
            val property = MqttProperty.valueOf(VariableByteInteger.readFrom(buf))
            @Suppress("IMPLICIT_CAST_TO_ANY")
            val value = when (property) {
                MqttProperty.PAYLOAD_FORMAT_INDICATOR -> OneByteInteger.readFrom(buf)
                MqttProperty.MESSAGE_EXPIRY_INTERVAL -> FourByteInteger.readFrom(buf)
                MqttProperty.CONTENT_TYPE -> Utf8EncodedString.readFrom(buf)
                MqttProperty.RESPONSE_TOPIC -> Utf8EncodedString.readFrom(buf)
                MqttProperty.CORRELATION_DATA -> BinaryData.readFrom(buf)
                MqttProperty.SUBSCRIPTION_IDENTIFIER -> VariableByteInteger.readFrom(buf)
                MqttProperty.SESSION_EXPIRY_INTERVAL -> FourByteInteger.readFrom(buf)
                MqttProperty.ASSIGNED_CLIENT_IDENTIFIER -> Utf8EncodedString.readFrom(buf)
                MqttProperty.SERVER_KEEP_ALIVE -> TwoByteInteger.readFrom(buf)
                MqttProperty.AUTHENTICATION_METHOD -> Utf8EncodedString.readFrom(buf)
                MqttProperty.AUTHENTICATION_DATA -> BinaryData.readFrom(buf)
                MqttProperty.REQUEST_PROBLEM_INFORMATION -> OneByteInteger.readFrom(buf)
                MqttProperty.WILL_DELAY_INTERVAL -> FourByteInteger.readFrom(buf)
                MqttProperty.REQUEST_RESPONSE_INFORMATION -> OneByteInteger.readFrom(buf)
                MqttProperty.RESPONSE_INFORMATION -> Utf8EncodedString.readFrom(buf)
                MqttProperty.SERVER_REFERENCE -> Utf8EncodedString.readFrom(buf)
                MqttProperty.REASON_STRING -> Utf8EncodedString.readFrom(buf)
                MqttProperty.RECEIVE_MAXIMUM -> TwoByteInteger.readFrom(buf)
                MqttProperty.TOPIC_ALIAS_MAXIMUM -> TwoByteInteger.readFrom(buf)
                MqttProperty.TOPIC_ALIAS -> TwoByteInteger.readFrom(buf)
                MqttProperty.MAXIMUM_QOS -> OneByteInteger.readFrom(buf)
                MqttProperty.RETAIN_AVAILABLE -> OneByteInteger.readFrom(buf)
                MqttProperty.USER_PROPERTY -> Utf8StringPair.readFrom(buf)
                MqttProperty.MAXIMUM_PACKET_SIZE -> FourByteInteger.readFrom(buf)
                MqttProperty.WILDCARD_SUBSCRIPTION_AVAILABLE -> OneByteInteger.readFrom(buf)
                MqttProperty.SUBSCRIPTION_IDENTIFIER_AVAILABLE -> OneByteInteger.readFrom(buf)
                MqttProperty.SHARED_SUBSCRIPTION_AVAILABLE -> OneByteInteger.readFrom(buf)
            }
            this[property] = value
        }
        return this
    }

    fun writeTo(buf: ByteBuf) {
        for (property in keys) {
            VariableByteInteger.writeTo(buf, property.id)
            val value = get(property)
            try {
                when (property) {
                    MqttProperty.PAYLOAD_FORMAT_INDICATOR -> OneByteInteger.writeTo(buf, value as Int)
                    MqttProperty.MESSAGE_EXPIRY_INTERVAL -> FourByteInteger.writeTo(buf, value as Long)
                    MqttProperty.CONTENT_TYPE -> Utf8EncodedString.writeTo(buf, value as String)
                    MqttProperty.RESPONSE_TOPIC -> Utf8EncodedString.writeTo(buf, value as String)
                    MqttProperty.CORRELATION_DATA -> BinaryData.writeTo(buf, value as ByteArray)
                    MqttProperty.SUBSCRIPTION_IDENTIFIER -> VariableByteInteger.writeTo(buf, value as Int)
                    MqttProperty.SESSION_EXPIRY_INTERVAL -> FourByteInteger.writeTo(buf, value as Long)
                    MqttProperty.ASSIGNED_CLIENT_IDENTIFIER -> Utf8EncodedString.writeTo(buf, value as String)
                    MqttProperty.SERVER_KEEP_ALIVE -> TwoByteInteger.writeTo(buf, value as Int)
                    MqttProperty.AUTHENTICATION_METHOD -> Utf8EncodedString.writeTo(buf, value as String)
                    MqttProperty.AUTHENTICATION_DATA -> BinaryData.writeTo(buf, value as ByteArray)
                    MqttProperty.REQUEST_PROBLEM_INFORMATION -> OneByteInteger.writeTo(buf, value as Int)
                    MqttProperty.WILL_DELAY_INTERVAL -> FourByteInteger.writeTo(buf, value as Long)
                    MqttProperty.REQUEST_RESPONSE_INFORMATION -> OneByteInteger.writeTo(buf, value as Int)
                    MqttProperty.RESPONSE_INFORMATION -> Utf8EncodedString.writeTo(buf, value as String)
                    MqttProperty.SERVER_REFERENCE -> Utf8EncodedString.writeTo(buf, value as String)
                    MqttProperty.REASON_STRING -> Utf8EncodedString.writeTo(buf, value as String)
                    MqttProperty.RECEIVE_MAXIMUM -> TwoByteInteger.writeTo(buf, value as Int)
                    MqttProperty.TOPIC_ALIAS_MAXIMUM -> TwoByteInteger.writeTo(buf, value as Int)
                    MqttProperty.TOPIC_ALIAS -> TwoByteInteger.writeTo(buf, value as Int)
                    MqttProperty.MAXIMUM_QOS -> OneByteInteger.writeTo(buf, value as Int)
                    MqttProperty.RETAIN_AVAILABLE -> OneByteInteger.writeTo(buf, value as Int)
                    MqttProperty.USER_PROPERTY -> Utf8StringPair.writeTo(buf, value as Utf8StringPair)
                    MqttProperty.MAXIMUM_PACKET_SIZE -> FourByteInteger.writeTo(buf, value as Long)
                    MqttProperty.WILDCARD_SUBSCRIPTION_AVAILABLE -> OneByteInteger.writeTo(buf, value as Int)
                    MqttProperty.SUBSCRIPTION_IDENTIFIER_AVAILABLE -> OneByteInteger.writeTo(buf, value as Int)
                    MqttProperty.SHARED_SUBSCRIPTION_AVAILABLE -> OneByteInteger.writeTo(buf, value as Int)
                }
            } catch (e: Exception) {
                throw InvalidPropertyValueException("Type of property value is invalid.")
            }
        }
    }

    fun readFrom(buf: ReadBuffer): MqttProperties {
        while (buf.isReadable) {
            val property = MqttProperty.valueOf(VariableByteInteger.readFrom(buf))
            @Suppress("IMPLICIT_CAST_TO_ANY")
            val value = when (property) {
                MqttProperty.PAYLOAD_FORMAT_INDICATOR -> OneByteInteger.readFrom(buf)
                MqttProperty.MESSAGE_EXPIRY_INTERVAL -> FourByteInteger.readFrom(buf)
                MqttProperty.CONTENT_TYPE -> Utf8EncodedString.readFrom(buf)
                MqttProperty.RESPONSE_TOPIC -> Utf8EncodedString.readFrom(buf)
                MqttProperty.CORRELATION_DATA -> BinaryData.readFrom(buf)
                MqttProperty.SUBSCRIPTION_IDENTIFIER -> VariableByteInteger.readFrom(buf)
                MqttProperty.SESSION_EXPIRY_INTERVAL -> FourByteInteger.readFrom(buf)
                MqttProperty.ASSIGNED_CLIENT_IDENTIFIER -> Utf8EncodedString.readFrom(buf)
                MqttProperty.SERVER_KEEP_ALIVE -> TwoByteInteger.readFrom(buf)
                MqttProperty.AUTHENTICATION_METHOD -> Utf8EncodedString.readFrom(buf)
                MqttProperty.AUTHENTICATION_DATA -> BinaryData.readFrom(buf)
                MqttProperty.REQUEST_PROBLEM_INFORMATION -> OneByteInteger.readFrom(buf)
                MqttProperty.WILL_DELAY_INTERVAL -> FourByteInteger.readFrom(buf)
                MqttProperty.REQUEST_RESPONSE_INFORMATION -> OneByteInteger.readFrom(buf)
                MqttProperty.RESPONSE_INFORMATION -> Utf8EncodedString.readFrom(buf)
                MqttProperty.SERVER_REFERENCE -> Utf8EncodedString.readFrom(buf)
                MqttProperty.REASON_STRING -> Utf8EncodedString.readFrom(buf)
                MqttProperty.RECEIVE_MAXIMUM -> TwoByteInteger.readFrom(buf)
                MqttProperty.TOPIC_ALIAS_MAXIMUM -> TwoByteInteger.readFrom(buf)
                MqttProperty.TOPIC_ALIAS -> TwoByteInteger.readFrom(buf)
                MqttProperty.MAXIMUM_QOS -> OneByteInteger.readFrom(buf)
                MqttProperty.RETAIN_AVAILABLE -> OneByteInteger.readFrom(buf)
                MqttProperty.USER_PROPERTY -> Utf8StringPair.readFrom(buf)
                MqttProperty.MAXIMUM_PACKET_SIZE -> FourByteInteger.readFrom(buf)
                MqttProperty.WILDCARD_SUBSCRIPTION_AVAILABLE -> OneByteInteger.readFrom(buf)
                MqttProperty.SUBSCRIPTION_IDENTIFIER_AVAILABLE -> OneByteInteger.readFrom(buf)
                MqttProperty.SHARED_SUBSCRIPTION_AVAILABLE -> OneByteInteger.readFrom(buf)
            }
            this[property] = value
        }
        return this
    }

    fun writeTo(buf: WriteBuffer) {
        for (property in keys) {
            VariableByteInteger.writeTo(buf, property.id)
            val value = get(property)
            try {
                when (property) {
                    MqttProperty.PAYLOAD_FORMAT_INDICATOR -> OneByteInteger.writeTo(buf, value as Int)
                    MqttProperty.MESSAGE_EXPIRY_INTERVAL -> FourByteInteger.writeTo(buf, value as Long)
                    MqttProperty.CONTENT_TYPE -> Utf8EncodedString.writeTo(buf, value as String)
                    MqttProperty.RESPONSE_TOPIC -> Utf8EncodedString.writeTo(buf, value as String)
                    MqttProperty.CORRELATION_DATA -> BinaryData.writeTo(buf, value as ByteArray)
                    MqttProperty.SUBSCRIPTION_IDENTIFIER -> VariableByteInteger.writeTo(buf, value as Int)
                    MqttProperty.SESSION_EXPIRY_INTERVAL -> FourByteInteger.writeTo(buf, value as Long)
                    MqttProperty.ASSIGNED_CLIENT_IDENTIFIER -> Utf8EncodedString.writeTo(buf, value as String)
                    MqttProperty.SERVER_KEEP_ALIVE -> TwoByteInteger.writeTo(buf, value as Int)
                    MqttProperty.AUTHENTICATION_METHOD -> Utf8EncodedString.writeTo(buf, value as String)
                    MqttProperty.AUTHENTICATION_DATA -> BinaryData.writeTo(buf, value as ByteArray)
                    MqttProperty.REQUEST_PROBLEM_INFORMATION -> OneByteInteger.writeTo(buf, value as Int)
                    MqttProperty.WILL_DELAY_INTERVAL -> FourByteInteger.writeTo(buf, value as Long)
                    MqttProperty.REQUEST_RESPONSE_INFORMATION -> OneByteInteger.writeTo(buf, value as Int)
                    MqttProperty.RESPONSE_INFORMATION -> Utf8EncodedString.writeTo(buf, value as String)
                    MqttProperty.SERVER_REFERENCE -> Utf8EncodedString.writeTo(buf, value as String)
                    MqttProperty.REASON_STRING -> Utf8EncodedString.writeTo(buf, value as String)
                    MqttProperty.RECEIVE_MAXIMUM -> TwoByteInteger.writeTo(buf, value as Int)
                    MqttProperty.TOPIC_ALIAS_MAXIMUM -> TwoByteInteger.writeTo(buf, value as Int)
                    MqttProperty.TOPIC_ALIAS -> TwoByteInteger.writeTo(buf, value as Int)
                    MqttProperty.MAXIMUM_QOS -> OneByteInteger.writeTo(buf, value as Int)
                    MqttProperty.RETAIN_AVAILABLE -> OneByteInteger.writeTo(buf, value as Int)
                    MqttProperty.USER_PROPERTY -> Utf8StringPair.writeTo(buf, value as Utf8StringPair)
                    MqttProperty.MAXIMUM_PACKET_SIZE -> FourByteInteger.writeTo(buf, value as Long)
                    MqttProperty.WILDCARD_SUBSCRIPTION_AVAILABLE -> OneByteInteger.writeTo(buf, value as Int)
                    MqttProperty.SUBSCRIPTION_IDENTIFIER_AVAILABLE -> OneByteInteger.writeTo(buf, value as Int)
                    MqttProperty.SHARED_SUBSCRIPTION_AVAILABLE -> OneByteInteger.writeTo(buf, value as Int)
                }
            } catch (e: Exception) {
                throw InvalidPropertyValueException("Type of property value is invalid.")
            }
        }
    }

    companion object {
        val EMPTY = MqttProperties()
    }
}