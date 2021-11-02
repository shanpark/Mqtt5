package io.github.shanpark.mqtt5.packet

import com.fasterxml.jackson.annotation.JsonIgnore
import io.github.shanpark.buffers.ReadBuffer
import io.github.shanpark.buffers.WriteBuffer
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
        get() = flags.and(FLAG_DUP_MASK) != 0
        set(value) {
            flags = if (value)
                flags.or(FLAG_DUP_MASK)
            else
                flags.and(FLAG_DUP_MASK.inv())
        }
    var qos: MqttQos
        @JsonIgnore
        get() = MqttQos.valueOf(flags.and(FLAG_QOS_MASK).shr(1))
        set(value) {
            flags = flags.and(FLAG_QOS_MASK.inv()).or(value.level.shl(1))
        }
    var retain: Boolean
        @JsonIgnore
        get() = flags.and(FLAG_RETAIN_MASK) != 0
        set(value) {
            flags = if (value)
                flags.or(FLAG_RETAIN_MASK)
            else
                flags.and(FLAG_RETAIN_MASK.inv())
        }

    abstract fun readFrom(buf: ByteBuf)
    abstract fun writeTo(buf: ByteBuf)
    abstract fun readFrom(buf: ReadBuffer)
    abstract fun writeTo(buf: WriteBuffer)
    protected abstract fun calcLength(): Int

    companion object {
        private const val FLAG_RETAIN_MASK = 0x01
        private const val FLAG_QOS_MASK = 0x06
        private const val FLAG_DUP_MASK = 0x08
    }
}