import com.github.shanpark.buffers.Buffer
import com.github.shanpark.mqtt5.packet.*
import com.github.shanpark.mqtt5.packet.derivative.MqttProperties
import com.github.shanpark.mqtt5.packet.derivative.MqttSubscriptionOptions
import com.github.shanpark.mqtt5.packet.primitive.OneByteInteger
import com.github.shanpark.mqtt5.packet.primitive.VariableByteInteger
import com.github.shanpark.mqtt5.packet.primitive.constants.MqttPacketType
import com.github.shanpark.mqtt5.packet.primitive.constants.MqttProperty
import com.github.shanpark.mqtt5.packet.primitive.constants.MqttQos
import com.github.shanpark.mqtt5.packet.primitive.constants.MqttReasonCode
import io.netty.buffer.ByteBuf
import io.netty.buffer.PooledByteBufAllocator
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Fail
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class Test {
    private val properties: MqttProperties = MqttProperties()

    @Test
    @DisplayName("Read/WriteBuffer Serialize/Deserialize 테스트")
    internal fun testFromBuffer() {
        properties[MqttProperty.SERVER_KEEP_ALIVE] = 65533

        var auth = MqttAuth()
        auth.properties = properties
        auth.authReasonCode = MqttReasonCode.BANNED
        auth = rwBufferTestUnit(auth) as MqttAuth
        assertThat(auth.authReasonCode).isEqualTo(MqttReasonCode.BANNED)
        assertThat(auth.properties[MqttProperty.SERVER_KEEP_ALIVE]).isEqualTo(65533)

        var connAck = MqttConnAck()
        connAck.properties = properties
        connAck.connectReasonCode = MqttReasonCode.BANNED
        connAck.connAckFlags = 3
        connAck = rwBufferTestUnit(connAck) as MqttConnAck
        assertThat(connAck.connAckFlags).isEqualTo(3)
        assertThat(connAck.connectReasonCode).isEqualTo(MqttReasonCode.BANNED)
        assertThat(connAck.properties[MqttProperty.SERVER_KEEP_ALIVE]).isEqualTo(65533)

        var connect = MqttConnect()
        connect.cleanStart = true
        connect.properties = properties
        connect.keepAlive = 4827
        connect.clientId = "client10283"
        connect.willFlag = true
        connect.willTopic = "hellotopic"
        connect.willPayload = byteArrayOf(1, 2, 3, 4, 5)
        connect.userNameFlag = true
        connect.userName = "userName"
        connect.passwordFlag = true
        connect.password = byteArrayOf(1, 2, 3, 4)
        connect = rwBufferTestUnit(connect) as MqttConnect
        assertThat(connect.password).isEqualTo(byteArrayOf(1, 2, 3, 4))
        assertThat(connect.passwordFlag).isTrue
        assertThat(connect.userName).isEqualTo("userName")
        assertThat(connect.userNameFlag).isTrue
        assertThat(connect.willPayload).isEqualTo(byteArrayOf(1, 2, 3, 4, 5))
        assertThat(connect.willTopic).isEqualTo("hellotopic")
        assertThat(connect.willFlag).isTrue
        assertThat(connect.clientId).isEqualTo("client10283")
        assertThat(connect.keepAlive).isEqualTo(4827)
        assertThat(connect.properties[MqttProperty.SERVER_KEEP_ALIVE]).isEqualTo(65533)
        assertThat(connect.cleanStart).isTrue

        var disconnect = MqttDisconnect()
        disconnect.properties = properties
        disconnect.disconnectReasonCode = MqttReasonCode.BANNED
        disconnect = rwBufferTestUnit(disconnect) as MqttDisconnect
        assertThat(disconnect.disconnectReasonCode).isEqualTo(MqttReasonCode.BANNED)
        assertThat(disconnect.properties[MqttProperty.SERVER_KEEP_ALIVE]).isEqualTo(65533)

        rwBufferTestUnit(MqttPingReq())

        rwBufferTestUnit(MqttPingResp())

        var pubAck = MqttPubAck()
        pubAck.properties = properties
        pubAck.pubAckReasonCode = MqttReasonCode.BANNED
        pubAck.packetId = 5938
        pubAck = rwBufferTestUnit(pubAck) as MqttPubAck
        assertThat(pubAck.packetId).isEqualTo(5938)
        assertThat(pubAck.pubAckReasonCode).isEqualTo(MqttReasonCode.BANNED)
        assertThat(pubAck.properties[MqttProperty.SERVER_KEEP_ALIVE]).isEqualTo(65533)

        var pubComp = MqttPubComp()
        pubComp.properties = properties
        pubComp.pubCompReasonCode = MqttReasonCode.BANNED
        pubComp.packetId = 5938
        pubComp = rwBufferTestUnit(pubComp) as MqttPubComp
        assertThat(pubComp.packetId).isEqualTo(5938)
        assertThat(pubComp.pubCompReasonCode).isEqualTo(MqttReasonCode.BANNED)
        assertThat(pubComp.properties[MqttProperty.SERVER_KEEP_ALIVE]).isEqualTo(65533)

        var publish = MqttPublish()
        publish.properties = properties
        publish.topicName = "hellotopic"
        publish.packetId = 5938
        publish.qos = MqttQos.EXACTLY_ONCE
        publish.payload = byteArrayOf(1, 2, 3, 4, 5)
        publish = rwBufferTestUnit(publish) as MqttPublish
        assertThat(publish.payload).isEqualTo(byteArrayOf(1, 2, 3, 4, 5))
        assertThat(publish.qos).isEqualTo(MqttQos.EXACTLY_ONCE)
        assertThat(publish.packetId).isEqualTo(5938)
        assertThat(publish.topicName).isEqualTo("hellotopic")
        assertThat(publish.properties[MqttProperty.SERVER_KEEP_ALIVE]).isEqualTo(65533)

        var pubRec = MqttPubRec()
        pubRec.properties = properties
        pubRec.pubRecReasonCode = MqttReasonCode.BANNED
        pubRec.packetId = 5938
        pubRec = rwBufferTestUnit(pubRec) as MqttPubRec
        assertThat(pubRec.packetId).isEqualTo(5938)
        assertThat(pubRec.pubRecReasonCode).isEqualTo(MqttReasonCode.BANNED)
        assertThat(pubRec.properties[MqttProperty.SERVER_KEEP_ALIVE]).isEqualTo(65533)

        var pubRel = MqttPubRel()
        pubRel.properties = properties
        pubRel.pubRelReasonCode = MqttReasonCode.BANNED
        pubRel.packetId = 5938
        pubRel = rwBufferTestUnit(pubRel) as MqttPubRel
        assertThat(pubRel.packetId).isEqualTo(5938)
        assertThat(pubRel.pubRelReasonCode).isEqualTo(MqttReasonCode.BANNED)
        assertThat(pubRel.properties[MqttProperty.SERVER_KEEP_ALIVE]).isEqualTo(65533)

        var subAck = MqttSubAck()
        subAck.properties = properties
        subAck.reasonCodes = listOf(MqttReasonCode.BANNED)
        subAck.packetId = 5938
        subAck = rwBufferTestUnit(subAck) as MqttSubAck
        assertThat(subAck.packetId).isEqualTo(5938)
        assertThat(subAck.reasonCodes).isEqualTo(listOf(MqttReasonCode.BANNED))
        assertThat(subAck.properties[MqttProperty.SERVER_KEEP_ALIVE]).isEqualTo(65533)

        var subscribe = MqttSubscribe()
        subscribe.properties = properties
        subscribe.packetId = 5938
        subscribe.topicFilters = listOf(Pair("hellotopic", MqttSubscriptionOptions(0x11)))
        subscribe = rwBufferTestUnit(subscribe) as MqttSubscribe
        assertThat(subscribe.packetId).isEqualTo(5938)
        assertThat(subscribe.topicFilters).isEqualTo(listOf(Pair("hellotopic", MqttSubscriptionOptions(0x11))))
        assertThat(subscribe.properties[MqttProperty.SERVER_KEEP_ALIVE]).isEqualTo(65533)

        var unsubAck = MqttUnsubAck()
        unsubAck.properties = properties
        unsubAck.packetId = 5938
        unsubAck.reasonCodes = listOf(MqttReasonCode.BANNED)
        unsubAck = rwBufferTestUnit(unsubAck) as MqttUnsubAck
        assertThat(unsubAck.reasonCodes).isEqualTo(listOf(MqttReasonCode.BANNED))
        assertThat(unsubAck.packetId).isEqualTo(5938)
        assertThat(unsubAck.properties[MqttProperty.SERVER_KEEP_ALIVE]).isEqualTo(65533)

        var unsubscribe = MqttUnsubscribe()
        unsubscribe.properties = properties
        unsubscribe.packetId = 5938
        unsubscribe.topicFilters = listOf("hellotopic")
        unsubscribe = rwBufferTestUnit(unsubscribe) as MqttUnsubscribe
        assertThat(unsubscribe.topicFilters).isEqualTo(listOf("hellotopic"))
        assertThat(unsubscribe.packetId).isEqualTo(5938)
        assertThat(unsubscribe.properties[MqttProperty.SERVER_KEEP_ALIVE]).isEqualTo(65533)
    }

    @Test
    @DisplayName("ByteBuf Serialize/Deserialize 테스트")
    internal fun testFromByteBuf() {
        properties[MqttProperty.SERVER_KEEP_ALIVE] = 65533

        var auth = MqttAuth()
        auth.properties = properties
        auth.authReasonCode = MqttReasonCode.BANNED
        auth = byteBufTestUnit(auth) as MqttAuth
        assertThat(auth.authReasonCode).isEqualTo(MqttReasonCode.BANNED)
        assertThat(auth.properties[MqttProperty.SERVER_KEEP_ALIVE]).isEqualTo(65533)

        var connAck = MqttConnAck()
        connAck.properties = properties
        connAck.connectReasonCode = MqttReasonCode.BANNED
        connAck.connAckFlags = 3
        connAck = byteBufTestUnit(connAck) as MqttConnAck
        assertThat(connAck.connAckFlags).isEqualTo(3)
        assertThat(connAck.connectReasonCode).isEqualTo(MqttReasonCode.BANNED)
        assertThat(connAck.properties[MqttProperty.SERVER_KEEP_ALIVE]).isEqualTo(65533)

        var connect = MqttConnect()
        connect.cleanStart = true
        connect.properties = properties
        connect.keepAlive = 4827
        connect.clientId = "client10283"
        connect.willFlag = true
        connect.willTopic = "hellotopic"
        connect.willPayload = byteArrayOf(1, 2, 3, 4, 5)
        connect.userNameFlag = true
        connect.userName = "userName"
        connect.passwordFlag = true
        connect.password = byteArrayOf(1, 2, 3, 4)
        connect = byteBufTestUnit(connect) as MqttConnect
        assertThat(connect.password).isEqualTo(byteArrayOf(1, 2, 3, 4))
        assertThat(connect.passwordFlag).isTrue
        assertThat(connect.userName).isEqualTo("userName")
        assertThat(connect.userNameFlag).isTrue
        assertThat(connect.willPayload).isEqualTo(byteArrayOf(1, 2, 3, 4, 5))
        assertThat(connect.willTopic).isEqualTo("hellotopic")
        assertThat(connect.willFlag).isTrue
        assertThat(connect.clientId).isEqualTo("client10283")
        assertThat(connect.keepAlive).isEqualTo(4827)
        assertThat(connect.properties[MqttProperty.SERVER_KEEP_ALIVE]).isEqualTo(65533)
        assertThat(connect.cleanStart).isTrue

        var disconnect = MqttDisconnect()
        disconnect.properties = properties
        disconnect.disconnectReasonCode = MqttReasonCode.BANNED
        disconnect = byteBufTestUnit(disconnect) as MqttDisconnect
        assertThat(disconnect.disconnectReasonCode).isEqualTo(MqttReasonCode.BANNED)
        assertThat(disconnect.properties[MqttProperty.SERVER_KEEP_ALIVE]).isEqualTo(65533)

        byteBufTestUnit(MqttPingReq())

        byteBufTestUnit(MqttPingResp())

        var pubAck = MqttPubAck()
        pubAck.properties = properties
        pubAck.pubAckReasonCode = MqttReasonCode.BANNED
        pubAck.packetId = 5938
        pubAck = byteBufTestUnit(pubAck) as MqttPubAck
        assertThat(pubAck.packetId).isEqualTo(5938)
        assertThat(pubAck.pubAckReasonCode).isEqualTo(MqttReasonCode.BANNED)
        assertThat(pubAck.properties[MqttProperty.SERVER_KEEP_ALIVE]).isEqualTo(65533)

        var pubComp = MqttPubComp()
        pubComp.properties = properties
        pubComp.pubCompReasonCode = MqttReasonCode.BANNED
        pubComp.packetId = 5938
        pubComp = byteBufTestUnit(pubComp) as MqttPubComp
        assertThat(pubComp.packetId).isEqualTo(5938)
        assertThat(pubComp.pubCompReasonCode).isEqualTo(MqttReasonCode.BANNED)
        assertThat(pubComp.properties[MqttProperty.SERVER_KEEP_ALIVE]).isEqualTo(65533)

        var publish = MqttPublish()
        publish.properties = properties
        publish.topicName = "hellotopic"
        publish.packetId = 5938
        publish.qos = MqttQos.EXACTLY_ONCE
        publish.payload = byteArrayOf(1, 2, 3, 4, 5)
        publish = byteBufTestUnit(publish) as MqttPublish
        assertThat(publish.payload).isEqualTo(byteArrayOf(1, 2, 3, 4, 5))
        assertThat(publish.qos).isEqualTo(MqttQos.EXACTLY_ONCE)
        assertThat(publish.packetId).isEqualTo(5938)
        assertThat(publish.topicName).isEqualTo("hellotopic")
        assertThat(publish.properties[MqttProperty.SERVER_KEEP_ALIVE]).isEqualTo(65533)

        var pubRec = MqttPubRec()
        pubRec.properties = properties
        pubRec.pubRecReasonCode = MqttReasonCode.BANNED
        pubRec.packetId = 5938
        pubRec = byteBufTestUnit(pubRec) as MqttPubRec
        assertThat(pubRec.packetId).isEqualTo(5938)
        assertThat(pubRec.pubRecReasonCode).isEqualTo(MqttReasonCode.BANNED)
        assertThat(pubRec.properties[MqttProperty.SERVER_KEEP_ALIVE]).isEqualTo(65533)

        var pubRel = MqttPubRel()
        pubRel.properties = properties
        pubRel.pubRelReasonCode = MqttReasonCode.BANNED
        pubRel.packetId = 5938
        pubRel = byteBufTestUnit(pubRel) as MqttPubRel
        assertThat(pubRel.packetId).isEqualTo(5938)
        assertThat(pubRel.pubRelReasonCode).isEqualTo(MqttReasonCode.BANNED)
        assertThat(pubRel.properties[MqttProperty.SERVER_KEEP_ALIVE]).isEqualTo(65533)

        var subAck = MqttSubAck()
        subAck.properties = properties
        subAck.reasonCodes = listOf(MqttReasonCode.BANNED)
        subAck.packetId = 5938
        subAck = byteBufTestUnit(subAck) as MqttSubAck
        assertThat(subAck.packetId).isEqualTo(5938)
        assertThat(subAck.reasonCodes).isEqualTo(listOf(MqttReasonCode.BANNED))
        assertThat(subAck.properties[MqttProperty.SERVER_KEEP_ALIVE]).isEqualTo(65533)

        var subscribe = MqttSubscribe()
        subscribe.properties = properties
        subscribe.packetId = 5938
        subscribe.topicFilters = listOf(Pair("hellotopic", MqttSubscriptionOptions(0x11)))
        subscribe = byteBufTestUnit(subscribe) as MqttSubscribe
        assertThat(subscribe.packetId).isEqualTo(5938)
        assertThat(subscribe.topicFilters).isEqualTo(listOf(Pair("hellotopic", MqttSubscriptionOptions(0x11))))
        assertThat(subscribe.properties[MqttProperty.SERVER_KEEP_ALIVE]).isEqualTo(65533)

        var unsubAck = MqttUnsubAck()
        unsubAck.properties = properties
        unsubAck.packetId = 5938
        unsubAck.reasonCodes = listOf(MqttReasonCode.BANNED)
        unsubAck = byteBufTestUnit(unsubAck) as MqttUnsubAck
        assertThat(unsubAck.reasonCodes).isEqualTo(listOf(MqttReasonCode.BANNED))
        assertThat(unsubAck.packetId).isEqualTo(5938)
        assertThat(unsubAck.properties[MqttProperty.SERVER_KEEP_ALIVE]).isEqualTo(65533)

        var unsubscribe = MqttUnsubscribe()
        unsubscribe.properties = properties
        unsubscribe.packetId = 5938
        unsubscribe.topicFilters = listOf("hellotopic")
        unsubscribe = byteBufTestUnit(unsubscribe) as MqttUnsubscribe
        assertThat(unsubscribe.topicFilters).isEqualTo(listOf("hellotopic"))
        assertThat(unsubscribe.packetId).isEqualTo(5938)
        assertThat(unsubscribe.properties[MqttProperty.SERVER_KEEP_ALIVE]).isEqualTo(65533)
    }

    private fun rwBufferTestUnit(packet: MqttFixedHeader): MqttFixedHeader {
        val buf = Buffer(1024)

        packet.writeTo(buf)
        assertThat(buf.readableBytes).isEqualTo(2 + packet.remainingLength)

        val decoded = rwBufferDecode(buf)
        assertThat(buf.readableBytes).isEqualTo(0)
        assertThat(decoded.type).isEqualTo(packet.type)

        return decoded
    }

    private fun byteBufTestUnit(packet: MqttFixedHeader): MqttFixedHeader {
        val buf = PooledByteBufAllocator.DEFAULT.buffer()

        packet.writeTo(buf)
        assertThat(buf.readableBytes()).isEqualTo(2 + packet.remainingLength)

        val decoded = byteBufDecode(buf)
        assertThat(buf.readableBytes()).isEqualTo(0)
        assertThat(decoded.type).isEqualTo(packet.type)

        buf.release()

        return decoded
    }

    private fun rwBufferDecode(buf: Buffer): MqttFixedHeader {
        val typeAndFlags = OneByteInteger.readFrom(buf)
        val remainingLength = VariableByteInteger.readFrom(buf)
        if (buf.readableBytes < remainingLength)
            Fail.fail<Unit>("'buf' should have enough data.")

        val packet = when (MqttPacketType.valueOf(typeAndFlags.and(0xf0))) {
            MqttPacketType.CONNECT -> MqttConnect(
                typeAndFlags.and(0x0f),
                remainingLength
            )
            MqttPacketType.CONNACK -> MqttConnAck(
                typeAndFlags.and(0x0f),
                remainingLength
            )
            MqttPacketType.PUBLISH -> MqttPublish(
                typeAndFlags.and(0x0f),
                remainingLength
            )
            MqttPacketType.PUBACK -> MqttPubAck(
                typeAndFlags.and(0x0f),
                remainingLength
            )
            MqttPacketType.PUBREC -> MqttPubRec(
                typeAndFlags.and(0x0f),
                remainingLength
            )
            MqttPacketType.PUBREL -> MqttPubRel(
                typeAndFlags.and(0x0f),
                remainingLength
            )
            MqttPacketType.PUBCOMP -> MqttPubComp(
                typeAndFlags.and(0x0f),
                remainingLength
            )
            MqttPacketType.SUBSCRIBE -> MqttSubscribe(typeAndFlags.and(0x0f), remainingLength)
            MqttPacketType.SUBACK -> MqttSubAck(
                typeAndFlags.and(0x0f),
                remainingLength
            )
            MqttPacketType.UNSUBSCRIBE -> MqttUnsubscribe(typeAndFlags.and(0x0f), remainingLength)
            MqttPacketType.UNSUBACK -> MqttUnsubAck(typeAndFlags.and(0x0f), remainingLength)
            MqttPacketType.PINGREQ -> MqttPingReq(
                typeAndFlags.and(0x0f),
                remainingLength
            )
            MqttPacketType.PINGRESP -> MqttPingResp(
                typeAndFlags.and(0x0f),
                remainingLength
            )
            MqttPacketType.DISCONNECT -> MqttDisconnect(
                typeAndFlags.and(0x0f),
                remainingLength
            )
            MqttPacketType.AUTH -> MqttAuth(typeAndFlags.and(0x0f), remainingLength)
        }
        packet.readFrom(buf.readSlice(remainingLength)) // 반드시 slice로 잘라서 넘겨야함. 남은 데이터 끝까지 읽어야 하는 경우가 있기 때문에
        return packet
    }

    private fun byteBufDecode(buf: ByteBuf): MqttFixedHeader {
        val typeAndFlags = OneByteInteger.readFrom(buf)
        val remainingLength = VariableByteInteger.readFrom(buf)
        if (buf.readableBytes() < remainingLength)
            Fail.fail<Unit>("'buf' should have enough data.")

        val packet = when (MqttPacketType.valueOf(typeAndFlags.and(0xf0))) {
            MqttPacketType.CONNECT -> MqttConnect(
                typeAndFlags.and(0x0f),
                remainingLength
            )
            MqttPacketType.CONNACK -> MqttConnAck(
                typeAndFlags.and(0x0f),
                remainingLength
            )
            MqttPacketType.PUBLISH -> MqttPublish(
                typeAndFlags.and(0x0f),
                remainingLength
            )
            MqttPacketType.PUBACK -> MqttPubAck(
                typeAndFlags.and(0x0f),
                remainingLength
            )
            MqttPacketType.PUBREC -> MqttPubRec(
                typeAndFlags.and(0x0f),
                remainingLength
            )
            MqttPacketType.PUBREL -> MqttPubRel(
                typeAndFlags.and(0x0f),
                remainingLength
            )
            MqttPacketType.PUBCOMP -> MqttPubComp(
                typeAndFlags.and(0x0f),
                remainingLength
            )
            MqttPacketType.SUBSCRIBE -> MqttSubscribe(typeAndFlags.and(0x0f), remainingLength)
            MqttPacketType.SUBACK -> MqttSubAck(
                typeAndFlags.and(0x0f),
                remainingLength
            )
            MqttPacketType.UNSUBSCRIBE -> MqttUnsubscribe(typeAndFlags.and(0x0f), remainingLength)
            MqttPacketType.UNSUBACK -> MqttUnsubAck(typeAndFlags.and(0x0f), remainingLength)
            MqttPacketType.PINGREQ -> MqttPingReq(
                typeAndFlags.and(0x0f),
                remainingLength
            )
            MqttPacketType.PINGRESP -> MqttPingResp(
                typeAndFlags.and(0x0f),
                remainingLength
            )
            MqttPacketType.DISCONNECT -> MqttDisconnect(
                typeAndFlags.and(0x0f),
                remainingLength
            )
            MqttPacketType.AUTH -> MqttAuth(typeAndFlags.and(0x0f), remainingLength)
        }
        packet.readFrom(buf.readSlice(remainingLength)) // 반드시 slice로 잘라서 넘겨야함. 남은 데이터 끝까지 읽어야 하는 경우가 있기 때문에
        return packet
    }
}