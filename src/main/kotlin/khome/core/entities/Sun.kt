package khome.core.entities

import java.time.ZoneId
import java.time.LocalDateTime
import java.time.OffsetDateTime

object Sun : AbstractEntity<String>("sun", "sun") {
    val isUp get() = state.getValue<String>() == "above_horizon"
    val isDown get() = state.getValue<String>() == "below_horizon"

    val nextSunrise get() = getNextSunPosition("next_rising")
    val nextSunset get() = getNextSunPosition("next_setting")

    private fun getNextSunPosition(nextPosition: String): LocalDateTime {
        val nextSunPositionChange = Sun.getAttributeValue<String>(nextPosition)
        return convertUtcToLocalDateTime(nextSunPositionChange)
    }

    private fun convertUtcToLocalDateTime(utcDateTime: String): LocalDateTime {
        val offsetDateTime = OffsetDateTime.parse(utcDateTime)
        val zonedDateTime = offsetDateTime.atZoneSameInstant(ZoneId.systemDefault())
        return zonedDateTime.toLocalDateTime()
    }
}