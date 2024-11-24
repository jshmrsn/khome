package khome

import io.ktor.client.plugins.websocket.ClientWebSocketSession
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import khome.core.MessageInterface
import khome.core.mapping.ObjectMapperInterface
import khome.core.mapping.fromJson
import mu.KotlinLogging

internal class KhomeSession(
    delegate: DefaultClientWebSocketSession,
    val objectMapper: ObjectMapperInterface
) : ClientWebSocketSession by delegate {

    private val logger = KotlinLogging.logger {}
    suspend fun callWebSocketApi(message: String) =
        send(Frame.Text(message)).also { logger.debug { "Called hass api with message: $message" } }

    suspend fun callWebSocketApi(message: MessageInterface) =
        send(Frame.Text(message.toJson())).also { logger.debug { "Called hass api with message: ${message.toJson()}" } }

    suspend inline fun <reified M : Any> consumeSingleMessage(): M = incoming.receive().asObject()
    inline fun <reified M : Any> Frame.asObject() = (this as Frame.Text).toObject<M>()
    inline fun <reified M : Any> Frame.Text.toObject(): M = objectMapper.fromJson(readText())

    private fun MessageInterface.toJson(): String = objectMapper.toJson(this)
}
