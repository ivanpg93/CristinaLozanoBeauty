package ivan.pacheco.cristinalozanobeauty.core.client.domain.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.model.Appointment
import java.util.Date

enum class NailDisorder {
    LEUCONIQUIA,
    HEMATOMA,
    ONICOFAGIA,
    ONICOLISIS,
    ONICOMADESIS,
    ONICOMICOSIS,
    ONICOQUICIA,
    PADRASTRO,
    PARONIQUIA,
    PSORIASIS,
    UÑAS_AMARILLENTAS,
    SURCO,
    TRAQUILONIQUIA,
}

enum class SkinDisorder {
    CALLOS,
    DISHIDROSIS,
    HIPERHIDROSIS,
    ESCEMAS,
    PSORIASIS,
    QUERATOSIS_PALMAR,
    VERRUGAS,
    VITILIGO,
    SABAÑONES
}

data class Client(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val phone: String = "",
    val email: String = "",
    val birthday: Date? = null,
    val profession: String = "",
    val town: String = "",
    val nailDisorderList: List<NailDisorder> = listOf(),
    val skinDisorderList: List<SkinDisorder> = listOf(),
    val treatment: String = "",
    val allergy: String = "",
    val appointmentList: List<Appointment> = listOf()
) {

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "firstName" to firstName,
            "lastName" to lastName,
            "phone" to phone,
            "email" to email,
            "birthday" to birthday?.let { Timestamp(it) },
            "profession" to profession,
            "town" to town,
            "nailDisorderList" to nailDisorderList.map { it.name },
            "skinDisorderList" to skinDisorderList.map { it.name },
            "treatment" to treatment,
            "allergy" to allergy
        )
    }

}
