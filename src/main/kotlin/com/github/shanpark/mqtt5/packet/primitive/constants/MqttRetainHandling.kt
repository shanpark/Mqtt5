package com.github.shanpark.mqtt5.packet.primitive.constants

enum class MqttRetainHandling(val value: Int) {
    SEND_AT_SUBSCRIBE(0), // subscribe할 때 보낸다.
    SEND_AT_SUBSCRIBE_IF_NOT_EXIST(1), // subscribe할 때 이전 subscription이 없었다면 보낸다. 즉 새로 subscribe를 한 경우에만 보낸다.
    DO_NOT_SEND(2); // retain 메세지를 보내지 않는다.

    companion object {
        fun valueOf(value: Int): MqttRetainHandling {
            return when (value) {
                0 -> SEND_AT_SUBSCRIBE
                1 -> SEND_AT_SUBSCRIBE_IF_NOT_EXIST
                2 -> DO_NOT_SEND
                else -> throw IllegalArgumentException("No such Retain Handling option.")
            }
        }
    }
}