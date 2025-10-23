package ivan.pacheco.cristinalozanobeauty.core.client.domain.model

import com.google.firebase.Timestamp
import ivan.pacheco.cristinalozanobeauty.core.appointment.domain.model.Appointment
import java.util.Date

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
    val serviceList: List<Appointment.Service> = listOf(),
    val allergy: String = "",
    val hasDiabetes: Boolean = false,
    val hasPoorCoagulation: Boolean = false,
    val others: String = "",
    val appointmentList: List<Appointment> = listOf(),
    var enabled: Boolean = true
) {

    enum class NailDisorder {
        HEMATOMA,
        LEUCONIQUIA,
        ONICOFAGIA,
        ONICOLISIS,
        ONICOMADESIS,
        ONICOMICOSIS,
        ONICOQUICIA,
        PADRASTRO,
        PARONIQUIA,
        PSORIASIS,
        SURCO,
        TRAQUILONIQUIA,
        UÑAS_AMARILLENTAS,
    }

    enum class SkinDisorder {
        CALLOS,
        DISHIDROSIS,
        ESCEMAS,
        HIPERHIDROSIS,
        PSORIASIS,
        QUERATOSIS_PALMAR,
        SABAÑONES,
        VERRUGAS,
        VITILIGO
    }

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
        "serviceList" to serviceList.map { it.name },
        "allergy" to allergy,
        "hasDiabetes" to hasDiabetes,
        "hasPoorCoagulation" to hasPoorCoagulation,
        "others" to others,
        "enabled" to enabled
    )

}