package ivan.pacheco.cristinalozanobeauty.core.client.domain.model

import com.google.firebase.Timestamp
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
    val medication: String = "",
    val allergy: String = "",
    val hasDiabetes: Boolean = false,
    val hasPoorCoagulation: Boolean = false,
    val others: String = "",
    val appointmentList: List<Appointment> = listOf()
) {

    fun toMap() = mapOf(
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
        "medication" to medication,
        "allergy" to allergy,
        "hasDiabetes" to hasDiabetes,
        "hasPoorCoagulation" to hasPoorCoagulation,
        "others" to others
    )

}
