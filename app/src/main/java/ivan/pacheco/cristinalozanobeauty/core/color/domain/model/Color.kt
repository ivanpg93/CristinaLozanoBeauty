package ivan.pacheco.cristinalozanobeauty.core.color.domain.model

import com.google.firebase.Timestamp
import java.util.Date

data class Color(
    val id: String = "",
    val name: String = "",
    val date: Date = Date()
) {

    fun toMap() = mapOf(
        "id" to id,
        "name" to name,
        "date" to Timestamp(date)
    )

}
