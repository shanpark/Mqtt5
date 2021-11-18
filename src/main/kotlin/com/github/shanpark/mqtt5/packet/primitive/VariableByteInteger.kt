package com.github.shanpark.mqtt5.packet.primitive

import com.github.shanpark.buffers.ReadBuffer
import com.github.shanpark.buffers.WriteBuffer
import com.github.shanpark.mqtt5.exception.ExceedLimitException
import com.github.shanpark.mqtt5.exception.MalformedPacketException
import com.github.shanpark.mqtt5.exception.NotEnoughDataException
import io.netty.buffer.ByteBuf

object VariableByteInteger {
    fun length(value: Int): Int {
        return if ((value >= 0) && (value < 128)) {
            1
        } else if (value < 16_384) {
            2
        } else if (value < 2_097_152) {
            3
        } else if (value < 268_435_456) {
            4
        } else {
            throw ExceedLimitException("The value can't be expressed by VariableByteInt.")
        }
    }

    fun writeTo(buf: ByteBuf, value: Int) {
        if (value < 0 || value > 0x0fff_ffff)
            throw ExceedLimitException("Try to write value that exceeds limit of VariableByteInt.")

        var data = value
        do {
            var encodedByte = data % 0x80
            data = data.shr(7) // if there are more data to encode, set the top bit of this byte
            if (data > 0)
                encodedByte = encodedByte.or(0x80)
            buf.writeByte(encodedByte)
        } while (data > 0)
    }

    fun writeTo(buf: WriteBuffer, value: Int) {
        if (value < 0 || value > 0x0fff_ffff)
            throw ExceedLimitException("Try to write value that exceeds limit of VariableByteInt.")

        var data = value
        do {
            var encodedByte = data % 0x80
            data = data.shr(7) // if there are more data to encode, set the top bit of this byte
            if (data > 0)
                encodedByte = encodedByte.or(0x80)
            buf.write(encodedByte)
        } while (data > 0)
    }

    fun readFrom(buf: ByteBuf): Int {
        try {
            var value = 0
            var multiplier = 1
            do {
                if (multiplier > 0x80 * 0x80 * 0x80)
                    throw MalformedPacketException("Malformed Variable Byte Integer.")

                val encodedByte: Int = buf.readUnsignedByte().toInt()
                value += encodedByte.and(0x7f) * multiplier
                multiplier *= 0x80
            } while (encodedByte.and(0x80) != 0)
            return value
        } catch(e: IndexOutOfBoundsException) {
            throw NotEnoughDataException("Not enough data for VariableByteInteger.")
        }
    }

    fun readFrom(buf: ReadBuffer): Int {
        try {
            var value = 0
            var multiplier = 1
            do {
                if (multiplier > 0x80 * 0x80 * 0x80)
                    throw MalformedPacketException("Malformed Variable Byte Integer.")

                val encodedByte: Int = buf.read()
                value += encodedByte.and(0x7f) * multiplier
                multiplier *= 0x80
            } while (encodedByte.and(0x80) != 0)
            return value
        } catch(e: IndexOutOfBoundsException) {
            throw NotEnoughDataException("Not enough data for VariableByteInteger.")
        }
    }
}