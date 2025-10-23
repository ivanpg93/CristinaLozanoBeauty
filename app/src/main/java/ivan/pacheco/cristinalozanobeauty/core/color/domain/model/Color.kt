package ivan.pacheco.cristinalozanobeauty.core.color.domain.model

import com.google.firebase.Timestamp
import java.util.Date

data class Color(
    val id: String = "",
    val brand: NailPolishBrand? = null,
    val reference: String = "",
    val date: Date = Date()
) {

    enum class NailPolishBrand {
        ABNAILS,
        BEAUTILUX,
        DNKA,
        GAIRRY,
        GELFIX,
        GREENSTYLE,
        ILLUSION_BEAUTY,
        JOIA,
        LADYLACK,
        LYC,
        NEONAIL,
        ONYX,
        OTHER,
        PALU,
        PASSIONE_BEAUTY,
        RURO,
        SEMILAC,
        THUYA,
        VICTORIA_VYNN
    }

    fun toMap() = mapOf(
        "id" to id,
        "brand" to brand,
        "reference" to reference,
        "date" to Timestamp(date)
    )

}
