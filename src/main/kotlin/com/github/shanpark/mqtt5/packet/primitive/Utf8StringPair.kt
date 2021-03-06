package com.github.shanpark.mqtt5.packet.primitive

import com.github.shanpark.buffers.ReadBuffer
import com.github.shanpark.buffers.WriteBuffer
import io.netty.buffer.ByteBuf

class Utf8StringPair(val name: String, val value: String) {
    fun length(): Int {
        return Utf8EncodedString.length(name) + Utf8EncodedString.length(value)
    }

    companion object {
        fun length(value: Utf8StringPair): Int {
            return value.length()
        }

        fun writeTo(buf: ByteBuf, value: Utf8StringPair) {
            Utf8EncodedString.writeTo(buf, value.name)
            Utf8EncodedString.writeTo(buf, value.value)
        }

        fun writeTo(buf: WriteBuffer, value: Utf8StringPair) {
            Utf8EncodedString.writeTo(buf, value.name)
            Utf8EncodedString.writeTo(buf, value.value)
        }

        fun readFrom(buf: ByteBuf): Utf8StringPair {
            return Utf8StringPair(Utf8EncodedString.readFrom(buf), Utf8EncodedString.readFrom(buf))
        }

        fun readFrom(buf: ReadBuffer): Utf8StringPair {
            return Utf8StringPair(Utf8EncodedString.readFrom(buf), Utf8EncodedString.readFrom(buf))
        }
    }
}