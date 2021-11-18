package com.github.shanpark.mqtt5.packet.primitive.constants

enum class MqttPacketType(val value: Int) {
    CONNECT(1.shl(4)),
    CONNACK(2.shl(4)),
    PUBLISH(3.shl(4)),
    PUBACK(4.shl(4)),
    PUBREC(5.shl(4)),
    PUBREL(6.shl(4)),
    PUBCOMP(7.shl(4)),
    SUBSCRIBE(8.shl(4)),
    SUBACK(9.shl(4)),
    UNSUBSCRIBE(10.shl(4)),
    UNSUBACK(11.shl(4)),
    PINGREQ(12.shl(4)),
    PINGRESP(13.shl(4)),
    DISCONNECT(14.shl(4)),
    AUTH(15.shl(4));

    companion object {
        fun valueOf(type: Int): MqttPacketType {
            return when (type.and(0xf0)) {
                1.shl(4) -> CONNECT
                2.shl(4) -> CONNACK
                3.shl(4) -> PUBLISH
                4.shl(4) -> PUBACK
                5.shl(4) -> PUBREC
                6.shl(4) -> PUBREL
                7.shl(4) -> PUBCOMP
                8.shl(4) -> SUBSCRIBE
                9.shl(4) -> SUBACK
                10.shl(4) -> UNSUBSCRIBE
                11.shl(4) -> UNSUBACK
                12.shl(4) -> PINGREQ
                13.shl(4) -> PINGRESP
                14.shl(4) -> DISCONNECT
                15.shl(4) -> AUTH
                else -> throw IllegalArgumentException("No such MQTT Control packet type.")
            }
        }
    }
}
