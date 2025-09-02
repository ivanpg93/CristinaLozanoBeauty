package ivan.pacheco.cristinalozanobeauty.core.appointment.domain.repository

import io.reactivex.Completable
import io.reactivex.Single
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.model.Appointment

interface AppointmentRepository {
    fun list(clientId: String): Single<List<Appointment>>
    fun find(eventId: String): Single<Appointment>
    fun create(appointment: Appointment, clientId: String): Completable
    fun update(appointment: Appointment): Completable
    fun delete(eventId: String): Completable
}