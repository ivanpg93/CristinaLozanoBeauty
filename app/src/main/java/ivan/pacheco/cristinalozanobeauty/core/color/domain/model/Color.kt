package ivan.pacheco.cristinalozanobeauty.core.color.domain.model

import com.google.firebase.Timestamp
import java.util.Date

enum class NailPolishBrand {
    NEONAIL,
    SEMILAC,
    LADYLACK,
    RURO,
    THUYA,
    DNKA,
    ILLUSION_BEAUTY,
    PASSIONE_BEAUTY,
    VICTORIA_VYNN,
    JOIA,
    GELFIX,
    OTHER
}

data class Color(
    val id: String = "",
    val brand: NailPolishBrand? = null,
    val reference: String = "",
    val date: Date = Date()
) {

    fun toMap() = mapOf(
        "id" to id,
        "brand" to brand,
        "reference" to reference,
        "date" to Timestamp(date)
    )

}
