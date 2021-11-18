package com.github.shanpark.mqtt5.packet.primitive

import com.github.shanpark.buffers.ReadBuffer
import com.github.shanpark.buffers.WriteBuffer
import com.github.shanpark.mqtt5.exception.ExceedLimitException
import com.github.shanpark.mqtt5.exception.NotEnoughDataException
import io.netty.buffer.ByteBuf

object OneByteInteger {
    fun length(value: Int): Int {
        if (value > 0xff)
            throw ExceedLimitException("The value can't be expressed by OneByteInteger.")
        return 1
    }

    fun writeTo(buf: ByteBuf, value: Int) {
        if (value > 0xff)
            throw ExceedLimitException("Try to write value that exceeds limit of OneByteInteger.")
        buf.writeByte(value)
    }

    fun writeTo(buf: WriteBuffer, value: Int) {
        if (value > 0xff)
            throw ExceedLimitException("Try to write value that exceeds limit of OneByteInteger.")
        buf.write(value)
    }

    fun readFrom(buf: ByteBuf): Int {
        try {
            return buf.readUnsignedByte().toInt()
        } catch(e: IndexOutOfBoundsException) {
            throw NotEnoughDataException("OneByteInteger needs 1 byte.")
        }
    }

    fun readFrom(buf: ReadBuffer): Int {
        try {
            return buf.read()
        } catch(e: IndexOutOfBoundsException) {
            throw NotEnoughDataException("OneByteInteger needs 1 byte.")
        }
    }
}