package com.github.shanpark.mqtt5.packet.primitive.constants

import com.github.shanpark.mqtt5.exception.MalformedPacketException

enum class MqttQos(val level: Int) {
    AT_MOST_ONCE(0),
    AT_LEAST_ONCE(1),
    EXACTLY_ONCE(2);

    companion object {
        fun valueOf(level: Int): MqttQos {
            return when (level) {
                0 -> AT_MOST_ONCE
                1 -> AT_LEAST_ONCE
                2 -> EXACTLY_ONCE
                else -> throw MalformedPacketException("No such MQTT QOS Level.")
            }
        }

        fun min(qos0: MqttQos, qos1: MqttQos): MqttQos {
            return if (qos0.level < qos1.level)
                qos0
            else
                qos1
        }
    }
}
