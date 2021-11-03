package io.github.shanpark.mqtt5.packet.primitive

import io.github.shanpark.buffers.ReadBuffer
import io.github.shanpark.buffers.WriteBuffer
import io.github.shanpark.mqtt5.exception.ExceedLimitException
import io.github.shanpark.mqtt5.exception.NotEnoughDataException
import io.netty.buffer.ByteBuf

object BinaryData {
    fun length(value: ByteArray): Int {
        try {
            return TwoByteInteger.length(value.size) + value.size
        } catch (e: ExceedLimitException) {
            throw ExceedLimitException("Too long to be expressed in BinaryData", e)
        }
    }

    fun writeTo(buf: ByteBuf, value: ByteArray) {
        try {
            TwoByteInteger.writeTo(buf, value.size)
        } catch (e: ExceedLimitException) {
            throw ExceedLimitException("Try to write too long byte array(>= 64k).")
        }
        buf.writeBytes(value)
    }

    fun writeTo(buf: WriteBuffer, value: ByteArray) {
        try {
            TwoByteInteger.writeTo(buf, value.size)
        } catch (e: ExceedLimitException) {
            throw ExceedLimitException("Try to write too long byte array(>= 64k).")
        }
        buf.write(value)
    }

    fun readFrom(buf: ByteBuf): ByteArray {
        val length = TwoByteInteger.readFrom(buf)
        if (buf.readableBytes() < length)
            throw NotEnoughDataException("Not enough data for BinaryData")
        val data = ByteArray(length)
        buf.readBytes(data)
        return data
    }

    fun readFrom(buf: ReadBuffer): ByteArray {
        val length = TwoByteInteger.readFrom(buf)
        if (buf.readableBytes < length)
            throw NotEnoughDataException("Not enough data for BinaryData")
        val data = ByteArray(length)
        buf.read(data)
        return data
    }
}