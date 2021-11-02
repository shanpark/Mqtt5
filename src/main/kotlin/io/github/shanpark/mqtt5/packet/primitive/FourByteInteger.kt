package io.github.shanpark.mqtt5.packet.primitive

import io.github.shanpark.buffers.ReadBuffer
import io.github.shanpark.buffers.WriteBuffer
import io.github.shanpark.mqtt5.exception.ExceedLimitException
import io.github.shanpark.mqtt5.exception.NotEnoughDataException
import io.netty.buffer.ByteBuf

object FourByteInteger {
    fun length(value: Long): Int {
        if (value > 0xffff_ffff)
            throw ExceedLimitException("The value can't be expressed by FourByteInteger.")
        return 4
    }

    fun writeTo(buf: ByteBuf, value: Long) {
        if (value > 0xffff_ffff)
            throw ExceedLimitException("Try to write value that exceeds limit of FourByteInteger.")
        buf.writeInt(value.toInt()) // Int로 변환하면 비트만 잘라낼 뿐이므로 문제 없다.
    }

    fun writeTo(buf: WriteBuffer, value: Long) {
        if (value > 0xffff_ffff)
            throw ExceedLimitException("Try to write value that exceeds limit of FourByteInteger.")
        buf.writeInt(value.toInt()) // Int로 변환하면 비트만 잘라낼 뿐이므로 문제 없다.
    }

    fun readFrom(buf: ByteBuf): Long {
        try {
            return buf.readUnsignedInt()
        } catch (e: IndexOutOfBoundsException) {
            throw NotEnoughDataException("FourByteInteger needs 4 bytes.")
        }
    }

    fun readFrom(buf: ReadBuffer): Long {
        try {
            return buf.readUInt()
        } catch (e: IndexOutOfBoundsException) {
            throw NotEnoughDataException("FourByteInteger needs 4 bytes.")
        }
    }
}