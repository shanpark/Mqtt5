package com.github.shanpark.mqtt5.exception

class MalformedPacketException : MqttException {
    constructor() : super()
    constructor(msg: String) : super(msg)
    constructor(cause: Throwable) : super(cause)
    constructor(msg: String, cause: Throwable) : super(msg, cause)
}