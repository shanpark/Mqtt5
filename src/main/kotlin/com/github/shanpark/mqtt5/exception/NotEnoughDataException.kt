package com.github.shanpark.mqtt5.exception

/**
 * 패킷이 완성될 수 있을만큼 데이터 양이 아직 충분하지 않은 경우 발생한다.
 */
class NotEnoughDataException : MqttException {
    constructor() : super() {}
    constructor(msg: String) : super(msg) {}
    constructor(cause: Throwable) : super(cause) {}
    constructor(msg: String, cause: Throwable) : super(msg, cause) {}
}