package io.github.shanpark.mqtt5.packet.primitive

import io.github.shanpark.mqtt5.exception.ExceedLimitException
import io.github.shanpark.mqtt5.exception.NotEnoughDataException
import io.netty.buffer.ByteBuf

object TwoByteInteger {
    fun length(value: Int): Int {
        if (value > 0xffff)
            throw ExceedLimitException("The value can't be expressed by TwoByteInteger.")
        return 2
    }

    fun writeTo(buf: ByteBuf, value: Int) {
        if (value > 0xffff)
            throw ExceedLimitException("Try to write value that exceeds limit of TwoByteInteger.")
        buf.writeShort(value)
    }

    fun readFrom(buf: ByteBuf): Int {
        try {
            return buf.readUnsignedShort()
        } catch(e: IndexOutOfBoundsException) {
            throw NotEnoughDataException("TwoByteInteger needs 2 bytes.")
        }
    }
}