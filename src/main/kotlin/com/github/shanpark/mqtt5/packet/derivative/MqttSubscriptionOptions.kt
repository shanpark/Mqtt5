package com.github.shanpark.mqtt5.packet.derivative

import com.fasterxml.jackson.annotation.JsonIgnore
import com.github.shanpark.mqtt5.exception.MalformedPacketException
import com.github.shanpark.mqtt5.exception.ProtocolErrorException
import com.github.shanpark.mqtt5.packet.primitive.constants.MqttQos
import com.github.shanpark.mqtt5.packet.primitive.constants.MqttRetainHandling

class MqttSubscriptionOptions(val value: Int = 0) {
    val retainHandling: MqttRetainHandling
        @JsonIgnore
        get() = MqttRetainHandling.valueOf(value.and(0x30).shr(4))
    val retainAsPublished: Boolean // retained 메세지를 보낼 때 retain flag를 0으로 설정할지 1로 설정할 지. false이면 으로 설정해서 보낸다.
        @JsonIgnore
        get() = value.and(0x08) > 0
    val noLocal: Boolean // 자신이 PUBLISH한 메시지 수신 여부, true이면 자기 자신에게는 보내지지 않는다.
        @JsonIgnore
        get() = value.and(0x04) > 0
    val qos: MqttQos // Max QoS level
        @JsonIgnore
        get() = MqttQos.valueOf(value.and(0x03))

    init {
        if (value.and(0xC0) > 0)
            throw MalformedPacketException("Reserved bits of Subscription Options are not zero.")
        if (value.and(0x30).shr(4) == 0x03)
            throw ProtocolErrorException("Retain Handling option can't be 3.")
        if (value.and(0x03) == 0x03)
            throw MalformedPacketException("QoS of Subscription Options can't be 3.")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MqttSubscriptionOptions

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        return value
    }
}