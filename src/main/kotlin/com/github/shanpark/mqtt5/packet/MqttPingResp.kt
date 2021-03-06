package com.github.shanpark.mqtt5.packet

import com.github.shanpark.buffers.ReadBuffer
import com.github.shanpark.buffers.WriteBuffer
import com.github.shanpark.mqtt5.exception.InvalidPacketException
import com.github.shanpark.mqtt5.packet.primitive.OneByteInteger
import com.github.shanpark.mqtt5.packet.primitive.VariableByteInteger
import com.github.shanpark.mqtt5.packet.primitive.constants.MqttPacketType
import io.netty.buffer.ByteBuf

class MqttPingResp(flags: Int = 0, remainingLength: Int = -1): com.github.shanpark.mqtt5.packet.MqttFixedHeader(MqttPacketType.PINGRESP, flags, remainingLength) {

    override fun readFrom(buf: ByteBuf) {
        ///////////////////////////////////////////////////////////////////////////////////////
        // No Variable Header

        ///////////////////////////////////////////////////////////////////////////////////////
        // No Payload
    }

    override fun readFrom(buf: ReadBuffer) {
        ///////////////////////////////////////////////////////////////////////////////////////
        // No Variable Header

        ///////////////////////////////////////////////////////////////////////////////////////
        // No Payload
    }

    override fun writeTo(buf: ByteBuf) {
        if (remainingLength < 0)
            remainingLength = calcLength()

        ///////////////////////////////////////////////////////////////////////////////////////
        // Fixed Header
        OneByteInteger.writeTo(buf, type.value + flags)
        VariableByteInteger.writeTo(buf, remainingLength)

        ///////////////////////////////////////////////////////////////////////////////////////
        // No Variable Header

        ///////////////////////////////////////////////////////////////////////////////////////
        // No Payload
    }

    override fun writeTo(buf: WriteBuffer) {
        if (remainingLength < 0)
            remainingLength = calcLength()

        ///////////////////////////////////////////////////////////////////////////////////////
        // Fixed Header
        OneByteInteger.writeTo(buf, type.value + flags)
        VariableByteInteger.writeTo(buf, remainingLength)

        ///////////////////////////////////////////////////////////////////////////////////////
        // No Variable Header

        ///////////////////////////////////////////////////////////////////////////////////////
        // No Payload
    }

    /**
     * remainingLength를 계산하여 반환한다.
     * 따라서 모든 필요한 값들이 채워진 후에 호출해야한다. flags 상태와 값들의 상태가 일치하지 않으면 예외가 발생한다.
     *
     * @throws InvalidPacketException flags 상태와 값들의 상태가 일치하지 않을 때 발생한다.
     */
    override fun calcLength(): Int {
        return 0
    }

    companion object {
        val INSTANCE = MqttPingResp()
    }
}