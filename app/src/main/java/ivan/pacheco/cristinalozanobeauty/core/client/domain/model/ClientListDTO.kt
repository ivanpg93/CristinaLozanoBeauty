package ivan.pacheco.cristinalozanobeauty.core.client.domain.model

import java.util.Date

data class ClientListDTO(
    val id: String,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val birthday: Date? = null,
    var enabled: Boolean = true
)
