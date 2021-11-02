package io.github.shanpark.mqtt5.packet.primitive

import io.github.shanpark.buffers.ReadBuffer
import io.github.shanpark.buffers.WriteBuffer
import io.github.shanpark.mqtt5.exception.ExceedLimitException
import io.github.shanpark.mqtt5.exception.NotEnoughDataException
import io.netty.buffer.ByteBuf

/**
 * MQTT 5.0 규격의 [MQTT-1.5.4-1], [MQTT-1.5.4-2], [MQTT-1.5.4-3] 는
 * 무시하고 Java의 UTF-8 문자열 규칙을 따른다.
 *
 * TODO Java의 규칙에 이미 적용이 되어있는지 테스트 필요.
 */
object Utf8EncodedString {
    fun length(value: String): Int {
        val byteArray = value.toByteArray(Charsets.UTF_8)
        try {
            return TwoByteInteger.length(byteArray.size) + byteArray.size
        } catch (e: ExceedLimitException) {
            throw ExceedLimitException("Too long to be expressed in Utf8EncodedString", e)
        }
    }

    fun writeTo(buf: ByteBuf, value: String) {
        val byteArray = value.toByteArray(Charsets.UTF_8)
        try {
            TwoByteInteger.writeTo(buf, byteArray.size)
        } catch (e: ExceedLimitException) {
            throw ExceedLimitException("Try to write too long string(>= 64k).")
        }
        buf.writeBytes(byteArray)
    }

    fun writeTo(buf: WriteBuffer, value: String) {
        val byteArray = value.toByteArray(Charsets.UTF_8)
        try {
            TwoByteInteger.writeTo(buf, byteArray.size)
        } catch (e: ExceedLimitException) {
            throw ExceedLimitException("Try to write too long string(>= 64k).")
        }
        buf.write(byteArray)
    }

    fun readFrom(buf: ByteBuf): String {
        val length = TwoByteInteger.readFrom(buf)
        if (length > buf.readableBytes())
            throw NotEnoughDataException("Not enough data for Utf8EncodedString")
        return buf.readCharSequence(length, Charsets.UTF_8).toString()
    }

    fun readFrom(buf: ReadBuffer): String {
        val length = TwoByteInteger.readFrom(buf)
        if (length > buf.readableBytes())
            throw NotEnoughDataException("Not enough data for Utf8EncodedString")
        return buf.readString(length, Charsets.UTF_8)
    }
}