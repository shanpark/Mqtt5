package io.github.shanpark.mqtt5.packet.primitive.constants

enum class MqttProperty(val id: Int) {
    PAYLOAD_FORMAT_INDICATOR(1),
    MESSAGE_EXPIRY_INTERVAL(2),
    CONTENT_TYPE(3),
    RESPONSE_TOPIC(8),
    CORRELATION_DATA(9),
    SUBSCRIPTION_IDENTIFIER(11),
    SESSION_EXPIRY_INTERVAL(17),
    ASSIGNED_CLIENT_IDENTIFIER(18),
    SERVER_KEEP_ALIVE(19),
    AUTHENTICATION_METHOD(21),
    AUTHENTICATION_DATA(22),
    REQUEST_PROBLEM_INFORMATION(23),
    WILL_DELAY_INTERVAL(24),
    REQUEST_RESPONSE_INFORMATION(25),
    RESPONSE_INFORMATION(26),
    SERVER_REFERENCE(28),
    REASON_STRING(31),
    RECEIVE_MAXIMUM(33),
    TOPIC_ALIAS_MAXIMUM(34),
    TOPIC_ALIAS(35),
    MAXIMUM_QOS(36),
    RETAIN_AVAILABLE(37),
    USER_PROPERTY(38),
    MAXIMUM_PACKET_SIZE(39),
    WILDCARD_SUBSCRIPTION_AVAILABLE(40),
    SUBSCRIPTION_IDENTIFIER_AVAILABLE(41),
    SHARED_SUBSCRIPTION_AVAILABLE(42);

    companion object {
        fun valueOf(id: Int): MqttProperty {
            return when (id) {
                1 -> PAYLOAD_FORMAT_INDICATOR
                2 -> MESSAGE_EXPIRY_INTERVAL
                3 -> CONTENT_TYPE
                8 -> RESPONSE_TOPIC
                9 -> CORRELATION_DATA
                11 -> SUBSCRIPTION_IDENTIFIER
                17 -> SESSION_EXPIRY_INTERVAL
                18 -> ASSIGNED_CLIENT_IDENTIFIER
                19 -> SERVER_KEEP_ALIVE
                21 -> AUTHENTICATION_METHOD
                22 -> AUTHENTICATION_DATA
                23 -> REQUEST_PROBLEM_INFORMATION
                24 -> WILL_DELAY_INTERVAL
                25 -> REQUEST_RESPONSE_INFORMATION
                26 -> RESPONSE_INFORMATION
                28 -> SERVER_REFERENCE
                31 -> REASON_STRING
                33 -> RECEIVE_MAXIMUM
                34 -> TOPIC_ALIAS_MAXIMUM
                35 -> TOPIC_ALIAS
                36 -> MAXIMUM_QOS
                37 -> RETAIN_AVAILABLE
                38 -> USER_PROPERTY
                39 -> MAXIMUM_PACKET_SIZE
                40 -> WILDCARD_SUBSCRIPTION_AVAILABLE
                41 -> SUBSCRIPTION_IDENTIFIER_AVAILABLE
                42 -> SHARED_SUBSCRIPTION_AVAILABLE
                else -> throw IllegalArgumentException("No such MQTT Property.")
            }
        }
    }
}