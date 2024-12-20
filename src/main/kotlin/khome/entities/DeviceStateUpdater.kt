package khome.entities

import com.google.gson.JsonObject
import khome.ActuatorsByApiName
import khome.SensorsByApiName
import khome.values.EntityId
import mu.KotlinLogging

internal class ActuatorStateUpdater(private val actuatorsByApiName: ActuatorsByApiName) {
    private val logger = KotlinLogging.logger { }

    operator fun invoke(newActualState: JsonObject, entityId: EntityId) {
        actuatorsByApiName[entityId]?.let { entity ->
            entity.trySetAttributesFromAny(newAttributes = newActualState)
            entity.trySetActualStateFromAny(newState = newActualState)
            logger.debug { "Updated state for entity: $entityId with: $newActualState" }
        }
    }
}

internal class SensorStateUpdater(private val sensorsByApiName: SensorsByApiName) {
    private val logger = KotlinLogging.logger { }

    operator fun invoke(newActualState: JsonObject, entityId: EntityId) {
        sensorsByApiName[entityId]?.let { entity ->
            entity.trySetAttributesFromAny(newAttributes = newActualState)
            entity.trySetActualStateFromAny(newState = newActualState)
            logger.debug { "Updated state for entity: $entityId with: $newActualState" }
        }
    }
}
