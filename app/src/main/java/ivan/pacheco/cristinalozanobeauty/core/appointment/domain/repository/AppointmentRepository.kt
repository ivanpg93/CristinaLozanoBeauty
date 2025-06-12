package ivan.pacheco.cristinalozanobeauty.core.appointment.domain.repository

import io.reactivex.Completable
import io.reactivex.Single
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.model.Appointment

interface AppointmentRepository {
    fun list(): Single<List<Appointment>>
    fun find(id: String): Single<Appointment>
    fun create(client: Appointment): Completable
    fun update(client: Appointment): Completable
    fun delete(client: Appointment): Completable
}