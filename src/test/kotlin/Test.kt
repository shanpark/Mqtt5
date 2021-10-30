import io.github.shanpark.mqtt5.packet.*
import io.github.shanpark.mqtt5.packet.primitive.OneByteInteger
import io.github.shanpark.mqtt5.packet.primitive.VariableByteInteger
import io.github.shanpark.mqtt5.packet.primitive.constants.MqttPacketType
import io.github.shanpark.mqtt5.packet.primitive.constants.MqttProperty
import io.netty.buffer.ByteBuf
import io.netty.buffer.PooledByteBufAllocator
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Fail
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class Test {
    @Test
    @DisplayName("Serialize/Deserialize 테스트")
    internal fun packetTest() {

        testUnit(MqttPingReq())

        testUnit(MqttPingResp())

        var connect = MqttConnect()
        connect.connectFlags = 0x02
        connect.keepAlive = 65535
        connect.clientId = "10102020"
        connect.properties[MqttProperty.SERVER_KEEP_ALIVE] = 255
        connect = testUnit(connect) as MqttConnect
        assertThat(connect.connectFlags).isEqualTo(0x02)
        assertThat(connect.keepAlive).isEqualTo(65535)
        assertThat(connect.clientId).isEqualTo("10102020")
        assertThat(connect.properties[MqttProperty.SERVER_KEEP_ALIVE]).isEqualTo(255)
    }

    private fun testUnit(packet: MqttFixedHeader): MqttFixedHeader {
        val buf = PooledByteBufAllocator.DEFAULT.buffer()

        packet.writeTo(buf)
        assertThat(buf.readableBytes()).isEqualTo(2 + packet.remainingLength)

        val decoded = decode(buf)
        assertThat(buf.readableBytes()).isEqualTo(0)
        assertThat(decoded.type).isEqualTo(packet.type)

        buf.release()

        return decoded
    }

    private fun decode(buf: ByteBuf): MqttFixedHeader {
        val typeAndFlags = OneByteInteger.readFrom(buf)
        val remainingLength = VariableByteInteger.readFrom(buf)
        if (buf.readableBytes() < remainingLength)
            Fail.fail<Unit>("'buf' should have enough data.")

        val packet = when (MqttPacketType.valueOf(typeAndFlags.and(0xf0))) {
            MqttPacketType.CONNECT -> MqttConnect(typeAndFlags.and(0x0f), remainingLength)
            MqttPacketType.CONNACK -> MqttConnAck(typeAndFlags.and(0x0f), remainingLength)
            MqttPacketType.PUBLISH -> MqttPublish(typeAndFlags.and(0x0f), remainingLength)
            MqttPacketType.PUBACK -> MqttPubAck(typeAndFlags.and(0x0f), remainingLength)
            MqttPacketType.PUBREC -> MqttPubRec(typeAndFlags.and(0x0f), remainingLength)
            MqttPacketType.PUBREL -> MqttPubRel(typeAndFlags.and(0x0f), remainingLength)
            MqttPacketType.PUBCOMP -> MqttPubComp(typeAndFlags.and(0x0f), remainingLength)
            MqttPacketType.SUBSCRIBE -> MqttSubscribe(typeAndFlags.and(0x0f), remainingLength)
            MqttPacketType.SUBACK -> MqttSubAck(typeAndFlags.and(0x0f), remainingLength)
            MqttPacketType.UNSUBSCRIBE -> MqttUnsubscribe(typeAndFlags.and(0x0f), remainingLength)
            MqttPacketType.UNSUBACK -> MqttUnsubAck(typeAndFlags.and(0x0f), remainingLength)
            MqttPacketType.PINGREQ -> MqttPingReq(typeAndFlags.and(0x0f), remainingLength)
            MqttPacketType.PINGRESP -> MqttPingResp(typeAndFlags.and(0x0f), remainingLength)
            MqttPacketType.DISCONNECT -> MqttDisconnect(typeAndFlags.and(0x0f), remainingLength)
            MqttPacketType.AUTH -> MqttAuth(typeAndFlags.and(0x0f), remainingLength)
        }
        packet.readFrom(buf.readSlice(remainingLength))
        return packet
    }
}