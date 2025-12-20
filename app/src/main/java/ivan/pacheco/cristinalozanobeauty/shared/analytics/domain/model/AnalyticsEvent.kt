package ivan.pacheco.cristinalozanobeauty.shared.analytics.domain.model

import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.model.Appointment
import ivan.pacheco.cristinalozanobeauty.core.client.domain.model.Client
import ivan.pacheco.cristinalozanobeauty.core.event.domain.model.CalendarEvent
import java.time.LocalDate

sealed class AnalyticsEvent(
    val name: String,
    val params: Map<String, Any?> = emptyMap()
) {

    class ClientCreated(client: Client): AnalyticsEvent(
        "client_created",
        mapOf(
            "has_email" to client.email.isNotBlank(),
            "has_phone" to client.phone.isNotBlank(),
            "has_birthdate" to (client.birthday != null),
        )
    )

    class AppointmentCreated(event: CalendarEvent): AnalyticsEvent(
        "appointment_created",
        mapOf(
            "schedule" to schedule(event.startDateTime),
            "day_of_week" to dayOfWeek(event.startDateTime),
            "service" to (event.service?.name ?: "UNKNOWN")
        )
    )

    class AppointmentAttendanceUpdated(
        appointment: Appointment
    ) : AnalyticsEvent(
        "appointment_attendance_updated",
        mapOf(
            "id" to appointment.id,
            "assisted" to (appointment.event?.assisted ?: false),
            "schedule" to schedule(appointment.event?.startDateTime ?: ""),
            "day_of_week" to dayOfWeek(appointment.event?.startDateTime ?: ""),
            "service" to (appointment.event?.service?.name ?: "UNKNOWN")
        )
    )

    companion object {
        private fun schedule(startDateTime: String): String {
            val hour = startDateTime.substringAfter("T").substringBefore(":").toIntOrNull() ?: 0
            return if (hour in 9..14) "morning" else "afternoon"
        }

        private fun dayOfWeek(startDateTime: String): String {
            return try {
                val date = LocalDate.parse(startDateTime.substringBefore("T"))
                date.dayOfWeek.name.lowercase()
            } catch (_: Exception) {
                "unknown"
            }
        }
    }

}
