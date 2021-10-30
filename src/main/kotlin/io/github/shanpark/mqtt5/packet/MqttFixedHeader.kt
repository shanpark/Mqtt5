package io.github.shanpark.mqtt5.packet

import com.fasterxml.jackson.annotation.JsonIgnore
import io.github.shanpark.mqtt5.packet.primitive.constants.MqttPacketType
import io.github.shanpark.mqtt5.packet.primitive.constants.MqttQos
import io.netty.buffer.ByteBuf

abstract class MqttFixedHeader(
    val type: MqttPacketType,
    var flags: Int,
    var remainingLength: Int = -1
) {
    var dup: Boolean
        @JsonIgnore
        get() = flags.and(0x08) > 0
        set(value) {
            flags = if (value)
                flags.or(0x08)
            else
                flags.and(0x08.inv())
        }
    var qos: MqttQos
        @JsonIgnore
        get() = MqttQos.valueOf(flags.and(0x06).shr(1))
        set(value) {
            flags = flags.and(0x06.inv()).or(value.level.shl(1))
        }
    var retain: Boolean
        @JsonIgnore
        get() = flags.and(0x01) > 0
        set(value) {
            flags = if (value)
                flags.or(0x01)
            else
                flags.and(0x01.inv())
        }

    abstract fun readFrom(buf: ByteBuf)
    abstract fun writeTo(buf: ByteBuf)
    protected abstract fun calcLength(): Int
}